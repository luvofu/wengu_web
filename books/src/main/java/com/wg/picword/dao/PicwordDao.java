package com.wg.picword.dao;


import com.wg.picword.domain.Picword;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/12/23 0023.
 */
@Repository
public interface PicwordDao extends JpaRepository<Picword, Long> {

    Slice<Picword> findByUserIdOrderByCreatedTimeDesc(long userId, Pageable pageable);

    Slice<Picword> findByPermissionOrderByCreatedTimeDesc(int permission, Pageable pageable);

    Slice<Picword> findByCreatedTimeGreaterThanAndPermissionOrderByGoodNumDesc(Date startDate, int permission, Pageable pageable);

    Slice<Picword> findByCreatedTimeLessThanAndPermissionOrderByGoodNumDesc(Date startDate, int permission, Pageable pageable);

    List<Picword> findByUserId(Long userId);
}
