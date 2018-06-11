package com.wg.message.utils;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.OpenImUser;
import com.taobao.api.domain.Userinfos;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import com.wg.common.Enum.message.IMsgType;
import com.wg.common.PropConfig;
import com.wg.common.utils.Utils;
import com.wg.community.domain.CommMember;
import com.wg.community.domain.Community;
import com.wg.user.domain.UserInfo;
import com.wg.user.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2017/1/10 0010.
 */
public class IMUtils {
    public static final String DATA_EXIST = "data exist";
    public static final String WELCOME_TEXT = "文芽致力于为您提供最优质的纸质书籍管理与共享服务。当前提供的主要服务有：\n" +
            "· 藏书管理：你可以在书房模块扫描书籍isbn码识别图书信息，并录入书架进行管理。\n" +
            "· 图书共享：你可以将书架上的书籍设置为可借阅状态，这样你就可以与书友相互借阅图书啦。\n" +
            "文芽使用帮助：" + PropConfig.SERVER_URL + "/doc/help.html";

    //批量注册
    public static boolean sign(List<UserInfo> userInfoList) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimUsersAddRequest req = new OpenimUsersAddRequest();
        req.setUserinfos(loadUserInfos(userInfoList));
        try {
            OpenimUsersAddResponse rsp = CLIENT.execute(req);
            welcome(rsp.getUidSucc());
            return noFail(rsp.getUidFail(), rsp.getFailMsg());
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return false;
    }

    //单个注册
    public static boolean sign(UserInfo userInfo) {
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        userInfoList.add(userInfo);
        return sign(userInfoList);
    }

    //批量更新
    public static boolean update(List<UserInfo> userInfoList) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimUsersUpdateRequest req = new OpenimUsersUpdateRequest();
        req.setUserinfos(loadUserInfos(userInfoList));
        try {
            OpenimUsersUpdateResponse rsp = CLIENT.execute(req);
            return rsp.getUidFail() == null;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return false;
    }

    //单个更新
    public static boolean update(UserInfo userInfo) {
        List<UserInfo> userInfoList = new ArrayList<UserInfo>();
        userInfoList.add(userInfo);
        return update(userInfoList);
    }

