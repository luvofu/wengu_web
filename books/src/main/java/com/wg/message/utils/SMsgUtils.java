package com.wg.message.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import com.wg.bookorder.domain.BookOrder;
import com.wg.common.Enum.bookorder.OrderType;
import com.wg.common.Enum.bookorder.UserOrderType;
import com.wg.common.Enum.message.SMsgTemplate;
import com.wg.user.domain.UserLogin;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.userLoginDao;

/**
 * Created by Administrator on 2016/9/20.
 */
public class SMsgUtils {
    public static final String HOST = "https://eco.taobao.com/router/rest";

    public static final String APPKEY = "23511537";

    public static final String SECRET = "e70b082da23ccd8ff0a4907332052021";

    public static final String SIGNNAME = "文芽";

    public static final String SMS_TYPE = "normal";

    private static TaobaoClient client = new DefaultTaobaoClient(HOST, APPKEY, SECRET);

    //send  /validecode/new order/apply return/return book/
    public static boolean sendMsg(String mobile, SMsgTemplate temp, Object exParam) {
        try {
            String tempCode = temp.getTempCode();
            Map<String, String> map = new HashMap<String, String>();
            if (SMsgTemplate.ValidCode.getTempCode().equals(tempCode)) {
                map.put("validcode", (String) exParam);
            } else if (SMsgTemplate.NewOrder.getTempCode().equals(tempCode)
                    || SMsgTemplate.ApplyReturnNB.getTempCode().equals(tempCode)
                    || SMsgTemplate.ReturnBookNB.getTempCode().equals(tempCode)) {
                BookOrder bookOrder = (BookOrder) exParam;
                map.put("nickname", bookOrder.getUserInfo().getNickname());
                if (SMsgTemplate.NewOrder.getTempCode().equals(tempCode)) {
                    map.put("type", UserOrderType.getTypeInfo(OrderType.getUserOrderType(bookOrder.getOrderType(), false)));
                    map.put("title", bookOrder.getBook().getTitle());
                }
                UserLogin userLogin = userLoginDao.findByUserId(bookOrder.getUserId());
                mobile = userLogin.getRegMobile();
            }
            return send(mobile, tempCode, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //request message to send valicode for user
    private static boolean send(String mobile, String templateCode, Map<String, String> param) throws ApiException {
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType(SMS_TYPE);
        req.setSmsFreeSignName(SIGNNAME);
        JSONObject jsonObject = new JSONObject();
        for (String key : param.keySet()) {
            if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(param.get(key))) {
                jsonObject.put(key, param.get(key));
            }
        }
        req.setSmsParam(JSON.toJSONString(jsonObject));
        req.setRecNum(mobile);
        req.setSmsTemplateCode(templateCode);
        AlibabaAliqinFcSmsNumSendResponse response = client.execute(req);
        if (response.getResult().getSuccess()) {
            return true;
        } else {
            logger.error("send smsg failed,error code:" + response.getErrorCode());
            return false;
        }
    }
}
