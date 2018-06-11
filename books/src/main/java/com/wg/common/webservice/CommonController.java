package com.wg.common.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.domain.BookCircleReply;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.bookgroup.domain.GroupReply;
import com.wg.booksheet.domain.BookSheet;
import com.wg.common.Constant;
import com.wg.common.Enum.bookcircle.DynamicType;
import com.wg.common.Enum.common.*;
import com.wg.common.Enum.community.MemberType;
import com.wg.common.Enum.userbook.CategoryGroupType;
import com.wg.common.FileConfig;
import com.wg.common.ResponseContent;
import com.wg.common.model.request.CommonRequest;
import com.wg.common.model.response.CommonConfigResponse;
import com.wg.common.model.response.CommonEditIMGResponse;
import com.wg.common.model.response.CommonGoodResponse;
import com.wg.common.model.response.CommonHomeResponse;
import com.wg.common.utils.ImageUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.common.utils.dbutils.UpdateUtils;
import com.wg.common.utils.export.ExcelUtils;
import com.wg.common.utils.export.PdfUtils;
import com.wg.community.domain.CommMember;
import com.wg.community.domain.Community;
import com.wg.message.utils.EmailUtils;
import com.wg.message.utils.IMUtils;
import com.wg.notebook.domain.Note;
import com.wg.notebook.domain.Notebook;
import com.wg.user.domain.UserGood;
import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/9/1.
 */
@Controller
public class CommonController {

