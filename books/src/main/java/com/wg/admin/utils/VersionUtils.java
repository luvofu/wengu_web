package com.wg.admin.utils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

import static com.wg.common.utils.Utils.logger;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
@Component
public class VersionUtils {
    public static HashMap<String, Integer> versionMap = new HashMap<String, Integer>();

    @PostConstruct
    void init() {
        logger.info("VersionUtils construct");
        versionMap.put(null, 100);
        versionMap.put("1.0", 100);
        versionMap.put("1.0.1", 101);
        versionMap.put("1.0.2", 102);
        versionMap.put("1.0.3", 103);
        versionMap.put("1.1.0", 110);
        versionMap.put("1.1.1", 111);
        versionMap.put("1.1.2", 112);
        versionMap.put("1.1.3", 113);
        versionMap.put("2.0.0", 200);
        versionMap.put("2.0.1", 201);
        versionMap.put("2.0.2", 202);
    }
//
//    public static void version110() {
//        //set note sort
//        List<Notebook> notebookList = notebookDao.findAll();
//        for (Notebook notebook : notebookList) {
//            List<Note> noteList = noteDao.findByNotebookIdOrderByCreatedTimeAsc(notebook.getNotebookId());
//            int sort = 0;
//            for (Note note : noteList) {
//                sort += 10;
//                note.setSort(sort);
//                note = noteDao.save(note);
//            }
//        }
//    }
//
//    public static void version112() {
//        int page = 0;
//        List<UserInfo> userInfoList = userInfoDao.findAll(new PageRequest(page, 4 * Constant.PAGE_NUM_LARGE)).getContent();
//        while (userInfoList.size() > 0) {
//            //update concern & fan num
//            for (UserInfo userInfo : userInfoList) {
//                userInfo.setConcernNum(userFriendDao.countByUserId(userInfo.getUserId()));
//                userInfo.setFanNum(userFriendDao.countByFriendId(userInfo.getUserId()));
//                userInfo = UpdateUtils.updateUserInfo(userInfo);
//            }
//            //sign im
//            IMUtils.sign(userInfoList);
//            page += 1;
//            userInfoList = userInfoDao.findAll(new PageRequest(page, 4 * Constant.PAGE_NUM_LARGE)).getContent();
//        }
//    }
//
//    public static void version200() {
//        //set user bookNum all
//        int page = 0;
//        List<UserInfo> userInfoList = userInfoDao.findAll(new PageRequest(page, 4 * Constant.PAGE_NUM_LARGE)).getContent();
//        while (userInfoList.size() > 0) {
//            //update booknum
//            for (UserInfo userInfo : userInfoList) {
//                userInfo.setBookNum(userBookDao.countByUserId(userInfo.getUserId()));
//                userInfo.setNotebookNum(notebookDao.countByUserId(userInfo.getUserId()));
//                userInfo.setBookSheetNum(bookSheetDao.countByUserId(userInfo.getUserId()));
//                userInfo = UpdateUtils.updateUserInfo(userInfo);
//                //new account
//                UserAccount userAccount = AddUtils.addUserAccount(userInfo.getUserId());
//                //sign im
//                IMUtils.sign(userInfoList);
//            }
//            page += 1;
//            userInfoList = userInfoDao.findAll(new PageRequest(page, 4 * Constant.PAGE_NUM_LARGE)).getContent();
//        }
//        //set userbook default set
//        page = 0;
//        List<UserBook> userBookList = userBookDao.findAll(new PageRequest(page, 5 * Constant.PAGE_NUM_LARGE)).getContent();
//        while (userBookList.size() > 0) {
//            //update booknum
//            for (UserBook userBook : userBookList) {
//                userBook.setLease(false);
//                userBook.setSale(false);
//                userBook.setEvaluation(UserBookUtils.getEvaluation(bookDao.findOne(userBook.getBookId())));
//                userBook.setDayRentGold(UserBookUtils.DEFAULT_DAY_RENT_GOLD);
//                userBook = UpdateUtils.updateUserBook(userBook);
//            }
//            page += 1;
//            userBookList = userBookDao.findAll(new PageRequest(page, 5 * Constant.PAGE_NUM_LARGE)).getContent();
//        }
//    }
//
//    public static void version201() {
//        //update lease sale num
//        for (UserInfo userInfo : userInfoDao.findAll()) {
//            userInfo.setLeaseNum(userBookDao.countByUserIdAndIsLease(userInfo.getUserId(), true));
//            userInfo.setSaleNum(userBookDao.countByUserIdAndIsSale(userInfo.getUserId(), true));
//            userInfo = UpdateUtils.updateUserInfo(userInfo);
//        }
//        //update message_push platform
//        List<MessagePush> messagePushList = messagePushDao.findAll();
//        for (MessagePush messagePush : messagePushList) {
//            messagePush.setPlatform("iOS");
//        }
//        messagePushDao.save(messagePushList);
//    }
//
//    public static void version202() {
//        //update lease sale num
//        userInfoDao.setDefaultOcrNum(UserUtils.OCR_DAY_NUM);
//        userInfoDao.setDefaultExportNum(UserUtils.EXPORT_MOTH_NUM);
//    }

    public static void version203() {
        //update category
    }
}
