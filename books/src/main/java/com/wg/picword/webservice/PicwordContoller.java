package com.wg.picword.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.common.Constant;
import com.wg.common.Enum.common.HomeType;
import com.wg.common.Enum.common.Permission;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.ResponseContent;
import com.wg.common.domain.Home;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.common.utils.dbutils.DaoUtils;
import com.wg.common.utils.dbutils.DeleteUtils;
import com.wg.picword.domain.Picword;
import com.wg.picword.model.request.PicwordRequest;
import com.wg.picword.model.response.PicwordEntityResponse;

import com.wg.user.domain.UserToken;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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

import static com.wg.common.utils.dbutils.DaoUtils.bookDao;
import static com.wg.common.utils.dbutils.DaoUtils.picwordDao;
import static com.wg.common.utils.dbutils.DaoUtils.userTokenDao;

/**
 * Created by Administrator on 2016/12/23 0023.
 */
@Controller
public class PicwordContoller {

    @Transactional
    @RequestMapping(value = "/api/picword/add", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String add(HttpServletRequest request,
                      HttpServletResponse response, ModelMap modelMap, PicwordRequest picwordRequest, @RequestParam(value = "imageFile", required = false) MultipartFile file) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(picwordRequest.getToken());
        long bookId = picwordRequest.getBookId();
        int permisson = picwordRequest.getPermission();
        if (file != null) {
            if (bookId == -1 || bookDao.findOne(bookId) != null) {
                AddUtils.addPicword(userToken.getUserId(), bookId != -1 ? bookId : 0, permisson, file);
            } else {
                responseContent.update(ResponseCode.BOOK_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/picword/delete", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, PicwordRequest picwordRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(picwordRequest.getToken());
        long picwordId = picwordRequest.getPicwordId();
        if (picwordId != -1) {
            Picword picword = picwordDao.findOne(picwordId);
            if (picword != null) {
                if (picword.getUserId() == userToken.getUserId()) {
                    DeleteUtils.deletePicword(picword);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.PICWORD_NOT_EXIST);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/picword/my", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String my(HttpServletRequest request,
                     HttpServletResponse response, ModelMap modelMap, PicwordRequest picwordRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(picwordRequest.getToken());
        List<PicwordEntityResponse> picwordEntityResponseList = new ArrayList<PicwordEntityResponse>();
        Slice<Picword> picwordSlice = picwordDao.findByUserIdOrderByCreatedTimeDesc(userToken.getUserId(), new PageRequest(picwordRequest.getPage(), Constant.PAGE_NUM_LARGE));
        for (Picword picword : picwordSlice.getContent()) {
            picwordEntityResponseList.add(new PicwordEntityResponse(picword, userToken));
        }
        responseContent.putData("picwordList", picwordEntityResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/picword/hotest", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String hotest(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, PicwordRequest picwordRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(picwordRequest.getToken());
        List<PicwordEntityResponse> picwordEntityResponseList = new ArrayList<PicwordEntityResponse>();
        for (Home home : DaoUtils.homeDao.findByObjTypeOrderByHeatDesc(
                HomeType.Picword.getType(), new PageRequest(picwordRequest.getPage(), Constant.HOME_GET_NUM)).getContent()) {
            Picword picword = picwordDao.findOne(home.getObjId());
            if (picword != null) {
                picwordEntityResponseList.add(new PicwordEntityResponse(picword, userToken));
            }
        }
        responseContent.putData("picwordList", picwordEntityResponseList);
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/picword/newest", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String newest(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap, PicwordRequest picwordRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(picwordRequest.getToken());
        List<PicwordEntityResponse> picwordEntityResponseList = new ArrayList<PicwordEntityResponse>();
        Slice<Picword> picwordSlice = picwordDao.findByPermissionOrderByCreatedTimeDesc(
                Permission.Open.getType(), new PageRequest(picwordRequest.getPage(), Constant.PAGE_NUM_LARGE));
        for (Picword picword : picwordSlice.getContent()) {
            picwordEntityResponseList.add(new PicwordEntityResponse(picword, userToken));
        }
        responseContent.putData("picwordList", picwordEntityResponseList);
        return JSON.toJSONString(responseContent);
    }

}
