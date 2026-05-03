package com.easylive.base.service;

import com.easylive.base.entity.po.SearchKeyword;
import com.easylive.base.entity.po.Video;
import java.util.List;

public interface SearchService {
    List<Video> searchVideo(String keyword, Integer pageNo, Integer pageSize, String sortType);
    List<SearchKeyword> getHotKeywords();
    void recordSearchKeyword(String keyword);
}