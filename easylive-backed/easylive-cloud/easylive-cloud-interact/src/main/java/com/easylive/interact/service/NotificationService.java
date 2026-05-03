package com.easylive.interact.service;

import com.easylive.base.entity.po.UserCollection;
import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.po.VideoComment;
import com.easylive.base.entity.po.VideoInteraction;
import com.easylive.base.mapper.UserCollectionMapper;
import com.easylive.base.mapper.VideoCommentMapper;
import com.easylive.base.mapper.VideoInteractionMapper;
import com.easylive.base.mapper.VideoMapper;
import com.easylive.interact.entity.vo.NotificationCountsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务
 */
@Service
public class NotificationService {

    @Resource
    private VideoInteractionMapper videoInteractionMapper;

    @Resource
    private UserCollectionMapper userCollectionMapper;

    @Resource
    private VideoCommentMapper videoCommentMapper;

    @Resource
    private VideoMapper videoMapper;

    /**
     * 获取用户的所有通知计数
     *
     * @param userId 用户ID
     * @return 通知计数值对象
     */
    public NotificationCountsVO getNotificationCounts(String userId) {
        NotificationCountsVO counts = new NotificationCountsVO();

        // 获取用户发布的所有视频ID
        List<Video> userVideos = videoMapper.selectList(new LambdaQueryWrapper<Video>()
                .eq(Video::getUserId, userId));
        List<String> userVideoIds = userVideos.stream()
                .map(Video::getVideoId)
                .collect(Collectors.toList());

        if (!userVideoIds.isEmpty()) {
            // 获取收到的赞数（其他用户对当前用户视频的点赞）
            Long likeCount = videoInteractionMapper.selectCount(new LambdaQueryWrapper<VideoInteraction>()
                    .in(VideoInteraction::getVideoId, userVideoIds)
                    .eq(VideoInteraction::getType, (byte) 1)); // type=1 表示点赞
            counts.setLikeCount(likeCount.intValue());

            // 获取收到的收藏数（其他用户收藏当前用户的视频）
            Long collectionCount = userCollectionMapper.selectCount(new LambdaQueryWrapper<UserCollection>()
                    .in(UserCollection::getVideoId, userVideoIds));
            counts.setCollectionCount(collectionCount.intValue());

            // 获取评论数（其他用户对当前用户视频的评论）
            Long commentCount = videoCommentMapper.selectCount(new LambdaQueryWrapper<VideoComment>()
                    .in(VideoComment::getVideoId, userVideoIds));
            counts.setCommentCount(commentCount.intValue());
        } else {
            counts.setLikeCount(0);
            counts.setCollectionCount(0);
            counts.setCommentCount(0);
        }

        // 系统通知数（暂定为0，可后续扩展）
        counts.setSystemNotificationCount(0);

        // 未读消息数（由ChatMessageService处理，这里设为0）
        counts.setUnreadMessageCount(0);

        return counts;
    }
}