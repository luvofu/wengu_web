package com.wg.community.utils;

import com.wg.common.Constant;
import com.wg.common.Enum.community.JoinStatus;
import com.wg.common.Enum.community.MemberType;
import com.wg.common.Enum.message.DealStatus;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.userbook.CategoryGroupType;
import com.wg.common.Enum.userbook.OtherCategory;
import com.wg.common.model.NearbyRange;
import com.wg.common.utils.LngLatUtils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.community.domain.CommMember;
import com.wg.community.domain.Community;
import com.wg.message.utils.UserMsgUtils;
import com.wg.user.domain.UserToken;
import com.wg.userbook.model.CategoryStatis;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
public class CommunityUtils {

    //查询附近的社区
    public static List<Community> getNearbyCommunitys(double longitude, double latitude, String keyword, int page) {
        keyword = (keyword != null ? keyword : "");
        NearbyRange range = LngLatUtils.getNearbyRange(longitude, latitude, LngLatUtils.NEARBY_RADIUS);
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        List objectArray = communityDao.findNearbyCommunity(
                range.getLongitude(), range.getLatitude(), range.getFromLng(), range.getToLng(), range.getFromLat(), range.getToLat(), keyword, pageable);
        List<Community> communityList = new ArrayList<Community>();
        for (int index = 0; index < objectArray.size(); index++) {
            Object[] objects = (Object[]) objectArray.get(index);
            communityList.add((Community) objects[0]);
        }
        return communityList;
    }

    //邀请加入社区
    public static void inviteMember(Community community, CommMember commMember, List<Long> userIds) {
        if (commMember != null && (community.getJoinStatus() == JoinStatus.Accept.getStatus()
                || commMember.getMemberType() > MemberType.Commoner.getType())) {
            //delete invite msg before
            UserMsgUtils.deleteUserMessage(commMember.getUserId(), MessageType.COMM_INVITE_JOIN.getType(), community.getCommunityId());
            //send invite msg
            for (Long userId : userIds) {
                AddUtils.addUserMessage(
                        userId,
                        commMember.getUserId(),
                        null,
                        MessageType.COMM_INVITE_JOIN.getType(),
                        community.getCommunityId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            }
        }
    }

    //加入社区
    public static void joinMember(Community community, UserToken userToken, String content) {
        CommMember commMember = commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userToken.getUserId());
        if (commMember == null) {
            if (community.getJoinStatus() == JoinStatus.Accept.getStatus()) {
                commMember = AddUtils.addCommMember(community, userToken.getUserId(), MemberType.Commoner.getType());
            } else if (community.getJoinStatus() == JoinStatus.Vertify.getStatus()) {
                //delete vertify msg before
                UserMsgUtils.deleteUserMessage(userToken.getUserId(), MessageType.COMM_JOIN_VERTIFY.getType(), community.getCommunityId());
                //notify manager veritfy msg
                notifyManager(community.getCommunityId(), MessageType.COMM_JOIN_VERTIFY, userToken.getUserId(), content);
            }
        }
    }

    //社区书籍分类统计
    public static List<CategoryStatis> getNormalCategoryStatis(Community community) {
        List<CategoryStatis> normallCategoryStatisList = new ArrayList<CategoryStatis>();
        List objectArray = commBookDao.getCategoryStatis(community.getCommunityId());
        for (int index = 0; index < objectArray.size(); index++) {
            Object[] objects = (Object[]) objectArray.get(index);
            normallCategoryStatisList.add(new CategoryStatis((String) objects[0], (Long) objects[1]));
        }
        return normallCategoryStatisList;
    }

    //分类获取社区书籍
    public static List<Long> getBooks(Community community, int categoryType, String category, int page, UserToken userToken) {
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        List<Long> bookIds = new ArrayList<Long>();
        List objectArray = new ArrayList();
        if (categoryType == CategoryGroupType.All.getType()) {
            objectArray = commBookDao.getAllBooks(community.getCommunityId(), pageable);
        } else if (categoryType == CategoryGroupType.Normal.getType()) {
            objectArray = commBookDao.getCategoryBooks(community.getCommunityId(), category, pageable);
        } else if (categoryType == CategoryGroupType.Other.getType()) {
            if (category.equals(OtherCategory.My_share.getFilter())) {
                objectArray = commBookDao.getShareBooks(community.getCommunityId(), userToken.getUserId(), pageable);
            }
        }
        for (int index = 0; index < objectArray.size(); index++) {
            Object[] objects = (Object[]) objectArray.get(index);
            bookIds.add((Long) objects[0]);
        }
        return bookIds;
    }

    //查询社区书籍
    public static List<Long> getBooks(Community community, String keyword, int page) {
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        List<Long> bookIds = new ArrayList<Long>();
        keyword = (keyword != null ? keyword : "");
        List objectArray = commBookDao.getSearchBooks(community.getCommunityId(), keyword, pageable);
        for (int index = 0; index < objectArray.size(); index++) {
            Object objects = objectArray.get(index);
            bookIds.add((Long) objects);
        }
        return bookIds;
    }

    //附近社区不重名
    public static boolean nameOk(String name, double longitude, double latitude) {
        NearbyRange nearbyRange = LngLatUtils.getNearbyRange(longitude, latitude, LngLatUtils.NEARBY_RADIUS);
        List<Community> communityList = communityDao.findByLongitudeBetweenAndLatitudeBetweenAndName(
                nearbyRange.getFromLng(), nearbyRange.getToLng(),
                nearbyRange.getFromLat(), nearbyRange.getToLat(),
                name);
        return communityList.size() == 0;
    }

    //获得用户社区成员类型
    public static int getMemberType(Community community, UserToken userToken) {
        if (userToken != null) {
            CommMember commMember = commMemberDao.findByCommunityIdAndUserId(
                    community.getCommunityId(), userToken.getUserId());
            if (commMember != null) {
                return commMember.getMemberType();
            }
        }
        return MemberType.Stranger.getType();
    }

    //分类获取社区成员
    public static List<CommMember> getMembers(Community community, int memberType, int page) {
        List<CommMember> commMembers;
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        if (memberType == MemberType.Stranger.getType()) {
            commMembers = commMemberDao.findByCommunityIdOrderByMemberTypeDesc(
                    community.getCommunityId(), pageable).getContent();
        } else if (memberType == MemberType.Manager.getType()) {
            commMembers = commMemberDao.findByCommunityIdAndMemberType(
                    community.getCommunityId(), memberType);
        } else {
            commMembers = commMemberDao.findByCommunityIdAndMemberTypeOrderByCreatedTimeDesc(
                    community.getCommunityId(), memberType, pageable).getContent();
        }
        return commMembers;
    }

    //查询社区成员
    public static List<CommMember> getMembers(Community community, String keyword, int page) {
        keyword = (keyword != null ? keyword : "");
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        List<CommMember> commMemberList = commMemberDao.findByCommunityIdAndNicknameOrderByMemberTypeDesc(
                community.getCommunityId(), keyword, pageable);
        return commMemberList;
    }

    //通知管理员
    public static void notifyManager(long communityId, MessageType messageType, long sendUserId, String content) {
        List<CommMember> commMemberList = commMemberDao.findByCommunityIdAndMemberTypeGreaterThan(communityId, MemberType.Commoner.getType());
        for (CommMember cm : commMemberList) {
            AddUtils.addUserMessage(cm.getUserId(),
                    sendUserId,
                    content,
                    messageType.getType(),
                    communityId,
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
    }
}
