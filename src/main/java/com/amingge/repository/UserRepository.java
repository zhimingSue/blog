package com.amingge.repository;

import com.amingge.pojo.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    //  根据用户名分页查询用户列表
    Page<User>findByNameLike(String name, Pageable pageable);
    //根据用户姓名查询
    User findByUsername(String username);
    //根据用户姓名查询
    List<User> findByUsernameIn(Collection<String>usernames);
    //根据邮箱查找用户
    User findByEmail(String email);
    //登录用
    public User findByUsernameAndPassword(String username, String password);

}
