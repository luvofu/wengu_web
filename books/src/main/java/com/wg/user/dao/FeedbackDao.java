package com.wg.user.dao;

import com.wg.user.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface FeedbackDao extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(long userId);
}
