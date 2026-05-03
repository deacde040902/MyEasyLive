package com.easylive.base.service.impl;

import com.easylive.base.entity.po.FileResource;
import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.po.VideoEpisode;
import com.easylive.base.service.FileResourceService;
import com.easylive.base.service.VideoEpisodeService;
import com.easylive.base.service.VideoService;
import com.easylive.base.service.VideoTranscodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Service
public class VideoTranscodeServiceImpl implements VideoTranscodeService {

    private static final Logger log = LoggerFactory.getLogger(VideoTranscodeServiceImpl.class);

    @Resource
    private VideoEpisodeService videoEpisodeService;
    @Resource
    private FileResourceService fileResourceService;
    @Resource
    private VideoService videoService;

    @Value("${easylive.file.base-path}")
    private String fileBasePath;

    @Value("${easylive.hls.output-path:${easylive.file.base-path}/hls}")
    private String hlsOutputBasePath;

    @Value("${gateway.host:http://localhost:7071}")
    private String gatewayHost;

    @Async
    @Override
    public void transcodeEpisodeAsync(String episodeId) {
        log.info("开始异步转码，episodeId: {}", episodeId);
        try {
            // 1. 获取分P信息和文件路径
            VideoEpisode episode = videoEpisodeService.getById(episodeId);
            if (episode == null) {
                log.error("转码失败：分P不存在，episodeId={}", episodeId);
                return;
            }
            if (episode.getM3u8Url() != null && !episode.getM3u8Url().isEmpty()) {
                log.info("该分P已转码过，跳过。episodeId={}", episodeId);
                return;
            }

            FileResource fileResource = fileResourceService.getById(episode.getFileId());
            if (fileResource == null) {
                log.error("文件资源不存在，fileId={}", episode.getFileId());
                return;
            }

            String sourceFilePath = fileBasePath + File.separator + fileResource.getFilePath();
            File sourceFile = new File(sourceFilePath);
            if (!sourceFile.exists()) {
                log.error("源文件不存在：{}", sourceFilePath);
                return;
            }

            // 2. 获取视频时长
            Integer duration = getVideoDuration(sourceFilePath);
            if (duration != null) {
                log.info("获取视频时长成功，duration={}s", duration);
            }

            // 3. 创建输出目录：hls/{videoId}/{episodeId}/
            String outputDir = hlsOutputBasePath + File.separator + episode.getVideoId() + File.separator + episode.getEpisodeId();
            File outputDirFile = new File(outputDir);
            if (!outputDirFile.exists()) {
                boolean created = outputDirFile.mkdirs();
                if (!created) {
                    log.error("无法创建输出目录：{}", outputDir);
                    return;
                }
            }
            String m3u8FileName = "index.m3u8";
            String outputM3u8Path = outputDir + File.separator + m3u8FileName;

            // 4. 构建 FFmpeg 命令（简化参数）
            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -hls_time 10 -hls_list_size 0 -f hls \"%s\"",
                    sourceFilePath, outputM3u8Path);

            log.info("执行转码命令：{}", ffmpegCmd);

            // 5. 执行命令（使用 ProcessBuilder 合并 stderr）
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", ffmpegCmd);
            pb.redirectErrorStream(true);  // 关键：合并错误输出到标准输出
            pb.directory(new File(fileBasePath));  // 设置工作目录
            Process process = pb.start();

            // 读取合并后的输出（包含进度和错误）
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.info("FFmpeg: {}", line);
                }
            }

            // 6. 等待进程完成（设置合理的超时时间）
            boolean finished = process.waitFor(120, TimeUnit.MINUTES);  // 延长超时时间
            int exitCode = finished ? process.exitValue() : -1;

            if (finished && exitCode == 0) {
                // 7. 转码成功，更新数据库中的 m3u8_url 和 duration
                String m3u8Url = gatewayHost + "/resource/hls/" + episode.getVideoId() + "/" + episode.getEpisodeId() + "/" + m3u8FileName;
                episode.setM3u8Url(m3u8Url);
                if (duration != null) {
                    episode.setDuration(duration);
                }
                videoEpisodeService.updateById(episode);
                
                // 8. 更新视频主表的 duration（第一个分P）
                if (duration != null) {
                    Video video = videoService.getById(episode.getVideoId());
                    if (video != null) {
                        video.setDuration(duration);
                        videoService.updateById(video);
                    }
                }
                
                log.info("转码成功，episodeId={}, m3u8Url={}, duration={}s", episodeId, m3u8Url, duration);
            } else {
                log.error("转码失败或超时，episodeId={}, exitCode={}, output={}", episodeId, exitCode, output.toString());
            }
        } catch (Exception e) {
            log.error("转码异常，episodeId={}", episodeId, e);
        }
    }

    /**
     * 使用 FFmpeg 获取视频时长（秒）
     */
    private Integer getVideoDuration(String sourceFilePath) {
        try {
            // 先尝试用 ffprobe
            Integer duration = tryGetVideoDurationWithFFprobe(sourceFilePath);
            if (duration != null) {
                return duration;
            }
            
            // 如果 ffprobe 失败，尝试用 ffmpeg 的输出
            log.warn("ffprobe 获取时长失败，尝试用 ffmpeg 获取...");
            duration = tryGetVideoDurationWithFFmpeg(sourceFilePath);
            if (duration != null) {
                return duration;
            }
            
            // 如果都失败，返回一个默认时长（180秒 = 3分钟）
            log.warn("无法获取视频时长，使用默认值 180秒");
            return 180;
        } catch (Exception e) {
            log.error("获取视频时长异常", e);
            return 180;  // 异常时也返回默认值
        }
    }
    
    /**
     * 使用 ffprobe 获取视频时长
     */
    private Integer tryGetVideoDurationWithFFprobe(String sourceFilePath) {
        try {
            String cmd = String.format("ffprobe -v error -show_entries format=duration -of csv=p=0 \"%s\"", sourceFilePath);
            log.info("执行获取视频时长命令：{}", cmd);

            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", cmd);
            pb.redirectErrorStream(true);
            pb.directory(new File(fileBasePath));
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }

            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (finished && process.exitValue() == 0) {
                String durationStr = output.toString().trim();
                if (durationStr != null && !durationStr.isEmpty()) {
                    try {
                        double duration = Double.parseDouble(durationStr);
                        return (int) Math.ceil(duration);
                    } catch (NumberFormatException e) {
                        log.warn("解析视频时长失败：{}", durationStr);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("ffprobe 获取时长失败：{}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 使用 ffmpeg 的输出来获取时长（备用方案）
     */
    private Integer tryGetVideoDurationWithFFmpeg(String sourceFilePath) {
        try {
            // 使用 ffmpeg -i 会在 stderr 中输出时长信息
            String cmd = String.format("ffmpeg -i \"%s\" -f null -", sourceFilePath);
            log.info("执行 ffmpeg 获取时长命令：{}", cmd);

            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", cmd);
            pb.redirectErrorStream(true);
            pb.directory(new File(fileBasePath));
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            process.waitFor(30, TimeUnit.SECONDS);
            
            // 在输出中查找 "Duration: HH:MM:SS.xx"
            String outputStr = output.toString();
            int durationIndex = outputStr.indexOf("Duration: ");
            if (durationIndex != -1) {
                int start = durationIndex + "Duration: ".length();
                int end = outputStr.indexOf(",", start);
                if (end != -1) {
                    String durationStr = outputStr.substring(start, end).trim();
                    log.info("从 ffmpeg 输出中解析到时长字符串：{}", durationStr);
                    // 解析 "HH:MM:SS.xx" 格式
                    return parseDurationFromHMS(durationStr);
                }
            }
        } catch (Exception e) {
            log.warn("ffmpeg 获取时长失败：{}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 解析 HH:MM:SS.xx 格式的时长字符串为秒数
     */
    private Integer parseDurationFromHMS(String durationStr) {
        try {
            String[] parts = durationStr.split(":");
            if (parts.length == 3) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                double seconds = Double.parseDouble(parts[2]);
                return (int) Math.ceil(hours * 3600 + minutes * 60 + seconds);
            }
        } catch (Exception e) {
            log.warn("解析 HMS 时长失败：{}", durationStr, e);
        }
        return null;
    }
}