package com.tanhua.server.service;
import com.tanhua.domain.vo.*;
import org.bson.types.ObjectId;

import com.alibaba.fastjson.JSON;
import com.tanhua.commons.templates.AipFaceTemplate;
import com.tanhua.commons.templates.HuanXinTemplate;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.commons.templates.SmsTemplate;
import com.tanhua.domain.db.User;
import com.tanhua.domain.db.UserInfo;
import com.tanhua.domain.mongo.UserLike;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.UserLikeApi;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.utils.JwtUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * 业务层
 */
@Service
public class UserService {

    @Reference
    private UserApi userApi;

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String SMS_KEY = "SMS_KEY_"; // 验证码key前缀

    private final String TOKEN_KEY = "TOKEN_KEY_"; // token key前缀

    @Value("${tanhua.secret}")
    private String secret;

    @Reference
    private UserInfoApi userInfoApi;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Reference
    private UserLikeApi userLikeApi;

    @Reference
    private FriendApi friendApi;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    /**
     * 根据手机号码查询用户
     */
    public ResponseEntity<Object> findByMobile(String mobile) {
        User user = userApi.findByMobile(mobile);
        return ResponseEntity.ok(user);
    }

    /**
     * 保存用户
     */
    public ResponseEntity<Object> save(User user) {
        try {

            int i = 1 / 0;

            Long userId = userApi.save(user);
            // 没报错的情况
            return ResponseEntity.ok(userId);

        } catch (Exception e) {

            Map<String, Object> map = new HashMap<>();
            map.put("errCode", "10000");
            map.put("errMsg", "对不起，我错了！");

            return ResponseEntity.status(500).body(map);
        }
    }

    /**
     * 接口名称：登录第一步---手机号登录
     */
    public ResponseEntity<Object> login(String phone) {
        // 1.生成6位随机数作为验证码
        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        code = "123456";

        // 2.调用阿里云SMS发送短信
        // smsTemplate.sendSms(phone, code);

        // 3.将验证码存入redis，并设置过期时间
        redisTemplate.opsForValue().set(SMS_KEY + phone, code, Duration.ofMinutes(5));

        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：登录第二步---验证码校验
     */
    public ResponseEntity<Object> loginVerification(String phone, String verificationCode) {
        // 1.从redis中获取验证码，进行校验
        String code = redisTemplate.opsForValue().get(SMS_KEY + phone);
        if (code == null || !code.equals(verificationCode)) {
            return ResponseEntity.status(401).body(ErrorResult.loginError());
        }

        // 2.删除验证码
        redisTemplate.delete(SMS_KEY + phone);

        // 3.判断用户是否是新用户
        User user = userApi.findByMobile(phone);
        boolean isNew = false;
        // 【操作类型：登录】
        String type = "0101";
        if (user == null) {
            // 新用户注册
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));

            // 【如果是新用户返回主键，设置到user对象里】
            Long userId = userApi.save(user);
            user.setId(userId);

            isNew = true;

            // 注册用户到环信
            huanXinTemplate.register(userId);
            // 【操作类型：注册】
            type = "0102";
        }

        // 【定义消息内容】
        Map<String, Object> msg = new HashMap<>();
        msg.put("userId", user.getId());
        msg.put("type", type);
        msg.put("logTime", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        // 【发送消息到RocketMQ，参数1-主题，参数2-消息内容】
        rocketMQTemplate.convertAndSend("tanhua-log", JSON.toJSONString(msg));

        // 【生成token】
        String token = JwtUtils.createToken(user.getId(), user.getMobile(), secret);
        // 【将token和用户信息保存到redis中】
        redisTemplate.opsForValue().set(TOKEN_KEY + token, JSON.toJSONString(user), Duration.ofHours(4));

        // 4.封装返回结果
        Map<String, Object> result = new HashMap<>();
        // 【封装token到返回结果】
        result.put("token", token);
        result.put("isNew", isNew);
        return ResponseEntity.ok(result);
    }

    /**
     * 接口名称：新用户---1填写资料
     */
    public ResponseEntity<Object> loginReginfo(UserInfo userInfo) {
        User user = UserHolder.get();

        // 2.保存userInfo
        userInfo.setId(user.getId());

        // 3.调用服务提供者api保存用户详情
        userInfoApi.save(userInfo);

        return ResponseEntity.ok(null);
    }

