package com.easylive.base.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.easylive.base.entity.vo.ResponseVO;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Sentinel 限流异常处理
 */
@RestControllerAdvice
public class SentinelExceptionHandler {

    @ExceptionHandler(BlockException.class)
    public ResponseVO<String> handleBlockException(BlockException e) {
        String msg = "系统繁忙，请稍后重试";
        if (e.getRule() != null) {
            msg = "请求过于频繁，请稍后再试";
        }
        throw new BusinessException(msg);
    }
}
