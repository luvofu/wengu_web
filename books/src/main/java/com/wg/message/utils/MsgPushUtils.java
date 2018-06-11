package com.wg.message.utils;

import com.alibaba.fastjson.JSON;
import com.gexin.rp.sdk.base.IAliasResult;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.AbstractTemplate;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.wg.common.PropConfig;
import com.wg.message.domain.MessagePush;
import com.wg.message.model.response.MessageResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.Utils.logger;
import static com.wg.common.utils.dbutils.DaoUtils.messagePushDao;

/**
 * Created by Administrator on 2016/11/20 0020.
 */
public class MsgPushUtils {
    private static int dayOfflineExpireTime = 3;

    public static boolean updateAliasBind(final String clientId, final long userId, String platform) {
        if (StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(platform)) {
            //unbind old alias
            updateAliasUnbind(userId, platform);
            //update db
            MessagePush messagePush = messagePushDao.findByClientId(clientId);
            if (messagePush == null) {
                messagePush = new MessagePush();
                messagePush.setClientId(clientId);
            }
            messagePush.setUserId(userId);
            messagePush.setPlatform(platform);
            messagePush = messagePushDao.save(messagePush);
            new Thread() {
                @Override
                public void run() {
                    //bind alias
                    IGtPush push = new IGtPush(PropConfig.MP_HOST, PropConfig.MP_APPKEY, PropConfig.MP_SECRET);
                    IAliasResult bindSCid = push.bindAlias(PropConfig.MP_APPID, String.valueOf(userId), clientId);
                }
            }.start();
            return true;
        }
        return false;
    }

    public static boolean updateAliasUnbind(final long userId, String platform) {
        if (StringUtils.isNotBlank(platform)) {
            //update db
            MessagePush messagePush = messagePushDao.findByUserIdAndPlatform(userId, platform);
            if (messagePush != null) {
                final String clientId = messagePush.getClientId();
                messagePushDao.delete(messagePush);
                //unbind alias
                new Thread() {
                    @Override
                    public void run() {
                        IGtPush push = new IGtPush(PropConfig.MP_HOST, PropConfig.MP_APPKEY, PropConfig.MP_SECRET);
                        IAliasResult iAliasResult = push.unBindAlias(PropConfig.MP_APPID, String.valueOf(userId), clientId);
                    }
                }.start();
                return true;
            }
        }
        return false;
    }

    public static boolean pushToSingle(AbstractTemplate template, long userId) {
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        message.setOfflineExpireTime(dayOfflineExpireTime * 24 * 3600 * 1000);
        message.setData(template);
        message.setPushNetWorkType(0);

        Target target = new Target();
        target.setAppId(PropConfig.MP_APPID);
        target.setAlias(String.valueOf(userId));

        IGtPush push = new IGtPush(PropConfig.MP_HOST, PropConfig.MP_APPKEY, PropConfig.MP_SECRET);
        IPushResult iPushResult;
        try {
            iPushResult = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            logger.error(e.getMessage());
            iPushResult = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (iPushResult == null) {
            return false;
        }
        return true;
    }

    public static boolean pushToApp(AbstractTemplate template) {
        AppMessage message = new AppMessage();
        message.setData(template);
        List<String> appIds = new ArrayList<String>();
        appIds.add(PropConfig.MP_APPID);
        message.setAppIdList(appIds);
        message.setOffline(true);
        message.setOfflineExpireTime(1000 * 600);
        IGtPush push = new IGtPush(PropConfig.MP_HOST, PropConfig.MP_APPKEY, PropConfig.MP_SECRET);
        IPushResult ret = null;
        try {
            ret = push.pushMessageToApp(message);
        } catch (RequestException e) {
            logger.error(e.getMessage());
        }
        if (ret != null) {
            return true;
        }
        return false;
    }

    public static NotificationTemplate getNotificationTemplate(String text, String content) {
        NotificationTemplate template = new NotificationTemplate();
        template.setAppId(PropConfig.MP_APPID);
        template.setAppkey(PropConfig.MP_APPKEY);
        template.setTitle("文芽");
        template.setText(text);
        template.setLogo("icon.png");
        template.setLogoUrl("http://www.uvnya.com/img/icon/lpic/icon.png");
        template.setIsRing(false);
        template.setIsVibrate(true);
        template.setIsClearable(true);
        template.setTransmissionType(2);
        template.setTransmissionContent(content);//json content
        return template;
    }

    public static LinkTemplate getLinkTemplate(String text, String content) {
        LinkTemplate template = new LinkTemplate();
        template.setAppId(PropConfig.MP_APPID);
        template.setAppkey(PropConfig.MP_APPKEY);
        template.setTitle("文芽");
        template.setText(text);
        template.setLogo("icon.png");
        template.setLogoUrl("http://www.uvnya.com/img/icon/lpic/icon.png");
        template.setIsRing(true);
        template.setIsVibrate(true);
        template.setIsClearable(true);
        template.setUrl("http://www.uvnya.com");
        return template;
    }

    public static TransmissionTemplate getTransmissionTemplate(MessageResponse messageResponse) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(PropConfig.MP_APPID);
        template.setAppkey(PropConfig.MP_APPKEY);
        template.setTransmissionContent(JSON.toJSONString(messageResponse));//json
        template.setTransmissionType(2);
        if (messageResponse.isNotify()) {
            APNPayload payload = new APNPayload();
            payload.setAutoBadge("+1");
            payload.addCustomMsg("messageId", messageResponse.getMessageId());
            payload.addCustomMsg("messageType", messageResponse.getMessageType());
            payload.addCustomMsg("messageObjId", messageResponse.getMessageObjId());
            payload.setSound("default");
            payload.setAlertMsg(new APNPayload.SimpleAlertMsg(
                    (messageResponse.getSendUserId() != PropConfig.OFFICER_USERID ? messageResponse.getNickname() + ":" : "")
                            + messageResponse.getContent()));
            template.setAPNInfo(payload);
        }
        return template;
    }
}
