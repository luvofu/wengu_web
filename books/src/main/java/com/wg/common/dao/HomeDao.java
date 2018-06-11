package com.wg.common.dao;

import com.wg.common.domain.Home;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wzhonggo on 8/15/2016.
 */
@Repository
public interface HomeDao extends JpaRepository<Home, Long> {
    Slice<Home> findByObjTypeOrderByHeatDesc(int objType, Pageable pageable);

    @Modifying
    @Query(value = "delete from Home home  where home.updatedTime<?1")
    void deleteOldData(Date startDate);
}
