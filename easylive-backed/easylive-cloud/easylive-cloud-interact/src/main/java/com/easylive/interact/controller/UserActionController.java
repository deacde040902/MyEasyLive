package com.easylive.interact.controller;

import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.interact.service.UserActionRedisService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/userAction")
public class UserActionController extends ABaseController {

    @Resource
    private UserActionRedisService userActionRedisService;

    @PostMapping("/like/{videoId}")
    public ResponseVO<String> likeVideo(@PathVariable String videoId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        userActionRedisService.like(videoId, userId);
        return getSuccessResponseVO("点赞成功");
    }

    @DeleteMapping("/like/{videoId}")
    public ResponseVO<String> unlikeVideo(@PathVariable String videoId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        userActionRedisService.unlike(videoId, userId);
        return getSuccessResponseVO("取消点赞成功");
    }
}