package com.easylive.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.entity.po.VideoCategory;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.mapper.VideoCategoryMapper;
import com.easylive.base.service.VideoCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VideoCategoryServiceImpl extends ServiceImpl<VideoCategoryMapper, VideoCategory> implements VideoCategoryService {

    @Override
    public List<VideoCategory> getCategoryList() {
        // 按排序顺序返回所有分类
        LambdaQueryWrapper<VideoCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(VideoCategory::getSortOrder);
        return this.list(wrapper);
    }

    @Override
    @Transactional
    public void saveCategory(VideoCategory category) {
        if (category == null) {
            throw new BusinessException("分类信息不能为空");
        }
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new BusinessException("分类名称不能为空");
        }
        if (category.getCategoryId() == null) {
            // 新增
            category.setCreateTime(LocalDateTime.now());
            this.save(category);
        } else {
            // 修改
            category.setUpdateTime(LocalDateTime.now());
            this.updateById(category);
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Integer categoryId) {
        if (categoryId == null) {
            throw new BusinessException("分类ID不能为空");
        }
        // 检查该分类下是否有视频（如果有，可以禁止删除或置空分类）
        // 这里简单删除，可扩展校验
        this.removeById(categoryId);
    }

    @Override
    @Transactional
    public void batchUpdateSort(List<VideoCategory> categories) {
        if (categories == null || categories.isEmpty()) {
            return;
        }
        for (VideoCategory c : categories) {
            if (c.getCategoryId() == null) {
                continue;
            }
            VideoCategory exist = this.getById(c.getCategoryId());
            if (exist != null) {
                exist.setSortOrder(c.getSortOrder());
                exist.setUpdateTime(LocalDateTime.now());
                this.updateById(exist);
            }
        }
    }
}