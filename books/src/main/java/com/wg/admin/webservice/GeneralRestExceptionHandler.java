package com.wg.admin.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.common.ResponseContent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.wg.common.utils.Utils.logger;

/**
 * Created by wzhonggo on 8/4/2016.
 */

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GeneralRestExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(CustomNotFoundException.class)
    public void handleNotFoundException(final Exception exception) {
        logException(exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
//    @ExceptionHandler(CustomForbiddenException.class)
    public void handleForbiddenException(final Exception exception) {
        logException(exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleGeneralException(final Exception exception) {
        logException(exception);
        return JSON.toJSONString(getResponseContent(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class })
    @ResponseBody
    public ResponseContent handleBadRequestException(final Exception exception) {
        logException(exception);
        return getResponseContent(HttpStatus.BAD_REQUEST.value());
    }

    // Add more exception handling as needed…
    // …

    private void logException(final Exception exception) {
        exception.printStackTrace();
        logger.error(exception.getMessage());
    }

    private ResponseContent getResponseContent(int code){
        ResponseContent responseContent = new ResponseContent();
        responseContent.setCode(code);
        responseContent.setMsg("error");
        return  responseContent;
    }

}
