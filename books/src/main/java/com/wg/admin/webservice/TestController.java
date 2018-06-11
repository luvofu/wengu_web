package com.wg.admin.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.admin.dao.TestDao;
import com.wg.admin.model.request.TestRequest;
import com.wg.common.ResponseContent;
import com.wg.common.utils.export.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2017/3/3 0003.
 */
@Controller
public class TestController {
    @Autowired
    TestDao testDao;

    @Transactional
    @RequestMapping(value = "api/test/test", produces = "application/json;charset=utf-8"/*, method = RequestMethod.POST*/)
    @ResponseBody
    public String test(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, TestRequest testRequest) {
        ResponseContent responseContent = new ResponseContent();
        try {
            PdfUtils.createPdf();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return JSON.toJSONString(responseContent);
    }
}
