package com.tanhua.server.service;


import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.vo.CommentVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Reference
    private CommentApi commentApi;

    @Reference
    private UserInfoApi userInfoApi;

    /**
     * 接口名称：评论列表
     */
    public ResponseEntity<Object> queryCommentsList(String movementId, Integer page, Integer pagesize) {

        // 1.查询服务提供者api，查询评论列表
        PageResult pageResult = commentApi.queryCommentsList(movementId, page, pagesize);

        // 获取当前页的内容
        List<Comment> commentList = (List<Comment>) pageResult.getItems();

        // 2.封装返回结果
        List<CommentVo> voList = new ArrayList<>();

        if (commentList != null) {
            for (Comment comment : commentList) {
                // 2.1 创建vo对象
                CommentVo vo = new CommentVo();

                // 2.2 封装数据
                UserInfo userInfo = userInfoApi.findById(comment.getUserId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                }

                vo.setId(comment.getId().toString());
                vo.setContent(comment.getContent());
                // 日期格式转换
                vo.setCreateDate(new DateTime(comment.getCreated()).toString("yyyy年MM月dd日 HH:mm"));
                vo.setLikeCount(comment.getLikeCount());
                vo.setHasLiked(0);

                // 2.3 添加vo到集合
                voList.add(vo);
            }
        }
        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);

    }

    /**
     * 接口名称：评论-提交
     */
    public ResponseEntity<Object> saveComments(String movementId, String content) {
        // 1.创建评论对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(2);
        comment.setPubType(1);
        comment.setContent(content);
        comment.setUserId(UserHolder.getUserId());
        comment.setCreated(System.currentTimeMillis());

        // 2.调用服务提供者api操作mongo, 往评论表插入数据、修改动态表的评论数
        commentApi.save(comment);

        return ResponseEntity.ok(null);
    }
}
