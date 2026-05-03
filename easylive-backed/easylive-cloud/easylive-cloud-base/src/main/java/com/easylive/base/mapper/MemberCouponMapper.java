package com.easylive.base.mapper;

import com.easylive.base.entity.po.MemberCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MemberCouponMapper extends BaseMapper<MemberCoupon> {
    List<MemberCoupon> selectAvailableCoupons(@Param("vipLevel") Integer vipLevel);
    List<MemberCoupon> selectByStatus(@Param("status") Integer status);
}
