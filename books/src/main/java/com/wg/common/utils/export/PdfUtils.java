package com.wg.common.utils.export;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.wg.book.domain.Book;
import com.wg.common.PropConfig;
import com.wg.common.utils.TimeUtils;
import com.wg.common.utils.Utils;
import com.wg.notebook.domain.Note;
import com.wg.notebook.domain.Notebook;
import com.wg.notebook.domain.Storyline;
import com.wg.user.domain.UserInfo;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import static com.wg.common.utils.dbutils.DaoUtils.*;

/**
 * Created by Administrator on 2017/4/13 0013.
 */
public class PdfUtils {
    public static final String NOTEBOOK_FONT = "/font/jianxikaiti.ttf";
    public static final String NOTEBOOK_FOLDER = "/down/notebook/";
    public static Color titleColor = new DeviceRgb(180, 180, 230);
    public static Color contentColor = new DeviceRgb(221, 234, 238);

    //笔记pdf导出,并返回相对文件路径
    public static String exportNotebookPdf(Notebook notebook) {
        if (notebook != null) {
            try {
                String fileName = TimeUtils.formatDate(TimeUtils.getCurrentDate(), TimeUtils.YYYYMMDDHHMMSSSSS) + "_" + notebook.getNotebookId() + ".pdf";
                File file = new File(PropConfig.UPLOAD_PATH + NOTEBOOK_FOLDER + fileName);
                file.getParentFile().mkdirs();
                //Initialize
                PdfWriter writer = new PdfWriter(file.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document doc = new Document(pdf, PageSize.A4.clone());
                doc.setMargins(30, 30, 30, 30);
                doc.setBackgroundColor(new DeviceRgb(255, 255, 255));
                PdfFont font = PdfFontFactory.createFont(PropConfig.UPLOAD_PATH + NOTEBOOK_FONT, PdfEncodings.IDENTITY_H, false);

                //load cover page
                doc = loadCover(doc, notebook, font);
                //load notes
                doc = loadNotes(doc, notebook, font);
                //load lines
//                doc = loadLines(doc, notebook, font);

                doc.close();
                return NOTEBOOK_FOLDER + fileName;
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static Document loadCover(Document doc, Notebook notebook, PdfFont font) throws MalformedURLException {
        //add nobebook cover
        UserInfo userInfo = userInfoDao.findOne(notebook.getUserId());
        Book book = bookDao.findOne(notebook.getBookId());
        //title
        doc.add(new Paragraph("笔记" + "《" + book.getTitle() + "》")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(28)
                .setMarginTop(90)
        );

        //book cover
        doc.add(new Image(ImageDataFactory.create(PropConfig.UPLOAD_PATH + book.getCover()))
                .scaleToFit(300, 300)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setMarginTop(60)
                .setMarginBottom(60)
        );
        //user
        doc.add(new Paragraph("By：" + userInfo.getNickname())
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(15)
                .setMargin(10)
        );
        //time
        doc.add(new Paragraph("From：" + TimeUtils.formatDate(notebook.getCreatedTime(), TimeUtils.YYYY_MM_DD_EX))
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(10)
        );
        doc.add(new Paragraph("To：" + TimeUtils.formatDate(notebook.getUpdatedTime(), TimeUtils.YYYY_MM_DD_EX))
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(10)
        );
        //number
        doc.add(new Paragraph("笔记数：" + notebook.getNoteNum())
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(10)
        );
//        doc.add(new Paragraph("情节：" + notebook.getStorylineNum())
//                .setTextAlignment(TextAlignment.CENTER)
//                .setFont(font)
//                .setFontSize(10)
//        );

        doc.add(new Paragraph("- design by 文芽")
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(20)
                .setFont(font)
                .setFontSize(11)
        );

        return doc;
    }

    public static Document loadNotes(Document doc, Notebook notebook, PdfFont font) throws MalformedURLException {
        //next area
        doc.add(new AreaBreak());

        //title
        doc.add(new Paragraph("我的笔记" + "（" + notebook.getNoteNum() + "）")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(24)
                .setMargin(24)
        );

        //add note
        for (Note note : noteDao.findByNotebookId(notebook.getNotebookId())) {
            //title
            String title = "";
            if (note.getChapter() != null) title += note.getChapter();
            if (note.getChapter() != null && note.getPages() > 0) title += " | ";
            if (note.getPages() > 0) title += note.getPages();
            Paragraph paragraphTitle = new Paragraph(title)
                    .setFont(font)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT);
            String time = TimeUtils.formatDate(note.getCreatedTime(), TimeUtils.YYYY_MM_DD_HH_MM_EX);
            Paragraph paragraphTime = new Paragraph(time)
                    .setFont(font)
                    .setFontSize(10)
                    .setPadding(2)
                    .setTextAlignment(TextAlignment.RIGHT);
            Table tableTitle = new Table(new float[2])
                    .setWidthPercent(100)
                    .setBackgroundColor(titleColor)
                    .setMarginTop(20)
                    .addCell(new Cell()
                            .add(paragraphTitle)
                            .setBorder(Border.NO_BORDER)
                            .setPadding(2)
                    )
                    .addCell(new Cell()
                            .add(paragraphTime)
                            .setBorder(Border.NO_BORDER)
                            .setPadding(2)
                    );
            doc.add(tableTitle);

            //oriText
            Cell oriTextCell = new Cell()
                    .setBorder(Border.NO_BORDER);
            if (note.getOriginText() != null) {
                Paragraph paragraphOriText = new Paragraph("摘录：" + note.getOriginText())
                        .setFont(font)
                        .setFontColor(Color.DARK_GRAY)
                        .setFontSize(9)
                        .setFirstLineIndent(18);
                oriTextCell.add(paragraphOriText)
                        .setPadding(5);
            }
            //content
            Cell contentCell = new Cell()
                    .setBorder(Border.NO_BORDER);
            if (note.getContent() != null) {
                Paragraph paragraphContent = new Paragraph(note.getContent())
                        .setFont(font)
                        .setFontSize(11)
                        .setFirstLineIndent(22);
                contentCell.add(paragraphContent)
                        .setPadding(5);
            }
            //image
            Cell imgCell = new Cell()
                    .setBorder(Border.NO_BORDER);
            if (note.getImage() != null) {
                Image image = new Image(ImageDataFactory.create(PropConfig.UPLOAD_PATH + note.getImage()))
                        .scaleToFit(100, 100)
                        .setHorizontalAlignment(HorizontalAlignment.RIGHT);
                imgCell.add(image)
                        .setPadding(5);
            }

            Table tableNewContent = new Table(new float[2])
                    .setWidthPercent(100)
                    .addCell(contentCell)
                    .addCell(imgCell);

            Cell tableNewCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(tableNewContent);

            Table tableContent = new Table(new float[1])
                    .setWidthPercent(100)
                    .setBackgroundColor(contentColor)
                    .addCell(oriTextCell)
                    .addCell(tableNewCell);

            doc.add(tableContent);
        }
        return doc;
    }

    public static Document loadLines(Document doc, Notebook notebook, PdfFont font) throws MalformedURLException {
        //next area
        doc.add(new AreaBreak());

        //title
        doc.add(new Paragraph("我的情节" + "（" + notebook.getStorylineNum() + "）")
                .setTextAlignment(TextAlignment.CENTER)
                .setFont(font)
                .setFontSize(24)
                .setMargin(24)
        );

        //add note
        for (Storyline storyline : storylineDao.findByNotebookId(notebook.getNotebookId())) {
            //title
            Paragraph paragraphTitle = new Paragraph(storyline.getNode())
                    .setFont(font)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT);
            String time = TimeUtils.formatDate(storyline.getCreatedTime(), TimeUtils.YYYY_MM_DD_HH_MM_EX);
            Paragraph paragraphTime = new Paragraph(time)
                    .setFont(font)
                    .setFontSize(10)
                    .setPadding(2)
                    .setTextAlignment(TextAlignment.RIGHT);
            Table tableTitle = new Table(new float[]{1, 1})
                    .setWidthPercent(100)
                    .addCell(new Cell()
                            .add(paragraphTitle)
                            .setBorder(Border.NO_BORDER)
                            .setBackgroundColor(titleColor)
                            .setPadding(2)
                    )
                    .addCell(new Cell()
                            .add(paragraphTime)
                            .setBorder(Border.NO_BORDER)
                            .setBackgroundColor(titleColor)
                            .setPadding(2)
                    );
            doc.add(tableTitle.setMarginTop(20));

            //content
            Paragraph paragraphContent = new Paragraph(storyline.getStory())
                    .setFont(font)
                    .setFontSize(10)
                    .setFirstLineIndent(20);
            Table tableContent = new Table(new float[]{1})
                    .setWidthPercent(100)
                    .setPadding(5);
            tableContent.addCell(new Cell()
                    .add(paragraphContent)
                    .setBorder(Border.NO_BORDER)
                    .setBackgroundColor(contentColor)
                    .setPadding(5)
            );
            doc.add(tableContent);
            //characters
            Table tableCharacter = null;
            if (StringUtils.isNotBlank(storyline.getCharacters())) {
                java.util.List<String> characters = Utils.getStringListByString(storyline.getCharacters());
                tableCharacter = new Table(new float[characters.size() + 1]);
                tableCharacter.addCell(new Cell()
                        .add(new Paragraph("人物:")
                                .setFont(font)
                                .setPaddingLeft(5)
                                .setPaddingRight(5)
                        )
                        .setBorder(Border.NO_BORDER)
                );
                for (String c : characters) {
                    tableCharacter.addCell(new Cell()
                            .add(new Paragraph(c).
                                    setFont(font).
                                    setBackgroundColor(Color.WHITE)
                                    .setPaddingLeft(5)
                                    .setPaddingRight(5)
                            )
                            .setBorder(Border.NO_BORDER)
                            .setPaddingLeft(5)
                    );
                }
            }
            //places
            Table tablePlace = null;
            if (StringUtils.isNotBlank(storyline.getPlaces())) {
                java.util.List<String> places = Utils.getStringListByString(storyline.getPlaces());
                tablePlace = new Table(new float[places.size() + 1]);
                tablePlace.addCell(new Cell()
                        .add(new Paragraph("地点:")
                                .setFont(font)
                                .setPaddingLeft(5)
                                .setPaddingRight(5)
                        )
                        .setBorder(Border.NO_BORDER)
                );
                for (String p : places) {
                    tablePlace.addCell(new Cell()
                            .add(new Paragraph(p)
                                    .setFont(font)
                                    .setBackgroundColor(Color.WHITE)
                                    .setPaddingLeft(5)
                                    .setPaddingRight(5)
                            )
                            .setBorder(Border.NO_BORDER)
                            .setPaddingLeft(5)
                    );
                }
            }

            if (tableCharacter != null || tablePlace != null) {
                Table tablePoint = new Table(new float[1])
                        .setWidthPercent(100)
                        .setBackgroundColor(contentColor);
                if (tableCharacter != null) {
                    tablePoint.addCell(new Cell().add(tableCharacter)
                            .setBorder(Border.NO_BORDER)
                    );
                }
                if (tablePlace != null) {
                    tablePoint.addCell(new Cell().add(tablePlace)
                            .setBorder(Border.NO_BORDER)
                    );
                }
                tablePoint.addCell(new Cell()
                        .setBorder(Border.NO_BORDER)
                );
                doc.add(tablePoint);
            }
        }
        return doc;
    }

    public static void createPdf() throws FileNotFoundException {
        String fileName = "test.pdf";
        File file = new File(PropConfig.UPLOAD_PATH + NOTEBOOK_FOLDER + fileName);
        file.getParentFile().mkdirs();

        PdfWriter pdfWriter = new PdfWriter(file.getAbsolutePath());
        PdfDocument pdf = new PdfDocument(pdfWriter);
        PageSize ps = new PageSize(PageSize.A4.rotate());
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);

        //Draw X axis
        canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .stroke();
        //Draw X axis arrow
        canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(ps.getWidth() / 2 - 25, -10)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .lineTo(ps.getWidth() / 2 - 25, 10).stroke()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);
        //Draw Y axis
        canvas.moveTo(0, -(ps.getHeight() / 2 - 15))
                .lineTo(0, ps.getHeight() / 2 - 15)
                .stroke();
        //Draw Y axis arrow
        canvas.saveState()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(-10, ps.getHeight() / 2 - 25)
                .lineTo(0, ps.getHeight() / 2 - 15)
                .lineTo(10, ps.getHeight() / 2 - 25).stroke()
                .restoreState();
        //Draw X serif
        for (int i = -((int) ps.getWidth() / 2 - 61);
             i < ((int) ps.getWidth() / 2 - 60); i += 40) {
            canvas.moveTo(i, 5).lineTo(i, -5);
        }
        //Draw Y serif
        for (int j = -((int) ps.getHeight() / 2 - 57);
             j < ((int) ps.getHeight() / 2 - 56); j += 40) {
            canvas.moveTo(5, j).lineTo(-5, j);
        }
        canvas.stroke();

        pdf.close();
    }
}
