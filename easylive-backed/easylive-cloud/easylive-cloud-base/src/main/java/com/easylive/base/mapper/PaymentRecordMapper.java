package com.easylive.base.mapper;

import com.easylive.base.entity.po.PaymentRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
    List<PaymentRecord> selectByOrderId(@Param("orderId") String orderId);
    List<PaymentRecord> selectByUserId(@Param("userId") String userId);
}
