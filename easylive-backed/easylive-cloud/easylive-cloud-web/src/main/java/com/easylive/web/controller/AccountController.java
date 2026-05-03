package com.easylive.web.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.constants.Constants;
import com.easylive.base.entity.dto.*;
import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.CheckCodeService;
import com.easylive.base.service.EmailService;
import com.easylive.base.service.QQLoginService;
import com.easylive.base.service.UserFollowService;
import com.easylive.base.service.UserInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.List;

/**
 * 用户信息 Controller
 */
@Controller
@RequestMapping("/userInfo")
public class AccountController extends ABaseController {

    @Resource
    private CheckCodeService checkCodeService;

    @Resource
    private EmailService emailService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserFollowService userFollowService;

    @Resource
    private QQLoginService qqLoginService;

    /**
     * 生成验证码（返回base64图片+验证码key）
     */
    @RequestMapping("/checkCode")
    @ResponseBody
    public ResponseVO checkCode() {
        Map<String, String> result = checkCodeService.generateCheckCode();
        return getSuccessResponseVO(result);
    }

    /**
     * 发送qq邮箱验证码
     */
    @PostMapping("/sendEmailCode")
    @ResponseBody
    public ResponseVO sendEmailCode(@Validated @RequestBody SendEmailCodeDTO dto) {
        userInfoService.sendEmailCode(dto);
        return getSuccessResponseVO("验证码发送成功，请注意查收");
    }
    /**
     * 用户注册接口
     */
    @PostMapping("/register")
    @ResponseBody
    public ResponseVO register(@Validated @RequestBody RegisterDTO dto) {
        userInfoService.register(dto);
        return getSuccessResponseVO("注册成功");
    }
    /**
     * 登录功能
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseVO<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, String> result = userInfoService.login(loginDTO);
        return getSuccessResponseVO(result);
    }

    /**
     * 重置密码接口 --忘记密码找回操作
     */
    @PostMapping("/resetPwd")
    @ResponseBody
    public ResponseVO resetPwd(@Validated @RequestBody ResetPwdDTO dto) {
        userInfoService.resetPwd(dto);
        return getSuccessResponseVO("密码重置成功，请使用新密码登录");
    }

    /**
     * 获取用户头像
     * @param userId 用户ID
     * @return 头像文件流或默认头像
     */
    @GetMapping("/getAvatar")
    @ResponseBody
    public void getAvatar(@RequestParam String userId,
                          HttpServletResponse response) {
        userInfoService.getAvatar(userId, response);
    }
    /**
     * 上传/更新用户头像
     * @param dto 包含用户ID和头像文件
     * @return 上传结果
     */
    @PostMapping("/uploadAvatar")
    @ResponseBody
    @GlobalInterceptor
    public ResponseVO uploadAvatar(@Validated AvatarUploadDTO dto) {
        String avatarPath = userInfoService.uploadAvatar(dto);
        String resultMsg = String.format("头像上传成功，存储路径：%s", avatarPath);
        return getSuccessResponseVO(resultMsg);
    }

    /**
     * 根据用户ID获取用户基本信息
     * @param userId 用户ID
     * @return 用户基本信息（不含密码等敏感字段）
     */
    @GetMapping("/getUserInfo/{userId}")
    @ResponseBody
    @GlobalInterceptor
    public ResponseVO getUserInfo(@PathVariable("userId") String userId) {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        if (userInfo != null) {
            userInfo.setPassword(null);
        }
        return getSuccessResponseVO(userInfo);
    }

    /**
     * 获取用户空间与基础信息
     * @param token 登录Token（从请求头获取）
     * @return 用户基础信息 + 空间使用情况
     */
    @GetMapping("/getUseSpace")
    @ResponseBody
    @GlobalInterceptor
    public ResponseVO getUseSpace(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token) {
        return getSuccessResponseVO(userInfoService.getUseSpace(token));
    }

    /**
     * 修改密码
     */
    @PostMapping("/changePwd")
    @ResponseBody
    @GlobalInterceptor
    public ResponseVO changePwd(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token,
                                @Validated @RequestBody ChangePwdDTO dto) {
        userInfoService.changePwd(token, dto);
        return getSuccessResponseVO("密码修改成功，请重新登录");
    }
    /**
     * 获取用户统计信息
     */
    @PostMapping("/getUserCountInfo")
    @ResponseBody
    public ResponseVO<Map<String, Long>> getUserCountInfo() {
        return getSuccessResponseVO(userInfoService.getUserCountInfo());
    }

    /**
     * 关注用户
     */
    @PostMapping("/follow/{userId}")
    @ResponseBody
    @GlobalInterceptor
    public ResponseVO followUser(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token, 
                               @PathVariable("userId") String followingId) {
        String followerId = userInfoService.getUserIdByToken(token);
        userFollowService.followUser(followerId, followingId);
        return getSuccessResponseVO("关注成功");
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/follow/{userId}")
    @ResponseBody
    @GlobalInterceptor
    public ResponseVO unfollowUser(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token, 
                                 @PathVariable("userId") String followingId) {
        String followerId = userInfoService.getUserIdByToken(token);
        userFollowService.unfollowUser(followerId, followingId);
        return getSuccessResponseVO("取消关注成功");
    }

    /**
     * 获取用户关注列表
     */
    @GetMapping("/follow/list")
    @ResponseBody
    public ResponseVO getFollowList(@RequestParam String userId, 
                                   @RequestParam(defaultValue = "1") Integer page, 
                                   @RequestParam(defaultValue = "10") Integer size) {
        return getSuccessResponseVO(userFollowService.getFollowList(userId, page, size));
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/follow/check")
    @ResponseBody
    @GlobalInterceptor
    public ResponseVO checkFollow(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token, 
                                @RequestParam String userId) {
        String followerId = userInfoService.getUserIdByToken(token);
        boolean isFollowing = userFollowService.isFollowing(followerId, userId);
        return getSuccessResponseVO(isFollowing);
    }

    /**
     * 获取用户关注和粉丝数量
     */
    @GetMapping("/follow/count")
    @ResponseBody
    public ResponseVO getFollowCount(@RequestParam String userId) {
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("followCount", userFollowService.getFollowCount(userId));
        result.put("fanCount", userFollowService.getFanCount(userId));
        return getSuccessResponseVO(result);
    }

    /**
     * QQ登录
     */
    @GetMapping("/qq/login")
    @ResponseBody
    public ResponseVO qqLogin(@RequestParam String code) {
        Map<String, Object> result = qqLoginService.qqLogin(code);
        return getSuccessResponseVO(result);
    }

    /**
     * 绑定QQ账号
     */
    @PostMapping("/qq/bind")
    @ResponseBody
    @GlobalInterceptor
    public ResponseVO bindQQ(@RequestHeader(Constants.REQUEST_HEADER_TOKEN) String token, 
                           @RequestParam String openid) {
        String userId = userInfoService.getUserIdByToken(token);
        qqLoginService.bindQQ(userId, openid);
        return getSuccessResponseVO("绑定QQ成功");
    }
}