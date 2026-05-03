package com.easylive.interact.controller;


import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.interact.service.ChatService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController extends ABaseController {

    @Resource
    private ChatService chatService;

    @PostMapping("/getMessageList")
    @GlobalInterceptor
    public ResponseVO<List<Map<String, Object>>> getMessageList(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        List<Map<String, Object>> contacts = chatService.getSessionList(userId);
        return getSuccessResponseVO(contacts);
    }

    @PostMapping("/getChatHistory")
    @GlobalInterceptor
    public ResponseVO<List<Map<String, Object>>> getChatHistory(@RequestParam String userId, HttpServletRequest request) {
        String currentUserId = getUserIdByToken(request);
        if (currentUserId == null) throw new BusinessException("请先登录");
        List<Map<String, Object>> messages = chatService.getChatHistoryMap(currentUserId, userId);
        return getSuccessResponseVO(messages);
    }

    @PostMapping("/sendMessage")
    @GlobalInterceptor
    public ResponseVO<Void> sendMessage(@RequestBody Map<String, Object> message, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        message.put("senderId", userId);
        chatService.sendMessage(message);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/markRead")
    @GlobalInterceptor
    public ResponseVO<Void> markRead(@RequestParam String userId, HttpServletRequest request) {
        String currentUserId = getUserIdByToken(request);
        if (currentUserId == null) throw new BusinessException("请先登录");
        chatService.markRead(currentUserId, userId);
        return getSuccessResponseVO(null);
    }
}