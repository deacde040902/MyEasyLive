package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.UserTheme;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.UserThemeService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
/**
 * <p>
 * 用户主题设置表 前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-22
 */
@RestController
@RequestMapping("/userTheme")
public class UserThemeController extends ABaseController {

    @Resource
    private UserThemeService userThemeService;
    @Resource
    private RedisComponent redisComponent;

    /**
     * 保存主题设置（需登录）
     * 参数：themeColor（颜色）, darkMode（0/1）
     */
    @PostMapping("/save")
    @GlobalInterceptor
    public ResponseVO<Void> saveTheme(@RequestParam(required = false) String themeColor,
                                      @RequestParam(required = false) Integer darkMode,
                                      HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        userThemeService.saveTheme(userId, themeColor, darkMode);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取当前用户的主题设置（需登录）
     */
    @PostMapping("/get")
    @GlobalInterceptor
    public ResponseVO<UserTheme> getTheme(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        UserTheme theme = userThemeService.getTheme(userId);
        return getSuccessResponseVO(theme);
    }
}