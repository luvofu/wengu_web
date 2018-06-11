package com.wg.user.dao;

import com.wg.user.domain.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wzhonggo on 8/2/2016.
 */
@Repository
public interface UserInfoDao extends JpaRepository<UserInfo, Long> {
    UserInfo findByNickname(String nickname);

    @Query(value = "select ui,((ui.longitude-?1)*(ui.longitude-?1)+(ui.latitude-?2)*(ui.latitude-?2)) as dis from UserInfo ui where ui.longitude between ?3 and ?4 and ui.latitude between ?5 and ?6 order by dis asc")
    List findNearbyUsers(double longitude, double latitude, double fromLng, double toLng, double fromLat, double toLat, Pageable pageable);

    @Modifying
    @Query(value = "update UserInfo set ocrRecogNum=?1")
    void setDefaultOcrNum(long ocrDayNum);

    @Modifying
    @Query(value = "update UserInfo set exportNum=?1")
    void setDefaultExportNum(long exportNum);
}
