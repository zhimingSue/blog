package com.amingge.controller;

import com.amingge.pojo.User;
import com.amingge.service.BlogService;
import com.amingge.service.VoteService;
import com.amingge.util.ConstraintViolationExceptionHandler;
import com.amingge.vo.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.ConstraintViolationException;

@Controller
@RequestMapping("/votes")
public class VoteController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private VoteService voteService;

    //发表点赞
    @PostMapping
    public ResponseEntity<Response>createVote(Long blogId){
        try {
            blogService.createVote(blogId);
        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "点赞成功", null));
    }
    //删除点赞
    @DeleteMapping("/{id}")
    public ResponseEntity<Response>delete(@PathVariable("id")Long id, Long blogId){
        boolean isOwner = false;
        User user = voteService.getVoteById(id).getUser();

        // 判断操作用户是否是点赞的所有者
        Session session= SecurityUtils.getSubject().getSession();
        User principal = (User) session.getAttribute("userSession");
        if (principal !=null && user.getUsername().equals(principal.getUsername())) {
            isOwner = true;
        }
        if (!isOwner) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限"));
        }
        try {
            blogService.removeVote(blogId, id);
            voteService.removeVote(id);
        } catch (ConstraintViolationException e)  {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "取消点赞成功", null));
    }

}
