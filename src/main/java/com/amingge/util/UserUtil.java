package com.amingge.util;


import com.amingge.pojo.User;

import javax.servlet.http.HttpSession;

//用户工具类
public class UserUtil {
    public static final String USER = "LOGIN_USER";

    //设置用户到session
    public static void saveUserToSession(HttpSession session, User user){
        session.setAttribute(USER,user);
    }
    //从Session获取当前用户信息
    public static User getUserFromSession(HttpSession session){
        Object attribute = session.getAttribute(USER);
        return attribute == null ? null : (User)attribute;
    }
    //从Session中删除登录用户的个人信息
    public static void deleteUserFromSession(HttpSession session){
        session.removeAttribute(USER);
    }
}
