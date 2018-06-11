package com.wg.bookorder.utils;

import com.wg.bookorder.domain.BookOrder;
import com.wg.common.Constant;
import com.wg.common.Enum.bookorder.*;
import com.wg.common.Enum.message.DealStatus;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.SMsgTemplate;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.user.PlatformType;
import com.wg.common.PropConfig;
import com.wg.common.utils.DecimalUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.message.utils.SMsgUtils;
import com.wg.user.domain.UserToken;
import com.wg.useraccount.utils.UserAccUtils;
import com.wg.userbook.domain.UserBook;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.bookOrderDao;


/**
 * Created by Administrator on 2017/2/16 0016.
 */
public class BookOrderUtils {
    public static final int MAX_ONGOING_LEASE_NUM = 2;//最大进行中借阅数
    public static final int DEAL_APPLY_DELAY_DAY = 3;//申请处理时间
    public static final int HAND_BOOK_DELAY_DAY = 5;//交书时间
    public static final int EARLY_NOTIFY_APPLY_RETURN_H = 12;//申请还书提前半天
    public static final int LEASE_DELAY_DAY = 7;//延时借阅时间
    public static final int RETURN_BOOK_DELAY_DAY = 3;//还书时间
    public static final int HALF_DAY_HOUR = 12;//申请还书提前时间

    public static List<BookOrder> getBookOrders(UserToken userToken, int userOrderType, int filterType, int page) {
        Pageable pageable = new PageRequest(page, Constant.PAGE_NUM_LARGE);
        Slice<BookOrder> bookOrderSlice = null;
        int orderType = UserOrderType.getOrderType(userOrderType);
        if (userOrderType == UserOrderType.LeaseIn.getType() || userOrderType == UserOrderType.BuyIn.getType()) {
            if (filterType == FilterType.All.getType()) {
                bookOrderSlice = bookOrderDao.findByUserIdAndOrderTypeAndIsDeleteOrderByUpdatedTimeDesc(
                        userToken.getUserId(), orderType, false, pageable);
            } else if (filterType == FilterType.Ongoing.getType()) {
                bookOrderSlice = bookOrderDao.findByUserIdAndOrderTypeAndFinishTypeAndIsDeleteOrderByUpdatedTimeDesc(
                        userToken.getUserId(), orderType, FinishType.Ongoing.getType(), false, pageable);
            } else if (filterType == FilterType.Finish.getType()) {
                bookOrderSlice = bookOrderDao.findByUserIdAndOrderTypeAndFinishTypeNotAndIsDeleteOrderByUpdatedTimeDesc(
                        userToken.getUserId(), orderType, FinishType.Ongoing.getType(), false, pageable);
            }
        } else if (userOrderType == UserOrderType.LeaseOut.getType() || userOrderType == UserOrderType.SaleOut.getType()) {
            if (filterType == FilterType.All.getType()) {
                bookOrderSlice = bookOrderDao.findByOwnerIdAndOrderTypeAndIsOwnerDeleteOrderByUpdatedTimeDesc(
                        userToken.getUserId(), orderType, false, pageable);
            } else if (filterType == FilterType.Ongoing.getType()) {
                bookOrderSlice = bookOrderDao.findByOwnerIdAndOrderTypeAndFinishTypeAndIsOwnerDeleteOrderByUpdatedTimeDesc(
                        userToken.getUserId(), orderType, FinishType.Ongoing.getType(), false, pageable);
            } else if (filterType == FilterType.Finish.getType()) {
                bookOrderSlice = bookOrderDao.findByOwnerIdAndOrderTypeAndFinishTypeNotAndIsOwnerDeleteOrderByUpdatedTimeDesc(
                        userToken.getUserId(), orderType, FinishType.Ongoing.getType(), false, pageable);
            }
        } else {
            bookOrderSlice = bookOrderDao.findByUserIdAndIsDeleteOrOwnerIdAndIsOwnerDeleteOrderByUpdatedTimeDesc(
                    userToken.getUserId(), false, userToken.getUserId(), false, pageable);
        }
        if (bookOrderSlice != null) {
            return bookOrderSlice.getContent();
        } else {
            return new ArrayList<BookOrder>();
        }
    }

    //生成订单号
    public static String generOrderNumber(UserToken userToken, int orderType) {
        String orderNumber = "";
        orderNumber += TimeUtils.formatDate(TimeUtils.getCurrentDate(), TimeUtils.YYDDMMHHMMSSSSS);//time
        orderNumber += PlatformType.valueOf(userToken.getPlatform()).getType();//platform
        orderNumber += orderType;//order type
        orderNumber += Utils.generRandCode(3);//rand code
        return orderNumber;
    }

