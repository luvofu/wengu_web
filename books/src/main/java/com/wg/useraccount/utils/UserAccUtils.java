package com.wg.useraccount.utils;

import com.wg.bookorder.domain.BookOrder;
import com.wg.bookorder.utils.BookOrderUtils;
import com.wg.common.Enum.bookorder.FinishType;
import com.wg.common.Enum.bookorder.OrderStatus;
import com.wg.common.Enum.bookorder.OrderType;
import com.wg.common.Enum.bookorder.UserOrderType;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.user.PlatformType;
import com.wg.common.Enum.useraccount.*;
import com.wg.common.PropConfig;
import com.wg.common.utils.DecimalUtils;
import com.wg.common.utils.LogUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.user.domain.UserToken;
import com.wg.useraccount.domain.ThirdPay;
import com.wg.useraccount.domain.UserAccount;
import com.wg.useraccount.domain.UserBill;
import com.wg.useraccount.utils.alipay.AlipayConfig;
import com.wg.useraccount.utils.wexinpay.WexinPayConfig;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2017/3/12 0012.
 */
public class UserAccUtils {
    //充值描述
    public static String chargeDes(UserBill userBill) {
        String des = "文芽-充值" + DecimalUtils.goldFormat(userBill.getBillGold()) + "元";
        return des;
    }

    //提现描述
    public static String fundDes(UserBill userBill, int payType) {
        if (payType == PayType.Alipay.getType()) {
            String detail = "";
            return detail;
        } else if (payType == PayType.Weixin.getType()) {
            return "文芽-提现" + DecimalUtils.goldFormat(userBill.getBillGold()) + "元";
        }
        return null;
    }

    //账单描述
    public static String billDes(UserBill userBill, BookOrder bookOrder) {
        String des = "";
        if (userBill.getBillType() == BillType.Income.getType() || userBill.getBillType() == BillType.Cost.getType()) {
            String opt = UserOrderType.getTypeInfo(OrderType.getUserOrderType(bookOrder.getOrderType(), userBill.getUserId() == bookOrder.getUserId()));
            String title = bookDao.findOne(bookOrder.getBookId()).getTitle();
            des = opt + ":《" + title + "》";
        } else if (userBill.getBillType() == BillType.Charge.getType()) {
            des = "文芽-充值" + DecimalUtils.goldFormat(userBill.getBillGold()) + "元";
            return des;
        } else if (userBill.getBillType() == BillType.Withdraw.getType()) {
            des = "文芽-提现" + DecimalUtils.goldFormat(userBill.getBillGold()) + "元";
            return des;
        }
        return des;
    }

    //生成交易号
    public static String tradeNumber(String platform, int billType, int payTye) {
        String time = TimeUtils.formatDate(TimeUtils.getCurrentDate(), TimeUtils.YYYYMMDDHHMMSSSSS);//时间
        String type = PlatformType.getInfo(platform) + "" + billType + "" + payTye;//平台类型，账单类型，支付类型
        String randcode = Utils.generRandCode(3);
        String tradeNumber = time + type + randcode;
        return tradeNumber;
    }

    //充值下单
    public static UserBill chargePlaceBill(UserToken userToken, int payType, BigDecimal gold) {
        if ((payType == PayType.Alipay.getType() || payType == PayType.Weixin.getType()) && DecimalUtils.greaterThanZero(gold)) {
            //add charge bill
            UserBill userBill = AddUtils.addUserBill(userToken, BillType.Charge.getType(), payType, gold);
            return userBill;
        }
        return null;
    }

    //提现下单
    public static UserBill withdrawPlaceBill(UserToken userToken, ThirdPay thirdPay, BigDecimal gold) {
        if (thirdPay != null && DecimalUtils.greaterThanZero(gold)) {
            if (withdrawAcc(userToken, gold)) {
                //add withdraw bill
                UserBill userBill = AddUtils.addUserBill(userToken, BillType.Withdraw.getType(), thirdPay, gold);
                //log withdraw
                LogUtils.logAcc(AccLogType.WithdrawAccount, userBill, null, null, null);
                return userBill;
            }
        }
        return null;
    }

    //用户提现
    public static void fund(UserBill userBill) {
        if (userBill != null && userBill.getBillType() == BillType.Withdraw.getType()
                && userBill.getDealStatus() == DealStatus.InDeal.getStatus()) {
            if (userBill.getPayType() == PayType.Alipay.getType()) {
                String status = AlipayUtils.fundQuery(userBill);
                if (status.equals(AlipayConfig.ORDER_NOT_EXIST)) {
                    AlipayUtils.fund(userBill);
                } else if (status.equals(AlipayConfig.FUND_STATUS_SUCCESS)) {
                    userBill = withdrawBill(userBill);
                } else if (AlipayUtils.fundFailed(status)) {
                    //roll back
                    userBill = rollBackBill(userBill, status);
                }
            } else if (userBill.getPayType() == PayType.Weixin.getType()) {
                String status = WexinPayUtils.fundQuery(userBill);
                if (status.equals(WexinPayConfig.NOT_FOUND)) {
                    WexinPayUtils.fund(userBill);
                } else if (status.equals(WexinPayConfig.FUND_STATUS_SUCCESS)) {
                    //withdraw bill
                    userBill = withdrawBill(userBill);
                } else if (WexinPayUtils.fundFailed(status)) {
                    //roll back
                    userBill = rollBackBill(userBill, status);
                }
            }
        }
    }

