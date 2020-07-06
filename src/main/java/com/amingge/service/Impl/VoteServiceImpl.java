package com.amingge.service.Impl;

import com.amingge.pojo.Vote;
import com.amingge.repository.VoteRepository;
import com.amingge.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class VoteServiceImpl implements VoteService {
    @Autowired
    private VoteRepository voteRepository;

    @Override
    public Vote getVoteById(Long id) {
        return voteRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }
}
