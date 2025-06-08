package com.caio.springsecurity.repository;

import com.caio.springsecurity.entities.TweetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetRepository extends JpaRepository<TweetEntity, Long> {
}