    /**
     * 根据token查询用户
     *
     * @return
     */
    public User findUserByToken(String token) {
        // 1.从redis中取出用户信息
        String key = TOKEN_KEY + token;
        String userJsonData = redisTemplate.opsForValue().get(key);

        // 2.判断用户信息是否存在
        if (StringUtils.isEmpty(userJsonData)) {
            return null;
        }
        // 3.将userjson字符串转为user对象
        User user = JSON.parseObject(userJsonData, User.class);

        // 4.将token的过期时间重置
        redisTemplate.opsForValue().set(TOKEN_KEY + token, JSON.toJSONString(user), Duration.ofHours(4));
        return user;
    }

    /**
     * 接口名称：新用户---2选取头像
     */
    public ResponseEntity<Object> updateUserHead(MultipartFile headPhoto) throws Exception {
        // 1.从当前线程里获取用户信息
        User user = UserHolder.get();

        // 2.人脸检测
        boolean detect = aipFaceTemplate.detect(headPhoto.getBytes());
        if (!detect) {
            // 非人脸
            return ResponseEntity.status(500).body(ErrorResult.faceError());
        }

        // 3.上传头像到oss
        String url = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());

        // 4.将头像的url设置到用户详情中
        UserInfo userInfo = new UserInfo();
        userInfo.setId(user.getId());
        userInfo.setAvatar(url);

        // 5.调用服务提供者更新用户详情
        userInfoApi.update(userInfo);

