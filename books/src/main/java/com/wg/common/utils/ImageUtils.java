package com.wg.common.utils;

import com.wg.common.Constant;
import com.wg.common.PropConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.wg.common.utils.Utils.logger;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-8-27
 * Time: 下午7:16
 * To change this template use File | Settings | File Templates.
 */

/**
 * 图片工具
 */
public class ImageUtils {

    //获取图片尺寸
    public static String getImageSize(File image) {
        String size = "";
        BufferedImage sourceImg;
        InputStream ips = null;
        try {
            if (image.exists()) {
                ips = new FileInputStream(image);
                sourceImg = ImageIO.read(ips);
                size = sourceImg.getWidth() + "x" + sourceImg.getHeight();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            if (ips != null) {
                IOUtils.closeQuietly(ips);
            }
        }
        return size;
    }

    //获取网络图片后缀,没有返回默认后缀
    public static String getImageSuffix(String url) {
        String[] strs = url.split(Constant.SUFFIX_SPLIT_CHAR);
        String suffix = Constant.SUFFIX_JOIN_CHAR + strs[strs.length - 1];
        if (suffix.length() < 5) {
            return suffix;
        }
        return Constant.IMAGE_SUFFIX;
    }

    //网络图片下载
    public static String saveImage(String folder, long id, String url) {
        if (url != null && id > 0 && folder != null) {
            try {
                String name = id / 10000 + "/" + id;
                String suffix = getImageSuffix(url);
                File image = new File(PropConfig.UPLOAD_PATH + folder + name + suffix);
                HttpUtils.requestFile(new HttpGet(url), image);
                String size = ImageUtils.getImageSize(image);
                if (StringUtils.isNotBlank(size)) {
                    String path = folder + name + "_" + size + suffix;
                    File finalImage = new File(PropConfig.UPLOAD_PATH + path);
                    image.renameTo(finalImage);
                    logger.info("[" + TimeUtils.getCurrentDate() + "] save file to " + finalImage.getAbsolutePath());
                    return path;
                } else {
                    return null;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e.getCause());
            }
        }
        return null;
    }

    //用户传输图片接收
    public static String saveImage(String folder, long id, MultipartFile file) {
        if (file != null && id > 0 && folder != null) {
            try {
                String name = id / 10000 + "/" + id;
                String[] strs = file.getOriginalFilename().split(Constant.SUFFIX_SPLIT_CHAR);
                String suffix = "." + strs[strs.length - 1];
                File image = new File(PropConfig.UPLOAD_PATH + folder + name + suffix);
                FileUtils.copyInputStreamToFile(file.getInputStream(), image);
                String path = folder + name + "_" + TimeUtils.getCurrentDate().getTime() + "_" + ImageUtils.getImageSize(image) + suffix;
                File finalImage = new File(PropConfig.UPLOAD_PATH + path);
                image.renameTo(finalImage);
                logger.info("[" + TimeUtils.getCurrentDate() + "] save file to " + finalImage.getAbsolutePath());
                return path;
            } catch (Exception e) {
                logger.error(e.getMessage(), e.getCause());
            }
        }
        return null;
    }

    //本地图片复制
    public static String saveImage(String folder, long id, File file) {
        if (file != null && id > 0 && folder != null) {
            try {
                String name = id / 10000 + "/" + id;
                String[] strs = file.getCanonicalPath().split(Constant.SUFFIX_SPLIT_CHAR);
                String suffix = "." + strs[strs.length - 1];
                String path = folder + name + "_" + ImageUtils.getImageSize(file) + suffix;
                File newImage = new File(PropConfig.UPLOAD_PATH + path);
                FileUtils.copyFile(file, newImage);
                logger.info("[" + TimeUtils.getCurrentDate() + "] save file to " + newImage.getAbsolutePath());
                return path;
            } catch (Exception e) {
                logger.error(e.getMessage(), e.getCause());
            }
        }
        return null;
    }

    //删除本地图片
    public static boolean deleteImage(String filePath) {
        if (filePath != null) {
            try {
                File oldFile = new File(PropConfig.UPLOAD_PATH + filePath);
                if (filePath.equals(Constant.BOOK_COVER_DEFAULT)
                        || filePath.equals(Constant.BOOK_SHEET_COVER_DEFAULT)
                        || filePath.equals(Constant.USER_AVATAR_DEFAULT)
                        || filePath.equals(Constant.USER_BACKGROUND_DEFAULT)) {
                } else {
                    FileUtils.deleteQuietly(oldFile);
                    logger.info("[" + TimeUtils.getCurrentDate() + "] delete file " + PropConfig.UPLOAD_PATH + filePath);
                }
                return true;
            } catch (Exception e) {
                logger.info("[" + TimeUtils.getCurrentDate() + "] delete file " + PropConfig.UPLOAD_PATH + filePath + " failed...");
            }
        }
        return false;
    }

}
