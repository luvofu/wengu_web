package com.wg.news.dao;

import com.wg.news.domain.News;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/13 0013.
 */
@Repository
public interface NewsDao extends JpaRepository<News, Long> {
    News findByUniqueKey(String uniqueKey);

    Slice<News> findAllByOrderByPubDateDesc(Pageable pageable);

    List<News> findByPubDateLessThan(Date pubDate);
}
