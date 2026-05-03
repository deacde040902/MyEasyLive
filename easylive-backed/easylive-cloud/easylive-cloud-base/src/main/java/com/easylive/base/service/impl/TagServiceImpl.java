package com.easylive.base.service.impl;

import com.easylive.base.entity.po.Tag;
import com.easylive.base.mapper.TagMapper;
import com.easylive.base.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-19
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}
