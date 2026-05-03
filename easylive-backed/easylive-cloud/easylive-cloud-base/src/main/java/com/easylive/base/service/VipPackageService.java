package com.easylive.base.service;

import com.easylive.base.entity.po.VipPackage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-23
 */
public interface VipPackageService extends IService<VipPackage> {
    List<VipPackage> getActivePackages();  // 获取可用套餐
    VipPackage getPackageById(String id);   // 获取套餐详情
}
