package com.wg.user.model.response;

import com.wg.user.domain.UserInfo;
import com.wg.user.domain.UserToken;
import com.wg.user.utils.UserUtils;
import com.wg.useraccount.domain.UserAccount;

import static com.wg.common.utils.dbutils.DaoUtils.userAccountDao;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午7:00
 * To change this template use File | Settings | File Templates.
 */
public class NewEntityResponse {
    private long userId;

    private long concernNum;
    private long fanNum;
    private long bookNum;
    private long notebookNum;
    private long bookSheetNum;
    private long ocrRecogNum;
    private long exportNum;

    private double totalGold;
    private double frozenGold;

    public NewEntityResponse(UserToken userToken) {
        UserInfo userInfo = userToken.getUserInfo();

        //update reset data like orcnum expnum
        userInfo = UserUtils.updateReset(userInfo);

        this.userId = userInfo.getUserId();
        this.concernNum = userInfo.getConcernNum();
        this.fanNum = userInfo.getFanNum();
        this.bookNum = userInfo.getBookNum();
        this.notebookNum = userInfo.getNotebookNum();
        this.bookSheetNum = userInfo.getBookSheetNum();
        this.ocrRecogNum = userInfo.getOcrRecogNum();
        this.exportNum = userInfo.getExportNum();

        UserAccount userAccount = userAccountDao.findByUserId(userToken.getUserId());
        this.totalGold = userAccount.getTotalGold().doubleValue();
        this.frozenGold = userAccount.getFrozenGold().doubleValue();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getConcernNum() {
        return concernNum;
    }

    public void setConcernNum(long concernNum) {
        this.concernNum = concernNum;
    }

    public long getFanNum() {
        return fanNum;
    }

    public void setFanNum(long fanNum) {
        this.fanNum = fanNum;
    }

    public long getBookNum() {
        return bookNum;
    }

    public void setBookNum(long bookNum) {
        this.bookNum = bookNum;
    }

    public long getNotebookNum() {
        return notebookNum;
    }

    public void setNotebookNum(long notebookNum) {
        this.notebookNum = notebookNum;
    }

    public long getBookSheetNum() {
        return bookSheetNum;
    }

    public void setBookSheetNum(long bookSheetNum) {
        this.bookSheetNum = bookSheetNum;
    }

    public long getOcrRecogNum() {
        return ocrRecogNum;
    }

    public void setOcrRecogNum(long ocrRecogNum) {
        this.ocrRecogNum = ocrRecogNum;
    }

    public long getExportNum() {
        return exportNum;
    }

    public void setExportNum(long exportNum) {
        this.exportNum = exportNum;
    }

    public double getTotalGold() {
        return totalGold;
    }

    public void setTotalGold(double totalGold) {
        this.totalGold = totalGold;
    }

    public double getFrozenGold() {
        return frozenGold;
    }

    public void setFrozenGold(double frozenGold) {
        this.frozenGold = frozenGold;
    }
}