        return ResponseEntity.ok(null);
    }

    /**
     * 接口名称：用户资料 - 读取
     */
    public ResponseEntity<Object> findUserInfoById() {
        User user = UserHolder.get();

        // 2.调用服务提供者查询用户详情
        UserInfo userInfo = userInfoApi.findById(user.getId());

        // 3.封装返回结果
        UserInfoVo vo = new UserInfoVo();
        // 3.1 对象拷贝，参数1-源对象，参数2-目标对象
        BeanUtils.copyProperties(userInfo, vo);

        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return ResponseEntity.ok(vo);
    }

    /**
     * 接口名称：用户资料 - 保存
     */
    public ResponseEntity<Object> updateUserInfo(UserInfo userInfo) {
        User user = UserHolder.get();

        // 2.设置userInfo的id
        userInfo.setId(user.getId());

        // 3.调用api更新用户详情
        userInfoApi.update(userInfo);

        return ResponseEntity.ok(null);

    }

    /**
     * 修改手机号 - 2 校验验证码
     */
    public ResponseEntity<Object> checkVerificationCode(String code) {
        // 1.获取用户信息
        User user = UserHolder.get();

        // 2.从redis中获取验证码
        String key = SMS_KEY + user.getMobile();
        String redisCode = redisTemplate.opsForValue().get(key);

        // 3.判断
        Boolean verification = true;
        if (code == null || redisCode == null || !redisCode.equals(code)) {
            // 3.1 校验失败
            verification = false;
        } else {
            // 3.2 校验成功，从redis中删除验证码
            redisTemplate.delete(key);
        }

        // 4.构造返回结果:{"verification":false/true}
        Map<String, Boolean> resultMap = new HashMap<>();
        resultMap.put("verification", verification);
        return ResponseEntity.ok(resultMap);
    }

    /**
     * 修改手机号 - 3 保存
     *
     * @param phone 修改后的手机号
     * @return
     */
    public ResponseEntity<Object> updateUserPhone(String phone) {
        // 1.根据修改后的手机号码查询，如果手机号码已经存在返回错误信息
        User user = userApi.findByMobile(phone);
        if (user != null) {
            return ResponseEntity.status(500).body(ErrorResult.mobileError());
        }

        // 2.获取用户信息、设置修改手机号
        User updateUser = UserHolder.get();
        updateUser.setMobile(phone);

        // 3.修改用户
        userApi.update(updateUser);
        return ResponseEntity.ok(null);
    }


    /**
     * 接口名称：互相喜欢，喜欢，粉丝 - 统计
     */
    public ResponseEntity<Object> queryCounts() {
        // 1.获取登录用户的id
        Long userId = UserHolder.getUserId();

        // 2.查询互相喜欢数、喜欢数、粉丝数
        // 2.1 统计互相喜欢
        Long eachLoveCount = userLikeApi.queryEachLoveCount(userId);
        // 2.2 统计喜欢
        Long loveCount = userLikeApi.queryLoveCount(userId);
        // 2.3 统计粉丝
        Long fanCount = userLikeApi.queryFanCount(userId);

        // 3.封装返回结果
        Map<String, Integer> result = new HashMap<>();
        result.put("eachLoveCount", eachLoveCount.intValue());
        result.put("loveCount", loveCount.intValue());
        result.put("fanCount", fanCount.intValue());

        return ResponseEntity.ok(result);
    }

    /**
     * 接口名称：互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表
     */
    public ResponseEntity<Object> queryUserLikeList(Integer type, Integer page, Integer pagesize) {
        // 1.获取登陆用户id
        Long userId = UserHolder.getUserId();

        PageResult pageResult = null;

        // 2.根据类型判断
        switch (type) {
            case 1:
                pageResult = userLikeApi.queryEachLoveList(userId, page, pagesize);
                break;
            case 2:
                pageResult = userLikeApi.queryLoveList(userId, page, pagesize);
                break;
            case 3:
                pageResult = userLikeApi.queryFanList(userId, page, pagesize);
                break;
            case 4:
                pageResult = userLikeApi.queryVisitorList(userId, page, pagesize);
                break;
        }

        // 获取当前页数据
        List<Map<String, Object>> items = (List<Map<String, Object>>) pageResult.getItems();

        // 3.封装返回结果
        List<UserLikeVo> voList = new ArrayList<>();
        if (items != null) {
            for (Map<String, Object> item : items) {
                // 3.1 创建vo
                UserLikeVo vo = new UserLikeVo();

                // 3.2 封装vo
                Long id = (Long) item.get("userId"); // 用户id
                Long score = (Long) item.get("score");  // 缘分值

                // 查询用户详情
                UserInfo userInfo = userInfoApi.findById(id);
                if (userInfo != null) {
                    BeanUtils.copyProperties(userInfo, vo);
                }
                // 设置缘分值
                vo.setMatchRate(score.intValue());

                // 3.3 添加vo
                voList.add(vo);
            }
        }

        // 4.重新设置items
        pageResult.setItems(voList);

        return ResponseEntity.ok(pageResult);
    }
    /**
     * 接口名称：粉丝 - 喜欢
     * uid-粉丝id
     */
    public ResponseEntity<Object> fansLike(Long uid) {
        // 1.删除粉丝中的喜欢数据
        userLikeApi.delete(UserHolder.getUserId(), uid);

        // 2.记录双向的好友关系
        friendApi.save(UserHolder.getUserId(), uid);

        // 3.注册好友关系到环信 （喜欢的用户、登陆的用户要先在环信注册）
        huanXinTemplate.contactUsers(UserHolder.getUserId(), uid);

        return ResponseEntity.ok(null);
    }

    /**
     * 江杰
     * @Params:
     * @Return
     */
    //桃花传音喜欢不喜欢
    public ResponseEntity<Object> love(Long id) {
        Long userId = UserHolder.getUserId();
        //首先查询shi好友表是否有
       UserLike fid =  userLikeApi.findUser(id,userId);
       //说明他喜欢的好人中没有我 我 就要添加喜欢到我的列表中
       if(fid==null){
           UserLike userLike = new UserLike();
           userLike.setId(new ObjectId());
           userLike.setUserId(userId);
           userLike.setLikeUserId(id);
           userLike.setCreated(System.currentTimeMillis());
           userLikeApi.save(userLike);
       }else {
           UserLike userLike = new UserLike();
           userLike.setId(new ObjectId());
           userLike.setUserId(userId);
           userLike.setLikeUserId(id);
           userLike.setCreated(System.currentTimeMillis());
           userLikeApi.save(userLike);
           //并且添加朋友关系
           friendApi.save(id,userId);
           friendApi.save(userId,id);
       }
       return ResponseEntity.ok(null);
    }

    /**
     * 江杰
     * @Params:
     * @Return
     */
    //删除桃花传音语言
    public ResponseEntity<Object> unlove(Long id) {
        //直接删除缓存的数据
        String str  =  "user_info_"+id;
        redisTemplate.delete(str);
        return ResponseEntity.ok(null);
    }
}
