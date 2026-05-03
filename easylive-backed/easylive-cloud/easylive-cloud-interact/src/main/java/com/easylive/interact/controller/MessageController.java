package com.easylive.interact.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.ChatMessage;
import com.easylive.base.entity.po.UserConversation;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.ChatMessageService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 聊天消息表 前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */

@RestController
@RequestMapping("/message")
public class MessageController extends ABaseController {

    @Resource
    private ChatMessageService chatMessageService;

    /**
     * 发送消息（文本/表情/图片）
     * 参数：toUserId, content, messageType(1文本2表情3图片), imageUrl(当type=3时必填)
     */
    @PostMapping("/send")
    @GlobalInterceptor
    public ResponseVO<ChatMessage> sendMessage(@RequestParam String toUserId,
                                               @RequestParam(required = false) String content,
                                               @RequestParam(defaultValue = "1") Integer messageType,
                                               @RequestParam(required = false) String imageUrl,
                                               HttpServletRequest request) {
        String fromUserId = getUserIdByToken(request);
        if (fromUserId == null) throw new BusinessException("请先登录");
        ChatMessage message = chatMessageService.sendMessage(fromUserId, toUserId, content, messageType, imageUrl);
        return getSuccessResponseVO(message);
    }

    /**
     * 获取与某人的聊天记录（分页）
     */
    @PostMapping("/history")
    @GlobalInterceptor
    public ResponseVO<List<ChatMessage>> getHistory(@RequestParam String targetId,
                                                    @RequestParam(defaultValue = "1") Integer pageNo,
                                                    @RequestParam(defaultValue = "20") Integer pageSize,
                                                    HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        List<ChatMessage> list = chatMessageService.getChatHistory(userId, targetId, pageNo, pageSize);
        return getSuccessResponseVO(list);
    }

    /**
     * 删除消息（逻辑删除）
     */
    @PostMapping("/delete")
    @GlobalInterceptor
    public ResponseVO<Void> deleteMessage(@RequestParam Long messageId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        chatMessageService.deleteMessage(messageId, userId);
        return getSuccessResponseVO(null);
    }

    /**
     * 标记与某人的所有消息为已读
     */
    @PostMapping("/read")
    @GlobalInterceptor
    public ResponseVO<Void> markAsRead(@RequestParam String targetId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        chatMessageService.markAsRead(userId, targetId);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取总未读消息数
     */
    @PostMapping("/getNoReadCount")
    @GlobalInterceptor
    public ResponseVO<Integer> getNoReadCount(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        return getSuccessResponseVO(chatMessageService.getTotalUnreadCount(userId));
    }

    /**
     * 获取每个联系人的未读消息数（分组）
     */
    @PostMapping("/getNoReadCountGroup")
    @GlobalInterceptor
    public ResponseVO<List<UserConversation>> getNoReadCountGroup(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        return getSuccessResponseVO(chatMessageService.getUnreadCountGroup(userId));
    }

    /**
     * 获取最近会话列表（含最后一条消息）
     */
    @PostMapping("/conversationList")
    @GlobalInterceptor
    public ResponseVO<List<UserConversation>> getConversationList(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        return getSuccessResponseVO(chatMessageService.getConversationList(userId));
    }

    /**
     * 撤回消息
     */
    @PostMapping("/recall")
    @GlobalInterceptor
    public ResponseVO<Void> recallMessage(@RequestParam Long messageId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        chatMessageService.recallMessage(messageId, userId);
        return getSuccessResponseVO(null);
    }

    /**
     * 清空与某人的聊天记录
     */
    @PostMapping("/clearHistory")
    @GlobalInterceptor
    public ResponseVO<Void> clearHistory(@RequestParam String targetId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        chatMessageService.clearChatHistory(userId, targetId);
        return getSuccessResponseVO(null);
    }

    /**
     * 删除会话
     */
    @PostMapping("/deleteConversation")
    @GlobalInterceptor
    public ResponseVO<Void> deleteConversation(@RequestParam String targetId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        chatMessageService.deleteConversation(userId, targetId);
        return getSuccessResponseVO(null);
    }
}