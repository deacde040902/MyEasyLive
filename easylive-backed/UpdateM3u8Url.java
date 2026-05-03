import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.po.VideoEpisode;
import com.easylive.base.mapper.VideoEpisodeMapper;
import com.easylive.base.service.VideoEpisodeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class UpdateM3u8Url {

    public static void main(String[] args) {
        // 视频ID
        String videoId = "2048050206913552386";
        // 网关地址
        String gatewayHost = "http://localhost:7071";
        
        // 加载Spring配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        
        // 获取VideoEpisodeService
        VideoEpisodeService videoEpisodeService = context.getBean(VideoEpisodeService.class);
        
        // 获取视频的分P列表
        List<VideoEpisode> episodes = videoEpisodeService.getEpisodesByVideoId(videoId);
        if (episodes == null || episodes.isEmpty()) {
            System.out.println("视频分P不存在");
            return;
        }
        
        // 为每个分P更新m3u8Url
        for (VideoEpisode episode : episodes) {
            String m3u8Url = gatewayHost + "/hls/" + videoId + "/" + episode.getEpisodeId() + "/index.m3u8";
            episode.setM3u8Url(m3u8Url);
            boolean updated = videoEpisodeService.updateById(episode);
            System.out.println("更新分P " + episode.getEpisodeId() + " 结果: " + updated);
            System.out.println("更新后的m3u8Url: " + episode.getM3u8Url());
        }
        
        System.out.println("m3u8Url更新完成");
        
        // 关闭Spring容器
        ((ClassPathXmlApplicationContext) context).close();
    }
}
