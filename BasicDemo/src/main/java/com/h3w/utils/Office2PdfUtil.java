package com.h3w.utils;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * 利用jodconverter(基于OpenOffice服务)将文件(*.doc、*.docx、*.xls、*.ppt)转化为html格式或者pdf格式，
 * 使用前请检查OpenOffice服务是否已经开启, OpenOffice进程名称：soffice.exe | soffice.bin
 *
 * @author hyyd
 */
public class Office2PdfUtil {
    private static final Logger log = LoggerFactory.getLogger(Office2PdfUtil.class);

    private static Office2PdfUtil office2PdfUtil;

    /**
     * 获取Office2PdfUtil实例
     */
    public static synchronized Office2PdfUtil getOffice2PdfUtilInstance() {
        if (office2PdfUtil == null) {
            office2PdfUtil = new Office2PdfUtil();
        }
        return office2PdfUtil;
    }

    /**
     * 转换文件成html/pdf
     *
     * @param fromFileInputStream:
     * @throws IOException
     */
    public String file2Html(InputStream fromFileInputStream, String toFilePath,String type) throws IOException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timesuffix = sdf.format(date);
        String docFileName = null;
        String htmFileName = null;
        if("doc".equals(type)){
            docFileName = "doc_" + timesuffix + ".doc";
            htmFileName = "doc_" + timesuffix + ".html";
        }else if("docx".equals(type)){
            docFileName = "docx_" + timesuffix + ".docx";
            htmFileName = "docx_" + timesuffix + ".html";
        }else if("xls".equals(type)){
            docFileName = "xls_" + timesuffix + ".xls";
            htmFileName = "xls_" + timesuffix + ".html";
        }else if("ppt".equals(type)){
            docFileName = "ppt_" + timesuffix + ".ppt";
            htmFileName = "ppt_" + timesuffix + ".html";
        }else{
            return null;
        }

        File htmlOutputFile = new File(toFilePath + File.separatorChar + htmFileName);
        File docInputFile = new File(toFilePath + File.separatorChar + docFileName);
        if (htmlOutputFile.exists())
            htmlOutputFile.delete();
        htmlOutputFile.createNewFile();
        if (docInputFile.exists())
            docInputFile.delete();
        docInputFile.createNewFile();
        /**
         * 由fromFileInputStream构建输入文件
         */
        try {
            OutputStream os = new FileOutputStream(docInputFile);
            int bytesRead = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((bytesRead = fromFileInputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            os.close();
            fromFileInputStream.close();
        } catch (IOException e) {
        }

        OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
        try {
            connection.connect();
        } catch (ConnectException e) {
            System.err.println("文件转换出错，请检查OpenOffice服务是否启动。");
        }
        // convert
        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
        converter.convert(docInputFile, htmlOutputFile);
        connection.disconnect();
        // 转换完之后删除word文件
        docInputFile.delete();
        return htmFileName;
    }

    /**
     * 转成pdf
     * @param sourceFile
     * @param pdfFile
     * @return
     */
    public static String file2pdf(File sourceFile, File pdfFile){
        final ExecutorService exec = Executors.newFixedThreadPool(1);

        Callable<String> call = new Callable<String>() {
            public String call() throws Exception {
                //开始执行耗时操作
                //Thread.sleep(1000 * 2);
                String message = "";
                log.debug("转换pdf文件：src="+sourceFile.getPath()+",pdf="+pdfFile.getPath());
                if(sourceFile.exists()) {
                    if(!pdfFile.exists()) {
                        //OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
                        OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1",8100);
                        try {
                            connection.connect();
                            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
                            converter.convert(sourceFile, pdfFile);
                            pdfFile.createNewFile();
                            connection.disconnect();
                            System.out.println("转换pdf成功");
                            message = "OK";
                        } catch (ConnectException e) {
                            //e.printStackTrace();
                            log.error("OpenOffice服务未启动",e);
                            message = "OpenOffice服务未启动";

                        } catch (com.artofsolving.jodconverter.openoffice.connection.OpenOfficeException e) {
                            //e.printStackTrace();
                            log.error("读取文件失败",e);
                            message = "读取文件失败";

                        } catch (Exception e){
                            log.error("其他异常",e);
                            message = "其他异常";
                        }
                    } else {
                        log.debug("已转换为PDF，无需再次转换");
                        message = "OKED";
                    }
                } else {
                    log.debug("要转换的文件不存在");
                    message = "要转换的文件不存在";
                }
                return message;
            }
        };

        String msg = "";
        try {
            Future<String> future = exec.submit(call);
            String obj = future.get(1000 * 300, TimeUnit.MILLISECONDS); //任务处理超时时间设为 30 秒
            msg = obj;
            System.out.println("任务成功返回:" + obj);
        } catch (Exception e) {
            System.out.println("处理失败.");
            e.printStackTrace();
            return "任务处理超时";
        }
        // 关闭线程池
        exec.shutdown();
        return msg;

    }

    public static void main(String[] args) throws IOException {
        Office2PdfUtil coc2HtmlUtil = getOffice2PdfUtilInstance();
        File file = null;
        FileInputStream fileInputStream = null;

//        file = new File("D:/poi-test/exportExcel.xls");
//        fileInputStream = new FileInputStream(file);
////		coc2HtmlUtil.file2Html(fileInputStream, "D:/poi-test/openOffice/xls","xls");
//        coc2HtmlUtil.file2pdf(fileInputStream, "D:/poi-test/openOffice/xls","xls");
//
//        file = new File("D:/poi-test/test.doc");
//        fileInputStream = new FileInputStream(file);
////		coc2HtmlUtil.file2Html(fileInputStream, "D:/poi-test/openOffice/doc","doc");
//        coc2HtmlUtil.file2pdf(fileInputStream, "D:/poi-test/openOffice/doc","doc");
//
//        file = new File("D:/poi-test/周报模版.ppt");
//        fileInputStream = new FileInputStream(file);
////		coc2HtmlUtil.file2Html(fileInputStream, "D:/poi-test/openOffice/ppt","ppt");
//        coc2HtmlUtil.file2pdf(fileInputStream, "D:/poi-test/openOffice/ppt","ppt");

        file = new File("D:/upload/数据查询接口方案.docx");
        fileInputStream = new FileInputStream(file);
//		coc2HtmlUtil.file2Html(fileInputStream, "D:/upload","docx");
        System.out.println(coc2HtmlUtil.file2pdf(file, new File("D:/upload/转换后.html")));

    }

}
