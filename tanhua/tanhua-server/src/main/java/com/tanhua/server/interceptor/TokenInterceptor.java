package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;
import com.tanhua.domain.vo.ErrorResult;
import com.tanhua.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一身份认证token处理
 */
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    // 进入控制器之前处理
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("前置处理，统一身份认证----->");
        
        // 1.获取token
        String token = request.getHeader("Authorization");

        // 2.判断token是否为空，如果为空，代表请求没有传递token
        if(StringUtils.isEmpty(token)){
            // 未认证
            response.setStatus(401);
            return false;
        }

        // 3.从redis中取出用户信息，判断是否存在
        User user = userService.findUserByToken(token);
        if (user == null) {
            // 未认证
            response.setStatus(401);
            return false;
        }

        // 4.将当前登录用户信息设置到当前线程里
        UserHolder.set(user);

        // false代表拦截请求，true代表放行
        return true;
    }
}
