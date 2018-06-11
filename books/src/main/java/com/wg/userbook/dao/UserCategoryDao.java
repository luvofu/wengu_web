package com.wg.userbook.dao;

import com.wg.userbook.domain.UserCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/16/2016.
 */
@Repository
public interface UserCategoryDao extends JpaRepository<UserCategory, Long> {

    UserCategory findByUserIdAndCategory(long userId, String category);

    List<UserCategory> findByUserId(long userId);

    List<UserCategory> findByUserIdOrderBySortAsc(long userId);

}
