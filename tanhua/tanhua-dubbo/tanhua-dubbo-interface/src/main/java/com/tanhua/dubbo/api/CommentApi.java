package com.tanhua.dubbo.api;


import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.vo.PageResult;

import java.util.List;

public interface CommentApi {

    /**
     * 动态-点赞
     * @param comment
     * @return
     */
    long save(Comment comment);

    /**
     * 取消点赞
     * @param comment
     * @return
     */
    long delete(Comment comment);

    /**
     * 分页查询评论列表
     * @param movementId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult queryCommentsList(String movementId, Integer page, Integer pagesize);

    /**
     * 查询当前登陆用户相关的评论；
     * @param userId
     * @return
     */
    PageResult findCommentsByUserId(Long userId, Integer commentType, Integer page, Integer pagesize);

}
