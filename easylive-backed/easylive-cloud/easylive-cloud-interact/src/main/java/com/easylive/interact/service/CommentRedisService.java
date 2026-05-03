package com.easylive.interact.service;

import com.easylive.base.entity.po.Comment;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentRedisService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CommentService commentService;

    @Lazy
    @Resource
    private CommentRedisService self;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String COMMENT_LIST_PREFIX = "comment:list:";
    private static final String COMMENT_TOP_PREFIX = "comment:top:";
    private static final String COMMENT_LIKE_COUNT_KEY = "comment:like:count";
    private static final String COMMENT_USER_LIKE_KEY = "comment:user:like:";
    private static final String COMMENT_TEMP_SET = "comment:temp:ids";
    private static final int BATCH_SIZE = 50;

    public void postComment(Comment comment) {
        if (comment.getCreateTime() == null) {
            comment.setCreateTime(LocalDateTime.now());
        }
        if (comment.getUpdateTime() == null) {
            comment.setUpdateTime(LocalDateTime.now());
        }
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0);
        }
        if (comment.getTopFlag() == null) {
            comment.setTopFlag(false);
        }
        if (comment.getStatus() == null) {
            comment.setStatus(true);
        }

        String videoKey = COMMENT_LIST_PREFIX + comment.getVideoId();
        try {
            String json = OBJECT_MAPPER.writeValueAsString(comment);
            stringRedisTemplate.opsForList().rightPush(videoKey, json);
            stringRedisTemplate.opsForList().trim(videoKey, -500, -1);

            if (comment.getTopFlag()) {
                String topKey = COMMENT_TOP_PREFIX + comment.getVideoId();
                stringRedisTemplate.opsForSet().add(topKey, comment.getCommentId().toString());
            }

            String tempId = comment.getVideoId() + ":" + System.nanoTime();
            stringRedisTemplate.opsForSet().add(COMMENT_TEMP_SET, tempId);

        } catch (JsonProcessingException e) {
            throw new BusinessException("评论序列化失败");
        }

        self.syncCommentToDb(comment);
    }

    public List<Comment> loadComment(String videoId, Integer pageNo, Integer pageSize, String orderType) {
        String videoKey = COMMENT_LIST_PREFIX + videoId;
        int start = (pageNo - 1) * pageSize;
        int end = start + pageSize - 1;

        List<String> redisCommentJson = stringRedisTemplate.opsForList().range(videoKey, -200, -1);
        List<Comment> redisComments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(redisCommentJson)) {
            for (String json : redisCommentJson) {
                try {
                    Comment comment = OBJECT_MAPPER.readValue(json, Comment.class);
                    redisComments.add(comment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if ("like_count".equals(orderType)) {
            redisComments.sort((a, b) -> {
                int topCompare = Boolean.compare(b.getTopFlag(), a.getTopFlag());
                if (topCompare != 0) return topCompare;
                return Integer.compare(b.getLikeCount(), a.getLikeCount());
            });
        } else {
            redisComments.sort((a, b) -> {
                int topCompare = Boolean.compare(b.getTopFlag(), a.getTopFlag());
                if (topCompare != 0) return topCompare;
                return b.getCreateTime().compareTo(a.getCreateTime());
            });
        }

        if (redisComments.size() >= pageSize) {
            return redisComments.subList(0, pageSize);
        }

        List<Comment> dbComments = commentService.loadComment(videoId, pageNo, pageSize, orderType);
        Set<Long> existIds = redisComments.stream()
                .map(Comment::getCommentId)
                .collect(Collectors.toSet());

        for (Comment db : dbComments) {
            if (!existIds.contains(db.getCommentId())) {
                redisComments.add(db);
            }
        }

        return redisComments.stream().limit(pageSize).collect(Collectors.toList());
    }

    public void likeComment(Long commentId, String userId) {
        String likeKey = COMMENT_USER_LIKE_KEY + commentId;
        Long added = stringRedisTemplate.opsForSet().add(likeKey, userId);
        if (added != null && added > 0) {
            stringRedisTemplate.opsForHash().increment(COMMENT_LIKE_COUNT_KEY, commentId.toString(), 1);
            self.syncCommentLikeToDb(commentId, userId, true);
        } else {
            throw new BusinessException("已经点过赞了");
        }
    }

    public void unlikeComment(Long commentId, String userId) {
        String likeKey = COMMENT_USER_LIKE_KEY + commentId;
        Long removed = stringRedisTemplate.opsForSet().remove(likeKey, userId);
        if (removed != null && removed > 0) {
            stringRedisTemplate.opsForHash().increment(COMMENT_LIKE_COUNT_KEY, commentId.toString(), -1);
            self.syncCommentLikeToDb(commentId, userId, false);
        } else {
            throw new BusinessException("还没有点过赞");
        }
    }

    public void topComment(Long commentId, String videoId) {
        String topKey = COMMENT_TOP_PREFIX + videoId;
        stringRedisTemplate.opsForSet().add(topKey, commentId.toString());
    }

    public void cancelTopComment(Long commentId, String videoId) {
        String topKey = COMMENT_TOP_PREFIX + videoId;
        stringRedisTemplate.opsForSet().remove(topKey, commentId.toString());
    }

    public void deleteComment(Long commentId, String videoId) {
        String videoKey = COMMENT_LIST_PREFIX + videoId;
        List<String> list = stringRedisTemplate.opsForList().range(videoKey, 0, -1);
        if (list != null) {
            for (String json : list) {
                try {
                    Comment comment = OBJECT_MAPPER.readValue(json, Comment.class);
                    if (comment.getCommentId().equals(commentId)) {
                        stringRedisTemplate.opsForList().remove(videoKey, 1, json);
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    @Async
    public void syncCommentToDb(Comment comment) {
        commentService.postComment(comment);
    }

    @Async
    public void syncCommentLikeToDb(Long commentId, String userId, boolean isLike) {
        if (isLike) {
            commentService.likeComment(commentId);
        } else {
            commentService.unlikeComment(commentId);
        }
    }

    @Async
    public void batchInsertCommentToDb() {
        Set<String> tempIds = stringRedisTemplate.opsForSet().members(COMMENT_TEMP_SET);
        if (CollectionUtils.isEmpty(tempIds)) {
            return;
        }

        List<Comment> toInsert = new ArrayList<>();
        for (String tempId : tempIds) {
            String videoId = tempId.split(":")[0];
            String videoKey = COMMENT_LIST_PREFIX + videoId;
            List<String> jsonList = stringRedisTemplate.opsForList().range(videoKey, 0, BATCH_SIZE - 1);
            if (CollectionUtils.isEmpty(jsonList)) {
                continue;
            }
            for (String json : jsonList) {
                try {
                    Comment comment = OBJECT_MAPPER.readValue(json, Comment.class);
                    if (comment.getCommentId() == null || commentService.getById(comment.getCommentId()) == null) {
                        toInsert.add(comment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!CollectionUtils.isEmpty(toInsert)) {
            try {
                for (Comment comment : toInsert) {
                    commentService.save(comment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        stringRedisTemplate.delete(COMMENT_TEMP_SET);
    }
}
