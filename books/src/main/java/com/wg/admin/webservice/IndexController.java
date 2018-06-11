package com.wg.admin.webservice;

import com.wg.common.PropConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-25
 * Time: 上午10:01
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class IndexController {
    public static final String WG = "沪ICP备16034920号-1";
    public static final String WY = "沪ICP备16034920号-2";

    @RequestMapping(value = "/")
    public String registNetNo(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        if (PropConfig.SERVER_URL.contains("uvnya")) {
            modelMap.addAttribute("msg", WY);
        } else {
            modelMap.addAttribute("msg", WG);
        }
        String agent = request.getHeader("User-Agent");
        if (agent == null || agent.toLowerCase().contains("iphone") || agent.toLowerCase().contains("ipad")
                || agent.toLowerCase().contains("android") || agent.toLowerCase().contains("mobile")) {
            return "mobile_index";
        } else {
            return "index";
        }
    }
}
