package com.easylive.base.service.impl;

import com.easylive.base.entity.po.UserInfo;
import com.easylive.base.mapper.UserInfoMapper;
import com.easylive.base.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-17
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
