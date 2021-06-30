package com.tanhua.server.service;

import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.Comment;
import com.tanhua.domain.mongo.Friend;
import com.tanhua.domain.vo.ContractVo;
import com.tanhua.domain.vo.MessageLikeVo;
import com.tanhua.domain.vo.PageResult;
import com.tanhua.domain.vo.UserInfoVo;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class IMService {

    @Reference
    private UserInfoApi userInfoApi;

    @Reference
    private FriendApi friendApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Reference
    private CommentApi commentApi;

    /**
     * 接口名称：根据环信id查询用户信息
     */
    public ResponseEntity<Object> getHuanxinUserInfo(Long huanxinId) {
        UserInfo userInfo = userInfoApi.findById(huanxinId);
        UserInfoVo vo = new UserInfoVo();

        if (userInfo != null) {
            BeanUtils.copyProperties(userInfo, vo);
            if (userInfo.getAge() != null) {
                vo.setAge(userInfo.getAge().toString());
            }
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：联系人添加
     */
    public ResponseEntity<Object> addContact(long friendId) {
        // 1.保存好友关系到Mongo
        friendApi.save(UserHolder.getUserId(), friendId);

        // 2.调用环信api，保存好友关系到环信
        huanXinTemplate.contactUsers(UserHolder.getUserId(), friendId);

        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：联系人列表
     */
    public ResponseEntity<Object> queryContractList(Integer page, Integer pagesize, String keyword) {
        // 1.调用服务提供者api分页查询联系人列表
        PageResult pageResult = friendApi.queryContractList(page, pagesize,
                keyword, UserHolder.getUserId());

        // 获取当前页的数据
        List<Friend> friendList = (List<Friend>) pageResult.getItems();

        // 2.封装返回结果
        List<ContractVo> voList = new ArrayList<>();
        if (friendList != null) {
            for (Friend friend : friendList) {
                // 2.1 创建vo
                ContractVo vo = new ContractVo();
                // 2.2 封装数据
                UserInfo userInfo = userInfoApi.findById(friend.getFriendId());
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                }

                vo.setUserId(friend.getFriendId().toString());
                // 城市只需要省份，例如：湖南省-衡阳市-珠晖区
                vo.setCity(StringUtils.substringBefore(userInfo.getCity(), "-"));

                // 2.3 添加vo到集合
                voList.add(vo);
            }
        }

        pageResult.setItems(voList);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 点赞评论喜欢列表
     *
     * @param commentType 评论类型，1-点赞，2-评论，3-喜欢
     * @param page        当前页
     * @param pagesize    页大小
     * @return 通过MessageLikeVo封装返回结果并返回
     */
    public ResponseEntity<Object> querylikesList(
            Integer commentType, Integer page, Integer pagesize) {

        // 1.查询当前登陆用户相关的评论；【当前用户不是评论人，是被评论人；被评论人其实就是发布动态的人】
        PageResult pageResult =
                commentApi.findCommentsByUserId(UserHolder.getUserId(), commentType, page, pagesize);

        // 2.获取评论数据
        List<Comment> commentList = (List<Comment>) pageResult.getItems();

        // 3.构造返回的结果数据
        List<MessageLikeVo> result = new ArrayList<>();

        // 4.遍历查询的评论数据，封装返回结果
        if (commentList != null && commentList.size() > 0) {
            for (Comment comment : commentList) {
                MessageLikeVo messageLikeVo = new MessageLikeVo();
                // 根据评论人用户id查询
                UserInfo userInfo = userInfoApi.findById(comment.getUserId());
                BeanUtils.copyProperties(userInfo, messageLikeVo);
                messageLikeVo.setId(comment.getId().toString());
                String createDate = new SimpleDateFormat("yyyy-MM-dd").format(comment.getCreated());
                messageLikeVo.setCreateDate(createDate);
                result.add(messageLikeVo);
            }
        }
        // 5.设置集合到分页对象中
        pageResult.setItems(result);
        return ResponseEntity.ok(pageResult);
    }
}
