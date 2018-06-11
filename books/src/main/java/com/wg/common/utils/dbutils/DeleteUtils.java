package com.wg.common.utils.dbutils;

import com.wg.book.domain.Book;
import com.wg.bookcircle.domain.BookCircleDynamic;
import com.wg.bookcircle.domain.BookCircleReply;
import com.wg.bookgroup.domain.BookGroup;
import com.wg.bookgroup.domain.GroupComment;
import com.wg.bookgroup.domain.GroupReply;
import com.wg.booksheet.domain.BookSheet;
import com.wg.booksheet.domain.BookSheetBook;
import com.wg.common.Enum.bookgroup.ReplyType;
import com.wg.common.Enum.common.CollectType;
import com.wg.common.Enum.common.GoodType;
import com.wg.common.Enum.message.DealStatus;
import com.wg.common.Enum.message.MessageType;
import com.wg.common.Enum.message.ReadStatus;
import com.wg.common.Enum.solr.CallType;
import com.wg.common.Enum.solr.SolrType;
import com.wg.common.PropConfig;
import com.wg.common.utils.DecimalUtils;
import com.wg.community.domain.CommBook;
import com.wg.community.domain.CommMember;
import com.wg.community.domain.Community;
import com.wg.message.utils.IMUtils;
import com.wg.message.utils.MsgPushUtils;
import com.wg.message.utils.UserMsgUtils;
import com.wg.notebook.domain.Note;
import com.wg.notebook.domain.Notebook;
import com.wg.notebook.domain.Storyline;
import com.wg.picword.domain.Picword;
import com.wg.solr.utils.SolrUtils;
import com.wg.user.domain.*;
import com.wg.user.utils.UserCollectionUtils;
import com.wg.user.utils.UserGoodUtils;
import com.wg.user.utils.UserUtils;
import com.wg.useraccount.domain.UserAccount;
import com.wg.userbook.domain.UserBook;
import com.wg.userbook.domain.UserBookmark;
import com.wg.userbook.domain.UserCategory;
import com.wg.userbook.utils.UserBookUtils;

import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2016/11/3 0003.
 */

/**
 * 删除工具
 */
public class DeleteUtils {
    //删除社区评论
    public static void deleteCommunityComment(GroupComment groupComment) {
        if (groupComment != null) {
            for (GroupReply groupReply : groupReplyDao.findByCommentId(groupComment.getCommentId())) {
                //delete message
                UserMsgUtils.deleteUserMessage(MessageType.CommunityReply.getType(), groupReply.getReplyId());
                //delete reply
                groupReplyDao.delete(groupReply);
            }
            //delete comment good
            UserGoodUtils.deleteUserGood(groupComment.getCommentId(), GoodType.Comment.getType());
            //delete comment
            groupCommentDao.delete(groupComment);
            //update bookgroup commentnum
            BookGroup bookGroup = bookGroupDao.findOne(groupComment.getCommunityId());
            bookGroup.setCommentNum(groupCommentDao.countByCommunityId(bookGroup.getCommunityId()));
            bookGroup = UpdateUtils.updateBookCommunity(bookGroup);
        }
    }

    //删除社区回复
    public static void deleteCommunityReply(GroupReply groupReply) {
        if (groupReply != null) {
            deleteCommReply(groupReply);
            //update comment replynum
            GroupComment groupComment = groupCommentDao.findOne(groupReply.getCommentId());
            groupComment.setReplyNum(groupReplyDao.countByCommentId(groupComment.getCommentId()));
            groupComment = groupCommentDao.save(groupComment);
        }
    }

    public static void deleteCommReply(GroupReply groupReply) {
        if (groupReply != null) {
            long replyId = groupReply.getReplyId();
            //delete message
            UserMsgUtils.deleteUserMessage(MessageType.CommunityReply.getType(), replyId);
            //delete reply
            groupReplyDao.delete(replyId);
            for (GroupReply newGroupReply : groupReplyDao.findByReplyObjIdAndReplyType(replyId, ReplyType.Reply.getType())) {
                deleteCommReply(newGroupReply);
            }
        }
    }

