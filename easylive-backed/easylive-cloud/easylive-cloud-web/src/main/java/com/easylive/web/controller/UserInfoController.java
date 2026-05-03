package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.UserInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户信息 前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-17
 */
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController {

    @Resource
    private UserInfoService  userInfoService;

    @RequestMapping(value = "/searchUser", method = {RequestMethod.GET, RequestMethod.POST})
    @GlobalInterceptor
    public ResponseVO<List<UserInfo>> searchUser(@RequestBody(required = false) Map<String, String> params, 
                                                 @RequestParam(required = false) String keyword,
                                                 HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        String searchKeyword = params != null ? params.get("keyword") : keyword;
        List<UserInfo> users = userInfoService.searchUser(searchKeyword);
        return getSuccessResponseVO(users);
    }

    @PostMapping("/addFriend")
    @GlobalInterceptor
    public ResponseVO<Void> addFriend(@RequestBody Map<String, String> params, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        String friendUserId = params.get("friendUserId");
        userInfoService.addFriend(userId, friendUserId);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/getFriendList")
    @GlobalInterceptor
    public ResponseVO<List<UserInfo>> getFriendList(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        List<UserInfo> friends = userInfoService.getFriendList(userId);
        return getSuccessResponseVO(friends);
    }
}
