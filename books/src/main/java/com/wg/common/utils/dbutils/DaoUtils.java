package com.wg.common.utils.dbutils;

import com.wg.admin.dao.AdminDao;
import com.wg.admin.dao.TestDao;
import com.wg.book.dao.BookDao;
import com.wg.book.dao.BookKeywordDao;
import com.wg.book.dao.BookRemarkDao;
import com.wg.book.dao.TempBookCoverDao;
import com.wg.bookcircle.dao.BookCircleDynamicDao;
import com.wg.bookcircle.dao.BookCircleReplyDao;
import com.wg.bookgroup.dao.BookGroupDao;
import com.wg.bookgroup.dao.GroupCommentDao;
import com.wg.bookgroup.dao.GroupReplyDao;
import com.wg.bookorder.dao.BookOrderDao;
import com.wg.bookorder.dao.OrderEvaluateDao;
import com.wg.booksheet.dao.BookSheetBookDao;
import com.wg.booksheet.dao.BookSheetDao;
import com.wg.common.dao.HomeDao;
import com.wg.common.dao.HotBookDao;
import com.wg.community.dao.CommBookDao;
import com.wg.community.dao.CommMemberDao;
import com.wg.community.dao.CommunityDao;
import com.wg.message.dao.MessagePushDao;
import com.wg.message.dao.UserMessageDao;
import com.wg.news.dao.NewsDao;
import com.wg.news.dao.TempNewsDao;
import com.wg.notebook.dao.NoteDao;
import com.wg.notebook.dao.NotebookDao;
import com.wg.notebook.dao.StorylineDao;
import com.wg.picword.dao.PicwordDao;
import com.wg.solr.dao.TempSolrIndexDao;
import com.wg.user.dao.*;
import com.wg.useraccount.dao.ThirdPayDao;
import com.wg.useraccount.dao.UserAccountDao;
import com.wg.useraccount.dao.UserBillDao;
import com.wg.userbook.dao.BookCheckDao;
import com.wg.userbook.dao.UserBookDao;
import com.wg.userbook.dao.UserBookmarkDao;
import com.wg.userbook.dao.UserCategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2016/9/27.
 */

/**
 * 工具dao
 */
@Component
public class DaoUtils {
    public static AdminDao adminDao;

    public static UserInfoDao userInfoDao;
    public static UserLoginDao userLoginDao;
    public static UserTokenDao userTokenDao;
    public static ValidDao validDao;

    public static BookDao bookDao;
    public static BookKeywordDao bookKeywordDao;
    public static TempBookCoverDao tempBookCoverDao;
    public static BookRemarkDao bookRemarkDao;

    public static BookGroupDao bookGroupDao;
    public static GroupCommentDao groupCommentDao;
    public static GroupReplyDao groupReplyDao;

    public static BookCircleDynamicDao bookCircleDynamicDao;
    public static BookCircleReplyDao bookCircleReplyDao;

    public static UserBookDao userBookDao;
    public static UserCategoryDao userCategoryDao;
    public static UserBookmarkDao userBookmarkDao;
    public static BookCheckDao bookCheckDao;

    public static BookSheetDao bookSheetDao;
    public static BookSheetBookDao bookSheetBookDao;

    public static NotebookDao notebookDao;
    public static NoteDao noteDao;
    public static StorylineDao storylineDao;

    public static PicwordDao picwordDao;

    public static UserFriendDao userFriendDao;
    public static UserMessageDao userMessageDao;
    public static UserCollectionDao userCollectionDao;
    public static UserGoodDao userGoodDao;
    public static FeedbackDao feedbackDao;
    public static MessagePushDao messagePushDao;

    public static HomeDao homeDao;
    public static HotBookDao hotBookDao;

    public static TestDao testDao;

    public static NewsDao newsDao;
    public static TempNewsDao tempNewsDao;

    public static TempSolrIndexDao tempSolrIndexDao;

    public static BookOrderDao bookOrderDao;
    public static UserBillDao userBillDao;
    public static OrderEvaluateDao orderEvaluateDao;
    public static ThirdPayDao thirdPayDao;
    public static UserAccountDao userAccountDao;

    public static CommunityDao communityDao;
    public static CommMemberDao commMemberDao;
    public static CommBookDao commBookDao;

