package com.easylive.interact.entity.vo;

/**
 * 通知计数响应DTO
 */
public class NotificationCountsVO {

    /**
     * 未读消息数
     */
    private Integer unreadMessageCount;

    /**
     * 系统通知数
     */
    private Integer systemNotificationCount;

    /**
     * 收到的赞数
     */
    private Integer likeCount;

    /**
     * 收到的收藏数
     */
    private Integer collectionCount;

    /**
     * 评论和@数
     */
    private Integer commentCount;

    public NotificationCountsVO() {
        this.unreadMessageCount = 0;
        this.systemNotificationCount = 0;
        this.likeCount = 0;
        this.collectionCount = 0;
        this.commentCount = 0;
    }

    public Integer getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(Integer unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public Integer getSystemNotificationCount() {
        return systemNotificationCount;
    }

    public void setSystemNotificationCount(Integer systemNotificationCount) {
        this.systemNotificationCount = systemNotificationCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCollectionCount() {
        return collectionCount;
    }

    public void setCollectionCount(Integer collectionCount) {
        this.collectionCount = collectionCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}