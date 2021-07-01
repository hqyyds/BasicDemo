package com.h3w.utils;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class FileUtil {

    /**
     * 保存文件
     * @param file
     * @param path
     * @return
     */
    public static boolean saveFile(MultipartFile file, String path){
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        String saveName = Identities.uuid2() + "." + FilenameUtil.getFileExt(file.getOriginalFilename());
        //文件保存
        File saveFile = new File(path + "/" + saveName);
        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), saveFile);
            System.out.println("保存路径"+path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = FileUtil.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = FileUtil.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }
    /**
     * FileChannels复制文件
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFile(File source, File dest) throws IOException {

        try {
            if (source.exists()) {
                FileChannel inputChannel = null;
                FileChannel outputChannel = null;
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                inputChannel.close();
                outputChannel.close();
                System.out.println("复制文件成功！"+dest.getName());
            }else {
                System.out.println("复制源文件不存在！");
            }

        } finally {
        }
    }
    /**
     * 把整个文件夹打包下载
     * @param dir
     * @param filePath
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public static String downLoadDir(File dir, String filePath , HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            //这里的文件你可以自定义是.rar还是.zip
            File file = new File(filePath);
            if (!file.exists()){
                file.createNewFile();
            }else{
                //如果压缩包已经存在则删除后重新打包压缩
                file.delete();
            }
            response.reset();
            //创建文件输出流
            FileOutputStream fous = new FileOutputStream(file);
            /**打包的方法用到ZipOutputStream这样一个输出流,所以这里把输出流转换一下*/
            ZipOutputStream zipOut = new ZipOutputStream(fous);
            /**这个方法接受的就是一个所要打包文件的集合，还有一个ZipOutputStream*/
            zipDir(dir, "",zipOut);
            zipOut.close();
            fous.close();
            return "OK";

        }catch (Exception e) {
            e.printStackTrace();
            //return "文件下载出错" ;
        }
        return "文件下载出错";
    }
    /**
     * 写入压缩包
     */
    public static void zipDir(File file, String parentPath, ZipOutputStream zos) {
        if (file.exists()) { // 需要压缩的文件夹是否存在
            if (file.isDirectory()) {// 判断是否是文件夹

                parentPath += file.getName() + File.separator; // 文件夹名称 + "/"
                File[] files = file.listFiles(); // 获取文件夹下的文件夹或文件
                if (files.length != 0) {
                    for (File f : files) {
                        zipDir(f, parentPath, zos);
                    }
                } else { // 空目录则创建当前目录
                    try {
                        zos.putNextEntry(new ZipEntry(parentPath));
                    } catch (IOException e) {
                        System.out.println(">>>>>>>:创建当前目录失败！");
                    }
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }
                } catch (Exception e) {
                    System.out.println("文件未找到！");
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                            if (file != null && file.exists()) {
                                file.delete();
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("输入流关闭失败！");
                    }
                }
            }
        }
    }

    /**
     * 文件导出保存
     * @param request
     * @param response
     * @param workbook
     * @param filename
     * @param ext
     * @throws IOException
     */
    public void writeFile(HttpServletRequest request, HttpServletResponse response, HSSFWorkbook workbook, String filename, String ext) throws IOException {
        // 保存到导出记录
        String subpath = "upload/export/" + DateUtil.formatDate(new Date(), "yyyyMM");
        String path = request.getSession().getServletContext().getRealPath("/") + subpath;
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        String saveName = Identities.uuid2() + ext;
        File toFile = new File(path + "/" + saveName);
        toFile.createNewFile();
        FileOutputStream excelFileOutPutStream = new FileOutputStream(toFile);
        workbook.write(excelFileOutPutStream);
        excelFileOutPutStream.flush();
        excelFileOutPutStream.close();
        if (toFile.exists()) {//如果路径存在
            FileInputStream fileInputStream;
            try {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setContentType("application/msexcel"); // 设置返回的文件类型
                response.setHeader("Content-disposition",
                        "attachment;filename=\"" + new String(filename.getBytes("gb2312"), "ISO8859-1") + ext +"\"");
                fileInputStream = new FileInputStream(toFile.getPath());
                int i = fileInputStream.available(); // 得到文件大小
                byte data[] = new byte[i];
                fileInputStream.read(data); // 读数据
                fileInputStream.close();
                ServletOutputStream outputStream = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
                outputStream.write(data); // 输出数据
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//  // 删除单个文件
//  String file = "c:/test/test.txt";
//  DeleteFileUtil.deleteFile(file);
//  System.out.println();
        // 删除一个目录
        String dir = "D:/home/web/upload/upload/files";
        FileUtil.deleteDirectory(dir);
//  System.out.println();
//  // 删除文件
//  dir = "c:/test/test0";
//  DeleteFileUtil.delete(dir);

    }

}