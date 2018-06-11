package com.wg.bookorder.webservice;

import com.alibaba.fastjson.JSON;
import com.wg.bookorder.domain.BookOrder;
import com.wg.bookorder.domain.OrderEvaluate;
import com.wg.bookorder.model.request.BookOrderRequest;
import com.wg.bookorder.model.response.BookOrderDetailResponse;
import com.wg.bookorder.model.response.BookOrderEvaluate;
import com.wg.bookorder.model.response.BookOrderInfoResponse;
import com.wg.bookorder.model.response.BookOrderTradeInfoResponse;
import com.wg.bookorder.utils.BookOrderUtils;
import com.wg.common.Constant;
import com.wg.common.Enum.bookorder.FinishType;
import com.wg.common.Enum.bookorder.OrderStatus;
import com.wg.common.Enum.bookorder.OrderType;
import com.wg.common.Enum.bookorder.UserOrderType;
import com.wg.common.Enum.common.ResponseCode;
import com.wg.common.Enum.message.DealStatus;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.useraccount.AccLogType;
import com.wg.common.PropConfig;
import com.wg.common.ResponseContent;
import com.wg.common.utils.DecimalUtils;
import com.wg.common.utils.LogUtils;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.common.utils.dbutils.AddUtils;
import com.wg.user.domain.UserToken;
import com.wg.useraccount.utils.UserAccUtils;
import com.wg.userbook.domain.UserBook;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.Enum.bookorder.OrderStatus.Hand;
import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2017/2/16 0016.
 */
@Controller
public class BookOrderController {

