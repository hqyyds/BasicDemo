
package com.h3w.utils;

import java.util.HashMap;
import java.util.Map;

public class FilenameUtil {

    public static final int FILE_TYPE_OFFICE = 0;
    public static final int FILE_TYPE_PDF = 1;
    public static final int FILE_TYPE_TXT = 2;
    public static final int FILE_TYPE_AUDIO = 3;
    public static final int FILE_TYPE_VIDEO = 4;
    public static final int FILE_TYPE_IMAGE = 5;
    public static final int FILE_TYPE_FLASH = 6;

    /**
     * 得到不包含后缀的文件名字。
     *
     * @return
     */
    public static String getRealName(String name) {
        if (name == null) {
            return "";
        }

        int endIndex = name.lastIndexOf(".");
        if (endIndex == -1) {
            return name;
        }
        return name.substring(0, endIndex);
    }

    public static String getFileExt(String fileName) {
        if (fileName == null)
            return "";

        String ext = "";
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex >= 0) {
            ext = fileName.substring(lastIndex + 1).toLowerCase();
        }

        return ext;
    }

    /**
     * 根据文件后缀名获取文件类型
     *
     * @param fileExt
     * @return
     */
    public static int getFileTypeByExt(String fileExt) {
        fileExt = fileExt.toLowerCase();
        if (".ppt.pptx.doc.docx.xls.xlsx.odt.".indexOf("." + fileExt) != -1) {
            return FILE_TYPE_OFFICE;
        } else if ("pdf".equals(fileExt)) {
            return FILE_TYPE_PDF;
        } else if ("txt".equals(fileExt)) {
            return FILE_TYPE_TXT;
        } else if (".mp3.wav.".indexOf("." + fileExt) != -1) {
            return FILE_TYPE_AUDIO;
        } else if (".jpg.jpeg.gif.png.bmp.".indexOf("." + fileExt) != -1) {
            return FILE_TYPE_IMAGE;
        } else if (".mp4.m4a.ogg.".indexOf("." + fileExt) != -1) {
            return FILE_TYPE_VIDEO;
        } else if (".swf.".indexOf("." + fileExt) != -1) {
            return FILE_TYPE_FLASH;
        } else {
            return -1;
        }
    }
}

