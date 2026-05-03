package com.easylive.interact.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.PlayHistory;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.PlayHistoryService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author licheng
 * @since 2026-04-21
 */
@RestController
@RequestMapping("/history")
public class HistoryController extends ABaseController {

    @Resource
    private PlayHistoryService playHistoryService;
    @Resource
    private RedisComponent redisComponent;

    @PostMapping("/loadHistory")
    @GlobalInterceptor
    public ResponseVO<List<PlayHistory>> loadHistory(@RequestParam(defaultValue = "1") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        return getSuccessResponseVO(playHistoryService.loadHistory(userId, pageNo, pageSize));
    }

    @PostMapping("/delHistory")
    @GlobalInterceptor
    public ResponseVO<Void> delHistory(@RequestParam Long historyId, HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        playHistoryService.delHistory(historyId, userId);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/clearHistory")
    @GlobalInterceptor
    public ResponseVO<Void> clearHistory(HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        playHistoryService.clearHistory(userId);
        return getSuccessResponseVO(null);
    }

    @PostMapping("/addHistory")
    @GlobalInterceptor
    public ResponseVO<Void> addHistory(@RequestParam String videoId,
                                       @RequestParam(required = false) String episodeId,
                                       @RequestParam(defaultValue = "0") Integer watchTime,
                                       HttpServletRequest request) {
        String userId = getUserIdByToken(request);
        if (userId == null) throw new BusinessException("请先登录");
        playHistoryService.addOrUpdateHistory(userId, videoId, episodeId, watchTime);
        return getSuccessResponseVO(null);
    }
}
