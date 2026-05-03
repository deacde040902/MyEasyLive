package com.easylive.interact.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easylive.base.entity.dto.TokenUserInfoDto;
import com.easylive.base.entity.po.ChatMessage;
import com.easylive.base.redis.RedisComponent;
import com.easylive.base.service.ChatMessageService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
@Component
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    private static final ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private ChatMessageService chatMessageService;

    private String getUserIdByToken(String token) {
        if (!StringUtils.hasText(token)) return null;
        try {
            TokenUserInfoDto dto = redisComponent.getTokenUserInfoDto(token);
            return dto != null ? dto.getUserId() : null;
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String json = msg.text();
        JSONObject obj = JSON.parseObject(json);
        String type = obj.getString("type");

        switch (type) {
            case "login": {
                String token = obj.getString("token");
                String userId = getUserIdByToken(token);
                if (userId != null) {
                    userChannelMap.put(userId, ctx.channel());
                    ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(
                            Collections.singletonMap("type", "login_success")
                    )));
                } else {
                    ctx.close();
                }
                break;
            }
            case "chat": {
                // 强制转换类型
                String fromUserId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
                if (fromUserId == null) {
                    ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(
                            Collections.singletonMap("type", "error")
                    )));
                    return;
                }
                String toUserId = obj.getString("to");
                String content = obj.getString("content");
                Integer messageType = obj.getInteger("messageType");
                String imageUrl = obj.getString("imageUrl");

                ChatMessage message = chatMessageService.sendMessage(fromUserId, toUserId, content, messageType, imageUrl);

                Channel toChannel = userChannelMap.get(toUserId);
                if (toChannel != null && toChannel.isActive()) {
                    toChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
                }
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(
                        Collections.singletonMap("type", "sent_success")
                )));
                break;
            }
            case "read": {
                String userId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
                String targetId = obj.getString("targetId");
                if (userId != null && StringUtils.hasText(targetId)) {
                    chatMessageService.markAsRead(userId, targetId);
                }
                break;
            }
            default:
                ctx.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(
                        Collections.singletonMap("type", "unknown")
                )));
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String userId = (String) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId != null) {
            userChannelMap.remove(userId);
            log.info("用户 {} 断开WebSocket连接", userId);
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("WebSocket异常", cause);
        ctx.close();
    }
}