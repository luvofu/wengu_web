package com.wg.community.dao;

import com.wg.community.domain.Community;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
@Repository
public interface CommunityDao extends JpaRepository<Community, Long> {
    List<Community> findByLongitudeBetweenAndLatitudeBetweenAndName(double fromLng, double toLng, double fromLat, double toLat, String name);

    @Query(value = "select co,((co.longitude-?1)*(co.longitude-?1)+(co.latitude-?2)*(co.latitude-?2)) as dis from Community co where co.longitude between ?3 and ?4 and co.latitude between ?5 and ?6 and co.name like concat('%',?7,'%') order by dis asc")
    List findNearbyCommunity(double longitude, double latitude, double fromLng, double toLng, double fromLat, double toLat, String keyword, Pageable pageable);
}
