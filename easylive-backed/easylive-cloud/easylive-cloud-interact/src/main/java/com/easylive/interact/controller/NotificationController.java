package com.easylive.interact.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.ChatMessageService;
import com.easylive.interact.entity.vo.NotificationCountsVO;
import com.easylive.interact.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/notification")
public class NotificationController extends ABaseController {

    @Resource
    private NotificationService notificationService;

    @Resource
    private ChatMessageService chatMessageService;

    /**
     * 获取所有通知计数
     * 返回用户的各类通知数量，用于消息中心角标显示
     */
    @PostMapping("/getNotificationCounts")
    @GlobalInterceptor
    public ResponseVO<NotificationCountsVO> getNotificationCounts(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 获取基础通知计数（赞、收藏、评论）
        NotificationCountsVO counts = notificationService.getNotificationCounts(userId);

        // 获取未读消息数
        Integer unreadMessageCount = chatMessageService.getTotalUnreadCount(userId);
        counts.setUnreadMessageCount(unreadMessageCount);

        return getSuccessResponseVO(counts);
    }
}