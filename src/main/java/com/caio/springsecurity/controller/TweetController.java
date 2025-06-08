package com.caio.springsecurity.controller;

import com.caio.springsecurity.controller.dto.CreateTweetDTO;
import com.caio.springsecurity.controller.dto.FeedDTO;
import com.caio.springsecurity.controller.dto.FeedItemDTO;
import com.caio.springsecurity.controller.dto.UpdateTweetDTO;
import com.caio.springsecurity.entities.TweetEntity;
import com.caio.springsecurity.entities.enums.RoleEnum;
import com.caio.springsecurity.repository.TweetRepository;
import com.caio.springsecurity.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/tweets") // <- Adicione isso
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetEntity> updateTweet(@PathVariable("id") Long tweetId,
                                                   @RequestBody UpdateTweetDTO tweetDTO,
                                                   JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        var tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.getRoles().stream()
                .anyMatch(roleEntity -> roleEntity.getName().equals(RoleEnum.ADMIN));

        var isOwner = tweet.getUser().getUserId().equals(user.getUserId());

        if (!isAdmin && !isOwner)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        tweet.setContent(tweetDTO.content());
        tweet.setUpdatedAt(Instant.now());

        var updatedTweet = tweetRepository.save(tweet);

        return ResponseEntity.ok(updatedTweet);
    }

    @PostMapping
    public ResponseEntity<Void> creatTweet(@RequestBody CreateTweetDTO tweetDTO,
                                           JwtAuthenticationToken token){
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = new TweetEntity();
        tweet.setUser(user.get());
        tweet.setContent(tweetDTO.content());

        tweetRepository.save(tweet);

        return ResponseEntity
                .created(URI.create("/tweets/" + tweet.getTweetId()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId,
                                            JwtAuthenticationToken token){
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        var tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.getRoles().stream()
                .anyMatch(roleEntity -> roleEntity.getName().equals(RoleEnum.ADMIN));

        var isOwner = tweet.getUser().getUserId().equals(user.getUserId());

        if (!isAdmin && !isOwner)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        tweetRepository.deleteById(tweetId);
        return ResponseEntity.noContent().build();
    }
}

