package com.easylive.base.service.impl;

import com.easylive.base.service.VipPackageService;
import com.easylive.base.entity.po.VipPackage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easylive.base.mapper.VipPackageMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author licheng
 * @since 2026-04-23
 */
@Service
public class VipPackageServiceImpl extends ServiceImpl<VipPackageMapper, VipPackage> implements VipPackageService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_KEY = "vip:packages";

    @Override
    public List<VipPackage> getActivePackages() {
        List<VipPackage> packages = (List<VipPackage>) redisTemplate.opsForValue().get(CACHE_KEY);
        if (packages != null && !packages.isEmpty()) {
            return packages;
        }

        QueryWrapper<VipPackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        packages = this.list(queryWrapper);

        if (packages == null || packages.isEmpty()) {
            packages = getDefaultPackages();
        }

        redisTemplate.opsForValue().set(CACHE_KEY, packages, 10, TimeUnit.MINUTES);
        return packages;
    }

    @Override
    public VipPackage getPackageById(String id) {
        VipPackage vipPackage = this.getById(id);
        if (vipPackage != null) {
            return vipPackage;
        }

        List<VipPackage> packages = getActivePackages();
        for (VipPackage pkg : packages) {
            if (pkg.getId().equals(id)) {
                return pkg;
            }
        }

        return getDefaultPackageById(id);
    }

    private List<VipPackage> getDefaultPackages() {
        List<VipPackage> packages = new ArrayList<>();
        
        VipPackage pkg1 = new VipPackage();
        pkg1.setId("1");
        pkg1.setName("月度大会员");
        pkg1.setMonths(1);
        pkg1.setPrice(new BigDecimal("25"));
        pkg1.setDiscountPrice(new BigDecimal("25"));
        pkg1.setPoints(250);
        pkg1.setStatus(1);
        packages.add(pkg1);

        VipPackage pkg2 = new VipPackage();
        pkg2.setId("2");
        pkg2.setName("季度大会员");
        pkg2.setMonths(3);
        pkg2.setPrice(new BigDecimal("68"));
        pkg2.setDiscountPrice(new BigDecimal("68"));
        pkg2.setPoints(680);
        pkg2.setStatus(1);
        packages.add(pkg2);

        VipPackage pkg3 = new VipPackage();
        pkg3.setId("3");
        pkg3.setName("年度大会员");
        pkg3.setMonths(12);
        pkg3.setPrice(new BigDecimal("198"));
        pkg3.setDiscountPrice(new BigDecimal("198"));
        pkg3.setPoints(1980);
        pkg3.setStatus(1);
        packages.add(pkg3);

        return packages;
    }

    private VipPackage getDefaultPackageById(String id) {
        switch (id) {
            case "1":
                VipPackage pkg1 = new VipPackage();
                pkg1.setId("1");
                pkg1.setName("月度大会员");
                pkg1.setMonths(1);
                pkg1.setPrice(new BigDecimal("25"));
                pkg1.setDiscountPrice(new BigDecimal("25"));
                pkg1.setPoints(250);
                pkg1.setStatus(1);
                return pkg1;
            case "2":
                VipPackage pkg2 = new VipPackage();
                pkg2.setId("2");
                pkg2.setName("季度大会员");
                pkg2.setMonths(3);
                pkg2.setPrice(new BigDecimal("68"));
                pkg2.setDiscountPrice(new BigDecimal("68"));
                pkg2.setPoints(680);
                pkg2.setStatus(1);
                return pkg2;
            case "3":
                VipPackage pkg3 = new VipPackage();
                pkg3.setId("3");
                pkg3.setName("年度大会员");
                pkg3.setMonths(12);
                pkg3.setPrice(new BigDecimal("198"));
                pkg3.setDiscountPrice(new BigDecimal("198"));
                pkg3.setPoints(1980);
                pkg3.setStatus(1);
                return pkg3;
            default:
                return null;
        }
    }
}