    //删除书圈动态
    public static void deleteBookCircleDynamic(BookCircleDynamic bookCircleDynamic) {
        if (bookCircleDynamic != null) {
            for (BookCircleReply bookCircleReply : bookCircleReplyDao.findByDynamicId(bookCircleDynamic.getDynamicId())) {
                //delete message
                UserMsgUtils.deleteUserMessage(MessageType.BookCircleReply.getType(), bookCircleReply.getReplyId());
                //delete reply
                bookCircleReplyDao.delete(bookCircleReply);
            }
            //delete good
            UserGoodUtils.deleteUserGood(bookCircleDynamic.getDynamicId(), GoodType.Dynamic.getType());
            //delete dynamic
            bookCircleDynamicDao.delete(bookCircleDynamic);
        }
    }

    //删除书圈回复
    public static void deleteBookCircleReply(BookCircleReply bookCircleReply) {
        if (bookCircleReply != null) {
            deleteDynamicReply(bookCircleReply);
            // update dynamic replynum
            BookCircleDynamic bookCircleDynamic = bookCircleDynamicDao.findOne(bookCircleReply.getDynamicId());
            bookCircleDynamic.setReplyNum(bookCircleReplyDao.countByDynamicId(bookCircleDynamic.getDynamicId()));
            bookCircleDynamic = bookCircleDynamicDao.save(bookCircleDynamic);
        }
    }

    public static void deleteDynamicReply(BookCircleReply bookCircleReply) {
        if (bookCircleReply != null) {
            long replyId = bookCircleReply.getReplyId();
            //delete message
            UserMsgUtils.deleteUserMessage(MessageType.BookCircleReply.getType(), replyId);
            //delete reply
            bookCircleReplyDao.delete(replyId);
            for (BookCircleReply newBookCircleReply : bookCircleReplyDao.findByReplyObjIdAndReplyType(replyId, com.wg.common.Enum.bookcircle.ReplyType.Reply.getType())) {
                deleteDynamicReply(newBookCircleReply);
            }
        }
    }

    //新增点赞
    public static void deleteUserGood(UserGood userGood) {
        if (userGood != null) {
            long userId = userGood.getUserId();
            long goodId = userGood.getGoodId();
            int goodType = userGood.getGoodType();
            long goodObjId = userGood.getGoodObjId();
            //delete user good
            userGoodDao.delete(userGood);
            long goodNum = userGoodDao.countByGoodObjIdAndGoodType(goodObjId, goodType);
            if (goodType == GoodType.Comment.getType()) {
                GroupComment groupComment = groupCommentDao.findOne(goodObjId);
                if (groupComment != null) {
                    groupComment.setGoodNum(goodNum);
                    groupComment = groupCommentDao.save(groupComment);
                    UserMsgUtils.deleteUserMessage(groupComment.getUserId(), userId, MessageType.Good_Comment.getType(), goodId);
                }
            } else if (goodType == GoodType.Dynamic.getType()) {
                BookCircleDynamic bookCircleDynamic = bookCircleDynamicDao.findOne(goodObjId);
                if (bookCircleDynamic != null) {
                    bookCircleDynamic.setGoodNum(goodNum);
                    bookCircleDynamic = bookCircleDynamicDao.save(bookCircleDynamic);
                    UserMsgUtils.deleteUserMessage(bookCircleDynamic.getUserId(), userId, MessageType.Good_Dynamic.getType(), goodId);
                }
            } else if (goodType == GoodType.BookSheet.getType()) {
                BookSheet bookSheet = bookSheetDao.findOne(goodObjId);
                if (bookSheet != null) {
                    bookSheet.setGoodNum(goodNum);
                    bookSheet = UpdateUtils.updateBookSheet(bookSheet);
                    UserMsgUtils.deleteUserMessage(bookSheet.getUserId(), userId, MessageType.Good_BookSheet.getType(), goodId);
                }
            } else if (goodType == GoodType.Picword.getType()) {
                Picword picword = picwordDao.findOne(goodObjId);
                if (picword != null) {
                    picword.setGoodNum(goodNum);
                    picword = picwordDao.save(picword);
                    UserMsgUtils.deleteUserMessage(picword.getUserId(), userId, MessageType.Good_Picword.getType(), goodId);
                }
            }
        }
    }

