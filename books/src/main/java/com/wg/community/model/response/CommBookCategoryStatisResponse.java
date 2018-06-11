package com.wg.community.model.response;

import com.wg.common.Enum.community.MemberType;
import com.wg.common.Enum.userbook.CategoryGroupType;
import com.wg.common.Enum.userbook.OtherCategory;
import com.wg.community.domain.Community;
import com.wg.community.utils.CommunityUtils;
import com.wg.user.domain.UserToken;
import com.wg.userbook.model.CategoryGroup;
import com.wg.userbook.model.CategoryStatis;

import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.commBookDao;

/**
 * Created by Administrator on 2016/11/11 0011.
 */
public class CommBookCategoryStatisResponse {
    private String name;
    private long total;
    private int memberType;
    private long imTribeId;
    private List<CategoryGroup> categoryGroupList = new ArrayList<CategoryGroup>();

    public CommBookCategoryStatisResponse(Community community, UserToken userToken) {
        this.name = community.getName();
        this.total = community.getBookNum();
        this.memberType = CommunityUtils.getMemberType(community, userToken);
        this.imTribeId = community.getImTribeId();
        //add normal statis
        this.categoryGroupList.add(new CategoryGroup(CategoryGroupType.Normal.getType(), CategoryGroupType.Normal.getName(), CommunityUtils.getNormalCategoryStatis(community)));
        //add other statis
        if (memberType != MemberType.Stranger.getType()) {
            List<CategoryStatis> categoryStatisList = new ArrayList<CategoryStatis>();
            categoryStatisList.add(new CategoryStatis(OtherCategory.My_share.getFilter(), commBookDao.countByCommunityIdAndUserId(community.getCommunityId(), userToken.getUserId())));
            this.categoryGroupList.add(new CategoryGroup(CategoryGroupType.Other.getType(), CategoryGroupType.Other.getName(), categoryStatisList));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }

    public long getImTribeId() {
        return imTribeId;
    }

    public void setImTribeId(long imTribeId) {
        this.imTribeId = imTribeId;
    }

    public List<CategoryGroup> getCategoryGroupList() {
        return categoryGroupList;
    }

    public void setCategoryGroupList(List<CategoryGroup> categoryGroupList) {
        this.categoryGroupList = categoryGroupList;
    }
}
