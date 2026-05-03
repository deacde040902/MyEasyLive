package com.easylive.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easylive.base.entity.dto.*;
import com.easylive.base.entity.po.UserInfo;


import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 用户信息 业务接口
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 核心：根据邮箱查询用户（保留原有方法）
     */
    UserInfo getUserInfoByEmail(String email);

    /**
     * 核心：新增用户（保留原有方法，可被 MP 的 save 替代）
     */
    Integer add(UserInfo bean);

    /**
     * 发送邮箱验证码（保留原有方法）
     */
    void sendEmailCode(SendEmailCodeDTO dto);

    /**
     * 用户注册（保留原有方法）
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录（保留原有方法）
     */
    Map<String, String> login(LoginDTO dto);

    /**
     * 重置密码（保留原有方法）
     */
    void resetPwd(ResetPwdDTO dto);

    Integer updateUserInfoByUserId(UserInfo bean, String userId);

    /**
     * 获取用户头像
     * @param userId 用户ID
     * @param response HTTP响应
     */
    void getAvatar(String userId, HttpServletResponse response);

    /**
     * 上传/更新用户头像
     * @param dto 上传参数
     * @return 头像存储的相对路径
     */
    String uploadAvatar(AvatarUploadDTO dto);

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfo getUserInfoByUserId(String userId);


    void changePwd(String token, ChangePwdDTO dto);

    UserInfo getUserInfoByQQOpenId(String qqOpenId);

    void openVip(String userId, Integer months);

    void expireVip(String userId);

    Map<String, Long> getUserCountInfo();

    /**
     * 获取用户空间信息
     * @param token 登录Token
     * @return 包含基础信息和空间信息的Map
     */
    Map<String, Object> getUseSpace(String token);

    Map<String, String> adminLogin(String userName, String password);
    List<UserInfo> getUserListForAdmin(Integer pageNo, Integer pageSize, String nickName, Integer status);
    void changeUserStatus(String userId, Integer status);
    
    /**
     * 根据token获取用户ID
     * @param token 登录token
     * @return 用户ID
     */
    String getUserIdByToken(String token);

    /**
     * 根据关键词搜索用户（昵称或邮箱模糊匹配）
     */
    List<UserInfo> searchUser(String keyword);

    /**
     * 添加好友（建立好友关系）
     */
    void addFriend(String userId, String friendUserId);

    /**
     * 获取好友列表
     * @param userId 用户ID
     * @return 好友用户信息列表
     */
    List<UserInfo> getFriendList(String userId);

    /**
     * 更新用户大会员状态
     * @param userId 用户ID
     * @param months 开通月数
     */
    void updateVipStatus(String userId, Integer months);

    /**
     * 检查用户是否为大会员
     * @param userId 用户ID
     * @return 是否为大会员
     */
    boolean isBigVip(String userId);

    /**
     * 获取用户大会员等级
     * @param userId 用户ID
     * @return 大会员等级
     */
    Integer getBigVipLevel(String userId);

    /**
     * 获取用户VIP信息
     * @param userId 用户ID
     * @return 包含isVip、vipLevel、expireTime的Map
     */
    Map<String, Object> getUserVipInfo(String userId);
}