package com.easylive.interact.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.Video;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.VideoService;
import com.easylive.interact.service.UserCollectionRedisService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/userCollection")
public class UserCollectionController extends ABaseController {

    @Resource
    private UserCollectionRedisService collectionRedisService;

    @Resource
    private VideoService videoService;

    @PostMapping("/collect/{videoId}")
    public ResponseVO<String> collectVideo(@PathVariable String videoId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        collectionRedisService.collect(videoId, userId);
        return getSuccessResponseVO("收藏成功");
    }

    @DeleteMapping("/collect/{videoId}")
    public ResponseVO<String> uncollectVideo(@PathVariable String videoId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        collectionRedisService.uncollect(videoId, userId);
        return getSuccessResponseVO("取消收藏成功");
    }

    @PostMapping("/list")
    @GlobalInterceptor
    public ResponseVO<List<Map<String, Object>>> getCollectionList(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                  HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        List<String> videoIds = collectionRedisService.getUserCollectionIds(userId, pageNo, pageSize);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (String videoId : videoIds) {
            Video video = videoService.getById(videoId);
            Map<String, Object> item = new HashMap<>();
            item.put("videoId", videoId);
            if (video != null) {
                item.put("title", video.getTitle());
                item.put("cover", video.getCoverFileId());
                item.put("authorId", video.getUserId());
                item.put("playCount", video.getPlayCount());
                item.put("duration", video.getDuration());
                item.put("createTime", video.getCreateTime());
            }
            result.add(item);
        }
        return getSuccessResponseVO(result);
    }
}
