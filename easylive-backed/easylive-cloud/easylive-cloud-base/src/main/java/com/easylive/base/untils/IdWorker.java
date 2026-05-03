package com.easylive.base.untils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class IdWorker {
    private static final Snowflake snowflake = IdUtil.getSnowflake(1, 1);
    public static String getIdStr() {
        return String.valueOf(snowflake.nextId());
    }
}