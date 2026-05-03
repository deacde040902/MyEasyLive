package com.easylive.base.service;

import com.easylive.base.entity.po.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
public interface CommentService extends IService<Comment> {
    /**
     * 发布评论
     */
    void postComment(Comment comment);

    /**
     * 获取评论列表（支持分页、排序）
     * @param videoId 视频ID
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @param orderType 排序：create_time（最新），like_count（最热）
     */
    List<Comment> loadComment(String videoId, Integer pageNo, Integer pageSize, String orderType);

    /**
     * 置顶评论（需校验权限：只有视频作者或管理员可操作）
     */
    void topComment(Long commentId, String operatorUserId, String videoAuthorId);

    /**
     * 取消置顶
     */
    void cancelTopComment(Long commentId, String operatorUserId, String videoAuthorId);

    void deleteComment(Long commentId, String userId);
    List<Comment> getCommentListForAdmin(String videoId, Integer pageNo, Integer pageSize);
    void deleteCommentByAdmin(Long commentId);

    void likeComment(Long commentId);
    void unlikeComment(Long commentId);

    /**
     * 获取视频评论数量
     * @param videoId 视频ID
     * @return 评论数量
     */
    int getCommentCount(String videoId);
}
