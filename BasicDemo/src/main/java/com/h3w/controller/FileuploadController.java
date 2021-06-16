package com.h3w.controller;

import com.h3w.ResultObject;
import com.h3w.StaticConstans;
import com.h3w.entity.Fileupload;
import com.h3w.exception.CustomException;
import com.h3w.service.FileuploadService;
import com.h3w.utils.ConvertToPdf;
import com.h3w.utils.DateUtil;
import com.h3w.utils.FileUtil;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hyyd on 2017/7/4.
 */
@Controller
@RequestMapping("/file")
public class FileuploadController {
    @Autowired
    private FileuploadService fileuploadService;
    public static FileuploadController fileuploadController;
    @PostConstruct
    public void init() {
        fileuploadController = this;
        fileuploadController.fileuploadService = this.fileuploadService;
    }

    @Value("${system.upload}")
    public String fileuploadpath;

    /**
     * 单文件上传,返回文件的访问url
     *
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(HttpServletRequest request) throws IllegalStateException,
            IOException, JSONException {
        JSONObject object = new JSONObject();
        ContentInfoUtil util = new ContentInfoUtil();
        ContentInfo info;
//        List<String> fileIdList = new ArrayList<String>();
        String mimeType = null;
        Fileupload fileUpload = new Fileupload();

        String datedir = DateUtil.formatDateToNumber(new Date());
        //文件上传的绝对路径
        String dirPath = "";
        String uploadDir = "";
        System.out.println("返回的文件路径："+uploadDir);

        // 创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        // 判断 request 是否有文件上传,即多部分请求
        if (multipartResolver.isMultipart(request)) {
            // 转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            // 取得request中的所有文件名
            Iterator<String> iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                // 取得上传文件
                MultipartFile file = multiRequest.getFile(iter.next());
                if (file != null) {
                    // 取得当前上传文件的文件名称
                    String originalFileName = file.getOriginalFilename();
                    // 取得当前上传文件的文件大小
                    long filesize = file.getSize();
                    // 如果名称不为“”,说明该文件存在，否则说明该文件不存在
                    if (originalFileName.trim() != "") {
                        String fileId = UUID.randomUUID().toString();
                        String realName = originalFileName.substring(0,
                                originalFileName.lastIndexOf("."));
                        String extName = originalFileName.substring(
                                realName.length(), originalFileName.length());

                        String dirname = extName.substring(1);
                        dirPath += fileuploadpath+datedir;
                        uploadDir = checkDir(request, dirPath);
                        // 重命名上传后的文件名
                        String fileName = fileId + "-" + new Date().getTime();
                        // 定义上传路径
                        File localFile = new File(uploadDir, fileName + extName);
                        file.transferTo(localFile);

                        info = util.findMatch(localFile);
                        if (info != null)
                            mimeType = info.getMimeType();
                        if (mimeType == null)
                            mimeType = "application/octet-stream";

                        if(mimeType.toString().length()<=50){
                            fileUpload.setFiletype(mimeType.toString());
                        }
                        fileUpload.setFilename(fileName);
                        fileUpload.setRealname(realName);
                        fileUpload.setFileExtname(extName);
                        fileUpload.setFilepath(dirPath);
                        fileUpload.setFilesize(filesize);
                        fileUpload.setUploadtime(new Timestamp(
                                new Date().getTime()));
                        try {
                            fileuploadController.fileuploadService.insertSelect(fileUpload);

                            if(extName.equals(".doc")||extName.equals(".docx")||extName.equals(".ppt")||extName.equals(".pptx")){
                                String realPath = uploadDir;//获取文件上传后的保存绝对路径
                                ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
                                singleThreadExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("执行线程：");
                                        try {
                                            System.out.println("开始转换pdf>>>>>>");
                                            String source = realPath+"/"+fileUpload.getFilename() + fileUpload.getFileExtname();
                                            String target = realPath+"/"+fileUpload.getFilename() + ".pdf";

                                            boolean rlt = ConvertToPdf.convert2PDF(source, target);
                                            if (rlt) {
                                                fileUpload.setTopdf(Fileupload.TOPDF_ED);
                                                fileuploadController.fileuploadService.updateSelect(fileUpload);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                                singleThreadExecutor.shutdown();
                                System.out.println("关闭线程:"+new Date(System.nanoTime()));
                                while (true) {
                                    if (singleThreadExecutor.isTerminated()) {
                                        System.out.println("线程结束了！");
                                        break;
                                    }
                                    Thread.sleep(200);
                                }
                            }
                            String fileurl = getUrlPath(request,realName,fileName);
                            object.put("fileid",fileUpload.getId());
                            object.put("filename",fileName);
                            object.put("ext",extName);
                            object.put("fileurl",fileurl);
                            object.put("realname",realName+extName);
                            return ResultObject.newJSONData(object).toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return ResultObject.newError("上传错误！").toString();
        } else {
            return null;
        }
    }


    /**
     * 多文件上传
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/upload/files", method = RequestMethod.POST)
    public String uploadFileData(HttpServletRequest request) throws IllegalStateException,
            IOException, JSONException {
        Integer failnum = 0;
        Integer successnum = 0;
        JSONObject object = new JSONObject();
        List<Integer> fileids = new ArrayList<>();
        List<String> fileurls = new ArrayList<>();
//        ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
        ContentInfoUtil util = new ContentInfoUtil();
        ContentInfo info;
//        List<String> fileIdList = new ArrayList<String>();
        String mimeType = null;
        String datedir = DateUtil.formatDateToNumber(new Date());
        //文件上传的绝对路径
        String dirPath = "";
        dirPath += fileuploadpath+datedir;
        String uploadDir = checkDir(request, dirPath);
        System.out.println("返回的文件路径："+uploadDir);

        // 创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        // 判断 request 是否有文件上传,即多部分请求
        if (multipartResolver.isMultipart(request)) {
            // 转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
//            //取得多张图片
//            List<MultipartFile> files = multiRequest.getFiles("file[]");
            // 取得request中的所有文件名
            Iterator<String> iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                // 取得上传文件
                MultipartFile file = multiRequest.getFile(iter.next());
                if (file != null) {
                    // 取得当前上传文件的文件名称
                    String originalFileName = file.getOriginalFilename();
                    // 取得当前上传文件的文件大小
                    long filesize = file.getSize();
                    // 如果名称不为“”,说明该文件存在，否则说明该文件不存在
                    if (originalFileName.trim() != "") {
                        Fileupload fileUpload = new Fileupload();
                        String fileId = UUID.randomUUID().toString();
                        String realName = originalFileName.substring(0,
                                originalFileName.lastIndexOf("."));
                        String extName = originalFileName.substring(
                                realName.length(), originalFileName.length());
                        // 重命名上传后的文件名
                        String fileName = fileId + "-" + new Date().getTime();
                        // 定义上传路径
                        File localFile = new File(uploadDir, fileName + extName);
                        file.transferTo(localFile);
                        System.out.println("上传后的文件名："+localFile.getName());

                        info = util.findMatch(localFile);
                        if (info != null)
                            mimeType = info.getMimeType();
                        if (mimeType == null)
                            mimeType = "application/octet-stream";

                        fileUpload.setFiletype(mimeType.toString());
                        fileUpload.setFilename(fileName);
                        fileUpload.setRealname(realName);
                        fileUpload.setFileExtname(extName);
                        fileUpload.setFilepath(dirPath);
                        fileUpload.setFilesize(filesize);
                        fileUpload.setUploadtime(new Timestamp(
                                new Date().getTime()));

                        try {
                            fileuploadController.fileuploadService.insertSelect(fileUpload);

                            fileids.add(fileUpload.getId());
                            fileurls.add(getUrlPath(request,realName,fileName));
                            successnum++;
                        } catch (Exception e) {
                            failnum++;
                        }
                    }
                }
            }
            object.put("statusCode",200);
            object.put("message","已成功上传"+successnum+"张图片，失败数"+failnum);
            object.put("fileids",fileids);
            object.put("fileurls",fileurls);
            return object.toString();
        } else {
            object.put("statusCode",300);
            object.put("message","上传出错，已成功上传"+successnum+"张图片，失败数"+failnum);
            return object.toString();
        }
    }


    /**
     * 上传文件并转为PDF(由于转pdf异常，已经去掉转为pdf)
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException
     */
    @ResponseBody
    @RequestMapping(value = "/upload/topdf", method = RequestMethod.POST)
    public String uploadFileToPdf(HttpServletRequest request) throws IllegalStateException,
            IOException, JSONException {
//        ResultObject object = new ResultObject();
        JSONObject object = new JSONObject();
        ContentInfoUtil util = new ContentInfoUtil();
        ContentInfo info;
        String mimeType = null;
        Fileupload fileUpload = new Fileupload();

        String datedir = DateUtil.formatDateToNumber(new Date());
        //文件上传的绝对路径
        String dirPath = "";
        String uploadDir = "";
        System.out.println("返回的文件路径："+uploadDir);

        // 创建一个通用的多部分解析器
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        // 判断 request 是否有文件上传,即多部分请求
        if (multipartResolver.isMultipart(request)) {
            // 转换成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            // 取得request中的所有文件名
            Iterator<String> iter = multiRequest.getFileNames();
            while (iter.hasNext()) {
                // 取得上传文件
                MultipartFile file = multiRequest.getFile(iter.next());
                if (file != null) {
                    // 取得当前上传文件的文件名称
                    String originalFileName = file.getOriginalFilename();
                    // 取得当前上传文件的文件大小
                    long filesize = file.getSize();
                    // 如果名称不为“”,说明该文件存在，否则说明该文件不存在
                    if (originalFileName.trim() != "") {
                        String fileId = UUID.randomUUID().toString();
                        String realName = originalFileName.substring(0,
                                originalFileName.lastIndexOf("."));
                        String extName = originalFileName.substring(
                                realName.length(), originalFileName.length());

                        String dirname = extName.substring(1);
                        dirPath += fileuploadpath+dirname+"/"+datedir;
                        uploadDir = checkDir(request, dirPath);
                        // 重命名上传后的文件名
                        String fileName = fileId + "-" + new Date().getTime();
                        // 定义上传路径
                        File localFile = new File(uploadDir, fileName + extName);
                        file.transferTo(localFile);

                        info = util.findMatch(localFile);
                        if (info != null)
                            mimeType = info.getMimeType();
                        if (mimeType == null)
                            mimeType = "application/octet-stream";

                        if(mimeType.toString().length()<=50){
                            fileUpload.setFiletype(mimeType.toString());
                        }
                        fileUpload.setFilename(fileName);
                        fileUpload.setRealname(realName);
                        fileUpload.setFileExtname(extName);
                        fileUpload.setFilepath(dirPath);
                        fileUpload.setFilesize(filesize);
                        fileUpload.setUploadtime(new Timestamp(
                                new Date().getTime()));
                        try {
                            fileuploadController.fileuploadService.insertSelect(fileUpload);

                            String fileurl = getUrlPath(request,realName,fileName);
                            object.put("rel",fileUpload.getId());
                            object.put("filename",fileName);
                            object.put("statusCode",ResultObject.STATUS_CODE_SUCCESS);
                            object.put("fileurl",fileurl);
                            object.put("message",realName+extName);
                            return object.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            object.put("message","上传错误！");
            object.put("statusCode",ResultObject.STATUS_CODE_FAILURE);
            return object.toString();
        } else {
            return null;
        }
    }

    /**
     * 文件下载/查看（pdf直接可查看）
     * @param fileid 文件的id
     * @param request
     * @param response
     * @return
     * @throws JSONException
     * @throws ServletException
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/get/{realname}")
    public void getFile(String fileid,HttpServletRequest request,HttpServletResponse response) throws JSONException, ServletException, IOException {
        JSONObject json = new JSONObject();
        Fileupload uploadedfile=fileuploadService.getByFilename(fileid);
        if(uploadedfile == null){
            throw new CustomException(300,"未找到文件！");
        }
//        String filepath = "/"+uploadedfile.getFilepath()+ "/"+uploadedfile.getFilename();
        String filepath = "/"+uploadedfile.getFilename();
        String realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        //如果已经转为pdf,就加载pdf
        if(StaticConstans.STATUS_1.equals(uploadedfile.getTopdf())){
            filepath+= ".pdf";
        }else {
            String extName = uploadedfile.getFileExtname();
            if(extName.equals(".doc")||extName.equals(".docx")||extName.equals(".ppt")||extName.equals(".pptx")){
                String source = realPath+"/"+uploadedfile.getFilename() + uploadedfile.getFileExtname();
                String target = realPath+"/"+uploadedfile.getFilename() + ".pdf";
//                    File localFile = new File(realPath, uploadedfile.getFilename() + uploadedfile.getFileExtname());
//                    File pdfFile = new File(realPath, uploadedfile.getFilename() + ".pdf");

                boolean rlt = ConvertToPdf.convert2PDF(source, target);
                if (rlt) {
                    uploadedfile.setTopdf(StaticConstans.STATUS_1);
                    fileuploadController.fileuploadService.updateSelect(uploadedfile);
                    filepath += ".pdf";
                } else {
                    filepath += extName;
                }

            }else {
                filepath += extName;
            }
        }

        //转发到文档的地址
//        request.getRequestDispatcher(filepath).forward(request,response);

        String pic_path=realPath+filepath;//图片路径
        File file = new File(pic_path);
        if (file.exists()) {//如果路径存在
            FileInputStream fileInputStream;
            try {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
//                response.setContentType(uploadedfile.getFiletype()); // 设置返回的文件类型
//                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(uploadedfile.getRealname()+uploadedfile.getFileExtname(), "UTF-8"));
                response.addHeader("Content-Disposition", "attachment;filename=" + new String((uploadedfile.getRealname()+uploadedfile.getFileExtname()).getBytes("GB2312"),"ISO-8859-1"));
                response.addHeader("X-Frame-Options","ALLOW-FROM");
                fileInputStream = new FileInputStream(pic_path);
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

    /**
     * 文件下载
     * @param fileid
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping("/down/{fileid}")
    public void getDown(@PathVariable String fileid,HttpServletRequest request,HttpServletResponse response) {
        Fileupload uploadedfile=fileuploadService.getByFilename(fileid);
        if(uploadedfile == null){
            System.out.println("未找到文件！");
        }
        String realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        String pic_path=realPath+"/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();//图片路径
        File file = new File(pic_path);
        if (file.exists()) {//如果路径存在
            FileInputStream fileInputStream;
            try {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setContentType(uploadedfile.getFiletype()); // 设置返回的文件类型
//                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(uploadedfile.getRealname()+uploadedfile.getFileExtname(), "UTF-8"));
                response.addHeader("Content-Disposition", "attachment;filename=" + new String((uploadedfile.getRealname()+uploadedfile.getFileExtname()).getBytes("GB2312"),"ISO-8859-1"));
                fileInputStream = new FileInputStream(pic_path);
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
    public String getpdfpath(Fileupload uploadedfile,String realPath,String pic_path){
        if(StaticConstans.STATUS_1.equals(uploadedfile.getTopdf())){
            pic_path+= ".pdf";
        }else {
            String extName = uploadedfile.getFileExtname();
            if(extName.equals(".doc")||extName.equals(".docx")||extName.equals(".ppt")||extName.equals(".pptx")){
                try {

                    String source = realPath+"/"+uploadedfile.getFilename() + uploadedfile.getFileExtname();
                    String target = realPath+"/"+uploadedfile.getFilename() + ".pdf";
//                    File localFile = new File(realPath, uploadedfile.getFilename() + uploadedfile.getFileExtname());
//                    File pdfFile = new File(realPath, uploadedfile.getFilename() + ".pdf");

                    boolean rlt = ConvertToPdf.convert2PDF(source, target);
                    if (rlt) {
                        uploadedfile.setTopdf(StaticConstans.STATUS_1);
                        fileuploadController.fileuploadService.updateSelect(uploadedfile);
                        pic_path += ".pdf";
                    } else {
                        pic_path += extName;
                    }
                }catch (Exception e){
                    e.printStackTrace();
//                    throw new CustomException("转换pdf异常");
                    pic_path += extName;
                }
            }else {
                pic_path += extName;
            }
        }
        return pic_path;
    }

    /**
     * 获取文件的直接访问路径
     * @param filename
     * @param request
     * @return
     * @throws JSONException
     */
    @ResponseBody
    @RequestMapping("/getfilepath/{filename}")
    public String getFilePath(@PathVariable String filename,HttpServletRequest request) throws JSONException {
        JSONObject json = new JSONObject();
        Fileupload uploadedfile=fileuploadService.getByFilename(filename);
        if(uploadedfile == null){
            System.out.println("未找到文件！");
            json.put("statusCode",300);
            json.put("message","未找到文件！");
            return json.toString();
        }
        String fileurl = getUrlPath(request,uploadedfile.getRealname(),uploadedfile.getFilename());
        String extName = uploadedfile.getFileExtname();
        if(extName.equals(".mp4")||extName.equals(".avi")){
            String realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
            String pic_path = getFilePath(request,uploadedfile.getFilepath(),uploadedfile.getFilename());
            fileurl = getpdfpath(uploadedfile,realPath,pic_path);
        }

//        File file = new File(fileurl);
//        if (!file.exists()){
//            json.put("message","未找到该文件");
//        }
        json.put("statusCode",200);
        json.put("path",fileurl);
        return json.toString();
    }

    public static String getFileuploadAbsolutepath(Integer fileid,HttpServletRequest request) throws JSONException {
        Fileupload uploadedfile=fileuploadController.fileuploadService.getById(fileid);
        if(uploadedfile == null){
            return "";
        }
        String pic_path = getFilePath(request,uploadedfile.getFilepath(),uploadedfile.getFilename()+uploadedfile.getFileExtname());
        return pic_path;
    }

    public static String getFileuploadAbsolutepath(String filename,HttpServletRequest request) throws JSONException {
        Fileupload uploadedfile=fileuploadController.fileuploadService.getByFilename(filename);
        if(uploadedfile == null){
            return "";
        }
        String pic_path = getFilePath(request,uploadedfile.getFilepath(),uploadedfile.getFilename()+uploadedfile.getFileExtname());
        return pic_path;
    }

    public static String getFileuploadRelativepath(String filename,HttpServletRequest request) throws JSONException {
        Fileupload uploadedfile=fileuploadController.fileuploadService.getByFilename(filename);
        if(uploadedfile == null){
            return "";
        }
        String pic_path = uploadedfile.getFilepath()+ "/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();
        return pic_path;
    }

    /**
     * 文件删除
     * @param filename 文件的名称id
     * @param request
     * @return
     */
    @RequestMapping("/del/{filename}")
    @ResponseBody
    public ResultObject deleteFile(@PathVariable Integer filename, HttpServletRequest request){
        Fileupload uploadedfile=fileuploadService.getById(filename);
        if(uploadedfile == null){
            System.out.println("未找到文件！");
            return ResultObject.newError("未找到文件");
        }else {
            fileuploadService.deleteById(uploadedfile.getId());
        }
        String realPath = "";
        realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        String pic_path=realPath+"/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();//图片路径
        boolean re = FileUtil.deleteFile(pic_path);
        if(re){
            return ResultObject.newOk("删除成功！");
        }else {
            return ResultObject.newError("删除失败！");
        }
    }

    public static boolean deleteFilename(String filename,HttpServletRequest request){
        Fileupload uploadedfile=fileuploadController.fileuploadService.getByFilename(filename);
        if(uploadedfile == null){
            System.out.println("未找到文件！");
        }else {
            fileuploadController.fileuploadService.deleteById(uploadedfile.getId());
        }
        String realPath = "";
        realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        String pic_path=realPath+"/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();//图片路径
//        System.out.println("返回的文件路径："+pic_path);
        return FileUtil.deleteFile(pic_path);
    }

    public static boolean deleteFileId(Integer fid,HttpServletRequest request){
        Fileupload uploadedfile=fileuploadController.fileuploadService.getById(fid);
        if(uploadedfile == null){
            System.out.println("未找到文件！");
        }else {
            fileuploadController.fileuploadService.deleteById(uploadedfile.getId());
        }
        String realPath = "";
        realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        String pic_path=realPath+"/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();//图片路径
        return FileUtil.deleteFile(pic_path);
    }

    /**
     * 文件上传路径
     *
     * @param request
     * @return
     */
    public static String checkDir(HttpServletRequest request, String dirPath) {
//        System.out.println("路径："+dirPath);
//        System.out.println("项目路径："+ request.getSession().getServletContext().getRealPath(""));
        String realPath = request.getSession().getServletContext().getRealPath("")+dirPath; // 获取文件上传后的保存绝对路径
//        System.out.println("获取文件上传后的保存绝对路径："+realPath);
        File file = new File(realPath);
        if (!file.exists())// 如果路径不存在则创建
            file.mkdirs();
        return realPath;
    }

    public static String checkDirDown(HttpServletRequest request, String dir) {
//        System.out.println("路径："+dir);
        String realPath = request.getSession().getServletContext().getRealPath("")+dir;// 获取文件上传后的保存绝对路径
//        System.out.println("获取文件上传后的保存绝对路径："+realPath);
        File file = new File(realPath);
        if (!file.exists())// 如果路径不存在则创建
            file.mkdirs();
        return realPath;
    }


    //拼接文件下载和查看的链接
    public static String getUrlPath(HttpServletRequest request,String realname,String filename){
        String path = request.getContextPath();
        String urlpath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        urlpath+= "file/get/"+realname+"?fileid="+ filename;
        return urlpath;
    }

    //获取文件下载链接
    public static String getDownUrl(HttpServletRequest request,String realname,String filename){
        String path = request.getContextPath();
        String urlpath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
//        urlpath+= "file/down/"+realname+"?fileid="+ filename;
        urlpath+= "file/down/"+ filename+"?name="+realname;
        return urlpath;
    }

    //拼接文件在服务器上的绝对路径
    public static String getFilePath(HttpServletRequest request,String filepath,String filename){
        String path = request.getContextPath();
        String urlpath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        urlpath+= filepath + "/"+filename;
        return urlpath;
    }
}