    //个人订单
    @Transactional
    @RequestMapping(value = "/api/bookOrder/personal", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String personal(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        int userOrderType = bookOrderRequest.getUserOrderType();
        int filterType = bookOrderRequest.getFilterType();
        int page = bookOrderRequest.getPage();
        List<BookOrderInfoResponse> bookOrderInfoResponseList = new ArrayList<BookOrderInfoResponse>();
        if ((userOrderType == UserOrderType.All.getType()
                || userOrderType == UserOrderType.LeaseIn.getType()
                || userOrderType == UserOrderType.LeaseOut.getType()
                || userOrderType == UserOrderType.BuyIn.getType()
                || userOrderType == UserOrderType.SaleOut.getType())) {
            List<BookOrder> bookOrderList = BookOrderUtils.getBookOrders(userToken, userOrderType, filterType, page);
            for (BookOrder bookOrder : bookOrderList) {
                bookOrderInfoResponseList.add(new BookOrderInfoResponse(bookOrder, userToken));
            }
            responseContent.putData("bookOrderList", bookOrderInfoResponseList);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //订单详情
    @Transactional
    @RequestMapping(value = "/api/bookOrder/detail", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        if (bookOrder != null) {
            if (userToken.getUserId() == bookOrder.getUserId() || userToken.getUserId() == bookOrder.getOwnerId()) {
                if (userToken.getUserId() == bookOrder.getUserId() && bookOrder.isDelete() == false
                        || userToken.getUserId() == bookOrder.getOwnerId() && bookOrder.isOwnerDelete() == false) {

                    responseContent.putData("bookOrderDetail", new BookOrderDetailResponse(bookOrder, userToken));

                } else {
                    responseContent.update(ResponseCode.BOOK_ORDER_IS_DELETE);
                }
            } else {
                responseContent.update(ResponseCode.ILLEGAL_USER);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //借阅、购买书籍信息
    @Transactional
    @RequestMapping(value = "/api/bookOrder/tradeInfo", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String tradeInfo(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        long userBookId = bookOrderRequest.getUserBookId();
        UserBook userBook = userBookDao.findOne(userBookId);
        if (userBook != null) {
            responseContent.putData("relationType", Utils.getPermission(userToken, userBook.getUserId()));
            responseContent.putData("tradeInfo", new BookOrderTradeInfoResponse(userBook));
        } else {
            responseContent.update(ResponseCode.USERBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookOrder/evaluate", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String evaluate(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        long userBookId = bookOrderRequest.getUserBookId();
        UserBook userBook = userBookDao.findOne(userBookId);
        if (userBook != null) {
            Pageable pageable = new PageRequest(bookOrderRequest.getPage(), Constant.PAGE_NUM_LARGE);
            Slice<OrderEvaluate> orderEvaluateSlice = orderEvaluateDao.findByOwnerIdAndBookIdOrderByCreatedTimeDesc(userBook.getUserId(), userBook.getBookId(), pageable);
            List<BookOrderEvaluate> bookOrderEvaluateList = new ArrayList<BookOrderEvaluate>();
            for (OrderEvaluate orderEvaluate : orderEvaluateSlice.getContent()) {
                bookOrderEvaluateList.add(new BookOrderEvaluate(orderEvaluate));
            }
            responseContent.putData("evaluateList", bookOrderEvaluateList);
        } else {
            responseContent.update(ResponseCode.USERBOOK_NOT_EXIST);
        }
        return JSON.toJSONString(responseContent);
    }

    @Transactional
    @RequestMapping(value = "/api/bookOrder/addEvaluate", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String addEvaluate(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        String content = bookOrderRequest.getContent();
        double rating = bookOrderRequest.getRating();
        if (bookOrder != null && content != null) {
            AddUtils.addOrderEvaluate(userToken, bookOrder, content, rating);
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //申请订单
    @Transactional
    @RequestMapping(value = "/api/bookOrder/applyOrder", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String applyOrder(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        long userBookId = bookOrderRequest.getUserBookId();
        int orderType = bookOrderRequest.getOrderType();
        int leaseDay = bookOrderRequest.getLeaseDay();
        UserBook userBook = userBookDao.findOne(userBookId);
        if (userBook != null && userToken.getUserId() != userBook.getUserId()
                && (orderType == OrderType.Lease.getType() && userBook.isLease() && leaseDay > 0
                || orderType == OrderType.Sale.getType() && userBook.isSale())) {
            if (orderType == OrderType.Sale.getType() ||
                    bookOrderDao.countByUserIdAndOrderTypeAndFinishType(userToken.getUserId(), OrderType.Lease.getType(), FinishType.Ongoing.getType())
                            < BookOrderUtils.MAX_ONGOING_LEASE_NUM) {
                BigDecimal payGold = BookOrderUtils.generOrderPayGold(userBook, orderType, leaseDay);
                if (UserAccUtils.frozenAccGold(userToken.getUserId(), payGold)) {
                    BookOrder bookOrder = AddUtils.addBookOrder(userToken, userBook, leaseDay, orderType, payGold);
                    //log forzen
                    LogUtils.logAcc(AccLogType.OrderGoldFrozen, null, bookOrder, null, null);
                    responseContent.putData("orderId", bookOrder.getOrderId());
                } else {
                    responseContent.update(ResponseCode.REMAINGOLD_NOT_ENOUGH);
                }
            } else {
                responseContent.update(ResponseCode.EXCEE_MAX_LEASE_ORDER);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //申请人取消订单
    @Transactional
    @RequestMapping(value = "/api/bookOrder/cancle", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String cancle(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        if (bookOrder != null) {
            if (bookOrder.getOrderStatus() == OrderStatus.Deal.getStatus()) {
                if (userToken.getUserId() == bookOrder.getUserId()) {
                    bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Finish.getStatus(), FinishType.Cancle.getType(), null);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_ORDER_ERROR_DEAL);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //拥有者处理订单
    @Transactional
    @RequestMapping(value = "/api/bookOrder/dealApply", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String dealApply(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        int dealStatus = bookOrderRequest.getDealStatus();
        String reason = bookOrderRequest.getReason();
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        if (bookOrder != null && (dealStatus == DealStatus.Accept.getStatus() || dealStatus == DealStatus.Refuse.getStatus())) {
            if (bookOrder.getOrderStatus() == OrderStatus.Deal.getStatus()) {
                if (userToken.getUserId() == bookOrder.getOwnerId()) {
                    BookOrderUtils.dealNewOrder(bookOrder, dealStatus, reason);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_ORDER_ERROR_DEAL);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }


    //验证交书
    @Transactional
    @RequestMapping(value = "/api/bookOrder/handBook", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String handBook(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        String bookHandCode = bookOrderRequest.getBookHandCode();
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        if (bookOrder != null && StringUtils.isNotBlank(bookHandCode)) {
            if (bookOrder.getOrderStatus() == Hand.getStatus()) {
                if (userToken.getUserId() == bookOrder.getOwnerId()) {
                    if (bookOrder.getBookHandCode().equals(bookHandCode)) {
                        if (bookOrder.getOrderType() == OrderType.Lease.getType()) {
                            bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Using.getStatus(), FinishType.Ongoing.getType(), TimeUtils.getCurrentDate());
                        } else {
                            bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Finish.getStatus(), FinishType.Normal.getType(), TimeUtils.getCurrentDate());
                        }
                        //notify applyer confirm handbook
                        AddUtils.addUserMessage(
                                bookOrder.getUserId(),
                                PropConfig.OFFICER_USERID,
                                null,
                                bookOrder.getOrderType() == OrderType.Lease.getType() ?
                                        MessageType.Li_Ar_ConfirmHandBook.getType() : MessageType.Bi_Ar_ConfirmHandBook.getType(),
                                bookOrder.getOrderId(),
                                DealStatus.NotDeal.getStatus(),
                                ReadStatus.NotRead.getStatus());
                    } else {
                        responseContent.update(ResponseCode.INVALID_HAND_CODE);
                    }
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_ORDER_ERROR_DEAL);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //申请还书
    @Transactional
    @RequestMapping(value = "/api/bookOrder/applyReturn", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String applyReturn(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        if (bookOrder != null && bookOrder.getOrderType() == OrderType.Lease.getType()) {
            if (bookOrder.getOrderStatus() == OrderStatus.Using.getStatus()) {
                if (userToken.getUserId() == bookOrder.getUserId()) {
                    if (TimeUtils.getCurrentDate().after(TimeUtils.getModifyDate(bookOrder.getHandBookTime(), bookOrder.getLeaseDay(), -BookOrderUtils.HALF_DAY_HOUR, null, null))) {
                        bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Return.getStatus(), FinishType.Ongoing.getType(), TimeUtils.getCurrentDate());
                        //notify applyer return book
                        AddUtils.addUserMessage(
                                bookOrder.getUserId(),
                                PropConfig.OFFICER_USERID,
                                null,
                                MessageType.Li_Ar_ReturnBook.getType(),
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
                    } else {
                        responseContent.update(ResponseCode.IN_MIN_USING_TIME);
                    }
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_ORDER_ERROR_DEAL);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //延期还书
    @Transactional
    @RequestMapping(value = "/api/bookOrder/delayReturn", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String delayReturn(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        int delayDay = bookOrderRequest.getDelayDay();
        if (bookOrder != null && delayDay > 0 && bookOrder.getDelayDay() == 0) {
            if (bookOrder.getOrderStatus() == OrderStatus.Return.getStatus()) {
                if (userToken.getUserId() == bookOrder.getOwnerId()) {
                    bookOrder.setDelayDay(delayDay);
                    bookOrder.setNotifyTime(TimeUtils.getModifyDate(
                            bookOrder.getApplyReturnTime(), bookOrder.getDelayDay() + BookOrderUtils.RETURN_BOOK_DELAY_DAY - 2, null, null, null));
                    bookOrder = bookOrderDao.save(bookOrder);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_ORDER_ERROR_DEAL);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //申述
    @Transactional
    @RequestMapping(value = "/api/bookOrder/complaint", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String complaint(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        String reason = bookOrderRequest.getReason();
        if (bookOrder != null && bookOrder.isComplaint() == false && StringUtils.isNotBlank(reason)) {
            if (bookOrder.getOrderStatus() == OrderStatus.Return.getStatus()) {
                if (userToken.getUserId() == bookOrder.getUserId() || userToken.getUserId() == bookOrder.getOwnerId()) {
                    bookOrder = BookOrderUtils.complaintOrder(bookOrder, userToken, reason);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_ORDER_ERROR_DEAL);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //还书
    @Transactional
    @RequestMapping(value = "/api/bookOrder/returnBook", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String returnBook(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        String bookReturnCode = bookOrderRequest.getBookReturnCode();
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        BigDecimal brokenGold = DecimalUtils.newDecimal(bookOrderRequest.getBrokenGold() != -1 ? bookOrderRequest.getBrokenGold() : 0);
        if (bookOrder != null && StringUtils.isNotBlank(bookReturnCode) && bookOrder.getOrderType() == OrderType.Lease.getType()) {
            if (bookOrder.getOrderStatus() == OrderStatus.Return.getStatus()) {
                if (userToken.getUserId() == bookOrder.getUserId()) {
                    if (bookOrder.getBookReturnCode().equals(bookReturnCode)) {
                        if (!DecimalUtils.lessThanZero(brokenGold) && bookOrder.getEvaluation().compareTo(brokenGold) > -1) {
                            bookOrder.setBrokenGold(brokenGold);
                            bookOrder = BookOrderUtils.controller(bookOrder, OrderStatus.Finish.getStatus(), FinishType.Normal.getType(), TimeUtils.getCurrentDate());
                            //notify owner appler confirm returnbook
                            AddUtils.addUserMessage(
                                    bookOrder.getOwnerId(),
                                    PropConfig.OFFICER_USERID,
                                    null,
                                    MessageType.Lo_Or_ConfirmReturnBook.getType(),
                                    bookOrder.getOrderId(),
                                    DealStatus.NotDeal.getStatus(),
                                    ReadStatus.NotRead.getStatus());
                        } else {
                            responseContent.update(ResponseCode.UNREASONABLE_BROKENGOLD);
                        }
                    } else {
                        responseContent.update(ResponseCode.INVALID_RETURN_CODE);
                    }
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_ORDER_ERROR_DEAL);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

    //删除订单
    @Transactional
    @RequestMapping(value = "/api/bookOrder/delete", produces = "application/json;charset=utf-8"/*,method = RequestMethod.POST*/)
    @ResponseBody
    public String delete(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, BookOrderRequest bookOrderRequest) {
        ResponseContent responseContent = new ResponseContent();
        UserToken userToken = userTokenDao.findByToken(bookOrderRequest.getToken());
        BookOrder bookOrder = bookOrderDao.findOne(bookOrderRequest.getOrderId());
        if (bookOrder != null) {
            if (bookOrder.getOrderStatus() == OrderStatus.Finish.getStatus()) {
                if (userToken.getUserId() == bookOrder.getUserId()) {
                    bookOrder.setDelete(true);
                    bookOrder = bookOrderDao.save(bookOrder);
                } else if (userToken.getUserId() == bookOrder.getOwnerId()) {
                    bookOrder.setOwnerDelete(true);
                    bookOrder = bookOrderDao.save(bookOrder);
                } else {
                    responseContent.update(ResponseCode.ILLEGAL_USER);
                }
            } else {
                responseContent.update(ResponseCode.BOOK_ORDER_ERROR_DEAL);
            }
        } else {
            responseContent.update(ResponseCode.ERROR_PARAM);
        }
        return JSON.toJSONString(responseContent);
    }

}
