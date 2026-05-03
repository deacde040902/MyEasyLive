package com.easylive.base.service;

import com.easylive.base.entity.po.UserAction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户行为记录表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-20
 */
public interface UserActionService extends IService<UserAction> {
    void doAction(UserAction userAction);
    void like(String videoId, String userId);
    void unlike(String videoId, String userId);
    boolean isLiked(String videoId, String userId);
}