    //充值,修改账单
    public static UserBill chargeBill(UserBill userBill, Map<String, String> map) {
        String serialNumber = null;
        Date payTime = null;
        if (userBill.getPayType() == PayType.Alipay.getType() && map != null) {
            serialNumber = map.get("trade_no");
            payTime = TimeUtils.parseDate(map.get("gmt_payment"), TimeUtils.YYYY_MM_DD_HH_MM_SS);
        } else if (userBill.getPayType() == PayType.Weixin.getType() && map != null) {
            serialNumber = map.get("transaction_id");
            payTime = TimeUtils.parseDate(map.get("time_end"), TimeUtils.YYYYMMDDHHMMSS);
        }
        if (userBill != null && serialNumber != null && payTime != null) {
            if (chargeAcc(userBill.getUserId(), userBill.getBillGold())) {
                //modify userbill
                userBill.setSerialNumber(serialNumber);
                userBill.setDealTime(payTime);
                userBill.setShow(true);
                userBill.setDealStatus(DealStatus.Success.getStatus());
                userBill = userBillDao.save(userBill);
                //notify user
                AddUtils.addUserMessage(
                        userBill.getUserId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.ACC_CHARGE_SUCCESS.getType(),
                        userBill.getBillId(),
                        com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus()
                );
                //log charge
                LogUtils.logAcc(AccLogType.ChargeAccount, userBill, null, null, null);
                return userBill;
            }
        }
        return null;
    }

    //提现成功，修改账单
    public static UserBill withdrawBill(UserBill userBill) {
        //修改交易状态，账单
        if (userBill != null) {
            userBill.setDealStatus(DealStatus.Success.getStatus());
            userBill.setDealTime(TimeUtils.getCurrentDate());
            userBill.settTime(null);
            userBill = userBillDao.save(userBill);
            //notify user
            AddUtils.addUserMessage(
                    userBill.getUserId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    MessageType.ACC_WITHDRAW_SUCCESS.getType(),
                    userBill.getBillId(),
                    com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus()
            );
        }
        return userBill;
    }

    //订单结算，创建账单
    public static void orderBill(BookOrder bookOrder) {
        if (bookOrder.getOrderStatus() == OrderStatus.Finish.getStatus() && DecimalUtils.greaterThanZero(bookOrder.getOrderPayGold())) {
            if (bookOrder.getFinishType() == FinishType.Normal.getType() || bookOrder.getFinishType() == FinishType.BreakContract.getType()) {
                //appler frozen gold ,cost、owner income、transfer gold
                BigDecimal transferGold = BookOrderUtils.transferGold(bookOrder);
                //defrozon gold、transfer gold
                if (defrozenAccGold(bookOrder.getUserId(), bookOrder.getOrderPayGold()) && transferAcc(bookOrder.getUserId(), bookOrder.getOwnerId(), transferGold)) {
                    //gener appler bill
                    UserBill applerBill = AddUtils.addBill(bookOrder.getUserId(), BillType.Cost.getType(), bookOrder.getOrderNumber(), transferGold);
                    //gener owner bill
                    UserBill ownerBill = AddUtils.addBill(bookOrder.getOwnerId(), BillType.Income.getType(), bookOrder.getOrderNumber(), transferGold);
                    //notify appler & owner
                    AddUtils.addUserMessage(
                            applerBill.getUserId(),
                            PropConfig.OFFICER_USERID,
                            null,
                            MessageType.ACC_ORDER_APPLER_BILL.getType(),
                            applerBill.getBillId(),
                            com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                            ReadStatus.NotRead.getStatus()
                    );
                    AddUtils.addUserMessage(
                            ownerBill.getUserId(),
                            PropConfig.OFFICER_USERID,
                            null,
                            MessageType.ACC_ORDER_OWNER_BILL.getType(),
                            ownerBill.getBillId(),
                            com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                            ReadStatus.NotRead.getStatus()
                    );
                    //log deforzen
                    LogUtils.logAcc(AccLogType.OrderGoldDefrozen, null, bookOrder, null, null);
                    //log cost
                    LogUtils.logAcc(AccLogType.OrderCostTrans, applerBill, null, null, null);
                    //log income
                    LogUtils.logAcc(AccLogType.OrderIncomeTrans, ownerBill, null, null, null);
                }
            } else if (bookOrder.getFinishType() == FinishType.Cancle.getType() || bookOrder.getFinishType() == FinishType.Refuse.getType()) {
                if (UserAccUtils.defrozenAccGold(bookOrder.getUserId(), bookOrder.getOrderPayGold())) {
                    //log deforzen
                    LogUtils.logAcc(AccLogType.OrderGoldDefrozen, null, bookOrder, null, null);
                }
            }
        }
    }

