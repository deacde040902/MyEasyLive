package com.easylive.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GlobalInterceptor {
    //登录校验
    boolean checkLogin() default true;

    //管理员权限校验
    boolean checkAdmin() default false;
}
