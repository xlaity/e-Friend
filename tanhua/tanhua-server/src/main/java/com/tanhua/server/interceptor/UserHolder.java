package com.tanhua.server.interceptor;

import com.tanhua.domain.db.User;

/**
 * 通过ThreadLocal对象，存储用户的数据
 * 1、声明ThreadLocal对象
 * 2、向当前线程存储数据：threadLocal.set();
 * 3、从当前线程获取数据：threadLocal.get();
 */
public class UserHolder {

    private static ThreadLocal<User> threadLocal = new ThreadLocal<>();

    // 将用户信息设置到当前线程中
    public static void set(User user){
        threadLocal.set(user);
    }

    // 从当前线程中获取用户信息
    public static User get(){
        return threadLocal.get();
    }
    // 获取当前登录用户id
    public static Long getUserId(){
        return threadLocal.get().getId();
    }

}
