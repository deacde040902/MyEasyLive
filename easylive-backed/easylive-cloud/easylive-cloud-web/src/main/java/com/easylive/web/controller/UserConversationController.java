package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.UserConversation;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.UserConversationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户会话列表 前端控制器
 */
@RestController
@RequestMapping("/userConversation")
public class UserConversationController extends ABaseController {

    @Resource
    private UserConversationService userConversationService;

    /**
     * 获取当前用户的会话列表（含最后一条消息和未读数）
     */
    @PostMapping("/list")
    @GlobalInterceptor
    public ResponseVO<List<UserConversation>> getConversationList(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        List<UserConversation> list = userConversationService.getConversationList(userId);
        return getSuccessResponseVO(list);
    }
}