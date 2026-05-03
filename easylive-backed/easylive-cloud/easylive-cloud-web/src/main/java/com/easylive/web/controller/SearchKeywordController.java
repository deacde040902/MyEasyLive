package com.easylive.web.controller;

import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.SearchKeyword;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.service.SearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * <p>
 * 搜索热词表 前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-22
 */
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/searchKeyword")
public class SearchKeywordController extends ABaseController {

    @Resource
    private SearchService searchService;

    /**
     * 获取热门搜索关键词（无需登录）
     */
    @PostMapping("/hotList")
    public ResponseVO<List<SearchKeyword>> getHotKeywords() {
        List<SearchKeyword> list = searchService.getHotKeywords();
        return getSuccessResponseVO(list);
    }
}