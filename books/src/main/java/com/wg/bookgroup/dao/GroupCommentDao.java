package com.wg.bookgroup.dao;


import com.wg.bookgroup.domain.GroupComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 */
@Repository
public interface GroupCommentDao extends JpaRepository<GroupComment, Long> {
    Slice<GroupComment> findAllByOrderByCreatedTimeDesc(Pageable pageable);

    Slice<GroupComment> findByCommunityIdOrderByCreatedTimeDesc(long communityId, Pageable pageable);

    Slice<GroupComment> findByCommunityIdOrderByReplyNumDesc(long communityId, Pageable pageable);

    Slice<GroupComment> findByUserIdOrderByCreatedTimeDesc(long userId, Pageable pageable);

    int countByCommunityId(long communityId);

    Slice<GroupComment> findAllByUpdatedTimeGreaterThanOrderByReplyNumDesc(Date startDate, Pageable pageable);

    Slice<GroupComment> findAllByUpdatedTimeLessThanOrderByReplyNumDesc(Date startDate, Pageable pageable);

    List<GroupComment> findByCommunityId(long communityId);

    List<GroupComment> findByUserId(Long userId);
}
