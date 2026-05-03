package com.easylive.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {

        // 数据库配置
        String url = "jdbc:mysql://127.0.0.1:3306/easylive?serverTimezone=GMT%2B8&useSSL=false";
        String username = "root";
        String password = "123456";

        // 项目里 easylive-cloud-base 的真实路径
        String basePath = "E:/Workspace-java/EasyLive/easylive-backed/easylive-cloud/easylive-cloud-base";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("licheng")
                            .outputDir(basePath + "/src/main/java");
                })

                .packageConfig(builder -> {
                    builder.parent("com.easylive.base")
                            .entity("entity.po")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    basePath + "/src/main/resources/mappers"));
                })

                .strategyConfig(builder -> {
                    // 生成所有新创建的表
                    builder.addInclude(
                                    "video_comment","user_action"      // 填入表名
                            )
                            .entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .idType(IdType.ASSIGN_ID)      // 默认雪花ID，自增表需手动改
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")
                            .mapperBuilder()
                            .enableBaseResultMap()
                            .enableBaseColumnList();
                })

                .templateEngine(new VelocityTemplateEngine())
                .execute();

        System.out.println("✅ 生成成功！路径：" + basePath);
    }
}