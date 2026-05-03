package com.easylive.web.controller;

import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.dto.VideoPublishDTO;
import com.easylive.base.entity.po.SearchKeyword;
import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.po.VideoEpisode;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.FileResourceService;
import com.easylive.base.service.VideoEpisodeService;
import com.easylive.base.service.VideoService;
import com.easylive.base.service.SearchService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * <p>
 * 视频主表 前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@RestController
@RequestMapping("/video")
public class VideoController extends ABaseController {

    @Resource
    private VideoService videoService;
    @Resource
    private VideoEpisodeService videoEpisodeService;

    @Resource
    private SearchService searchService;

    @Resource
    private FileResourceService fileResourceService;

    @Value("${easylive.file.base-path}")
    private String fileBasePath;

    @Value("${easylive.hls.output-path:${easylive.file.base-path}/hls}")
    private String hlsOutputBasePath;

    @Value("${gateway.host:http://localhost:7071}")
    private String gatewayHost;

    /**
     * 获取推荐视频（热门+随机混合）
     * @param pageNo 页码
     * @param pageSize 每页数量
     */
    @PostMapping("/recommend")
    public ResponseVO<List<Video>> getRecommendVideos(@RequestParam(defaultValue = "1") Integer pageNo,
                                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        // 简单实现：按播放量降序（热门推荐）
        List<Video> list = videoService.getVideoList(pageNo, pageSize, null, null, "play_count");
        // 如果不足 pageSize 条，可补充随机视频（或其它策略），这里暂不处理
        return getSuccessResponseVO(list);
    }

