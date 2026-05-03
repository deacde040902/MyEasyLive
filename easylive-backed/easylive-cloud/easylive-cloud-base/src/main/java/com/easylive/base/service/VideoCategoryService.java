package com.easylive.base.service;

import com.easylive.base.entity.po.VideoCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 视频分类表 服务类
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
public interface VideoCategoryService extends IService<VideoCategory> {

    void batchUpdateSort(List<VideoCategory> categories);

    void deleteCategory(Integer categoryId);

    void saveCategory(VideoCategory category);

    List<VideoCategory> getCategoryList();
}
