package com.wg.common;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;

import static com.wg.common.utils.Utils.logger;

/**
 * Created by Administrator on 2016/12/26 0026.
 */
@Component
public class Constant {
    public static final String CHAR_SET_UTF8 = "UTF-8";

    //param union char
    public static final String JOIN_CHAR = "|";
    public static final String SPLIT_CHAR = "[|]";

    //file suffix
    public static final String SUFFIX_JOIN_CHAR = ".";
    public static final String SUFFIX_SPLIT_CHAR = "[.]";
    public static final String IMAGE_SUFFIX = ".jpg";
    public static final String HTML_SUFFIX = ".html";

    //home num
    public static final int HOME_NUM = 200;//home获取数量
    public static final int HOME_GET_NUM = 20;//前端获取数量
    public static final int HOT_BOOK_NUM = 10;//热书选用数量

    //page num
    public static final int PAGE_NUM_SMALL = 6;//页面返回数量:小
    public static final int PAGE_NUM_MEDIUM = 10;//页面返回数量:中
    public static final int PAGE_NUM_LARGE = 21;//页面返回数量:大

    //全局文件相对路径配置
    public static final String BOOK_COVER_FOLDER = "/img/book/cover/lpic/";
    public static final String USER_AVATAR_FOLDER = "/img/user/avatar/lpic/";
    public static final String USER_BACKGROUND_FOLDER = "/img/user/background/lpic/";
    public static final String DYNAMIC_CONTENT_FOLDER = "/img/dynamic/content/lpic/";
    public static final String BOOK_SHEET_COVER_FOLDER = "/img/booksheet/cover/lpic/";
    public static final String NOTEBOOK_CONTENT_FOLDER = "/img/notebook/content/lpic/";
    public static final String BOOKCHECK_COVER_FOLDER = "/img/bookcheck/cover/lpic/";
    public static final String PICWORD_CONTENT_FOLDER = "/img/picword/content/lpic/";
    public static final String COMMUNITY_THEMEPIC_FOLDER = "/img/community/themepic/lpic/";
    public static final String NEWS_HTML_FOLDER = "/news/";
    public static final String IMG_DEFAULT = "default/default.jpg";
    public static final String BOOK_COVER_DEFAULT = BOOK_COVER_FOLDER + IMG_DEFAULT;
    public static final String USER_AVATAR_DEFAULT = USER_AVATAR_FOLDER + IMG_DEFAULT;
    public static final String USER_BACKGROUND_DEFAULT = USER_BACKGROUND_FOLDER + IMG_DEFAULT;
    public static final String BOOK_SHEET_COVER_DEFAULT = BOOK_SHEET_COVER_FOLDER + IMG_DEFAULT;
    public static final String COMMUNITY_THEMEPIC_DEFAULT = COMMUNITY_THEMEPIC_FOLDER + IMG_DEFAULT;

    //token unneed api set
    public static final HashSet<String> TOKENLESS_API_SET = new HashSet<String>();

    //common set
    public static final long ID_NOT_EXIST = 0L;

    @PostConstruct
    void init() {
        logger.info("Constant construct");
        loadTokenlessApiSet();
    }

    public static void loadTokenlessApiSet() {
        TOKENLESS_API_SET.add("/api/common/home");
        TOKENLESS_API_SET.add("/api/common/choiceness");
        TOKENLESS_API_SET.add("/api/common/bookClass");
        TOKENLESS_API_SET.add("/api/common/bookTag");
        TOKENLESS_API_SET.add("/api/common/bookSheetTag");
        TOKENLESS_API_SET.add("/api/common/config");

        TOKENLESS_API_SET.add("/api/book/search");
        TOKENLESS_API_SET.add("/api/book/lib");
        TOKENLESS_API_SET.add("/api/book/detail");
        TOKENLESS_API_SET.add("/api/book/remark");

        TOKENLESS_API_SET.add("/api/bookCommunity/search");
        TOKENLESS_API_SET.add("/api/bookCommunity/book");
        TOKENLESS_API_SET.add("/api/bookCommunity/detail");
        TOKENLESS_API_SET.add("/api/bookCommunity/comment");

        TOKENLESS_API_SET.add("/api/comment/lib");
        TOKENLESS_API_SET.add("/api/comment/detail");
        TOKENLESS_API_SET.add("/api/comment/reply");

        TOKENLESS_API_SET.add("/api/bookCircle/dynamic");
        TOKENLESS_API_SET.add("/api/dynamic/detail");

        TOKENLESS_API_SET.add("/api/notebook/personal");
        TOKENLESS_API_SET.add("/api/notebook/detail");
        TOKENLESS_API_SET.add("/api/notebook/note");
        TOKENLESS_API_SET.add("/api/notebook/note_v104");
        TOKENLESS_API_SET.add("/api/notebook/storyline");

        TOKENLESS_API_SET.add("/api/userBook/personal_v104");
        TOKENLESS_API_SET.add("/api/userBook/detail");
        TOKENLESS_API_SET.add("/api/userBook/categoryStatis_v104");
        TOKENLESS_API_SET.add("/api/userBook/categoryBooks");
        TOKENLESS_API_SET.add("/api/userBook/search_v200");
        TOKENLESS_API_SET.add("/api/userBook/nearbySearch");

        TOKENLESS_API_SET.add("/api/bookSheet/personal");
        TOKENLESS_API_SET.add("/api/bookSheet/detail");
        TOKENLESS_API_SET.add("/api/bookSheet/lib");

        TOKENLESS_API_SET.add("/api/userCollection/bookSheet");

        TOKENLESS_API_SET.add("/api/user/search");
        TOKENLESS_API_SET.add("/api/user/profile");
        TOKENLESS_API_SET.add("/api/user/concerns");
        TOKENLESS_API_SET.add("/api/user/fans");
        TOKENLESS_API_SET.add("/api/user/nearby");
        TOKENLESS_API_SET.add("/api/user/readingReport");

        TOKENLESS_API_SET.add("/api/user/signup");
        TOKENLESS_API_SET.add("/api/user/login");
        TOKENLESS_API_SET.add("/api/user/thirdPlatLogin");
        TOKENLESS_API_SET.add("/api/user/sendValidcode");
        TOKENLESS_API_SET.add("/api/user/forgotpsw");
        TOKENLESS_API_SET.add("/api/user/thirdLogin_v103");
        TOKENLESS_API_SET.add("/api/user/thirdBindLogin");

        TOKENLESS_API_SET.add("/api/news/top");

        TOKENLESS_API_SET.add("/api/picword/newest");
        TOKENLESS_API_SET.add("/api/picword/hotest");

        TOKENLESS_API_SET.add("/api/bookOrder/tradeInfo");
        TOKENLESS_API_SET.add("/api/bookOrder/evaluate");
        TOKENLESS_API_SET.add("/api/userAccount/notifyAliPay");
        TOKENLESS_API_SET.add("/api/userAccount/notifyWexinPay");

        TOKENLESS_API_SET.add("/api/community/nearbySearch");
        TOKENLESS_API_SET.add("/api/community/profile");
        TOKENLESS_API_SET.add("/api/community/members");
        TOKENLESS_API_SET.add("/api/community/memberSearch");
        TOKENLESS_API_SET.add("/api/community/categoryStatis");
        TOKENLESS_API_SET.add("/api/community/books");
        TOKENLESS_API_SET.add("/api/community/bookSearch");
        TOKENLESS_API_SET.add("/api/community/bookers");

        TOKENLESS_API_SET.add("/api/test/test");
    }

}