    //账单失败回滚
    public static UserBill rollBackBill(UserBill userBill, String desCode) {
        if (userBill != null) {
            if (userBill.getBillType() == BillType.Withdraw.getType() && StringUtils.isNotBlank(desCode)) {
                //账单提现失败，回滚
                if (rollBack(userBill)) {
                    userBill.setDealStatus(DealStatus.Failed.getStatus());
                    userBill.setDealTime(TimeUtils.getCurrentDate());
                    userBill.settTime(null);
                    userBill.setDesCode(desCode);
                    userBill.setRemark("FUND_FAILED_ROLLBACK");
                    userBill = userBillDao.save(userBill);
                    //notify user
                    AddUtils.addUserMessage(
                            userBill.getUserId(),
                            PropConfig.OFFICER_USERID,
                            null,
                            MessageType.ACC_WITHDRAW_ROLLBAK.getType(),
                            userBill.getBillId(),
                            com.wg.common.Enum.message.DealStatus.NotDeal.getStatus(),
                            ReadStatus.NotRead.getStatus()
                    );
                    //long rollback
                    LogUtils.logAcc(AccLogType.RollbackAccount, userBill, null, null, null);
                }
            }
        }
        return userBill;
    }

    //des code info
    public static String desCodeInfo(String desCode) {
        String desInfo = null;
        if (desCode != null) {
            try {
                desInfo = AccDesCode.valueOf(desCode).getDes();
            } catch (IllegalArgumentException e) {
                desInfo = AccDesCode.TRYAGAIN_OR_CALLWENYA.getDes();
            }
        }
        return desInfo;
    }

    //钱包金额变动操作
    //充值
    public static boolean chargeAcc(long userId, BigDecimal gold) {
        UserAccount userAccount = userAccountDao.findByUserId(userId);
        if (userAccount != null && DecimalUtils.greaterThanZero(gold)) {
            userAccount.setTotalGold(userAccount.getTotalGold().add(gold));
            userAccount = userAccountDao.save(userAccount);
            return true;
        } else {
            return false;
        }
    }

    //提现
    public static boolean withdrawAcc(UserToken userToken, BigDecimal gold) {
        UserAccount userAccount = userAccountDao.findByUserId(userToken.getUserId());
        BigDecimal usefullGold = userAccount.getTotalGold().subtract(userAccount.getFrozenGold());
        if (userAccount != null && DecimalUtils.greaterThanZero(gold) && usefullGold.compareTo(gold) > -1) {
            userAccount.setTotalGold(userAccount.getTotalGold().subtract(gold));
            userAccount = userAccountDao.save(userAccount);
            return true;
        } else {
            return false;
        }
    }

    //冻结
    public static boolean frozenAccGold(long userId, BigDecimal frozenGold) {
        UserAccount userAccount = userAccountDao.findByUserId(userId);
        if (userAccount != null && DecimalUtils.greaterThanZero(frozenGold) && userAccount.getTotalGold().subtract(userAccount.getFrozenGold()).compareTo(frozenGold) > -1) {
            userAccount.setFrozenGold(userAccount.getFrozenGold().add(frozenGold));
            userAccount = userAccountDao.save(userAccount);
            return true;
        } else {
            return false;
        }
    }

    //解冻
    public static boolean defrozenAccGold(long userId, BigDecimal frozenGold) {
        UserAccount userAccount = userAccountDao.findByUserId(userId);
        if (userAccount != null && DecimalUtils.greaterThanZero(frozenGold) && userAccount.getFrozenGold().compareTo(frozenGold) > -1) {
            userAccount.setFrozenGold(userAccount.getFrozenGold().subtract(frozenGold));
            userAccount = userAccountDao.save(userAccount);
            return true;
        } else {
            return false;
        }
    }

    //转账
    public static boolean transferAcc(long fromId, long toId, BigDecimal transGold) {
        UserAccount fromAcc = userAccountDao.findByUserId(fromId);
        UserAccount toAcc = userAccountDao.findByUserId(toId);
        if (DecimalUtils.greaterThanZero(transGold) && fromAcc.getTotalGold().compareTo(transGold) > -1) {
            fromAcc.setTotalGold(fromAcc.getTotalGold().subtract(transGold));
            toAcc.setTotalGold(toAcc.getTotalGold().add(transGold));
            fromAcc = userAccountDao.save(fromAcc);
            toAcc = userAccountDao.save(toAcc);
            return true;
        }
        return false;
    }

    //回滚
    public static boolean rollBack(UserBill userBill) {
        if (userBill.getBillType() == BillType.Withdraw.getType()) {
            UserAccount userAccount = userAccountDao.findByUserId(userBill.getUserId());
            if (userAccount != null && !DecimalUtils.lessThanZero(userBill.getBillGold())) {
                userAccount.setTotalGold(userAccount.getTotalGold().add(userBill.getBillGold()));
                userAccount = userAccountDao.save(userAccount);
                return true;
            }
        }
        return false;
    }

}