    //获取用户信息
    public static String getUserinfos(long userId) {
        TaobaoClient client = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimUsersGetRequest req = new OpenimUsersGetRequest();
        req.setUserids(String.valueOf(userId));
        OpenimUsersGetResponse rsp = null;
        try {
            rsp = client.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return rsp.getBody();
    }

    //发消息
    public static void sendMsg(long senderId, List<String> accpterIds, String msg, long msgType) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimImmsgPushRequest req = new OpenimImmsgPushRequest();
        OpenimImmsgPushRequest.ImMsg imMsg = new OpenimImmsgPushRequest.ImMsg();
        imMsg.setFromUser(String.valueOf(senderId));
        imMsg.setToUsers(accpterIds);
        if (msgType == IMsgType.Text.getType() || msgType == IMsgType.LngLat.getType()) {//文本、地理位置（111，222）
            imMsg.setContext(msg);
        } else if (msgType == IMsgType.Img.getType() || msgType == IMsgType.Audio.getType()) {//图片、语音编码字符串
            imMsg.setContext(Utils.getUrl(msg));
        }
        imMsg.setMsgType(msgType);
        req.setImmsg(imMsg);
        try {
            OpenimImmsgPushResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    private static boolean noFail(List<String> failUids, List<String> failMsgs) {
        boolean noErr = true;
        if (failUids != null && failUids.size() > 0) {
            for (int index = 0; index < failUids.size(); index++) {
                String uid = failUids.get(index);
                String msg = failMsgs.get(index);
                UserInfo userInfo = userInfoDao.findOne(Long.valueOf(uid));
                if (userInfo == null) {
                    logger.info("no exist uid:" + uid);
                } else if (userInfo != null && !msg.equals(DATA_EXIST)) {
                    userInfo.setExtendInfo(msg);
                    noErr = false;
                } else {
                    logger.info("repeat sign uid:" + uid);
                }
            }
        }
        return noErr;
    }

    //装载用户信息
    public static List<Userinfos> loadUserInfos(List<UserInfo> userInfoList) {
        List<Userinfos> userinfosList = new ArrayList<Userinfos>();
        for (UserInfo userInfo : userInfoList) {
            Userinfos userinfos = new Userinfos();
            userinfos.setNick(UserUtils.getSafeNickname(userInfo.getNickname()));
            userinfos.setIconUrl(Utils.getUrl(userInfo.getAvatar()));
            userinfos.setUserid(String.valueOf(userInfo.getUserId()));
            userinfos.setPassword(Utils.MD5(String.valueOf(userInfo.getUserId())));
            userinfosList.add(userinfos);
        }
        return userinfosList;
    }


    //给单个用户发消息
    public static void sendMsg(long senderId, long accpterId, String msg, long msgType) {
        List<String> userIds = new ArrayList<String>();
        userIds.add(String.valueOf(accpterId));
        sendMsg(senderId, userIds, msg, msgType);
    }

    //注册欢迎消息
    public static void welcome(final List<String> succUids) {
        if (succUids != null && succUids.size() > 0) {
            sendMsg(PropConfig.OFFICER_USERID, succUids, WELCOME_TEXT, IMsgType.Text.getType());
        }
    }

    //创建社群
    public static void createTribe(Community community, long ownerId) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeCreateRequest req = new OpenimTribeCreateRequest();
        OpenImUser master = new OpenImUser();
        master.setUid(String.valueOf(ownerId));
        master.setTaobaoAccount(false);
        master.setAppKey(PropConfig.IM_APPKEY);
        req.setUser(master);
        req.setTribeName(community.getName());
        req.setNotice(community.getAddress());
        req.setTribeType(0L);

        List<OpenImUser> members = new ArrayList<OpenImUser>();
        for (CommMember commMember : commMemberDao.findByCommunityId(community.getCommunityId())) {
            OpenImUser member = new OpenImUser();
            member.setUid(String.valueOf(commMember.getUserId()));
            member.setTaobaoAccount(false);
            member.setAppKey(PropConfig.IM_APPKEY);
            members.add(member);
        }
        req.setMembers(members);

        OpenimTribeCreateResponse rsp = null;
        try {
            rsp = CLIENT.execute(req);
            if (rsp.isSuccess()) {
                community.setImTribeId(rsp.getTribeInfo().getTribeId());
                community = communityDao.save(community);
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //加入社群
    public static void joinTribe(Community community, long memberId) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeJoinRequest req = new OpenimTribeJoinRequest();
        OpenImUser member = new OpenImUser();
        member.setUid(String.valueOf(memberId));
        member.setTaobaoAccount(false);
        member.setAppKey(PropConfig.IM_APPKEY);
        req.setUser(member);
        req.setTribeId(community.getImTribeId());
        try {
            OpenimTribeJoinResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //退出社群
    public static void quitTribe(Community community, long memberId) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeQuitRequest req = new OpenimTribeQuitRequest();
        OpenImUser member = new OpenImUser();
        member.setUid(String.valueOf(memberId));
        member.setTaobaoAccount(false);
        member.setAppKey(PropConfig.IM_APPKEY);
        req.setUser(member);
        req.setTribeId(community.getImTribeId());
        try {
            OpenimTribeQuitResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //剔除群成员
    public static void expelTribe(Community community, long ownerId, long memberId) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeExpelRequest req = new OpenimTribeExpelRequest();
        OpenImUser master = new OpenImUser();
        master.setUid(String.valueOf(ownerId));
        master.setTaobaoAccount(false);
        master.setAppKey(PropConfig.IM_APPKEY);
        req.setUser(master);
        req.setTribeId(community.getImTribeId());
        OpenImUser member = new OpenImUser();
        member.setUid(String.valueOf(memberId));
        member.setTaobaoAccount(false);
        member.setAppKey(PropConfig.IM_APPKEY);
        req.setMember(member);
        try {
            OpenimTribeExpelResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //设置管理员
    public static void setManager(Community community, long ownerId, long memberId) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeSetmanagerRequest req = new OpenimTribeSetmanagerRequest();
        OpenImUser master = new OpenImUser();
        master.setUid(String.valueOf(ownerId));
        master.setTaobaoAccount(false);
        master.setAppKey(PropConfig.IM_APPKEY);
        req.setUser(master);
        req.setTid(community.getImTribeId());
        OpenImUser member = new OpenImUser();
        member.setUid(String.valueOf(memberId));
        member.setTaobaoAccount(false);
        member.setAppKey(PropConfig.IM_APPKEY);
        req.setMember(member);
        try {
            OpenimTribeSetmanagerResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //取消管理员
    public static void unsetManager(Community community, long ownerId, long memberId) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeUnsetmanagerRequest req = new OpenimTribeUnsetmanagerRequest();
        OpenImUser master = new OpenImUser();
        master.setUid(String.valueOf(ownerId));
        master.setTaobaoAccount(false);
        master.setAppKey(PropConfig.IM_APPKEY);
        req.setUser(master);
        req.setTid(community.getImTribeId());
        OpenImUser member = new OpenImUser();
        member.setUid(String.valueOf(memberId));
        member.setTaobaoAccount(false);
        member.setAppKey(PropConfig.IM_APPKEY);
        req.setMember(member);
        try {
            OpenimTribeUnsetmanagerResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //群解散
    public static void dismissTribe(Community community, long ownerId) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeDismissRequest req = new OpenimTribeDismissRequest();
        OpenImUser master = new OpenImUser();
        master.setUid(String.valueOf(ownerId));
        master.setTaobaoAccount(false);
        master.setAppKey(PropConfig.IM_APPKEY);
        req.setUser(master);
        req.setTribeId(community.getImTribeId());
        try {
            OpenimTribeDismissResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //群信息修改
    public static void modifyTribeInfo(Community community, long managerId) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeModifytribeinfoRequest req = new OpenimTribeModifytribeinfoRequest();
        OpenImUser master = new OpenImUser();
        master.setUid(String.valueOf(managerId));
        master.setTaobaoAccount(false);
        master.setAppKey(PropConfig.IM_APPKEY);
        req.setUser(master);
        req.setTribeName(community.getName());
        req.setNotice(StringUtils.isNotBlank(community.getCommNote()) ? community.getCommNote() : community.getAddress());
        req.setTribeId(community.getImTribeId());
        try {
            OpenimTribeModifytribeinfoResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    //发送群消息
    public static void sendTribeMsg(Community community, long ownerId, String content) {
        TaobaoClient CLIENT = new DefaultTaobaoClient(PropConfig.IM_HOST, PropConfig.IM_APPKEY, PropConfig.IM_SECRET);
        OpenimTribeSendmsgRequest req = new OpenimTribeSendmsgRequest();
        OpenimTribeSendmsgRequest.User master = new OpenimTribeSendmsgRequest.User();
        master.setAppkey(PropConfig.IM_APPKEY);
        master.setTaobaoAccount(false);
        master.setUid(String.valueOf(ownerId));
        req.setUser(master);
        req.setTribeId(community.getImTribeId());

        OpenimTribeSendmsgRequest.TribeMsg tribeMsg = new OpenimTribeSendmsgRequest.TribeMsg();
        tribeMsg.setAtFlag(0L);
        List<OpenimTribeSendmsgRequest.User> members = new ArrayList<OpenimTribeSendmsgRequest.User>();
        for (CommMember commMember : commMemberDao.findByCommunityId(community.getCommunityId())) {
            OpenimTribeSendmsgRequest.User member = new OpenimTribeSendmsgRequest.User();
            member.setAppkey(PropConfig.IM_APPKEY);
            member.setTaobaoAccount(false);
            member.setUid(String.valueOf(commMember.getUserId()));
            members.add(member);
        }
        tribeMsg.setAtmembers(members);
        tribeMsg.setCustomPush("{\"d\":\"custom push\", \"sound\":\"dingdong\", \"title\" : \"title\"}");
        tribeMsg.setMediaAttrs("{\"height\": 10, \"width\": 10, \"type\": \"jpg\"}");
        tribeMsg.setMsgContent(content);
        tribeMsg.setMsgType(0L);
        tribeMsg.setPush(true);
        req.setMsg(tribeMsg);
        try {
            OpenimTribeSendmsgResponse rsp = CLIENT.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}
