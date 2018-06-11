package com.wg.interceptor;

import com.alibaba.fastjson.JSON;
import com.wg.common.Constant;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.userTokenDao;

/**
 * Created by wzhonggo on 9/13/2016.
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("token");
        String uri = request.getRequestURI();
        logger.info("ACCESS:" + uri + ", PARAM:" + JSON.toJSONString(request.getParameterMap()));
        if (token != null) {
            if (userTokenDao.findByToken(token) == null) {
                request.getRequestDispatcher("/api/user/exceptAcc").forward(request, response);
                return false;
            }
        } else if (!Constant.TOKENLESS_API_SET.contains(uri)) {
            request.getRequestDispatcher("/api/user/notlogin").forward(request, response);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }
}