    //删除用户收藏
    public static void deleteUserCollection(UserCollection userCollection) {
        if (userCollection != null) {
            long userId = userCollection.getUserId();
            long collectionId = userCollection.getCollectionId();
            int collectType = userCollection.getCollectType();
            long collectObjId = userCollection.getCollectObjId();
            //delete usercollection
            userCollectionDao.delete(userCollection);
            long collectionNum = userCollectionDao.countByCollectObjIdAndCollectType(collectObjId, collectType);
            if (collectType == CollectType.Book.getType()) {
                Book book = bookDao.findOne(collectObjId);
                if (book != null) {
                    book.setCollectionNum(collectionNum);
                    book = UpdateUtils.updateBook(book);
                }
            } else if (collectType == CollectType.BookSheet.getType()) {
                BookSheet bookSheet = bookSheetDao.findOne(collectObjId);
                if (bookSheet != null) {
                    bookSheet.setCollectionNum(collectionNum);
                    bookSheet = UpdateUtils.updateBookSheet(bookSheet);
                    UserMsgUtils.deleteUserMessage(bookSheet.getUserId(), userId, MessageType.Collect_BookSheet.getType(), collectionId);
                }
            } else if (collectType == CollectType.Picword.getType()) {
                Picword picword = picwordDao.findOne(collectObjId);
                if (picword != null) {
                    picword.setCollectionNum(collectionNum);
                    picword = picwordDao.save(picword);
                    UserMsgUtils.deleteUserMessage(picword.getUserId(), userId, MessageType.Collect_Picword.getType(), collectionId);
                }
            }
        }
    }

    //删除社区
    public static void deleteBookCommunity(BookGroup bookGroup) {
        if (bookGroup != null) {
            //delete bookgroup comment
            for (GroupComment groupComment : groupCommentDao.findByCommunityId(bookGroup.getCommunityId())) {
                deleteCommunityComment(groupComment);
            }
            //set solr delete index
            AddUtils.addTempSolrIndex(SolrType.BookGroup, bookGroup.getCommunityId());
            SolrUtils.tryDelete(SolrType.BookGroup.getType(), CallType.Api.getType());
            //delete bookgroup
            bookGroupDao.delete(bookGroup);
        }
    }

    //删除藏书
    public static void deleteUserBook(UserBook userBook) {
        if (userBook != null) {
            long userId = userBook.getUserId();
            UserCategory userCategory = userCategoryDao.findOne(userBook.getCategoryId());
            //delete comm book
            commBookDao.delete(commBookDao.findByUserIdAndBookId(userBook.getUserId(), userBook.getBookId()));
            //delete bookmark
            UserBookmark userBookmark = userBookmarkDao.findByUserBookId(userBook.getUserBookId());
            if (userBookmark != null) {
                userBookmarkDao.delete(userBookmark);
            }
            //create solr index to delete
            AddUtils.addTempSolrIndex(SolrType.UserBook, userBook.getUserBookId());
            SolrUtils.tryDelete(SolrType.UserBook.getType(), CallType.Api.getType());
            //delete userbook
            userBookDao.delete(userBook);
            //update category book num
            userCategory.setBookNum(userBookDao.countByCategoryId(userCategory.getCategoryId()));
            //update user info book num
            UserInfo userInfo = userInfoDao.findOne(userId);
            userInfo.setBookNum(userBookDao.countByUserId(userInfo.getUserId()));
            userInfo.setLeaseNum(userBookDao.countByUserIdAndIsLease(userInfo.getUserId(), true));
            userInfo.setSaleNum(userBookDao.countByUserIdAndIsSale(userInfo.getUserId(), true));
            userInfo = UpdateUtils.updateUserInfo(userInfo);
        }
    }

