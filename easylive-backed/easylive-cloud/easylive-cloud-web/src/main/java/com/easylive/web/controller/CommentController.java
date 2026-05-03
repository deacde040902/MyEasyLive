package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.Comment;
import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.CommentService;
import com.easylive.base.service.VideoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评论表 前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@RestController
@RequestMapping("/comment")
public class CommentController extends ABaseController {

    @Resource
    private CommentService commentService;
    @Resource
    private VideoService videoService;

    /**
     * 发布评论（需登录）
     * 参数：videoId, content, replyCommentId(可选), imgPath(可选)
     */
    @PostMapping("/postComment")
    @GlobalInterceptor
    public ResponseVO<Void> postComment(@RequestParam String videoId,
                                        @RequestParam String content,
                                        @RequestParam(required = false) Long replyCommentId,
                                        @RequestParam(required = false) String imgPath,
                                        HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        Comment comment = new Comment();
        comment.setVideoId(videoId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setReplyCommentId(replyCommentId);
        comment.setImgPath(imgPath);
        commentService.postComment(comment);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取评论列表（无需登录）
     * 参数：videoId, pageNo(默认1), pageSize(默认10), orderType(create_time/like_count)
     */
    @PostMapping("/loadComment")
    public ResponseVO<Map<String, Object>> loadComment(@RequestParam String videoId,
                                                       @RequestParam(defaultValue = "1") Integer pageNo,
                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam(defaultValue = "create_time") String orderType) {
        List<Comment> list = commentService.loadComment(videoId, pageNo, pageSize, orderType);
        int total = commentService.getCommentCount(videoId);  // 需要实现这个方法
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return getSuccessResponseVO(result);
    }

    /**
     * 评论置顶（需登录，仅视频作者）
     * 参数：commentId
     */
    @PostMapping("/topComment")
    @GlobalInterceptor
    public ResponseVO<Void> topComment(@RequestParam Long commentId,
                                       HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        // 查询评论所属视频
        Comment comment = commentService.getById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        Video video = videoService.getById(comment.getVideoId());
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        // 只有视频作者可置顶
        if (!video.getUserId().equals(userId)) {
            throw new BusinessException("只有视频作者可以置顶评论");
        }
        commentService.topComment(commentId, userId, video.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * 取消置顶（需登录，仅视频作者）
     * 参数：commentId
     */
    @PostMapping("/cancelTopComment")
    @GlobalInterceptor
    public ResponseVO<Void> cancelTopComment(@RequestParam Long commentId,
                                             HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        Comment comment = commentService.getById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        Video video = videoService.getById(comment.getVideoId());
        if (video == null) {
            throw new BusinessException("视频不存在");
        }
        if (!video.getUserId().equals(userId)) {
            throw new BusinessException("只有视频作者可以取消置顶");
        }
        commentService.cancelTopComment(commentId, userId, video.getUserId());
        return getSuccessResponseVO(null);
    }

    /**
     * 删除评论（需登录）
     * 参数：commentId
     */
    @PostMapping("/userDelComment")
    @GlobalInterceptor
    public ResponseVO<Void> userDelComment(@RequestParam Long commentId,
                                           HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        commentService.deleteComment(commentId, userId);
        return getSuccessResponseVO(null);
    }
}
