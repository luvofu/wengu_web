package com.wg.message.dao;

import com.wg.message.domain.UserMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
@Repository
public interface UserMessageDao extends JpaRepository<UserMessage, Long> {
    Slice<UserMessage> findByAcceptUserIdAndMessageTypeInOrderByCreatedTimeDesc(long userId, List<Integer> messageTypeList, Pageable pageable);

    List<UserMessage> findByAcceptUserIdAndMessageTypeInAndReadStatus(long userId, List<Integer> readMessageTypeList, int readStatus);

    List<UserMessage> findByAcceptUserIdAndSendUserIdAndMessageTypeIn(long accpterId, long senderId, List<Integer> msgTypes);

    List<UserMessage> findByAcceptUserIdAndReadStatus(long userId, int readStatus);

    List<UserMessage> findByAcceptUserIdAndSendUserIdAndMessageObjIdAndMessageType(long acceptUserId, long sendUserId, long messageObjId, int messageType);

    List<UserMessage> findBySendUserIdAndMessageObjIdAndMessageType(long sendUserId, long messageObjId, int messageType);

    List<UserMessage> findByMessageObjIdAndMessageType(long messageObjId, int messageType);

    @Modifying
    @Query(value = "delete from UserMessage userMessage where userMessage.sendUserId=?1")
    void deleteMsgBySender(Long userId);

    @Modifying
    @Query(value = "delete from UserMessage userMessage where userMessage.acceptUserId=?1")
    void deleteMsgByAccepter(Long userId);

    List<UserMessage> findByAcceptUserIdAndMessageTypeBetweenAndReadStatus(long userId, Integer from, Integer to, int status);

    Slice<UserMessage> findByAcceptUserIdAndMessageTypeBetweenOrderByCreatedTimeDesc(long userId, int from, int to, Pageable pageable);

    @Query(value = "select um from UserMessage um where um.acceptUserId=?1 and (um.messageType=?2 or um.messageType>?3 and um.messageType<?4) order by um.createdTime desc ")
    List<UserMessage> getSysMsgs(long accpterId, int msgType, int fromType, int toType, Pageable pageable);

    @Modifying
    @Query(value = "update UserMessage userMessage set userMessage.readStatus= ?2 where userMessage.acceptUserId=?1")
    void setUserMsgsRead(long userId, int readStatus);
}