    //删除藏书list
    public static void deleteUserBook(List<UserBook> userBookList) {
        if (userBookList != null && userBookList.size() > 0) {
            long userId = userBookList.get(0).getUserId();
            for (UserBook userBook : userBookList) {
                //delete comm book
                commBookDao.delete(commBookDao.findByUserIdAndBookId(userBook.getUserId(), userBook.getBookId()));
                //delete bookmark
                UserBookmark userBookmark = userBookmarkDao.findByUserBookId(userBook.getUserBookId());
                if (userBookmark != null) {
                    userBookmarkDao.delete(userBookmark);
                }
                //create solr index to delete
                AddUtils.addTempSolrIndex(SolrType.UserBook, userBook.getUserBookId());
            }
            //request delete solr index
            SolrUtils.tryDelete(SolrType.UserBook.getType(), CallType.Api.getType());
            //delete userbooklist
            userBookDao.delete(userBookList);
            //update category statis
            UserBookUtils.updateUserCategory(userId);
            //update user info book num
            UserInfo userInfo = userInfoDao.findOne(userId);
            userInfo.setBookNum(userBookDao.countByUserId(userInfo.getUserId()));
            userInfo.setLeaseNum(userBookDao.countByUserIdAndIsLease(userInfo.getUserId(), true));
            userInfo.setSaleNum(userBookDao.countByUserIdAndIsSale(userInfo.getUserId(), true));
            userInfo = UpdateUtils.updateUserInfo(userInfo);
        }
    }

    //删除用户分类
    public static void deleteUserCategory(UserCategory userCategory) {
        if (userCategory != null) {
            //delete userbooklist
            List<UserBook> userBookList = userBookDao.findByCategoryId(userCategory.getCategoryId());
            DeleteUtils.deleteUserBook(userBookList);
            //delete usercategory
            userCategoryDao.delete(userCategory);
        }
    }

    //删除书单
    public static void deleteBookSheet(BookSheet bookSheet) {
        if (bookSheet != null) {
            if (bookSheet.getGoodNum() == 0 && bookSheet.getCollectionNum() == 0 || bookSheet.getUserId() == PropConfig.OFFICER_USERID) {

                long userId = bookSheet.getUserId();

                //delete booksheet book
                bookSheetBookDao.delete(bookSheetBookDao.findBySheetId(bookSheet.getSheetId()));

                //delete good
                UserGoodUtils.deleteUserGood(bookSheet.getSheetId(), GoodType.BookSheet.getType());

                //delete collection
                UserCollectionUtils.deleteUserCollection(bookSheet.getSheetId(), CollectType.BookSheet.getType());

                //create solr index to delete
                AddUtils.addTempSolrIndex(SolrType.BookSheet, bookSheet.getSheetId());
                SolrUtils.tryDelete(SolrType.BookSheet.getType(), CallType.Api.getType());

                //delete booksheet
                bookSheetDao.delete(bookSheet);

                //set booksheet num
                UserInfo userInfo = userInfoDao.findOne(userId);
                userInfo.setBookSheetNum(bookSheetDao.countByUserId(userInfo.getUserId()));
                userInfo = UpdateUtils.updateUserInfo(userInfo);
            } else {
                bookSheet.setUserId(PropConfig.OFFICER_USERID);
                bookSheet.setName("漂流·" + bookSheet.getName());
                bookSheet = UpdateUtils.updateBookSheet(bookSheet);
            }
        }
    }

    //删除图文
    public static void deletePicword(Picword picword) {
        if (picword != null) {
            //delete good
            UserGoodUtils.deleteUserGood(picword.getPicwordId(), GoodType.Picword.getType());
            //delete collection
            UserCollectionUtils.deleteUserCollection(picword.getPicwordId(), CollectType.Picword.getType());
            //delete picword
            picwordDao.delete(picword);
        }
    }

    //删除书单书籍
    public static void deleteBookSheetBook(BookSheetBook bookSheetBook) {
        //delete book sheet book
        bookSheetBookDao.delete(bookSheetBook);
        //update booksheet booknum
        BookSheet bookSheet = bookSheetDao.findOne(bookSheetBook.getSheetId());
        bookSheet.setBookNum(bookSheetBookDao.countBySheetId(bookSheet.getSheetId()));
        bookSheet = UpdateUtils.updateBookSheet(bookSheet);
    }

