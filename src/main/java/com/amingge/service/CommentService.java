package com.amingge.service;

import com.amingge.pojo.Comment;

public interface CommentService {
    //根据id获取 Comment
    Comment getCommentById(Long id);
    //删除评论
    void removeComment(Long id);
}
