package com.easylive.base.mapper;

import com.easylive.base.entity.po.VipOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VipOrderMapper extends BaseMapper<VipOrder> {
    List<VipOrder> selectByUserId(@Param("userId") String userId);
    List<VipOrder> selectByStatus(@Param("status") Integer status);
}