    //删除笔记本
    public static void deleteNotebook(Notebook notebook) {
        if (notebook != null) {
            long userId = notebook.getUserId();

            //delete note
            noteDao.delete(noteDao.findByNotebookId(notebook.getNotebookId()));
            //delete storyline
            storylineDao.delete(storylineDao.findByNotebookId(notebook.getNotebookId()));
            //delete notebook
            notebookDao.delete(notebook);

            //set notebook num
            UserInfo userInfo = userInfoDao.findOne(userId);
            userInfo.setNotebookNum(notebookDao.countByUserId(userInfo.getUserId()));
            userInfo = UpdateUtils.updateUserInfo(userInfo);
        }
    }

    //删除笔记
    public static void deleteNote(Note note) {
        if (note != null) {
            Notebook notebook = notebookDao.findOne(note.getNotebookId());
            //delete note
            noteDao.delete(note);
            //update note num
            notebook.setNoteNum(noteDao.countByNotebookId(note.getNotebookId()));
            notebook = notebookDao.save(notebook);
        }
    }

    //删除storyline
    public static void deleteStoryline(Storyline storyline) {
        if (storyline != null) {
            Notebook notebook = notebookDao.findOne(storyline.getNotebookId());
            storylineDao.delete(storyline);
            //update storyline num
            notebook.setStorylineNum(storylineDao.countByNotebookId(notebook.getNotebookId()));
            notebook = notebookDao.save(notebook);
        }
    }

    //取消好友关注
    public static void deleteUserFriend(UserFriend userFriend) {
        if (userFriend != null) {
            //delete msg
            UserMsgUtils.deleteUserMessage(userFriend.getFriendId(), userFriend.getUserId(), MessageType.FriendConcern.getType(), userFriend.getUserFriendId());
            //delete user friend
            userFriendDao.delete(userFriend);

            //update concern num & fan num
            UserInfo user = userInfoDao.findOne(userFriend.getUserId());
            user.setConcernNum(userFriendDao.countByUserId(user.getUserId()));
            user = UpdateUtils.updateUserInfo(user);

            UserInfo friend = userInfoDao.findOne(userFriend.getFriendId());
            friend.setFanNum(userFriendDao.countByFriendId(friend.getUserId()));
            friend = UpdateUtils.updateUserInfo(friend);
        }
    }

    //删除用户
    public static void deleteUser(Long userId) {
        UserInfo userInfo = userInfoDao.findOne(userId);
        if (userInfo != null) {
            //del msg
            //sender
            userMessageDao.deleteMsgBySender(userId);
            //accepter
            userMessageDao.deleteMsgByAccepter(userId);

            //del book remark
            bookRemarkDao.deleteBookRemarkByUserId(userId);

            //del booksheet
            for (BookSheet bookSheet : bookSheetDao.findByUserId(userId)) {
                deleteBookSheet(bookSheet);
            }

            //del notebook
            for (Notebook notebook : notebookDao.findByUserId(userId)) {
                deleteNotebook(notebook);
            }

            //del userbook
            for (UserBook userBook : userBookDao.findByUserId(userId)) {
                deleteUserBook(userBook);
            }

            //del user category
            for (UserCategory userCategory : userCategoryDao.findByUserId(userId)) {
                deleteUserCategory(userCategory);
            }

            //del bookcheck
            bookCheckDao.deleteByUserId(userId);

            //del picword
            for (Picword picword : picwordDao.findByUserId(userId)) {
                deletePicword(picword);
            }

            //del good
            for (UserGood userGood : userGoodDao.findByUserId(userId)) {
                deleteUserGood(userGood);
            }

            //del collect
            for (UserCollection userCollection : userCollectionDao.findByUserId(userId)) {
                deleteUserCollection(userCollection);
            }

            //del dynamic
            for (BookCircleDynamic bookCircleDynamic : bookCircleDynamicDao.findByUserId(userId)) {
                deleteBookCircleDynamic(bookCircleDynamic);
            }

            //del dynamic reply
            for (BookCircleReply bookCircleReply : bookCircleReplyDao.findByUserId(userId)) {
                if (bookCircleReplyDao.exists(bookCircleReply.getReplyId())) {//maybe deleted
                    deleteBookCircleReply(bookCircleReply);
                }
            }

            //del comment
            for (GroupComment groupComment : groupCommentDao.findByUserId(userId)) {
                deleteCommunityComment(groupComment);
            }

            //del comment reply
            for (GroupReply groupReply : groupReplyDao.findByUserId(userId)) {
                if (groupReplyDao.exists(groupReply.getReplyId())) {//maybe deleted
                    deleteCommReply(groupReply);
                }
            }

            //del concerns
            for (UserFriend userFriend : userFriendDao.findByUserId(userId)) {
                deleteUserFriend(userFriend);
            }

            //del fans
            for (UserFriend userFriend : userFriendDao.findByFriendId(userId)) {
                deleteUserFriend(userFriend);
            }

            //del order evaluate

            //del bill

            //del book order

            //del feedback
            feedbackDao.delete(feedbackDao.findByUserId(userId));

            //user account
            UserAccount userAccount = userAccountDao.findByUserId(userId);
            if (DecimalUtils.equalZero(userAccount.getTotalGold())
                    && DecimalUtils.equalZero(userAccount.getFrozenGold())) {
                userAccountDao.delete(userAccount);
            }

            //del user token
            UserUtils.disableAllToken(userId);

            //del user login
            UserLogin userLogin = userLoginDao.findByUserId(userInfo.getUserId());
            if (userLogin != null) {
                userLoginDao.delete(userLogin);
            }

            //del user
            userInfoDao.delete(userInfo);
        }
    }

