package com.h3w.utils;

import com.h3w.exception.CustomException;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import java.io.File;

/**
 *
 * 将jacob.dll放入JDK的bin目录下 把jacob.jar放入项目的buildPath中（web项目放到WEB-INF\lib目录下）
 *
 * @author 单红宇
 *
 */
public class ConvertToPdf {

    /*转PDF格式值*/
    private static final int wdFormatPDF = 17;
    private static final int xlFormatPDF = 0;
    private static final int ppFormatPDF = 32;
    private static final int msoTrue = -1;
    private static final int msofalse = 0;

    /*转HTML格式值*/
    private static final int wdFormatHTML = 8;
    private static final int ppFormatHTML = 12;
    private static final int xlFormatHTML = 44;

    /*转TXT格式值*/
    private static final int wdFormatTXT = 2;

    public static boolean convert2PDF(String inputFile, String pdfFile) {
        String suffix = getFileSufix(inputFile);
        File file = new File(inputFile);
        if (!file.exists()) {
            throw new CustomException(300,"文件不存在！");
        }
        if (suffix.equals("pdf")) {
            System.out.println("PDF not need to convert!");
            return false;
        }
        if (suffix.equals("doc") || suffix.equals("docx") || suffix.equals("txt")) {
            return word2PDF(inputFile, pdfFile);
        } else if (suffix.equals("ppt") || suffix.equals("pptx")) {
            return ppt2PDF(inputFile, pdfFile);
        } else if (suffix.equals("xls") || suffix.equals("xlsx")) {
            return excel2PDF(inputFile, pdfFile);
        } else {
            throw new CustomException(300,"文件格式不支持转换！");
        }
    }

    /**
     * 获取文件后缀
     *
     * @param fileName
     * @return
     * @author SHANHY
     */
    private static String getFileSufix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(splitIndex + 1);
    }

    /**
     * Word文档转换
     *
     * @param inputFile
     * @param pdfFile
     * @author SHANHY
     */
    public static boolean word2PDF(String inputFile, String pdfFile) {
        try {
            ComThread.InitSTA();
        }catch (NoClassDefFoundError e){
            throw new CustomException(300,"初始化失败，缺少Jar包！");
        }catch (Exception e){
            throw new CustomException(300,"初始化失败，未知异常！");
        }

        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch doc = null;
        try {
            app = new ActiveXComponent("Word.Application");// 创建一个word对象
            app.setProperty("Visible", new Variant(false)); // 不可见打开word
            app.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            Dispatch docs = app.getProperty("Documents").toDispatch();// 获取文挡属性

            System.out.println("打开文档 >>> " + inputFile);
            // Object[]第三个参数是表示“是否只读方式打开”
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            doc = Dispatch.call(docs, "Open", inputFile, false, true).toDispatch();
            // 调用Document对象的SaveAs方法，将文档保存为pdf格式
            System.out.println("转换文档 [" + inputFile + "] >>> [" + pdfFile + "]");
            Dispatch.call(doc, "SaveAs", pdfFile, wdFormatPDF);//word保存为pdf格式宏，值为17
//            Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, wdFormatPDF); // word保存为pdf格式宏，值为17

            long end = System.currentTimeMillis();

            System.out.println("用时：" + (end - start) + "ms.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(doc, "Close", false);
            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[] {});
        }
        // 如果没有这句话,winword.exe进程将不会关闭
        ComThread.Release();
        ComThread.quitMainSTA();
        return false;
    }

    /**
     * PPT文档转换
     *
     * @param inputFile
     * @param pdfFile
     * @author SHANHY
     */
    private static boolean ppt2PDF(String inputFile, String pdfFile) {
        ComThread.InitSTA();

        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch ppt = null;
        try {
            app = new ActiveXComponent("PowerPoint.Application");// 创建一个PPT对象
            // app.setProperty("Visible", new Variant(false)); // 不可见打开（PPT转换不运行隐藏，所以这里要注释掉）
            // app.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            Dispatch ppts = app.getProperty("Presentations").toDispatch();// 获取文挡属性

            System.out.println("打开文档 >>> " + inputFile);
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            ppt = Dispatch.call(ppts, "Open", inputFile,
                    true,// ReadOnly
                    true,// Untitled指定文件是否有标题
                    false// WithWindow指定文件是否可见
            ).toDispatch();

            System.out.println("转换文档 [" + inputFile + "] >>> [" + pdfFile + "]");
            Dispatch.call(ppt, "SaveAs", pdfFile, ppFormatPDF);

            long end = System.currentTimeMillis();

            System.out.println("用时：" + (end - start) + "ms.");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(ppt, "Close");
            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[] {});
        }
        ComThread.Release();
        ComThread.quitMainSTA();
        return false;
    }

    /**
     * Excel文档转换
     *
     * @param inputFile
     * @param pdfFile
     * @author SHANHY
     */
    private static boolean excel2PDF(String inputFile, String pdfFile) {
        ComThread.InitSTA();

        long start = System.currentTimeMillis();
        ActiveXComponent app = null;
        Dispatch excel = null;
        try {
            app = new ActiveXComponent("Excel.Application");// 创建一个PPT对象
            app.setProperty("Visible", new Variant(false)); // 不可见打开
            // app.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            Dispatch excels = app.getProperty("Workbooks").toDispatch();// 获取文挡属性

            System.out.println("打开文档 >>> " + inputFile);
            // 调用Documents对象中Open方法打开文档，并返回打开的文档对象Document
            excel = Dispatch.call(excels, "Open", inputFile, false, true).toDispatch();
            // 调用Document对象方法，将文档保存为pdf格式
            System.out.println("转换文档 [" + inputFile + "] >>> [" + pdfFile + "]");
            // Excel 不能调用SaveAs方法
            Dispatch.call(excel, "ExportAsFixedFormat", xlFormatPDF, pdfFile);

            long end = System.currentTimeMillis();

            System.out.println("用时：" + (end - start) + "ms.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("========Error:文档转换失败：" + e.getMessage());
        } finally {
            Dispatch.call(excel, "Close", false);
            System.out.println("关闭文档");
            if (app != null)
                app.invoke("Quit", new Variant[] {});
        }
        ComThread.Release();
        ComThread.quitMainSTA();
        return false;
    }

    /**
     * WPS转PDF
     * @param inputFile
     * @param pdfFile
     */
    public void wps2PDF(String inputFile,String pdfFile) {
        File sFile = new File(inputFile);
        File tFile = new File(pdfFile);
        ActiveXComponent wps = null;
        try {
            ComThread.InitSTA();
            wps = new ActiveXComponent("wps.application");
            ActiveXComponent doc = wps.invokeGetComponent("Documents").invokeGetComponent("Open", new Variant(sFile.getAbsolutePath()));
            doc.invoke("ExportPdf", new Variant(tFile.getAbsolutePath()));
            doc.invoke("Close");
            doc.safeRelease();
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (wps != null) {
                wps.invoke("Terminate");
                wps.safeRelease();
            }
            ComThread.Release();
        }
    }
    /**
     * 测试
     *
     * @param args
     * @author SHANHY
     */
    public static void main(String[] args) {
        ConvertToPdf d = new ConvertToPdf();
        d.convert2PDF("g:\\test.docx", "g:\\test.pdf");
        d.convert2PDF("g:\\testppt.pptx", "g:\\testppt.pdf");
        d.convert2PDF("g:\\testexcel.xlsx", "g:\\testexcel.pdf");
    }

}



