package com.easylive.base.service;

import com.easylive.base.entity.po.Danmu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 弹幕表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-20
 */
public interface DanmuService extends IService<Danmu> {
    void saveDanmu(Danmu danmu);
    List<Danmu> getDanmuList(String videoId, String episodeId);
    List<Danmu> getDanmuListForAdmin(String videoId, Integer pageNo, Integer pageSize);
    void deleteDanmuByAdmin(Long danmuId);
}
