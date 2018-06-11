package com.wg.news.dao;

import com.wg.news.domain.TempNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2016/12/13 0013.
 */
@Repository
public interface TempNewsDao extends JpaRepository<TempNews, Long> {

    TempNews findByUniqueKey(String ukey);
}
