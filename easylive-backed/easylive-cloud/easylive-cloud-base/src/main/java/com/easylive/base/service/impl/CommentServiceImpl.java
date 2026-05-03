package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easylive.base.entity.po.Comment;
import com.easylive.base.entity.po.Video;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.CommentMapper;
import com.easylive.base.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private VideoService videoService;

    @Override
    @Transactional
    public void postComment(Comment comment) {
        if (!StringUtils.hasText(comment.getVideoId())) {
            throw new BusinessException("视频ID不能为空");
        }
        if (!StringUtils.hasText(comment.getContent())) {
            throw new BusinessException("评论内容不能为空");
        }
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setLikeCount(0);
        comment.setTopFlag(false);          // Boolean 类型，false 表示未置顶
        comment.setStatus(true);            // Boolean 类型，true 表示正常状态
        this.save(comment);
        // 可选：更新视频表的评论计数
    }

    @Override
    public List<Comment> loadComment(String videoId, Integer pageNo, Integer pageSize, String orderType) {
        Page<Comment> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getVideoId, videoId)
                .eq(Comment::getStatus, true);   // 只查询正常状态的评论
        // 置顶评论优先展示（true 排在前面）
        wrapper.orderByDesc(Comment::getTopFlag);
        if ("like_count".equals(orderType)) {
            wrapper.orderByDesc(Comment::getLikeCount);
        } else {
            wrapper.orderByDesc(Comment::getCreateTime);
        }
        Page<Comment> result = this.page(page, wrapper);
        return result.getRecords();
    }

    @Override
    @Transactional
    public void topComment(Long commentId, String operatorUserId, String videoAuthorId) {
        if (!operatorUserId.equals(videoAuthorId)) {
            throw new BusinessException("只有视频作者可以置顶评论");
        }
        Comment comment = this.getById(commentId);
        if (comment == null || comment.getStatus() == null || !comment.getStatus()) {
            throw new BusinessException("评论不存在或已删除");
        }
        if (comment.getTopFlag() != null && comment.getTopFlag()) {
            throw new BusinessException("评论已经是置顶状态");
        }
        comment.setTopFlag(true);
        comment.setUpdateTime(LocalDateTime.now());
        this.updateById(comment);
    }

    @Override
    @Transactional
    public void cancelTopComment(Long commentId, String operatorUserId, String videoAuthorId) {
        if (!operatorUserId.equals(videoAuthorId)) {
            throw new BusinessException("只有视频作者可以取消置顶");
        }
        Comment comment = this.getById(commentId);
        if (comment == null || comment.getStatus() == null || !comment.getStatus()) {
            throw new BusinessException("评论不存在或已删除");
        }
        if (comment.getTopFlag() == null || !comment.getTopFlag()) {
            throw new BusinessException("评论未置顶");
        }
        comment.setTopFlag(false);
        comment.setUpdateTime(LocalDateTime.now());
        this.updateById(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String userId) {
        Comment comment = this.getById(commentId);
        if (comment == null || comment.getStatus() == null || !comment.getStatus()) {
            throw new BusinessException("评论不存在或已删除");
        }
        Video video = videoService.getById(comment.getVideoId());
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        // 只有评论作者或视频作者可以删除
        if (!comment.getUserId().equals(userId) && !video.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }
        comment.setStatus(false); // 逻辑删除
        comment.setUpdateTime(LocalDateTime.now());
        this.updateById(comment);
        // 可选：更新视频表的评论数
    }

    @Override
    public List<Comment> getCommentListForAdmin(String videoId, Integer pageNo, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(videoId)) {
            wrapper.eq(Comment::getVideoId, videoId);
        }
        wrapper.orderByDesc(Comment::getCreateTime);
        return this.page(page, wrapper).getRecords();
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        this.removeById(commentId);
    }

    @Override
    @Transactional
    public void likeComment(Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment != null && comment.getStatus() != null && comment.getStatus()) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            this.updateById(comment);
        }
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment != null && comment.getStatus() != null && comment.getStatus() && comment.getLikeCount() > 0) {
            comment.setLikeCount(comment.getLikeCount() - 1);
            this.updateById(comment);
        }
    }

    @Override
    public int getCommentCount(String videoId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getVideoId, videoId)
                .eq(Comment::getStatus, true);
        return (int) this.count(queryWrapper);
    }
}
