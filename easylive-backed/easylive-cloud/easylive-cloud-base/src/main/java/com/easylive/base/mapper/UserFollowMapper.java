package com.easylive.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easylive.base.entity.po.UserFollow;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户关注关系 Mapper
 */
public interface UserFollowMapper extends BaseMapper<UserFollow> {
    
    /**
     * 查询用户关注列表
     * @param followerId 关注者ID
     * @param page 页码
     * @param size 每页大小
     * @return 关注列表
     */
    List<UserFollow> selectFollowList(@Param("followerId") String followerId, 
                                     @Param("page") Integer page, 
                                     @Param("size") Integer size);
    
    /**
     * 查询用户粉丝列表
     * @param followingId 被关注者ID
     * @param page 页码
     * @param size 每页大小
     * @return 粉丝列表
     */
    List<UserFollow> selectFanList(@Param("followingId") String followingId, 
                                   @Param("page") Integer page, 
                                   @Param("size") Integer size);
    
    /**
     * 检查是否已关注
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @return 关注关系
     */
    UserFollow selectByFollowerAndFollowing(@Param("followerId") String followerId, 
                                           @Param("followingId") String followingId);
}