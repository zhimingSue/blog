package com.amingge.service;

import com.amingge.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService {
    //判断名字是否重复
    boolean isExist(String name);
    //用户登录
    User findByUsernameAndPassword(String username,String password);
    // 判断登录
    boolean checkLogin(User user);
    //保存用户
    User saveOrUpdateUser(User user);
    //删除用户
    void removeUser(Long id);
    //删除列表里面的用户
    void removeUsersInBatch(List<User>users);
    //更新用户
    User updateUser(User user);
    //根据id获取用户
    User getUserById(Long id);
    //获取用户列表
    List<User>listUsers();
    //根据用户名进行分页模糊查询
    Page<User>listUsersByNameLike(String name, Pageable pageable);

    User findByUsername(String username);

    User findByEmail(String email);
    //es查询用
    List<User> listUsersByUserNames(Collection<String> userNames);
}
