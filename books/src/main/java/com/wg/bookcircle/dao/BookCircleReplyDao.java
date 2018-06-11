package com.wg.bookcircle.dao;

import com.wg.bookcircle.domain.BookCircleReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-10
 * Time: 下午6:04
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface BookCircleReplyDao extends JpaRepository<BookCircleReply, Long> {
    List<BookCircleReply> findByDynamicIdOrderByCreatedTimeAsc(long dynamicId);

    List<BookCircleReply> findByDynamicIdAndUserIdInAndCreatedTimeLessThanOrderByCreatedTimeAsc(long dynamicId, HashSet<Long> ids, Date createdTime);

    List<BookCircleReply> findByReplyObjIdAndReplyTypeAndUserIdOrderByCreatedTimeAsc(long replyObjId, int replyType, long userId);

    List<BookCircleReply> findByReplyObjIdAndReplyType(long replyObjId, int type);

    long countByDynamicId(long dynamicId);

    List<BookCircleReply> findByDynamicId(long dynamicId);

    List<BookCircleReply> findByUserId(Long userId);
}
