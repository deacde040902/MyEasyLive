package com.easylive.admin.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.service.SysConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 系统配置表 前端控制器（管理员专属）
 *
 * @author licheng
 * @since 2026-04-19
 */
@RestController
@RequestMapping("/admin")
public class SysConfigController extends ABaseController {

    @Resource
    private SysConfigService sysConfigService;

    /**
     * 获取系统配置（管理员权限）
     */
    @PostMapping("/getSysConfig")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO getSysConfig() {
        return getSuccessResponseVO(sysConfigService.getSysConfigMap());
    }

    /**
     * 保存系统配置（管理员权限）
     */
    @PostMapping("/saveSysConfig")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveSysConfig(@RequestBody Map<String, String> configMap) {
        sysConfigService.saveSysConfig(configMap);
        return getSuccessResponseVO("系统配置保存成功");
    }
}