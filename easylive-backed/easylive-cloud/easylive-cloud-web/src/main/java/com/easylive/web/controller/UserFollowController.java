package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.UserFollow;
import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.UserFollowService;
import com.easylive.base.service.UserInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户关注关系表 前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@RestController
@RequestMapping("/userFollow")
public class UserFollowController extends ABaseController {

    @Resource
    private UserFollowService userFollowService;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private RedisComponent redisComponent;

    @PostMapping("/follow")
    @GlobalInterceptor
    public ResponseVO<Void> follow(@RequestParam String followId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        userFollowService.followUser(userId, followId);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/unfollow")
    @GlobalInterceptor
    public ResponseVO<Void> unfollow(@RequestParam String followId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        userFollowService.unfollowUser(userId, followId);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/followList")
    public ResponseVO<List<UserInfo>> followList(@RequestParam String userId) {
        List<UserFollow> follows = userFollowService.getFollowList(userId, 1, 1000);
        List<String> ids = follows.stream().map(UserFollow::getFollowingId).collect(Collectors.toList());
        List<UserInfo> users = userInfoService.listByIds(ids);
        users.forEach(u -> u.setPassword(null));
        return getSuccessResponseVO(users);
    }

    @PostMapping("/fansList")
    public ResponseVO<List<UserInfo>> fansList(@RequestParam String userId) {
        List<UserFollow> fans = userFollowService.getFanList(userId, 1, 1000);
        List<String> ids = fans.stream().map(UserFollow::getFollowerId).collect(Collectors.toList());
        List<UserInfo> users = userInfoService.listByIds(ids);
        users.forEach(u -> u.setPassword(null));
        return getSuccessResponseVO(users);
    }
}
