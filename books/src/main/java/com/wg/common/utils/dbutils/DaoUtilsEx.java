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
public class DaoUtilsEx {
    @Autowired
    public AdminDao adminDao;

    @Autowired
    public UserInfoDao userInfoDao;
    @Autowired
    public UserLoginDao userLoginDao;
    @Autowired
    public UserTokenDao userTokenDao;
    @Autowired
    public ValidDao validDao;

    @Autowired
    public BookDao bookDao;
    @Autowired
    public BookKeywordDao bookKeywordDao;
    @Autowired
    public TempBookCoverDao tempBookCoverDao;
    @Autowired
    public BookRemarkDao bookRemarkDao;

    @Autowired
    public BookGroupDao bookGroupDao;
    @Autowired
    public GroupCommentDao groupCommentDao;
    @Autowired
    public GroupReplyDao groupReplyDao;

    @Autowired
    public BookCircleDynamicDao bookCircleDynamicDao;
    @Autowired
    public BookCircleReplyDao bookCircleReplyDao;

    @Autowired
    public UserBookDao userBookDao;
    @Autowired
    public UserCategoryDao userCategoryDao;
    @Autowired
    public UserBookmarkDao userBookmarkDao;
    @Autowired
    public BookCheckDao bookCheckDao;

    @Autowired
    public BookSheetDao bookSheetDao;
    @Autowired
    public BookSheetBookDao bookSheetBookDao;

    @Autowired
    public NotebookDao notebookDao;
    @Autowired
    public NoteDao noteDao;
    @Autowired
    public StorylineDao storylineDao;

    @Autowired
    public PicwordDao picwordDao;

    @Autowired
    public UserFriendDao userFriendDao;
    @Autowired
    public UserMessageDao userMessageDao;
    @Autowired
    public UserCollectionDao userCollectionDao;
    @Autowired
    public UserGoodDao userGoodDao;
    @Autowired
    public FeedbackDao feedbackDao;
    @Autowired
    public MessagePushDao messagePushDao;

    @Autowired
    public HomeDao homeDao;
    @Autowired
    public HotBookDao hotBookDao;

    @Autowired
    public TestDao testDao;

    @Autowired
    public NewsDao newsDao;
    @Autowired
    public TempNewsDao tempNewsDao;

    @Autowired
    public TempSolrIndexDao tempSolrIndexDao;

    @Autowired
    public BookOrderDao bookOrderDao;
    @Autowired
    public UserBillDao userBillDao;
    @Autowired
    public OrderEvaluateDao orderEvaluateDao;
    @Autowired
    public ThirdPayDao thirdPayDao;
    @Autowired
    public UserAccountDao userAccountDao;

    @Autowired
    public CommunityDao communityDao;
    @Autowired
    public CommMemberDao commMemberDao;
    @Autowired
    public CommBookDao commBookDao;

    public DaoUtilsEx() {
        daoUtilsEx = this;
    }

    public static DaoUtilsEx daoUtilsEx;
}
