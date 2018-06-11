package com.wg.booksheet.model.response;

import com.wg.booksheet.domain.BookSheet;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-4
 * Time: 下午3:11
 * To change this template use File | Settings | File Templates.
 */
public class BookSheetAddResponse {
    private long sheetId;

    public BookSheetAddResponse(BookSheet bookSheet) {
        this.sheetId = bookSheet.getSheetId();
    }

    public long getSheetId() {
        return sheetId;
    }

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
    }
}
