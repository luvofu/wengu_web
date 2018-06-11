package com.wg.community.dao;

import com.wg.community.domain.CommMember;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
@Repository
public interface CommMemberDao extends JpaRepository<CommMember, Long> {

    CommMember findByCommunityIdAndUserId(long communityId, long userId);

    List<CommMember> findByUserIdOrderByMemberTypeDesc(long userId);

    Slice<CommMember> findByCommunityIdOrderByMemberTypeDesc(long communityId, Pageable pageable);

    List<CommMember> findByCommunityIdAndMemberTypeGreaterThan(long communityId, int memberType);

    long countByCommunityId(long communityId);

    List<CommMember> findByCommunityId(long communityId);

    @Query("select cm from CommMember as cm,UserInfo as ui where cm.communityId=?1 and cm.userId=ui.userId and ui.nickname like concat('%',?2,'%') order by cm.memberType desc")
    List<CommMember> findByCommunityIdAndNicknameOrderByMemberTypeDesc(long communityId, String keyword, Pageable pageable);

    Slice<CommMember> findByCommunityIdAndMemberTypeOrderByCreatedTimeDesc(long communityId, int memberType, Pageable pageable);

    List<CommMember> findByCommunityIdAndMemberType(long communityId, int memberType);

    Slice<CommMember> findByCommunityIdAndUserIdNotOrderByUpdatedTimeDesc(long communityId, long userId, Pageable pageable);

    long countByUserIdAndMemberType(long userId, int memberType);
}