    //结算订单支付金
    public static BigDecimal generOrderPayGold(UserBook userBook, int orderType, int leaseDay) {
        if (orderType == OrderType.Lease.getType()) {
            return DecimalUtils.newDecimal(userBook.getEvaluation() + userBook.getDayRentGold() * (leaseDay + LEASE_DELAY_DAY));
        } else {
            return DecimalUtils.newDecimal(userBook.getEvaluation());
        }
    }

    //计算实际借阅天数
    public static int generRealLeaseDay(BookOrder bookOrder) {
        int realLeaseDay = 0;
        if (bookOrder.getApplyReturnTime() != null && bookOrder.getHandBookTime() != null) {
            double leaseDay = (bookOrder.getApplyReturnTime().getTime() - bookOrder.getHandBookTime().getTime()) * 1.0 / (24 * 60 * 60 * 1000);
            if (leaseDay > bookOrder.getLeaseDay() + LEASE_DELAY_DAY) {
                leaseDay = bookOrder.getLeaseDay() + LEASE_DELAY_DAY;
            }
            realLeaseDay = (DecimalUtils.newDecimal(leaseDay).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
        }
        return realLeaseDay;
    }

    //计算租金
    public static BigDecimal generRentGold(BookOrder bookOrder) {
        BigDecimal rentGold = BigDecimal.ZERO;
        if (bookOrder.getOrderType() == OrderType.Lease.getType() && bookOrder.getOrderStatus() == OrderStatus.Finish.getStatus()
                && (bookOrder.getFinishType() == FinishType.Normal.getType() || bookOrder.getFinishType() == FinishType.BreakContract.getType())) {
            rentGold = bookOrder.getDayRentGold().multiply(DecimalUtils.newDecimal(generRealLeaseDay(bookOrder)));
        }
        return rentGold;
    }

    //订单结算，返还金
    public static BigDecimal transferGold(BookOrder bookOrder) {
        BigDecimal returnGold = bookOrder.getOrderPayGold();
        if (bookOrder.getFinishType() == FinishType.Normal.getType()) {
            if (bookOrder.getOrderType() == OrderType.Lease.getType()) {
                returnGold = bookOrder.getOrderPayGold().subtract(bookOrder.getTotalRentGold()).subtract(bookOrder.getBrokenGold());
            } else {
                returnGold = BigDecimal.ZERO;
            }
        } else if (bookOrder.getFinishType() == FinishType.BreakContract.getType()) {
            returnGold = bookOrder.getOrderPayGold().subtract(bookOrder.getTotalRentGold()).subtract(bookOrder.getBreakGold());
        }
        return bookOrder.getOrderPayGold().subtract(returnGold);
    }

    //提醒信息
    public static String generNotifyInfo(BookOrder bookOrder, UserToken userToken) {
        int orderStatus = bookOrder.getOrderStatus();
        Date currTime = TimeUtils.getCurrentDate();
        String notifyInfo = "";
        boolean isAppler = bookOrder.getUserId() == userToken.getUserId();
        if (orderStatus == OrderStatus.Deal.getStatus()) {//处理中
            // 超时未处理
            notifyInfo = (isAppler ? "对方同意剩余：" : "处理订单剩余：") + desTimeLeft(currTime, TimeUtils.getModifyDate(bookOrder.getCreatedTime(), DEAL_APPLY_DELAY_DAY, null, null, null));
        } else if (orderStatus == OrderStatus.Hand.getStatus()) {//交书中
            //超时未交书
            notifyInfo = (isAppler ? "取书剩余：" : "交书剩余：") + desTimeLeft(currTime, TimeUtils.getModifyDate(bookOrder.getAgreeApplyTime(), HAND_BOOK_DELAY_DAY, null, null, null));
        } else if (orderStatus == OrderStatus.Using.getStatus()) {//借阅中
            if (currTime.before(TimeUtils.getModifyDate(bookOrder.getHandBookTime(), bookOrder.getLeaseDay(), null, null, null))) {
                //延期阅读
                notifyInfo = "借阅剩余：" + desTimeLeft(currTime, TimeUtils.getModifyDate(bookOrder.getHandBookTime(), bookOrder.getLeaseDay(), null, null, null));
            } else {
                //超时申请还书
                notifyInfo = "延时借阅剩余：" + desTimeLeft(currTime, TimeUtils.getModifyDate(bookOrder.getHandBookTime(), bookOrder.getLeaseDay() + LEASE_DELAY_DAY, null, null, null));
            }
        } else if (orderStatus == OrderStatus.Return.getStatus()) {//还书中
            if (bookOrder.isComplaint() == false) {
                //超时还书
                notifyInfo = (isAppler ? "还书剩余：" : "收书剩余：") + desTimeLeft(currTime, TimeUtils.getModifyDate(bookOrder.getApplyReturnTime(), bookOrder.getDelayDay() + RETURN_BOOK_DELAY_DAY, null, null, null));
            } else {
                notifyInfo = "订单申诉中，请友好协商";
            }
        } else if (orderStatus == OrderStatus.Finish.getStatus()) {//完成
            if (bookOrder.getFinishType() == FinishType.Normal.getType()) {
                if (isAppler) {
                    if (bookOrder.getOrderType() == OrderType.Lease.getType()) {
                        notifyInfo = "借书花费：" + DecimalUtils.goldFormat(bookOrder.getTotalRentGold().add(bookOrder.getBreakGold()).add(bookOrder.getBrokenGold())) + "元";
                    } else {
                        notifyInfo = "购买花费：" + DecimalUtils.goldFormat(bookOrder.getOrderPayGold()) + "元";
                    }
                } else {
                    if (bookOrder.getOrderType() == OrderType.Lease.getType()) {
                        notifyInfo = "借出收益：" + DecimalUtils.goldFormat(bookOrder.getTotalRentGold().add(bookOrder.getBreakGold()).add(bookOrder.getBrokenGold())) + "元";
                    } else {
                        notifyInfo = "卖出收益：" + DecimalUtils.goldFormat(bookOrder.getOrderPayGold()) + "元";
                    }
                }
            }
        }
        return notifyInfo;
    }

    //描述时间
    public static String desTimeLeft(Date from, Date to) {
        long totalMinute = (to.getTime() - from.getTime()) / (60 * 1000);
        long totalHours = totalMinute / 60;
        long minute = totalMinute % 60;
        long hours = totalHours % 24;
        long day = totalHours / 24;
        String des = "";
        if (day > 0) {
            des += day + "天" + hours + "小时";
        } else if (hours > 0) {
            des += hours + "小时" + minute + "分钟";
        } else if (minute > 0) {
            des += minute + "分钟";
        } else {
            des += "1分钟";
        }
        return des;
    }

    //订单申诉
    public static BookOrder complaintOrder(BookOrder bookOrder, UserToken userToken, String reason) {
        bookOrder.setComplaint(true);
        bookOrder.setNotifyTime(null);
        if (bookOrder.getUserId() == userToken.getUserId()) {
            bookOrder.setRemark("借阅人申诉：" + reason);
        } else {
            bookOrder.setRemark("藏书人申诉：" + reason);
        }
        bookOrder = bookOrderDao.save(bookOrder);
        if (bookOrder.getUserId() == userToken.getUserId()) {
            //notify owner complaint
            AddUtils.addUserMessage(
                    bookOrder.getOwnerId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    MessageType.Lo_Or_Complaint.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        } else {
            //notify applyer complaint
            AddUtils.addUserMessage(
                    bookOrder.getUserId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    MessageType.Li_Ar_Complaint.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
        return bookOrder;
    }

    //订单状态转换,记录操作时间,设置通知时间
    public static BookOrder controller(BookOrder bookOrder, int orderStatus, int finishType, Date dealTime) {
        if (bookOrder.getFinishType() == FinishType.Ongoing.getType()) {
            if (orderStatus == OrderStatus.Deal.getStatus()) {//处理中
                bookOrder.setOrderStatus(OrderStatus.Deal.getStatus());
                bookOrder.setCreatedTime(dealTime);
                //超时未处理
                bookOrder.setNotifyTime(TimeUtils.getModifyDate(bookOrder.getCreatedTime(), DEAL_APPLY_DELAY_DAY, null, null, null));
            } else if (orderStatus == OrderStatus.Hand.getStatus()) {//交书中
                bookOrder.setOrderStatus(OrderStatus.Hand.getStatus());
                bookOrder.setAgreeApplyTime(dealTime);
                bookOrder.setBookHandCode(Utils.generRandCode(4));
                //超时未交书
                bookOrder.setNotifyTime(TimeUtils.getModifyDate(bookOrder.getAgreeApplyTime(), HAND_BOOK_DELAY_DAY, null, null, null));
            } else if (orderStatus == OrderStatus.Using.getStatus()) {//借阅中
                if (orderStatus != bookOrder.getOrderStatus()) {
                    bookOrder.setOrderStatus(OrderStatus.Using.getStatus());
                    bookOrder.setHandBookTime(dealTime);
                    //临近延时借阅
                    bookOrder.setNotifyTime(TimeUtils.getModifyDate(bookOrder.getHandBookTime(), bookOrder.getLeaseDay(), -EARLY_NOTIFY_APPLY_RETURN_H, null, null));
                } else {
                    //超时申请还书
                    bookOrder.setNotifyTime(TimeUtils.getModifyDate(bookOrder.getHandBookTime(), bookOrder.getLeaseDay() + LEASE_DELAY_DAY, null, null, null));
                }
            } else if (orderStatus == OrderStatus.Return.getStatus()) {//还书中
                if (orderStatus != bookOrder.getOrderStatus()) {
                    bookOrder.setOrderStatus(OrderStatus.Return.getStatus());
                    bookOrder.setApplyReturnTime(dealTime);
                    bookOrder.setBookReturnCode(Utils.generRandCode(4));
                    //临近还书
                    bookOrder.setNotifyTime(TimeUtils.getModifyDate(bookOrder.getApplyReturnTime(), bookOrder.getDelayDay() + RETURN_BOOK_DELAY_DAY - 2, null, null, null));
                } else {
                    //超时还书
                    bookOrder.setNotifyTime(TimeUtils.getModifyDate(bookOrder.getApplyReturnTime(), bookOrder.getDelayDay() + RETURN_BOOK_DELAY_DAY, null, null, null));
                }
            } else if (orderStatus == OrderStatus.Finish.getStatus()) {//完成
                bookOrder.setOrderStatus(OrderStatus.Finish.getStatus());
                bookOrder.setFinishType(finishType);
                bookOrder.setNotifyTime(null);
                if (bookOrder.getOrderType() == OrderType.Lease.getType()
                        && (bookOrder.getFinishType() == FinishType.Normal.getType()
                        || bookOrder.getFinishType() == FinishType.BreakContract.getType())) {
                    bookOrder.setTotalRentGold(generRentGold(bookOrder));
                    bookOrder.setReturnBookTime(dealTime);
                } else if (bookOrder.getOrderType() == OrderType.Sale.getType()
                        && bookOrder.getFinishType() == FinishType.Normal.getType()) {
                    bookOrder.setHandBookTime(dealTime);
                }
            }
            bookOrder = bookOrderDao.save(bookOrder);
            if (orderStatus == OrderStatus.Finish.getStatus()) {
                UserAccUtils.orderBill(bookOrder);
            }
        }
        return bookOrder;
    }

    //处理新订单
    public static void dealNewOrder(BookOrder bookOrder, int dealStatus, String reason) {
        if (dealStatus == DealStatus.Accept.getStatus()) {
            bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Hand.getStatus(), FinishType.Ongoing.getType(), TimeUtils.getCurrentDate());
            //notify applyer take book
            AddUtils.addUserMessage(
                    bookOrder.getUserId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    bookOrder.getOrderType() == OrderType.Lease.getType() ?
                            MessageType.Li_Ar_TakeBook.getType() : MessageType.Bi_Ar_TakeBook.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
            //notify owner hand book
            AddUtils.addUserMessage(
                    bookOrder.getOwnerId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    bookOrder.getOrderType() == OrderType.Lease.getType() ?
                            MessageType.Lo_Or_HandBook.getType() : MessageType.So_Or_HandBook.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        } else {
            bookOrder.setRemark("藏书人拒绝" + (StringUtils.isNotBlank(reason) ? "：" + reason : ""));
            bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Finish.getStatus(), FinishType.Refuse.getType(), null);
            //notify applyer order be refused
            AddUtils.addUserMessage(
                    bookOrder.getUserId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    bookOrder.getOrderType() == OrderType.Lease.getType() ?
                            MessageType.Li_Ar_refuseApply.getType() : MessageType.Bi_Ar_refuseApply.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
    }

    //超时处理
    public static void testDealOrder(Date currDate) {
        for (BookOrder bookOrder : bookOrderDao.findByOrderStatusAndNotifyTimeLessThan(OrderStatus.Deal.getStatus(), currDate)) {
            bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Finish.getStatus(), FinishType.Cancle.getType(), null);
            //notify applyer
            AddUtils.addUserMessage(
                    bookOrder.getUserId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    bookOrder.getOrderType() == OrderType.Lease.getType() ?
                            MessageType.Li_Ar_OvertimeDeal_Close.getType() : MessageType.Bi_Ar_OvertimeDeal_Close.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
            //notify owner
            AddUtils.addUserMessage(
                    bookOrder.getOwnerId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    bookOrder.getOrderType() == OrderType.Lease.getType() ?
                            MessageType.Lo_Or_OvertimeDeal_Close.getType() : MessageType.So_Or_OvertimeDeal_Close.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
    }

    //超时交书
    public static void testHandBook(Date currDate) {
        for (BookOrder bookOrder : bookOrderDao.findByOrderStatusAndNotifyTimeLessThan(OrderStatus.Hand.getStatus(), currDate)) {
            bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Finish.getStatus(), FinishType.Cancle.getType(), null);
            //notify applyer
            AddUtils.addUserMessage(
                    bookOrder.getUserId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    bookOrder.getOrderType() == OrderType.Lease.getType() ?
                            MessageType.Li_Ar_OvertimeTakeBook_Close.getType() : MessageType.Bi_Ar_OvertimeTakeBook_Close.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
            //notify owner
            AddUtils.addUserMessage(
                    bookOrder.getOwnerId(),
                    PropConfig.OFFICER_USERID,
                    null,
                    bookOrder.getOrderType() == OrderType.Lease.getType() ?
                            MessageType.Lo_Or_OvertimeHandBook_Close.getType() : MessageType.So_Or_OvertimeHandBook_Close.getType(),
                    bookOrder.getOrderId(),
                    DealStatus.NotDeal.getStatus(),
                    ReadStatus.NotRead.getStatus());
        }
    }

    //临近延时借阅、超时借阅
    public static void testUsingBook(Date currDate) {
        for (BookOrder bookOrder : bookOrderDao.findByOrderStatusAndNotifyTimeLessThan(OrderStatus.Using.getStatus(), currDate)) {
            if (currDate.before(TimeUtils.getModifyDate(bookOrder.getHandBookTime(), bookOrder.getLeaseDay() + LEASE_DELAY_DAY, null, null, null))) {//临近延时借阅
                bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Using.getStatus(), FinishType.Ongoing.getType(), TimeUtils.getCurrentDate());
                //msg appler
                SMsgUtils.sendMsg(null, SMsgTemplate.ApplyReturnNB, bookOrder);
                //notify applyer apply return
                AddUtils.addUserMessage(
                        bookOrder.getUserId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.Li_Ar_ApplyReturn.getType(),
                        bookOrder.getOrderId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            } else {//超时借阅、到达最大借阅
                bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Return.getStatus(), FinishType.Ongoing.getType(),
                        TimeUtils.getModifyDate(bookOrder.getHandBookTime(), bookOrder.getLeaseDay() + LEASE_DELAY_DAY, null, null, null));
                //notify applyer return book
                AddUtils.addUserMessage(
                        bookOrder.getUserId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.Li_Ar_ReturnBookSB.getType(),
                        bookOrder.getOrderId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
                //notify owner take book
                AddUtils.addUserMessage(
                        bookOrder.getOwnerId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.Lo_Or_TakeBook.getType(),
                        bookOrder.getOrderId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            }
        }
    }

    //临近还书、超时还书
    public static void testReturnBook(Date currDate) {
        for (BookOrder bookOrder : bookOrderDao.findByOrderStatusAndNotifyTimeLessThanAndIsComplaint(OrderStatus.Return.getStatus(), currDate, false)) {
            if (currDate.before(TimeUtils.getModifyDate(bookOrder.getApplyReturnTime(), bookOrder.getDelayDay() + RETURN_BOOK_DELAY_DAY, null, null, null))) {//临近还书
                bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Return.getStatus(), FinishType.Ongoing.getType(), TimeUtils.getCurrentDate());
                //msg appler
                SMsgUtils.sendMsg(null, SMsgTemplate.ReturnBookNB, bookOrder);
                //notify applyer return book
                AddUtils.addUserMessage(
                        bookOrder.getUserId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.Li_Ar_ReturnBookNB.getType(),
                        bookOrder.getOrderId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
                //notify owner take book
                AddUtils.addUserMessage(
                        bookOrder.getOwnerId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.Lo_Or_TakeBookNB.getType(),
                        bookOrder.getOrderId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            } else {//超时还书
                bookOrder.setBreakGold(bookOrder.getEvaluation());
                bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Finish.getStatus(), FinishType.BreakContract.getType(), null);
                //notify applyer return book
                AddUtils.addUserMessage(
                        bookOrder.getUserId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.Li_Ar_OvertimeReturnBook_Close.getType(),
                        bookOrder.getOrderId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
                //notify owner take book
                AddUtils.addUserMessage(
                        bookOrder.getOwnerId(),
                        PropConfig.OFFICER_USERID,
                        null,
                        MessageType.Lo_Or_OvertimeTakeBook_Close.getType(),
                        bookOrder.getOrderId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            }
        }
    }

}