    /**
     * 获取视频列表（推荐/分类/标签等）
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @param categoryId 分类ID（可选）
     * @param tagId 标签ID（可选）
     * @param orderBy 排序：play_count（最热）、create_time（最新）
     */
    @PostMapping("/list")
    public ResponseVO<Map<String, Object>> getVideoList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @RequestParam(required = false) Integer categoryId,
                                                        @RequestParam(required = false) Integer tagId,
                                                        @RequestParam(defaultValue = "create_time") String orderBy) {
        List<Video> list = videoService.getVideoList(pageNo, pageSize, categoryId, tagId, orderBy);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return getSuccessResponseVO(result);
    }
    /**
     * 获取视频详情（增加播放量）
     * @param videoId 视频ID
     */
    @PostMapping("/detail")
    public ResponseVO<Video> getVideoDetail(@RequestParam String videoId,
                                            HttpServletRequest request) {
        String userId = null;
        try {
            userId = getUserIdByToken(request);
        } catch (Exception e) {
            // 用户未登录也可以访问视频详情
        }
        Video video = videoService.getVideoDetailForUser(videoId, userId);
        // 增加播放量
        videoService.incrementPlayCount(videoId);
        return getSuccessResponseVO(video);
    }

    /**
     * 获取视频分P列表
     * @param videoId 视频ID
     */
    @PostMapping("/episodes")
    public ResponseVO<List<VideoEpisode>> getVideoEpisodes(@RequestParam String videoId) {
        List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(videoId);
        return getSuccessResponseVO(episodes);
    }

    /**
     * 获取视频的m3u8播放地址（返回URL，前端可直接播放）
     * @param episodeId 分P ID
     */
    @PostMapping("/m3u8Url")
    public ResponseVO<String> getM3u8Url(@RequestParam String episodeId) {
        VideoEpisode episode = videoEpisodeService.getById(episodeId);
        if (episode == null || StringUtils.isEmpty(episode.getM3u8Url())) {
            throw new BusinessException("该视频暂未转码完成");
        }
        // 返回可通过网关访问的m3u8地址（假设网关有静态资源路由）
        return getSuccessResponseVO(episode.getM3u8Url());
    }

    // 搜索视频
    @PostMapping("/search")
    public ResponseVO<List<Video>> search(@RequestBody java.util.Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        Integer pageNo = params.get("pageNo") != null ? Integer.parseInt(params.get("pageNo").toString()) : 1;
        Integer pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;
        String sortType = (String) params.get("sortType");
        List<Video> list = searchService.searchVideo(keyword, pageNo, pageSize, sortType);
        return getSuccessResponseVO(list);
    }

    // 热词
    @PostMapping("/getSearchKeywordTop")
    public ResponseVO<List<SearchKeyword>> getSearchKeywordTop() {
        List<SearchKeyword> list = searchService.getHotKeywords();
        return getSuccessResponseVO(list);
    }

    // 推荐视频
    @PostMapping("/getVideoRecommend")
    public ResponseVO<List<Video>> getVideoRecommend(@RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Video> list = videoService.getRecommendVideos(pageNo, pageSize);
        return getSuccessResponseVO(list);
    }

    // 热门视频
    @PostMapping("/loadHotVideoList")
    public ResponseVO<List<Video>> loadHotVideoList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Video> list = videoService.getHotVideos(pageNo, pageSize);
        return getSuccessResponseVO(list);
    }

    /**
     * 发布视频
     * @param file 视频文件
     * @param coverFile 封面文件（可选）
     * @param title 视频标题
     * @param description 视频描述
     * @param categoryId 分类ID
     * @param tags 标签
     */
    @PostMapping("/publish")
    public ResponseVO<String> publishVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("tags") String tags,
            @RequestParam(value = "isBigVideo", defaultValue = "0") Integer isBigVideo,
            HttpServletRequest request) {

        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 1. 先将视频文件上传到资源服务，获取 fileId
        String fileId;
        try {
            fileId = fileResourceService.uploadFile(file, userId);
        } catch (Exception e) {
            throw new BusinessException("视频文件上传失败：" + e.getMessage());
        }

        // 2. 处理封面文件（如果提供了封面）
        String coverFileId = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                coverFileId = fileResourceService.uploadFile(coverFile, userId);
            } catch (Exception e) {
                throw new BusinessException("封面文件上传失败：" + e.getMessage());
            }
        }

        // 3. 构造 DTO
        VideoPublishDTO videoDTO = new VideoPublishDTO();
        videoDTO.setTitle(title);
        videoDTO.setDescription(description);
        videoDTO.setCategoryId(categoryId);
        videoDTO.setCoverFileId(coverFileId);
        videoDTO.setIsBigVideo(isBigVideo);

        VideoPublishDTO.EpisodeDTO episode = new VideoPublishDTO.EpisodeDTO();
        episode.setTitle(title);
        episode.setEpisodeNum(1);
        episode.setFileId(fileId);

        videoDTO.setEpisodes(Collections.singletonList(episode));

        // 4. 调用 service 发布（内部会保存视频、分P、异步转码）
        videoService.publishVideo(videoDTO, userId);

        return getSuccessResponseVO("视频发布成功");
    }

    /**
     * 更新视频
     * @param videoId 视频ID
     * @param title 视频标题
     * @param description 视频描述
     * @param categoryId 分类ID
     * @param tags 标签
     * @param isBigVideo 是否为大视频
     * @param coverFile 封面文件（可选）
     */
    @PutMapping("/update/{videoId}")
    public ResponseVO<String> updateVideo(@PathVariable String videoId,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                          @RequestParam(value = "tags", required = false) String tags,
                                          @RequestParam(value = "isBigVideo", required = false) Integer isBigVideo,
                                          @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                                          HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");

        // 将 DTO 转为 Video 实体（只传递允许修改的字段）
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setCategoryId(categoryId);
        video.setIsBigVideo(isBigVideo);

        // 如果上传了新的封面，先上传封面
        if (coverFile != null && !coverFile.isEmpty()) {
            String coverFileId = fileResourceService.uploadFile(coverFile, userId);
            video.setCoverFileId(coverFileId);
        }

        videoService.updateVideo(videoId, video, userId);
        return getSuccessResponseVO("视频更新成功");
    }

    /**
     * 删除视频
     * @param videoId 视频ID
     */
    @DeleteMapping("/delete/{videoId}")
    public ResponseVO<String> deleteVideo(@PathVariable String videoId,
                                          HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        videoService.deleteVideo(videoId, userId);
        return getSuccessResponseVO("视频删除成功");
    }


    /**
     * 获取视频评论
     * @param videoId 视频ID
     * @param page 页码
     * @param size 每页数量
     */
    @PostMapping("/comments/{videoId}")
    public ResponseVO<String> getVideoComments(@PathVariable String videoId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        // 这里需要实现获取视频评论逻辑

        return getSuccessResponseVO("获取评论成功");
    }

    /**
     * 发表视频评论
     * @param videoId 视频ID
     * @param content 评论内容
     */
    @PostMapping("/comments/{videoId}/add")
    public ResponseVO<String> addComment(@PathVariable String videoId,
                                         @RequestBody String content,
                                         HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 这里需要实现发表视频评论逻辑

        return getSuccessResponseVO("评论成功");
    }

    /**
     * 视频播放（返回视频的直接播放地址）
     * @param videoId 视频ID
     */
    @GetMapping("/play/{videoId}")
    public ResponseVO<String> playVideo(@PathVariable String videoId) {
        Video video = videoService.getById(videoId);
        if (video == null || video.getStatus() != 1) {
            throw new BusinessException("视频不存在或已下架");
        }

        // 查询视频播放地址
        List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(videoId);
        if (episodes == null || episodes.isEmpty()) {
            throw new BusinessException("视频暂未转码完成");
        }

        VideoEpisode firstEpisode = episodes.get(0);
        if (StringUtils.isEmpty(firstEpisode.getM3u8Url())) {
            // 检查转码文件是否存在
            String outputDir = hlsOutputBasePath + File.separator + videoId + File.separator + firstEpisode.getEpisodeId();
            String m3u8FilePath = outputDir + File.separator + "index.m3u8";
            File m3u8File = new File(m3u8FilePath);
            
            if (m3u8File.exists()) {
                // 如果转码文件存在但数据库未更新，手动更新
                String m3u8Url = gatewayHost + "/resource/hls/" + videoId + "/" + firstEpisode.getEpisodeId() + "/index.m3u8";
                firstEpisode.setM3u8Url(m3u8Url);
                videoEpisodeService.updateById(firstEpisode);
                return getSuccessResponseVO(m3u8Url);
            } else {
                throw new BusinessException("视频暂未转码完成");
            }
        }

        return getSuccessResponseVO(firstEpisode.getM3u8Url());
    }

    /**
     * 手动更新视频的m3u8Url（临时端点）
     */
    @PostMapping("/updateM3u8Url")
    public ResponseVO<String> updateM3u8Url(@RequestParam String videoId) {
        // 获取视频的分P列表
        List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(videoId);
        if (episodes == null || episodes.isEmpty()) {
            throw new BusinessException("视频分P不存在");
        }
        
        // 为每个分P更新m3u8Url
        for (VideoEpisode episode : episodes) {
            String m3u8Url = gatewayHost + "/resource/hls/" + videoId + "/" + episode.getEpisodeId() + "/index.m3u8";
            episode.setM3u8Url(m3u8Url);
            videoEpisodeService.updateById(episode);
        }
        
        return getSuccessResponseVO("m3u8Url更新成功");
    }

    /**
     * 获取大视频列表（最多3个）
     */
    @PostMapping("/big/list")
    public ResponseVO<List<Video>> getBigVideos() {
        List<Video> bigVideos = videoService.getBigVideos();
        return getSuccessResponseVO(bigVideos);
    }
}