package com.caio.springsecurity.controller;

import com.caio.springsecurity.controller.dto.FeedDTO;
import com.caio.springsecurity.controller.dto.FeedItemDTO;
import com.caio.springsecurity.repository.TweetRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedController {

    private final TweetRepository tweetRepository;

    public FeedController(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDTO> feed(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){

        var tweets = tweetRepository.findAll(
                        PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimeStamp"))
                .map(tweetEntity -> new FeedItemDTO(
                        tweetEntity.getTweetId(),
                        tweetEntity.getContent(),
                        tweetEntity.getUser().getUserName()));

        return ResponseEntity.ok(new FeedDTO(
                tweets.getContent(),
                page,
                pageSize,
                tweets.getTotalPages(),
                tweets.getTotalElements()));
    }

}
