package com.wg.common.utils.export;

import com.wg.book.domain.Book;
import com.wg.book.utils.BookUtils;
import com.wg.common.Enum.userbook.CategoryGroupType;
import com.wg.common.PropConfig;
import com.wg.common.utils.TimeUtils;
import com.wg.user.domain.UserInfo;
import com.wg.userbook.domain.UserBook;
import com.wg.userbook.model.CategoryStatis;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.wg.common.utils.dbutils.DaoUtils.userBookDao;
import static com.wg.userbook.utils.UserBookUtils.getNormalCategoryStatis;

/**
 * Created by Administrator on 2017/4/20 0020.
 */
public class ExcelUtils {
    public static final String USERBOOK_FOLDER = "/down/userbook/";

    public static String exportBookExcel(UserInfo userInfo, int categoryType) {
        HSSFWorkbook wb = new HSSFWorkbook();
        try {
            String fileName = TimeUtils.formatDate(TimeUtils.getCurrentDate(), TimeUtils.YYYYMMDDHHMMSSSSS);
            //get normal category statis
            if (categoryType == CategoryGroupType.All.getType()) {
                fileName += "_a_" + userInfo.getUserId() + ".xls";
                String sheetName = CategoryGroupType.All.getName() + "（" + userBookDao.countByUserId(userInfo.getUserId()) + "）";
                loadOneSheetBooks(wb, sheetName, CategoryGroupType.All, null, userInfo);
            } else if (categoryType == CategoryGroupType.Normal.getType()) {
                fileName += "_na_" + userInfo.getUserId() + ".xls";
                List<CategoryStatis> categoryStatisList = getNormalCategoryStatis(userInfo.getUserId());
                CategoryStatis nullCategory = null;
                for (CategoryStatis categoryStatis : categoryStatisList) {
                    if (!categoryStatis.getCategory().equals(BookUtils.NULL_CATEGORY)) {
                        String sheetName = categoryStatis.getCategory() + "（" + categoryStatis.getStatis() + "）";
                        loadOneSheetBooks(wb, sheetName, CategoryGroupType.Normal, categoryStatis.getCategory(), userInfo);
                    } else {
                        nullCategory = categoryStatis;
                    }
                }
                if (nullCategory != null) {
                    String sheetName = nullCategory.getCategory() + "（" + nullCategory.getStatis() + "）";
                    loadOneSheetBooks(wb, sheetName, CategoryGroupType.Normal, nullCategory.getCategory(), userInfo);
                }
            }
            File file = new File(PropConfig.UPLOAD_PATH + USERBOOK_FOLDER + fileName);
            file.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(file);
            wb.write(fos);
            fos.close();
            return USERBOOK_FOLDER + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HSSFSheet loadOneSheetBooks(HSSFWorkbook wb, String sheetName, CategoryGroupType categoryGroupType, String category, UserInfo userInfo) {
        HSSFSheet sheet = null;
        //add books
        List<UserBook> userBookList = new ArrayList<UserBook>();
        if (categoryGroupType.getType() == CategoryGroupType.All.getType()) {
            userBookList = userBookDao.findByUserId(userInfo.getUserId());
            sheet = wb.createSheet(sheetName);
        } else if (categoryGroupType.getType() == CategoryGroupType.Normal.getType()) {
            userBookList = userBookDao.findByUserIdAndCategory(userInfo.getUserId(), category);
            sheet = wb.createSheet(sheetName);
        }

        //add head row
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        String[] header = {"书名", "作者", "译者", "ISBN", "中图法", "定价", "出版社", "出版日期", "装帧", "页数"};
        for (int ih = 0; ih < header.length; ih++) {
            HSSFCell cell = row.createCell((short) ih);
            cell.setCellValue(header[ih]);
            cell.setCellStyle(style);
        }
        //add row
        for (int ib = 0; ib < userBookList.size(); ib++) {
            row = sheet.createRow(ib + 1);
            UserBook userBook = userBookList.get(ib);
            Book book = userBook.getBook();
            row.createCell((short) 0).setCellValue(book.getTitle());
            row.createCell((short) 1).setCellValue(book.getAuthor());
            row.createCell((short) 2).setCellValue(book.getTranslator());
            row.createCell((short) 3).setCellValue(book.getIsbn13());
            row.createCell((short) 4).setCellValue(book.getCategory());
            row.createCell((short) 5).setCellValue(book.getPrice());
            row.createCell((short) 6).setCellValue(book.getPublisher());
            row.createCell((short) 7).setCellValue(book.getPubDate());
            row.createCell((short) 8).setCellValue(book.getBinding());
            row.createCell((short) 9).setCellValue(book.getPages());
        }
        return sheet;
    }

}
