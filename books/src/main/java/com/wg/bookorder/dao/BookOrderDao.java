package com.wg.bookorder.dao;

import com.wg.bookorder.domain.BookOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/14 0014.
 */
@Repository
public interface BookOrderDao extends JpaRepository<BookOrder, Long> {

    long countByUserIdAndOrderTypeAndFinishType(long userId, int orderType, int finishType);

    Slice<BookOrder> findByUserIdAndOrderTypeAndFinishTypeAndIsDeleteOrderByUpdatedTimeDesc(long userId, int orderType, int orderStatus, boolean isDelete, Pageable pageable);

    Slice<BookOrder> findByUserIdAndOrderTypeAndFinishTypeNotAndIsDeleteOrderByUpdatedTimeDesc(long userId, int orderType, int finishType, boolean isDelete, Pageable pageable);

    Slice<BookOrder> findByUserIdAndOrderTypeAndIsDeleteOrderByUpdatedTimeDesc(long userId, int orderType, boolean isDelete, Pageable pageable);

    Slice<BookOrder> findByOwnerIdAndOrderTypeAndIsOwnerDeleteOrderByUpdatedTimeDesc(long userId, int orderType, boolean isOwnerDelete, Pageable pageable);

    Slice<BookOrder> findByOwnerIdAndOrderTypeAndFinishTypeNotAndIsOwnerDeleteOrderByUpdatedTimeDesc(long userId, int orderType, int finishType, boolean isOwnerDelete, Pageable pageable);

    Slice<BookOrder> findByOwnerIdAndOrderTypeAndFinishTypeAndIsOwnerDeleteOrderByUpdatedTimeDesc(long userId, int orderType, int orderStatus, boolean isOwnerDelete, Pageable pageable);

    List<BookOrder> findByOrderStatusAndNotifyTimeLessThan(int orderStatus, Date currDate);

    List<BookOrder> findByOrderStatusAndNotifyTimeLessThanAndIsComplaint(int orderStatus, Date currDate, boolean isComplaint);

    Slice<BookOrder> findByUserIdAndIsDeleteOrOwnerIdAndIsOwnerDeleteOrderByUpdatedTimeDesc(long userId, boolean isDelete, long ownerId, boolean isOwnerDelete, Pageable pageable);

    BookOrder findByOrderNumber(String out_trade_no);

}
