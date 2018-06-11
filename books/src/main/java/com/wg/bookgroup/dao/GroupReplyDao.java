package com.wg.bookgroup.dao;

import com.wg.bookgroup.domain.GroupReply;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
@Repository
public interface GroupReplyDao extends JpaRepository<GroupReply, Long> {
    Slice<GroupReply> findByCommentIdOrderByCreatedTimeDesc(long commentId, Pageable pageable);

    List<GroupReply> findByReplyObjIdAndReplyTypeAndUserIdOrderByCreatedTimeAsc(long replyObjId, int type, long userId);

    List<GroupReply> findByCommentIdAndUserIdInAndCreatedTimeLessThanOrderByCreatedTimeAsc(long commentId, HashSet<Long> ids, Date createdTime);

    List<GroupReply> findByReplyObjIdAndReplyType(long commentId, int type);

    long countByCommentId(long commentId);

    List<GroupReply> findByCommentId(long commentId);

    List<GroupReply> findByUserId(Long userId);
}
