package com.wg.community.dao;

import com.wg.community.domain.CommBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
@Repository
public interface CommBookDao extends JpaRepository<CommBook, Long> {
    CommBook findByCommunityIdAndUserIdAndBookId(long communityId, long userId, long bookId);

    long countByCommunityId(long communityId);

    @Query(value = "select cb.category,count(category) as cnum from CommBook cb where cb.communityId=?1 group by cb.category order by cnum desc")
    List getCategoryStatis(long communityId);

    @Query(value = "select cb.bookId,max(cb.createdTime) as time from CommBook cb where cb.communityId=?1 group by cb.bookId order by time desc")
    List getAllBooks(long communityId, Pageable pageable);

    @Query(value = "select cb.bookId,max(cb.createdTime) as time from CommBook cb where cb.communityId=?1 and cb.category=?2 group by cb.bookId order by time desc")
    List getCategoryBooks(long communityId, String category, Pageable pageable);

    @Query(value = "select cb.bookId,cb.createdTime as time from CommBook cb where cb.communityId=?1 and cb.userId=?2 order by time desc")
    List getShareBooks(long communityId, long userId, Pageable pageable);

    @Query(value = "select distinct cb.bookId from CommBook cb where cb.communityId=?1 and (cb.title like concat('%',?2,'%') or cb.author like concat('%',?2,'%'))")
    List getSearchBooks(long communityId, String keyword, Pageable pageable);

    Slice<CommBook> findByCommunityIdAndBookId(long communityId, long bookId, Pageable pageable);

    List<CommBook> findByCommunityId(long communityId);

    @Modifying
    @Query(value = "update CommBook cb set cb.category=?1 where cb.bookId=?2")
    void updateCategory(String category, long bookId);

    long countByCommunityIdAndUserId(long communityId, long userId);

    List<CommBook> findByUserIdAndBookId(long userId, long bookId);

    List<CommBook> findByCommunityIdAndUserId(long communityId, long userId);
}
