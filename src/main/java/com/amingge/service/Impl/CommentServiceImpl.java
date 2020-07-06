package com.amingge.service.Impl;

import com.amingge.pojo.Comment;
import com.amingge.repository.CommentRepository;
import com.amingge.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public void removeComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
}
