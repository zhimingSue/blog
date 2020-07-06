package com.amingge.service;

import com.amingge.pojo.Vote;

public interface VoteService {
    //根据id获取Vote
    Vote getVoteById(Long id);
    //删除Vote
    void removeVote(Long id);
}
