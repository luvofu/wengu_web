package com.wg.userbook.dao;

import com.wg.userbook.domain.UserBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/16/2016.
 */
@Repository
public interface UserBookDao extends JpaRepository<UserBook, Long> {
    List<UserBook> findByUserIdOrderByUpdatedTimeDesc(long userId);

    UserBook findByBookIdAndUserId(long bookId, long userId);

    //全部
    Slice<UserBook> findByUserIdAndPermissionLessThanOrderByCreatedTimeDesc(long userId, int permission, Pageable pageable);

    //标准分类查询
    Slice<UserBook> findByUserIdAndPermissionLessThanAndCategoryOrderByCreatedTimeDesc(long userId, int permission, String category, Pageable pageable);

    //自定义分类查询
    Slice<UserBook> findByCategoryIdAndPermissionLessThanOrderByCreatedTimeDesc(long categoryId, int permission, Pageable pageable);

    //私密查询
    Slice<UserBook> findByUserIdAndPermissionOrderByCreatedTimeDesc(long userId, int permission, Pageable pageable);

    //读状态查询
    Slice<UserBook> findByUserIdAndReadStatusOrderByCreatedTimeDesc(long userId, int readStatus, Pageable pageable);

    //可租借状态查询
    Slice<UserBook> findByUserIdAndIsLeaseAndPermissionLessThanOrderByCreatedTimeDesc(long userId, boolean isLease, int permission, Pageable pageable);

    //可出售状态查询
    Slice<UserBook> findByUserIdAndIsSaleAndPermissionLessThanOrderByCreatedTimeDesc(long userId, boolean isSale, int permission, Pageable pageable);

    //全部统计
    long countByUserId(long userId);

    //标准数量统计
    long countByCategoryId(long categoryId);

    //权限统计
    long countByUserIdAndPermission(long userId, int permission);

    //读状态统计
    long countByUserIdAndReadStatus(long userId, int readStatus);

    //可租借统计
    long countByUserIdAndIsLease(long userId, boolean isLease);

    //可出售统计
    long countByUserIdAndIsSale(long userId, boolean isSale);

    List<UserBook> findByCategoryId(long categoryId);

    @Modifying
    @Query(value = "update UserBook userbook set userbook.category=?1 where userbook.bookId=?2")
    void updateCategory(String category, long bookId);

    //统计用户书籍标准分类
    @Query(value = "select category,count(category) as cnum from UserBook userbook where userbook.userId=?1 group by category order by cnum desc")
    List getCategoryStatis(long userId);

    List<UserBook> findByUserId(Long userId);

    @Query(value = "select ub,ui,(ABS(ui.longitude-?1)+ABS(ui.latitude-?2)) as dis from UserInfo ui ,UserBook ub where ui.longitude between ?3 and ?4 and ui.latitude between ?5 and ?6 and ub.userId=ui.userId and ub.title like concat('%',?7,'%') order by dis asc")
    List findNearbyUserbook(double longitude, double latitude, double fromLng, double toLng, double fromLat, double toLat, String keyword, Pageable pageable);

    @Query(value = "select ub from UserBook ub where ub.userId=?1 and ub.title like concat('%',?2,'%') and (ub.isLease=true or ub.isSale=true) order by ub.createdTime desc ")
    List findTradeAbleBooks(long userId, String keyword, Pageable pageable);

    @Query(value = "select ub from UserBook ub where ub.userId=?1 and ub.title like concat('%',?2,'%') and (ub.isLease=true or ub.isSale=true) and not exists (select cb from CommBook cb where cb.communityId=?3 and ub.userId=cb.userId and ub.bookId=cb.bookId) order by ub.createdTime desc")
    List findShareAbleBooks(long userId, String keyword, long communityId, Pageable pageable);

    @Query(value = "select ub from UserBook ub where ub.userId=?1 and ub.title like concat('%',?2,'%') and not exists (select ubm from UserBookmark ubm where ub.userBookId=ubm.userBookId) order by ub.createdTime desc")
    List<UserBook> findAddmarkUserBooks(long userId, String keyword, Pageable pageable);

    List<UserBook> findByUserIdAndCategory(long userId, String category);
}
