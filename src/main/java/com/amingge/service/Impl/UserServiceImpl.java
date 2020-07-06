package com.amingge.service.Impl;


import com.amingge.pojo.User;
import com.amingge.repository.UserRepository;
import com.amingge.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;



    @Override
    public boolean isExist(String name) {
        User user = userRepository.findByUsername(name);
        return null!=user;
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username,password);
        return user;
    }

    @Override
    public boolean checkLogin(User user) {
        user = userRepository.findByUsernameAndPassword(user.getUsername(),user.getPassword());
        return user!=null;
    }

    @Transactional
    @Override
    public User saveOrUpdateUser(User user) {
        return userRepository.save(user);
    }
    @Transactional
    @Override
    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }
    @Transactional
    @Override
    public void removeUsersInBatch(List<User> users) {
        userRepository.deleteInBatch(users);
    }
    @Transactional
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> listUsersByNameLike(String name, Pageable pageable) {
        //模糊查询
        name = "%" + name + "%";
        Page<User>users = userRepository.findByNameLike(name,pageable);
        return users;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public List<User> listUsersByUserNames(Collection<String> userNames) {
        return userRepository.findByUsernameIn(userNames);
    }
}
