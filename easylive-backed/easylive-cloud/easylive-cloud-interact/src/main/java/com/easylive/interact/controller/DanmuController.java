package com.easylive.interact.controller;

import com.easylive.base.annotation.GlobalInterceptor;
import com.easylive.base.controller.ABaseController;
import com.easylive.base.entity.po.Danmu;
import com.easylive.base.entity.vo.ResponseVO;
import com.easylive.base.exception.BusinessException;
import com.easylive.base.service.DanmuService;
import org.apache.rocketmq.logging.inner.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/danmu")
public class DanmuController extends ABaseController {

    Logger logger = Logger.getLogger(DanmuController.class);

    @Resource
    private DanmuService danmuService;

    @Resource
    private HttpServletRequest request;

    /**
     * 发布弹幕（需要登录认证）
     * 接口路径：POST /danmu/postDanmu
     * 参数（form-data）：videoId, episodeId（可选）, text, mode, color, time
     */
    @PostMapping("/postDanmu")
    @GlobalInterceptor
    public ResponseVO<Void> postDanmu(@RequestParam("videoId") String videoId,
                                      @RequestParam(value = "episodeId", required = false) String episodeId,
                                      @RequestParam("text") String text,
                                      @RequestParam("type") Integer type,
                                      @RequestParam("color") String color,
                                      @RequestParam("time") Integer time,
                                      HttpServletRequest request) {
        // 获取登录用户ID（认证）
        String userId = getUserIdByToken(request);
        if (userId == null) {
            throw new BusinessException("请先登录");
        }

        // 参数校验
        if (!StringUtils.hasText(videoId)) {
            throw new BusinessException("视频ID不能为空");
        }
        if (!StringUtils.hasText(text)) {
            throw new BusinessException("弹幕内容不能为空");
        }
        if (time == null || time < 0) {
            throw new BusinessException("弹幕时间无效");
        }

        // 构建弹幕实体
        Danmu danmu = new Danmu();
        danmu.setVideoId(videoId);
        danmu.setEpisodeId(episodeId);   // 可选，分P ID
        danmu.setUserId(userId);
        danmu.setContent(text);
        danmu.setType(type);              // 弹幕类型：1滚动 2顶部 3底部
        danmu.setColor(color);
        danmu.setTime(time);              // 出现时间（秒）

        danmuService.saveDanmu(danmu);
        return getSuccessResponseVO(null);
    }

    /**
     * 获取弹幕列表（无需认证）
     * 接口路径：POST /danmu/loadDanmu
     * 参数（form-data）：videoId, episodeId（可选）
     */
    @PostMapping("/loadDanmu")
    public ResponseVO<List<Danmu>> loadDanmu(@RequestParam("videoId") String videoId,
                                             @RequestParam(value = "episodeId", required = false) String episodeId) {
        if (!StringUtils.hasText(videoId)) {
            throw new BusinessException("视频ID不能为空");
        }
        List<Danmu> list = danmuService.getDanmuList(videoId, episodeId);
        return getSuccessResponseVO(list);
    }

    /**
     * 获取弹幕列表
     */
    @PostMapping("/video/danmaku/list")
    public ResponseVO<List<Danmu>> getDanmakuList(@RequestParam String videoId) {
        if (videoId == null || videoId.trim().isEmpty()) {
            throw new BusinessException("视频ID不能为空");
        }
        try {
            List<Danmu> danmuList = danmuService.getDanmuList(videoId, null);
            return getSuccessResponseVO(danmuList);
        } catch (Exception e) {
            logger.error("获取弹幕列表异常，videoId={}");
            throw new BusinessException("系统繁忙，请稍后再试");
        }
    }

    /**
     * 添加弹幕
     */
    @PostMapping("/video/danmaku/add")
    public ResponseVO<Void> addDanmaku(@RequestParam String videoId,
                                       @RequestParam String content,
                                       @RequestParam(defaultValue = "0") Long playTime) {
        if (videoId == null || videoId.trim().isEmpty()) {
            throw new BusinessException("视频ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("弹幕内容不能为空");
        }

        try {
            Danmu danmu = new Danmu();
            danmu.setVideoId(videoId);
            danmu.setContent(content.trim());
            // 修正：time 字段为 Integer，playTime 为秒（Long → Integer）
            danmu.setTime(playTime.intValue());
            // 从 Token 获取当前登录用户ID
            danmu.setUserId(getCurrentUserId());
            danmu.setEpisodeId(null);

            danmuService.saveDanmu(danmu);
            return getSuccessResponseVO(danmu);
        } catch (Exception e) {
            logger.error("添加弹幕失败，videoId={}, content={}");
            throw new BusinessException("发送弹幕失败，请稍后再试");
        }
    }

    /**
     * 通过请求头中的 Token 获取当前用户ID
     * 直接复用已有的 getUserIdByToken 逻辑（或将其提取至工具类/基类）
     */
    private String getCurrentUserId() {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException("请先登录");
        }
        try {
            return redisComponent.getTokenUserInfoDto(token).getUserId();
        } catch (Exception e) {
            throw new BusinessException("token 无效或已过期");
        }
    }
}