    @Transactional
    @RequestMapping(value = "/api/common/good", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String good(HttpServletRequest request,
                       HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        CommonGoodResponse commonGoodResponse;
        int goodType = commonRequest.getGoodType();
        UserToken userToken = userTokenDao.findByToken(commonRequest.getToken());
        if (goodType == GoodType.Comment.getType()
                || goodType == GoodType.Dynamic.getType()
                || goodType == GoodType.BookSheet.getType()
                || goodType == GoodType.Picword.getType()) {
            UserGood userGood = userGoodDao.findByUserIdAndGoodObjIdAndGoodType(userToken.getUserId(), commonRequest.getGoodObjId(), goodType);
            if (userGood == null) {
                AddUtils.addUserGood(userToken.getUserId(), commonRequest.getGoodType(), commonRequest.getGoodObjId());
                commonGoodResponse = new CommonGoodResponse(true);
            } else {
                DeleteUtils.deleteUserGood(userGood);
                commonGoodResponse = new CommonGoodResponse(false);
            }
            responseContent.setData(commonGoodResponse);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/share", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String share(HttpServletRequest request,
                        HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(commonRequest.getToken());
        //create book circle dynamic
        BookCircleDynamic bookCircleDynamic = AddUtils.addBookCircleDynamic(
                userToken.getUserId(),
                commonRequest.getContent(),
                commonRequest.getShareObjId(),
                commonRequest.getShareType(),
                commonRequest.getLocation(),
                null,
                Permission.Open.getType(),
                DynamicType.Personal.getType());
        if (bookCircleDynamic == null) {
            responseContent.update(ResponseCode.CREATE_FAILD);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(commonRequest.getToken());
        int deleteType = commonRequest.getDeleteType();
        long deleteObjId = commonRequest.getDeleteObjId();
        if (userToken != null && deleteType != -1 && deleteObjId != -1) {
            if (deleteType == DeleteType.Comment.getType()) {
                GroupComment groupComment = groupCommentDao.findOne(deleteObjId);
                if (userToken.getUserId() == groupComment.getUserId()) {
                    DeleteUtils.deleteCommunityComment(groupComment);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else if (deleteType == DeleteType.CommentReply.getType()) {
                GroupReply groupReply = groupReplyDao.findOne(deleteObjId);
                if (userToken.getUserId() == groupReply.getUserId()) {
                    DeleteUtils.deleteCommunityReply(groupReply);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else if (deleteType == DeleteType.Dynamic.getType()) {
                BookCircleDynamic bookCircleDynamic = bookCircleDynamicDao.findOne(deleteObjId);
                if (userToken.getUserId() == bookCircleDynamic.getUserId()) {
                    DeleteUtils.deleteBookCircleDynamic(bookCircleDynamic);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else if (deleteType == DeleteType.DynamicReply.getType()) {
                BookCircleReply bookCircleReply = bookCircleReplyDao.findOne(deleteObjId);
                if (userToken.getUserId() == bookCircleReply.getUserId()) {
                    DeleteUtils.deleteBookCircleReply(bookCircleReply);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.ERROR_TYPE);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }


    @Transactional
    @RequestMapping(value = "/api/common/bookClass", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String bookClass(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        responseContent.putData("bookClassList", FileConfig.topClassList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/bookTag", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String bookTag(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        responseContent.putData("bookTagList", FileConfig.bookTagList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/bookSheetTag", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String bookSheetTag(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        responseContent.putData("bookSheetTagList", FileConfig.bookSheetTagList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/logo", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String logo(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/deleteIMG", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String deleteIMG(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(commonRequest.getToken());
        boolean success = false;
        if (commonRequest.getImageType() == ImageType.UserAvatar.getType()) {
            UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
            if (userInfo.getUserId() == userToken.getUserId()) {
                if (ImageUtils.deleteImage(userInfo.getAvatar())) {
                    userInfo.setAvatar(Constant.USER_AVATAR_DEFAULT);
                    userInfo = UpdateUtils.updateUserInfo(userInfo);
                    success = true;
                }
            }
        } else if (commonRequest.getImageType() == ImageType.UserBackground.getType()) {
            UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
            if (userInfo.getUserId() == userToken.getUserId()) {
                if (ImageUtils.deleteImage(userInfo.getBackground())) {
                    userInfo.setBackground(Constant.USER_BACKGROUND_DEFAULT);
                    userInfo = UpdateUtils.updateUserInfo(userInfo);
                    success = true;
                }
            }
        } else if (commonRequest.getImageType() == ImageType.BookSheetCover.getType()) {
            BookSheet bookSheet = bookSheetDao.findOne(commonRequest.getImageObjId());
            if (bookSheet.getUserId() == userToken.getUserId()) {
                if (ImageUtils.deleteImage(bookSheet.getCover())) {
                    bookSheet.setCover(Constant.BOOK_SHEET_COVER_DEFAULT);
                    bookSheet = UpdateUtils.updateBookSheet(bookSheet);
                    success = true;
                }
            }
        } else if (commonRequest.getImageType() == ImageType.NotebookContent.getType()) {
            Note note = noteDao.findOne(commonRequest.getImageObjId());
            if (notebookDao.findOne(note.getNotebookId()).getUserId() == userToken.getUserId()) {
                if (ImageUtils.deleteImage(note.getImage())) {
                    note.setImage(null);
                    noteDao.save(note);
                    success = true;
                }
            }
        } else if (commonRequest.getImageType() == ImageType.NotebookContent.getType()) {
            BookCircleDynamic bookCircleDynamic = bookCircleDynamicDao.findOne(commonRequest.getImageObjId());
            if (bookCircleDynamic.getUserId() == userToken.getUserId()) {
                if (ImageUtils.deleteImage(bookCircleDynamic.getImage())) {
                    bookCircleDynamic.setImage(null);
                    bookCircleDynamic = bookCircleDynamicDao.save(bookCircleDynamic);
                    success = true;
                }
            }
        }
        if (success) {
        } else {
            responseContent.update(ResponseCode.DELETE_IMAGE_FAILD);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/editIMG", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String editIMG(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        CommonEditIMGResponse commonEditIMGResponse;
        UserToken userToken = userTokenDao.findByToken(commonRequest.getToken());
        boolean success = false;
        String imageUrl = null;
        if (commonRequest.getImageType() == ImageType.UserAvatar.getType()) {
            UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
            if (userInfo.getUserId() == userToken.getUserId()) {
                imageUrl = ImageUtils.saveImage(Constant.USER_AVATAR_FOLDER, userToken.getUserId(), file);
                if (imageUrl != null) {
                    ImageUtils.deleteImage(userInfo.getAvatar());
                    userInfo.setAvatar(imageUrl);
                    userInfo = UpdateUtils.updateUserInfo(userInfo);
                    IMUtils.update(userInfo);
                    success = true;
                }
            }
        } else if (commonRequest.getImageType() == ImageType.UserBackground.getType()) {
            UserInfo userInfo = userInfoDao.findOne(userToken.getUserId());
            if (userInfo.getUserId() == userToken.getUserId()) {
                imageUrl = ImageUtils.saveImage(Constant.USER_BACKGROUND_FOLDER, userToken.getUserId(), file);
                if (imageUrl != null) {
                    ImageUtils.deleteImage(userInfo.getBackground());
                    userInfo.setBackground(imageUrl);
                    userInfo = UpdateUtils.updateUserInfo(userInfo);
                    success = true;
                }
            }
        } else if (commonRequest.getImageType() == ImageType.BookSheetCover.getType()) {
            BookSheet bookSheet = bookSheetDao.findOne(commonRequest.getImageObjId());
            if (bookSheet.getUserId() == userToken.getUserId()) {
                imageUrl = ImageUtils.saveImage(Constant.BOOK_SHEET_COVER_FOLDER, bookSheet.getSheetId(), file);
                if (imageUrl != null) {
                    ImageUtils.deleteImage(bookSheet.getCover());
                    bookSheet.setCover(imageUrl);
                    bookSheet = UpdateUtils.updateBookSheet(bookSheet);
                    success = true;
                }
            }
        } else if (commonRequest.getImageType() == ImageType.NotebookContent.getType()) {
            Note note = noteDao.findOne(commonRequest.getImageObjId());
            if (notebookDao.findOne(note.getNotebookId()).getUserId() == userToken.getUserId()) {
                imageUrl = ImageUtils.saveImage(Constant.NOTEBOOK_CONTENT_FOLDER, note.getNoteId(), file);
                if (imageUrl != null) {
                    ImageUtils.deleteImage(note.getImage());
                    note.setImage(imageUrl);
                    noteDao.save(note);
                    success = true;
                }
            }
        } else if (commonRequest.getImageType() == ImageType.CommunityTheme.getType()) {
            Community community = communityDao.findOne(commonRequest.getImageObjId());
            CommMember commMember = commMemberDao.findByCommunityIdAndUserId(community.getCommunityId(), userToken.getUserId());
            if (commMember != null && commMember.getMemberType() == MemberType.Owner.getType()) {
                imageUrl = ImageUtils.saveImage(Constant.COMMUNITY_THEMEPIC_FOLDER, community.getCommunityId(), file);
                if (imageUrl != null) {
                    ImageUtils.deleteImage(community.getThemePic());
                    community.setThemePic(imageUrl);
                    communityDao.save(community);
                    success = true;
                }
            }
        }
        if (success) {
            commonEditIMGResponse = new CommonEditIMGResponse(imageUrl);
            responseContent.setData(commonEditIMGResponse);
        } else {
            responseContent.update(ResponseCode.EDIT_IMAGE_FAILD);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/home", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String home(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(commonRequest.getToken());
        responseContent.setData(new CommonHomeResponse(userToken, true));
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/common/config", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String config(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        responseContent.setData(new CommonConfigResponse(commonRequest.getUpdatedTime()));
        return JSON.toJSONString(responseContent);
    }

    /*version 104*version 104*version 104*version 104*version 104*version 104*version 104*version 104*version 104*version 104*/
    @Transactional
    @RequestMapping(value = "/api/common/choiceness", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String choiceness(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(commonRequest.getToken());
        responseContent.setData(new CommonHomeResponse(userToken, false));
        return JSON.toJSONString(responseContent);
    }

    //////////////////////////////////////////////////////version 202///////////////////////////////////////////////////////////////
    @Transactional
    @RequestMapping(value = "api/common/export", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String test(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, CommonRequest commonRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(commonRequest.getToken());
        UserInfo userInfo = UserUtils.updateReset(userToken.getUserInfo());
        int exportType = commonRequest.getExportType();
        String email = userInfo.getMailbox();
        if (UserUtils.isEmail(email)) {
            boolean success = false;
            if (userInfo.getExportNum() > 0) {
                Date consumeDate = TimeUtils.getCurrentDate();
                if (exportType == ExportType.Notebook.getType()) {
                    Notebook notebook = notebookDao.findOne(commonRequest.getId());
                    if (notebook != null) {
                        String path = PdfUtils.exportNotebookPdf(notebook);
                        if (path != null) {
                            EmailUtils.reportExp(userInfo, notebook.getBook().getTitle(), path, exportType);
                            success = true;
                        }
                    }
                } else if (exportType == ExportType.Userbook.getType()) {
                    int categoryType = CategoryGroupType.Normal.getType();
                    if (categoryType == CategoryGroupType.All.getType() || categoryType == CategoryGroupType.Normal.getType()) {
                        String path = ExcelUtils.exportBookExcel(userInfo, categoryType);
                        if (path != null) {
                            EmailUtils.reportExp(userInfo, null, path, exportType);
                            success = true;
                        }
                    }
                }
                if (success) {
                    UserUtils.consumeExport(userInfo, consumeDate);
                }
                responseContent.putData("exportNum", userInfo.getExportNum());
            } else {
                responseContent.update(ResponseCode.EXPORT_NUM_NOT_ENOUGH);
            }
        } else {
            responseContent.update(ResponseCode.INVALID_EMAIL);
        }
        return JSON.toJSONString(responseContent);
    }
}
