package com.easylive.base.service;

import com.easylive.base.entity.po.PlayHistory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
public interface PlayHistoryService extends IService<PlayHistory> {
    List<PlayHistory> loadHistory(String userId, Integer pageNo, Integer pageSize);
    void addOrUpdateHistory(String userId, String videoId, String episodeId, Integer watchTime);
    void delHistory(Long historyId, String userId);
    void clearHistory(String userId);
}