    @Autowired
    public DaoUtils(AdminDao adminDao, UserInfoDao userInfoDao, UserLoginDao userLoginDao, UserTokenDao userTokenDao, ValidDao validDao, BookDao bookDao, BookKeywordDao bookKeywordDao, TempBookCoverDao tempBookCoverDao, BookRemarkDao bookRemarkDao, BookGroupDao bookGroupDao, GroupCommentDao groupCommentDao, GroupReplyDao groupReplyDao, BookCircleDynamicDao bookCircleDynamicDao, BookCircleReplyDao bookCircleReplyDao, UserBookDao userBookDao, UserCategoryDao userCategoryDao, UserBookmarkDao userBookmarkDao, BookCheckDao bookCheckDao, BookSheetDao bookSheetDao, BookSheetBookDao bookSheetBookDao, NotebookDao notebookDao, NoteDao noteDao, StorylineDao storylineDao, PicwordDao picwordDao, UserFriendDao userFriendDao, UserMessageDao userMessageDao, UserCollectionDao userCollectionDao, UserGoodDao userGoodDao, FeedbackDao feedbackDao, MessagePushDao messagePushDao, HomeDao homeDao, HotBookDao hotBookDao, TestDao testDao, NewsDao newsDao, TempNewsDao tempNewsDao, TempSolrIndexDao tempSolrIndexDao, BookOrderDao bookOrderDao, UserBillDao userBillDao, OrderEvaluateDao orderEvaluateDao, ThirdPayDao thirdPayDao, UserAccountDao userAccountDao, CommunityDao communityDao, CommMemberDao commMemberDao, CommBookDao commBookDao
    ) {
        DaoUtils.adminDao = adminDao;
        DaoUtils.userInfoDao = userInfoDao;
        DaoUtils.userLoginDao = userLoginDao;
        DaoUtils.userTokenDao = userTokenDao;
        DaoUtils.validDao = validDao;
        DaoUtils.bookDao = bookDao;
        DaoUtils.bookKeywordDao = bookKeywordDao;
        DaoUtils.tempBookCoverDao = tempBookCoverDao;
        DaoUtils.bookRemarkDao = bookRemarkDao;
        DaoUtils.bookGroupDao = bookGroupDao;
        DaoUtils.groupCommentDao = groupCommentDao;
        DaoUtils.groupReplyDao = groupReplyDao;
        DaoUtils.bookCircleDynamicDao = bookCircleDynamicDao;
        DaoUtils.bookCircleReplyDao = bookCircleReplyDao;
        DaoUtils.userBookDao = userBookDao;
        DaoUtils.userCategoryDao = userCategoryDao;
        DaoUtils.userBookmarkDao = userBookmarkDao;
        DaoUtils.bookCheckDao = bookCheckDao;
        DaoUtils.bookSheetDao = bookSheetDao;
        DaoUtils.bookSheetBookDao = bookSheetBookDao;
        DaoUtils.notebookDao = notebookDao;
        DaoUtils.noteDao = noteDao;
        DaoUtils.storylineDao = storylineDao;
        DaoUtils.picwordDao = picwordDao;
        DaoUtils.userFriendDao = userFriendDao;
        DaoUtils.userMessageDao = userMessageDao;
        DaoUtils.userCollectionDao = userCollectionDao;
        DaoUtils.userGoodDao = userGoodDao;
        DaoUtils.feedbackDao = feedbackDao;
        DaoUtils.messagePushDao = messagePushDao;
        DaoUtils.homeDao = homeDao;
        DaoUtils.hotBookDao = hotBookDao;
        DaoUtils.testDao = testDao;
        DaoUtils.newsDao = newsDao;
        DaoUtils.tempNewsDao = tempNewsDao;
        DaoUtils.tempSolrIndexDao = tempSolrIndexDao;
        DaoUtils.bookOrderDao = bookOrderDao;
        DaoUtils.userBillDao = userBillDao;
        DaoUtils.orderEvaluateDao = orderEvaluateDao;
        DaoUtils.thirdPayDao = thirdPayDao;
        DaoUtils.userAccountDao = userAccountDao;
        DaoUtils.communityDao = communityDao;
        DaoUtils.commMemberDao = commMemberDao;
        DaoUtils.commBookDao = commBookDao;
    }
}
