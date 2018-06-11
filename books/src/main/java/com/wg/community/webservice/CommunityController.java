package com.wg.community.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.book.domain.Book;
import com.wg.common.Constant;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.community.JoinStatus;
import com.wg.common.Enum.community.MemberType;
import com.wg.common.Enum.message.DealStatus;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.userbook.CategoryGroupType;
import com.wg.common.ResponseContent;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.community.domain.CommBook;
import com.wg.community.domain.CommMember;
import com.wg.community.domain.Community;
import com.wg.community.model.request.CommunityRequest;
import com.wg.community.model.response.*;
import com.wg.community.utils.CommunityUtils;
import com.wg.message.domain.UserMessage;
import com.wg.message.utils.IMUtils;
import com.wg.user.domain.UserFriend;
import com.wg.user.domain.UserToken;
import com.wg.user.model.response.UserProfileResponse;
import com.wg.userbook.domain.UserBook;
import com.wg.userbook.model.response.UserBookInfoResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2017/3/27 0027.
 */
@Controller
public class CommunityController {

    //测试新建社区
    @Transactional
    @RequestMapping(value = "/api/community/testAdd", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String testAdd(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        if (commMemberDao.countByUserIdAndMemberType(userToken.getUserId(), MemberType.Owner.getType()) < 2) {

        } else {
            responseContent.update(ResponseCode.EXCEE_MAX_OWN_NUM);
        }
        return JSON.toJSONString(responseContent);
    }

    //新建社区
    @Transactional
    @RequestMapping(value = "/api/community/add", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,
                      CommunityRequest communityRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        long userId = userToken.getUserId();
        String name = communityRequest.getName();
        String address = communityRequest.getAddress();
        double longitude = communityRequest.getLongitude();
        double latitude = communityRequest.getLatitude();
        String tag = communityRequest.getTag();
        int joinStatus = communityRequest.getJoinStatus();
        String commDes = communityRequest.getCommDes();
        if (StringUtils.isNotBlank(name) && file != null) {
            if (commMemberDao.countByUserIdAndMemberType(userId, MemberType.Owner.getType()) < 2) {
                if (CommunityUtils.nameOk(name, longitude, latitude)) {
                    Community community = AddUtils.addCommunity(userId, name, address, longitude, latitude, tag, joinStatus, commDes, file);
                    if (community == null) {
                        responseContent.update(ResponseCode.CREATE_FAILD);
                    }
                } else {
                    responseContent.update(ResponseCode.NEARBY_SAME_COMMUNITY);
                }
            } else {
                responseContent.update(ResponseCode.EXCEE_MAX_OWN_NUM);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //我的社区、我加入的社区
    @Transactional
    @RequestMapping(value = "/api/community/my", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String my(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        List<CommMember> commMemberList = commMemberDao.findByUserIdOrderByMemberTypeDesc(userToken.getUserId());
        List<CommunityInfoResponse> communityInfoResponseList = new ArrayList<CommunityInfoResponse>();
        for (CommMember commMember : commMemberList) {
            communityInfoResponseList.add(new CommunityInfoResponse(commMember, userToken));
        }
        responseContent.putData("communityList", communityInfoResponseList);
        return JSON.toJSONString(responseContent);
    }

    //附近的社区、社区搜索
    @Transactional
    @RequestMapping(value = "/api/community/nearbySearch", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String nearbySearch(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        double longitude = communityRequest.getLongitude();
        double latitude = communityRequest.getLatitude();
        String keyword = communityRequest.getKeyword();
        List<CommunityInfoResponse> communityInfoResponseList = new ArrayList<CommunityInfoResponse>();
        for (Community community : CommunityUtils.getNearbyCommunitys(longitude, latitude, keyword, communityRequest.getPage())) {
            communityInfoResponseList.add(new CommunityInfoResponse(community, userToken));
        }
        responseContent.putData("communityList", communityInfoResponseList);
        return JSON.toJSONString(responseContent);
    }

    //社区资料
    @Transactional
    @RequestMapping(value = "/api/community/profile", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String profile(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        if (community != null) {
            CommunityDetailResponse communityDetailResponse = new CommunityDetailResponse(community, userToken);
            responseContent.putData("communityDetail", communityDetailResponse);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //编辑社区
    @Transactional
    @RequestMapping(value = "/api/community/edit", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String edit(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        if (community != null) {
            CommMember commMember = commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userToken.getUserId());
            if (commMember != null && commMember.getMemberType() > MemberType.Commoner.getType()) {
                boolean nameOk = true;
                boolean modifyTribe = false;
                if (communityRequest.getName() != null) {
                    if (CommunityUtils.nameOk(communityRequest.getName(), community.getLongitude(), community.getLatitude())) {
                        community.setName(communityRequest.getName());
                        modifyTribe = true;
                    } else {
                        nameOk = false;
                    }
                }
                if (nameOk) {
                    if (communityRequest.getAddress() != null && communityRequest.getLatitude() >= 0 && communityRequest.getLatitude() >= 0) {
                        community.setAddress(communityRequest.getAddress());
                        community.setLongitude(communityRequest.getLongitude());
                        community.setLatitude(communityRequest.getLatitude());
                        modifyTribe = true;
                    }
                    if (communityRequest.getTag() != null) {
                        community.setTag(communityRequest.getTag());
                    }
                    if (communityRequest.getJoinStatus() > 0) {
                        community.setJoinStatus(communityRequest.getJoinStatus());
                    }
                    if (communityRequest.getCommDes() != null) {
                        community.setCommDes(communityRequest.getCommDes());
                    }
                    if (communityRequest.getCommNote() != null) {
                        community.setCommNote(communityRequest.getCommNote());
                        modifyTribe = true;
                    }
                    community = communityDao.save(community);
                    //update tribe info
                    if (modifyTribe) {
                        IMUtils.modifyTribeInfo(community, commMember.getUserId());
                    }
                } else {
                    responseContent.update(ResponseCode.NEARBY_SAME_COMMUNITY);
                }
            } else {
                responseContent.update(ResponseCode.NO_PPERMISSION);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //社区成员
    @Transactional
    @RequestMapping(value = "/api/community/members", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String members(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        if (community != null) {
            List<CommMember> commMemberList = CommunityUtils.getMembers(
                    community, communityRequest.getMemberType(), communityRequest.getPage());
            List<CommunityMemberResponse> communityMemberResponseList = new ArrayList<CommunityMemberResponse>();
            for (CommMember commMember : commMemberList) {
                communityMemberResponseList.add(new CommunityMemberResponse(commMember));
            }
            responseContent.putData("communityMembers", communityMemberResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //社区成员搜索
    @Transactional
    @RequestMapping(value = "/api/community/memberSearch", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String memberSearch(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        String keyword = communityRequest.getKeyword();
        if (community != null) {
            List<CommunityMemberResponse> communityMemberResponseList = new ArrayList<CommunityMemberResponse>();
            for (CommMember commMember : CommunityUtils.getMembers(community, keyword, communityRequest.getPage())) {
                communityMemberResponseList.add(new CommunityMemberResponse(commMember));
            }
            responseContent.putData("communityMembers", communityMemberResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //加入社区
    @Transactional
    @RequestMapping(value = "/api/community/joinMember", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String joinMember(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        String content = communityRequest.getContent();
        if (community != null) {
            if (community.getJoinStatus() != JoinStatus.Refuse.getStatus()) {
                CommunityUtils.joinMember(community, userToken, content);
            } else {
                responseContent.update(ResponseCode.JOIN_REFUSE);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //可邀请成员
    @Transactional
    @RequestMapping(value = "/api/community/invitableFriends", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String invitableFriends(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        List<UserFriend> userFriendList = userFriendDao.findByUserId(userToken.getUserId());
        List<UserProfileResponse> invitableFriends = new ArrayList<UserProfileResponse>();
        if (community != null) {
            for (UserFriend userFriend : userFriendList) {
                if (commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userFriend.getFriendId()) == null) {
                    invitableFriends.add(new UserProfileResponse(userToken, userInfoDao.findOne(userFriend.getFriendId())));
                }
            }
            responseContent.putData("inviteFriends", invitableFriends);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //邀请社区成员
    @Transactional
    @RequestMapping(value = "/api/community/inviteMember", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String inviteMember(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        String ids = communityRequest.getIds();
        List<Long> userIds = Utils.getLongListByString(ids);
        if (community != null && userIds.size() > 0) {
            CommMember commMember = commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userToken.getUserId());
            if (commMember != null && (community.getJoinStatus() == JoinStatus.Accept.getStatus()
                    || commMember.getMemberType() > MemberType.Commoner.getType())) {
                CommunityUtils.inviteMember(community, commMember, userIds);
            } else {
                responseContent.update(ResponseCode.NO_PPERMISSION);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //加入社区验证处理
    @Transactional
    @RequestMapping(value = "/api/community/dealVertify", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String dealVertify(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        UserMessage userMessage = userMessageDao.findOne(communityRequest.getMessageId());
        int dealStatus = communityRequest.getDealStatus();
        if (userMessage != null && (dealStatus == DealStatus.Accept.getStatus() || dealStatus == DealStatus.Refuse.getStatus())) {
            if (userToken.getUserId() == userMessage.getAcceptUserId()) {
                Community community = communityDao.findOne(userMessage.getMessageObjId());
                if (community != null) {
                    boolean exist = DaoUtils.commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userMessage.getSendUserId()) != null;
                    if (dealStatus == DealStatus.Accept.getStatus()) {
                        CommMember commMember = AddUtils.addCommMember(community, userMessage.getSendUserId(), MemberType.Commoner.getType());
                        if (!exist) {
                            AddUtils.addUserMessage(
                                    userMessage.getSendUserId(),
                                    userMessage.getAcceptUserId(),
                                    null,
                                    MessageType.COMM_ACCEPTE_JOIN.getType(),
                                    commMember.getCommunityId(),
                                    DealStatus.NotDeal.getStatus(),
                                    ReadStatus.NotRead.getStatus());
                        }
                    }
                    userMessage.setReadStatus(ReadStatus.Read.getStatus());
                    if (exist) {
                        userMessage.setDealStatus(DealStatus.Accept.getStatus());
                    } else {
                        userMessage.setDealStatus(dealStatus);
                    }
                    userMessage = userMessageDao.save(userMessage);
                } else {
                    userMessageDao.delete(userMessage);
                }
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //同意邀请加入社区
    @Transactional
    @RequestMapping(value = "/api/community/dealInvite", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String dealInvite(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        UserMessage userMessage = userMessageDao.findOne(communityRequest.getMessageId());
        int dealStatus = communityRequest.getDealStatus();
        if (userMessage != null && (dealStatus == DealStatus.Accept.getStatus() || dealStatus == DealStatus.Refuse.getStatus())) {
            if (userToken.getUserId() == userMessage.getAcceptUserId()) {
                Community community = communityDao.findOne(userMessage.getMessageObjId());
                if (community != null) {
                    if (dealStatus == DealStatus.Accept.getStatus()) {
                        CommMember commMember = AddUtils.addCommMember(community, userMessage.getAcceptUserId(), MemberType.Commoner.getType());
                    }
                    userMessage.setReadStatus(ReadStatus.Read.getStatus());
                    userMessage.setDealStatus(dealStatus);
                    userMessage = userMessageDao.save(userMessage);
                } else {
                    userMessageDao.delete(userMessage);
                }
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //退出社区
    @Transactional
    @RequestMapping(value = "/api/community/quiteMember", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String quiteMember(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        CommMember commMember = commMemberDao.findByCommunityIdAndUserId(communityRequest.getCommunityId(), userToken.getUserId());
        if (commMember != null) {
            if (commMember.getMemberType() != MemberType.Owner.getType()) {
                long communityId = commMember.getCommunityId();
                DeleteUtils.deleteCommMember(commMember, -1);
                CommunityUtils.notifyManager(communityId, MessageType.COMM_QUITE, userToken.getUserId(), null);
            } else {
                responseContent.update(ResponseCode.OWNER_CANT_QUITE);
            }
        }
        return JSON.toJSONString(responseContent);
    }

    //剔除社区成员
    @Transactional
    @RequestMapping(value = "/api/community/deleteMember", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String deleteMember(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        CommMember manager = commMemberDao.findByCommunityIdAndUserId(communityRequest.getCommunityId(), userToken.getUserId());
        if (manager != null && manager.getMemberType() > MemberType.Commoner.getType()) {
            List<Long> commMemberIds = Utils.getLongListByString(communityRequest.getIds());
            List<CommMember> commMemberList = commMemberDao.findAll(commMemberIds);
            for (CommMember commMember : commMemberList) {
                DeleteUtils.deleteCommMember(commMember, manager.getUserId());
            }
        } else {
            responseContent.update(ResponseCode.NO_PPERMISSION);
        }
        return JSON.toJSONString(responseContent);
    }

    //添加社区管理员
    @Transactional
    @RequestMapping(value = "/api/community/setManager", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String setManager(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        CommMember commMember = commMemberDao.findOne(communityRequest.getCommMemberId());
        if (commMember != null) {
            CommMember owner = commMemberDao.findByCommunityIdAndUserId(commMember.getCommunityId(), userToken.getUserId());
            if (owner != null && owner.getMemberType() == MemberType.Owner.getType()) {
                commMember.setMemberType(MemberType.Manager.getType());
                commMember = commMemberDao.save(commMember);
                IMUtils.setManager(communityDao.findOne(commMember.getCommunityId()), owner.getUserId(), commMember.getUserId());
                AddUtils.addUserMessage(
                        commMember.getUserId(),
                        owner.getUserId(),
                        null,
                        MessageType.COMM_SET_MANAGER.getType(),
                        commMember.getCommunityId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            } else {
                responseContent.update(ResponseCode.NO_PPERMISSION);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //取消社区管理员
    @Transactional
    @RequestMapping(value = "/api/community/unsetManager", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String unsetManager(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        CommMember commMember = commMemberDao.findOne(communityRequest.getCommMemberId());
        if (commMember != null) {
            CommMember owner = commMemberDao.findByCommunityIdAndUserId(commMember.getCommunityId(), userToken.getUserId());
            if (owner != null && owner.getMemberType() == MemberType.Owner.getType()) {
                commMember.setMemberType(MemberType.Commoner.getType());
                commMember = commMemberDao.save(commMember);
                IMUtils.unsetManager(communityDao.findOne(commMember.getCommunityId()), owner.getUserId(), commMember.getUserId());
            } else {
                responseContent.update(ResponseCode.NO_PPERMISSION);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //转让社主
    @Transactional
    @RequestMapping(value = "/api/community/transOwner", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String transOwner(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        CommMember commMember = commMemberDao.findOne(communityRequest.getCommMemberId());
        if (commMember != null) {
            CommMember owner = commMemberDao.findByCommunityIdAndUserId(commMember.getCommunityId(), userToken.getUserId());
            if (owner != null && owner.getMemberType() == MemberType.Owner.getType()) {
                if (commMemberDao.countByUserIdAndMemberType(commMember.getUserId(), MemberType.Owner.getType()) < 2) {
                    owner.setMemberType(MemberType.Commoner.getType());
                    owner = commMemberDao.save(owner);
                    commMember.setMemberType(MemberType.Owner.getType());
                    commMember = commMemberDao.save(commMember);
                    IMUtils.modifyTribeInfo(communityDao.findOne(commMember.getCommunityId()), commMember.getUserId());
                    AddUtils.addUserMessage(
                            commMember.getUserId(),
                            owner.getUserId(),
                            null,
                            MessageType.COMM_TRANS_OWNER.getType(),
                            commMember.getCommunityId(),
                            DealStatus.NotDeal.getStatus(),
                            ReadStatus.NotRead.getStatus());
                } else {
                    responseContent.update(ResponseCode.EXCEE_OBJ_MAX_OWN_NUM);
                }
            } else {
                responseContent.update(ResponseCode.NO_PPERMISSION);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //可分享书籍
    @Transactional
    @RequestMapping(value = "/api/community/shareAbleBooks", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String shareAbleBooks(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        List<UserBookInfoResponse> userBookInfoResponseList = new ArrayList<UserBookInfoResponse>();
        String keyword = communityRequest.getKeyword() != null ? communityRequest.getKeyword() : "";
        List objectArray = userBookDao.findShareAbleBooks(
                userToken.getUserId(), keyword, communityRequest.getCommunityId(), new PageRequest(communityRequest.getPage(), Constant.PAGE_NUM_LARGE));
        for (int index = 0; index < objectArray.size(); index++) {
            UserBook userBook = (UserBook) objectArray.get(index);
            userBookInfoResponseList.add(new UserBookInfoResponse(userBook));
        }
        responseContent.putData("userBookList", userBookInfoResponseList);
        return JSON.toJSONString(responseContent);
    }

    //分享书籍到社区
    @Transactional
    @RequestMapping(value = "/api/community/shareBook", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String shareBook(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        CommMember commMember = commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userToken.getUserId());
        List<Long> userBookIds = Utils.getLongListByString(communityRequest.getIds());
        if (community != null && commMember != null && userBookIds.size() > 0) {
            AddUtils.addCommBook(community, userToken, userBookDao.findAll(userBookIds));
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //取消分享书籍到社区
    @Transactional
    @RequestMapping(value = "/api/community/unshareBook", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String unshareBook(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        long communityId = communityRequest.getCommunityId();
        long bookId = communityRequest.getBookId();
        if (communityId != -1 && bookId != -1) {
            CommBook commBook = commBookDao.findByCommunityIdAndUserIdAndBookId(communityId, userToken.getUserId(), bookId);
            DeleteUtils.deleteCommBook(commBook);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //分类统计
    @Transactional
    @RequestMapping(value = "/api/community/categoryStatis", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String categoryStatis(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        if (community != null) {
            responseContent.setData(new CommBookCategoryStatisResponse(community, userToken));
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //社区图书
    @Transactional
    @RequestMapping(value = "/api/community/books", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String books(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        int categoryType = communityRequest.getCategoryType();
        String category = communityRequest.getCategory();
        int page = communityRequest.getPage();
        if (categoryType == CategoryGroupType.All.getType() ||
                categoryType == CategoryGroupType.Normal.getType() && StringUtils.isNotBlank(category) ||
                categoryType == CategoryGroupType.Other.getType() && StringUtils.isNotBlank(category)) {
            List<Long> bookIds = CommunityUtils.getBooks(community, categoryType, category, page, userToken);
            List<CommBookInfoResponse> commBookInfoResponseList = new ArrayList<CommBookInfoResponse>();
            for (Long bookId : bookIds) {
                Book book = bookDao.findOne(bookId);
                if (book != null) {
                    commBookInfoResponseList.add(new CommBookInfoResponse(community, book, userToken));
                }
            }
            responseContent.putData("commBookList", commBookInfoResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //社区图书搜索
    @Transactional
    @RequestMapping(value = "/api/community/bookSearch", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookSearch(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        String keyword = communityRequest.getKeyword();
        int page = communityRequest.getPage();
        List<CommBookInfoResponse> commBookInfoResponseList = new ArrayList<CommBookInfoResponse>();
        for (Long bookId : CommunityUtils.getBooks(community, keyword, page)) {
            Book book = bookDao.findOne(bookId);
            if (book != null) {
                commBookInfoResponseList.add(new CommBookInfoResponse(community, book, userToken));
            }
        }
        responseContent.putData("commBookList", commBookInfoResponseList);
        return JSON.toJSONString(responseContent);
    }

    //社区图书拥有者
    @Transactional
    @RequestMapping(value = "/api/community/bookers", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String bookers(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        int page = communityRequest.getPage();
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        List<CommunityBookerResponse> communityBookerResponseList = new ArrayList<CommunityBookerResponse>();
        for (CommBook commBook : commBookDao.findByCommunityIdAndBookId(communityRequest.getCommunityId(), communityRequest.getBookId(), pageable)) {
            communityBookerResponseList.add(new CommunityBookerResponse(commBook));
        }
        responseContent.putData("bookers", communityBookerResponseList);
        return JSON.toJSONString(responseContent);
    }

    //解散社区
    @Transactional
    @RequestMapping(value = "/api/community/dismiss", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String dismiss(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(communityRequest.getToken());
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        if (community != null) {
            CommMember owner = commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userToken.getUserId());
            if (owner != null && owner.getMemberType() == MemberType.Owner.getType()) {
                DeleteUtils.deleteCommunity(community, userToken);
            } else {
                responseContent.update(ResponseCode.NO_PPERMISSION);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //社群失败重建（imTribeId==0）
    @Transactional
    @RequestMapping(value = "/api/community/createImTribe", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String recreateImTribe(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommunityRequest communityRequest) {
        ResponseContent responseContent = new ResponseContent();
        Community community = communityDao.findOne(communityRequest.getCommunityId());
        if (community != null && community.getImTribeId() == 0) {
            List<CommMember> ownerList = commMemberDao.findByCommunityIdAndMemberType(community.getCommunityId(), MemberType.Owner.getType());
            if (ownerList != null && ownerList.size() > 0) {
                CommMember owner = ownerList.get(0);
                IMUtils.createTribe(community, owner.getUserId());
                responseContent.putData("imTribeId", community.getImTribeId());
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }
}