    //delete commBook
    public static void deleteCommBook(CommBook commBook) {
        if (commBook != null) {
            long communityId = commBook.getCommunityId();

            commBookDao.delete(commBook);

            //update comm book num
            Community community = communityDao.findOne(communityId);
            community.setBookNum(commBookDao.countByCommunityId(communityId));
            community = communityDao.save(community);
        }
    }

    //退出社区
    public static void deleteCommMember(CommMember commMember, long managerId) {
        if (commMember != null) {
            long userId = commMember.getUserId();

            Community community = communityDao.findOne(commMember.getCommunityId());
            commMemberDao.delete(commMember);

            //delete comm book
            for (CommBook commBook : commBookDao.findByCommunityIdAndUserId(community.getCommunityId(), userId)) {
                deleteCommBook(commBook);
            }

            //update community member num
            community.setMemberNum(commMemberDao.countByCommunityId(community.getCommunityId()));
            community = communityDao.save(community);

            //im tribe quite
            if (managerId == -1) {
                IMUtils.quitTribe(community, userId);
            } else {
                IMUtils.expelTribe(community, managerId, userId);
                AddUtils.addUserMessage(
                        userId,
                        managerId,
                        null,
                        MessageType.COMM_EXPLE_OUT.getType(),
                        community.getCommunityId(),
                        DealStatus.NotDeal.getStatus(),
                        ReadStatus.NotRead.getStatus());
            }
        }
    }

    //删除社区
    public static void deleteCommunity(Community community, UserToken userToken) {
        //dismiss im Tribe
        IMUtils.dismissTribe(community, userToken.getUserId());

        //delete msg
        UserMsgUtils.deleteUserMessage(MessageType.COMM_JOIN_VERTIFY.getType(), community.getCommunityId());
        UserMsgUtils.deleteUserMessage(MessageType.COMM_INVITE_JOIN.getType(), community.getCommunityId());
        UserMsgUtils.deleteUserMessage(MessageType.COMM_ACCEPTE_JOIN.getType(), community.getCommunityId());
        UserMsgUtils.deleteUserMessage(MessageType.COMM_NOTE.getType(), community.getCommunityId());

        //delete member
        commMemberDao.delete(commMemberDao.findByCommunityId(community.getCommunityId()));

        //delete book
        commBookDao.delete(commBookDao.findByCommunityId(community.getCommunityId()));

        //delete community
        communityDao.delete(community);

    }

    //删除token,解绑推送
    public static void deleteUserToken(UserToken userToken) {
        if (userToken != null) {
            //unbind msg push
            MsgPushUtils.updateAliasUnbind(userToken.getUserId(), userToken.getPlatform());
            //delete token
            userTokenDao.delete(userToken);
        }
    }
}
