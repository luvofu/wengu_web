package com.wg.message.dao;

import com.wg.message.domain.MessagePush;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2016/9/2.
 */
@Repository
public interface MessagePushDao extends JpaRepository<MessagePush, Long> {

    MessagePush findByClientId(String clientId);

    MessagePush findByUserIdAndPlatform(long userId, String platform);
}
