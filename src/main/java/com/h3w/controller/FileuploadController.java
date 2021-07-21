package com.h3w.controller;

import com.h3w.ResultObject;
import com.h3w.entity.Fileupload;
import com.h3w.entity.User;
import com.h3w.utils.*;
import com.h3w.service.FileuploadService;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

/**
 * 文件上传接口
 * @author hyyds
 * @date 2021/6/16
 */
@Api(value = "上传接口", tags = "文件上传")
@Controller
@RequestMapping("/file")
public class FileuploadController {
    @Autowired
    private FileuploadService fileuploadService;
    @Autowired
    private LoginController loginController;
    public static FileuploadController fileuploadController;
    @PostConstruct
    public void init() {
        fileuploadController = this;
        fileuploadController.fileuploadService = this.fileuploadService;
        fileuploadController.loginController = this.loginController;
    }
    @Value("${system.upload-path}")
    public String fileBase;
    public static final String initFileBase = "WEB-INF/upload/";

    /**
     * 文件上传,返回文件的访问url
     *
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @ApiOperation("上传文件")
    @ResponseBody
    @PostMapping(value = "/upload")
    public String uploadFile(HttpServletRequest request) throws IllegalStateException,
            IOException {
        User user = fileuploadController.loginController.getCurrentUser();
//        Integer userId = user.id;
        ContentInfoUtil util = new ContentInfoUtil();
        ContentInfo info;
//        List<String> fileIdList = new ArrayList<String>();
        String mimeType = null;
        Fileupload fileUpload = new Fileupload();

        String datedir = DateUtil.formatDateToNumber(new Date());
        //文件上传的绝对路径
//        String dirPath = initFileBase + datedir;
//        String uploadDir = checkDir(request, dirPath);
        String dirPath = fileBase + datedir;
        String uploadDir = fileBase+datedir;
        File f = new File(uploadDir);
        if (!f.exists())// 如果路径不存在则创建
            f.mkdirs();
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
                    //上传文件是否已存在
                    boolean haveold = false;
                    String filemd5 = MD5Util.getFileMD5String(file);
                    Fileupload uploadedfile = fileuploadController.fileuploadService.getByMd5(filemd5);
                    if(uploadedfile != null){
                        String realPath = "";
                        if(StringUtil.matchReg(uploadedfile.getFilepath(),"^WEB-INF/")){
                            realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
                        }else {
                            realPath = checkDirDown2(uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
                        }
                        String pic_path=realPath+"/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();//图片路径
                        File ofile = new File(pic_path);
                        if (ofile.exists()) {//如果路径存在
                            haveold = true;
                        }
                    }
                    //如果上传文件已存在，直接复制
                    if(haveold){
                        String realName = originalFileName.substring(0,
                                originalFileName.lastIndexOf("."));
                        BeanUtil.beanCopy(uploadedfile,fileUpload);
                        fileUpload.setRealname(realName);
                        fileUpload.setUploadtime(new Date());
                        fileUpload.setUserid(user.getId());
                        fileuploadController.fileuploadService.insertSelect(fileUpload);
                    }else {
                        // 如果名称不为“”,说明该文件存在，否则说明该文件不存在
                        if (originalFileName.trim() != "") {
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
                            System.out.println(localFile.getName());

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
                            fileUpload.setUserid(user.getId());
                            fileuploadController.fileuploadService.insertSelect(fileUpload);
                        }
                    }
                    JSONObject fobj = new JSONObject();
                    fobj.put("id",fileUpload.getId());
                    fobj.put("url",getUrlPath(request,fileUpload.getId()));
                    fobj.put("ext",fileUpload.getFileExtname());
                    return ResultObject.newJSONData(fobj).toString();
                }
            }
        } else {
            return ResultObject.newError("未找到上传文件").toString();
        }
        return ResultObject.newError("上传错误").toString();
    }

    /**
     * 多图片上传
     * @param request
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @ApiOperation("多文件上传")
    @ApiResponses({
            @ApiResponse(code = 200, message = "fileids文件id， fileurls文件地址"),
    })
    @ResponseBody
    @PostMapping(value = "/upload/files")
    public String uploadFileData(HttpServletRequest request) throws IllegalStateException,
            IOException, JSONException {
        Integer failnum = 0;
        Integer successnum = 0;
        JSONObject object = new JSONObject();
        List<Integer> fileids = new ArrayList<>();
        List<String> fileurls = new ArrayList<>();
        JSONArray files = new JSONArray();
        User user = fileuploadController.loginController.getCurrentUser();
        ContentInfoUtil util = new ContentInfoUtil();
        ContentInfo info;
//        List<String> fileIdList = new ArrayList<String>();
        String mimeType = null;
        String datedir = DateUtil.formatDateToNumber(new Date());
        //文件上传的绝对路径
        String dirPath = "";
        String uploadDir = "";
        if(StringUtil.isNotBlank(fileBase)){
            dirPath = fileBase + datedir;
            uploadDir = checkDir2(dirPath);
        }else {
            dirPath = initFileBase + datedir;
            uploadDir = checkDir(request, dirPath);
        }

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
                    //上传文件是否已存在
                    boolean haveold = false;
                    String filemd5 = MD5Util.getFileMD5String(file);
                    Fileupload uploadedfile = fileuploadController.fileuploadService.getByMd5(filemd5);
                    if(uploadedfile != null){
                        String realPath = "";
                        if(StringUtil.matchReg(uploadedfile.getFilepath(),"^WEB-INF/")){
                            realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
                        }else {
                            realPath = checkDirDown2(uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
                        }
                        String pic_path=realPath+"/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();//图片路径
                        File ofile = new File(pic_path);
                        if (ofile.exists()) {//如果路径存在
                            haveold = true;
                        }
                    }
                    //如果上传文件已存在，直接复制
                    if(haveold){
                        String realName = originalFileName.substring(0,
                                originalFileName.lastIndexOf("."));
                        copyFileupload(uploadedfile,realName,fileids,fileurls,files,request,user);
                        successnum++;
                    } else {
                        // 如果名称不为“”,说明该文件存在，否则说明该文件不存在
                        if (originalFileName.trim() != "") {
                            String fileId = UUID.randomUUID().toString();
                            String realName = originalFileName.substring(0,
                                    originalFileName.lastIndexOf("."));
                            String extName = originalFileName.substring(
                                    realName.length(), originalFileName.length());
                            // 重命名上传后的文件名
                            String fileName = fileId + "-" + new Date().getTime();
                            // 定义上传路径
                            File localFile = new File(uploadDir, fileName + extName);
                            try {
                                file.transferTo(localFile);
                            } catch (Exception e) {
                                object.put("statusCode", 300);
                                object.put("message", "无效的上传路径！");
                                return object.toString();
                            }
                            System.out.println("上传后的文件名：" + localFile.getName());

                            info = util.findMatch(localFile);
                            if (info != null)
                                mimeType = info.getMimeType();
                            if (mimeType == null)
                                mimeType = "application/octet-stream";

                            if (extName.equals(".pdf")) {
                                pdf2Image(localFile, dirPath, 300, 10, fileids, fileurls,files, request, user);
                            }
                            Fileupload fileUpload = new Fileupload();
                            fileUpload.setFiletype(mimeType.toString());
                            fileUpload.setFilename(fileName);
                            fileUpload.setRealname(realName);
                            fileUpload.setFileExtname(extName);
                            fileUpload.setFilepath(dirPath);
                            fileUpload.setFilesize(filesize);
                            fileUpload.setUploadtime(new Date());
                            fileUpload.setUserid(user.getId());
                            fileUpload.setFilemd5(filemd5);
                            try {
                                fileuploadController.fileuploadService.insertSelect(fileUpload);
                                if (!extName.equals(".pdf")) {
                                    fileids.add(fileUpload.getId());
                                    fileurls.add(getUrlPath(request, fileUpload.getId()));
                                    JSONObject fobj = new JSONObject();
                                    fobj.put("id",fileUpload.getId());
                                    fobj.put("url",getUrlPath(request,fileUpload.getId()));
                                    fobj.put("ext",extName);
                                    files.put(fobj);
                                }
                                successnum++;
                            } catch (Exception e) {
                                failnum++;
                            }
                        }
                    }
                }
            }
            object.put("statusCode",200);
            object.put("message","已成功上传"+successnum+"张图片，失败数"+failnum);
            object.put("fileids",fileids);
            object.put("fileurls",fileurls);
            object.put("files",files);
            return object.toString();
        } else {
            object.put("statusCode",300);
            object.put("message","上传出错，已成功上传"+successnum+"张图片，失败数"+failnum);
            return object.toString();
        }
    }
    /**
     * 文件下载和查看
     * @param id
     * @param request
     * @param response
     */
    @ApiOperation("文件下载和查看")
    @GetMapping(value = "/down/{id}")
    public void getFile(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) {
        Fileupload uploadedfile=fileuploadService.getById(id);
        if(uploadedfile == null){
            System.out.println("未找到文件！");
        }
        String realPath = "";
        if(StringUtil.matchReg(uploadedfile.getFilepath(),"^WEB-INF/")){
            realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        }else {
            realPath = checkDirDown2(uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        }
//        realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        String pic_path=realPath+"/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();//图片路径
//        System.out.println("返回的文件路径："+pic_path);
        File file = new File(pic_path);
        if (file.exists()) {//如果路径存在
            FileInputStream fileInputStream;
            try {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setContentType(uploadedfile.getFiletype()); // 设置返回的文件类型
                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(uploadedfile.getRealname()+uploadedfile.getFileExtname(), "UTF-8"));
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


    public static void pdf2Image(File file, String dstImgFolder, int dpi,int flag,List<Integer> fileids,List<String> fileurls,JSONArray files,HttpServletRequest request,User user) {
        PDDocument pdDocument;
        try {
            String imgPDFPath = file.getParent();
            int dot = file.getName().lastIndexOf('.');
            String imagePDFName = file.getName().substring(0, dot);

            if (createDirectory(imgPDFPath)) {
                pdDocument = PDDocument.load(file);

                PDFRenderer renderer = new PDFRenderer(pdDocument);
                int pages = pdDocument.getNumberOfPages();
//                if(flag > 0) {//大于0则打印具体页数
//                    if(flag<pages) {
//                        pages = flag;
//                    }
//                }

                StringBuffer imgFilePath = null;
                for (int i = 0; i < pages; i++) {
                    String imageName = imagePDFName;
                    String imgFilePathPrefix = imgPDFPath+File.separator + imagePDFName;
                    imgFilePath = new StringBuffer();
                    imgFilePath.append(imgFilePathPrefix);
                    imageName+= "-"+String.valueOf(i + 1);
                    imgFilePath.append("-");
                    imgFilePath.append(String.valueOf(i + 1));
                    imgFilePath.append(".png");
                    File dstFile = new File(imgFilePath.toString());
                    BufferedImage image = renderer.renderImageWithDPI(i, dpi);
                    ImageIO.write(image, "png", dstFile);

                    Fileupload fileUpload = new Fileupload();
                    fileUpload.setFiletype("image/png");
                    fileUpload.setRealname(imageName);
                    fileUpload.setFilename(imageName);
                    fileUpload.setFileExtname(".png");
                    fileUpload.setFilepath(dstImgFolder);
                    fileUpload.setFilesize(dstFile.length());
                    fileUpload.setUploadtime(new Timestamp(
                            new Date().getTime()));
                    fileUpload.setUserid(user.getId());
                    fileuploadController.fileuploadService.insertSelect(fileUpload);
                    fileids.add(fileUpload.getId());
                    fileurls.add(getUrlPath(request,fileUpload.getId()));
                    JSONObject fobj = new JSONObject();
                    fobj.put("id",fileUpload.getId());
                    fobj.put("url",getUrlPath(request,fileUpload.getId()));
                    fobj.put("ext",fileUpload.getFileExtname());
                    files.put(fobj);
                }
                System.out.println("成功");
            } else {
                System.out.println("错误:" + "创建" + imgPDFPath + "失败");
            }

        } catch (Exception e) {
            System.out.println("Exception");
            e.printStackTrace();
        }
    }

    public static void copyFileupload(Fileupload uploadedfile,String realName,List<Integer> fileids,List<String> fileurls,JSONArray files,HttpServletRequest request,User user){
        Fileupload fileUpload = new Fileupload();
        BeanUtil.beanCopy(uploadedfile,fileUpload);
        fileUpload.setRealname(realName);
        fileUpload.setUploadtime(new Date());
        fileUpload.setUserid(user.getId());
        fileuploadController.fileuploadService.insertSelect(fileUpload);
        fileids.add(fileUpload.getId());
        fileurls.add(getUrlPath(request,fileUpload.getId()));
        JSONObject fobj = new JSONObject();
        fobj.put("id",fileUpload.getId());
        fobj.put("url",getUrlPath(request,fileUpload.getId()));
        fobj.put("ext",fileUpload.getFileExtname());
        files.put(fobj);
    }

    /**
     * 获取并保存EChart图片到本地.
     * @param picInfo 图片信息
     * @param imageName 图片名字
     */
    @ApiOperation("获取并保存EChart图片到本地")
    @PostMapping(value="/saveChartImage")
    @ResponseBody
    private void saveChartImage(HttpServletRequest request,String picInfo, @RequestParam("imageName") String imageName) {
        if (StringUtil.isBlank(picInfo)) {
            System.out.println("picInfo为空,未从前台获取到base64图片信息!");
            return;
        }
//      String imagePath=System.getProperty("evan.webapp")+"/WEB-INF";
//      System.out.println(System.getProperty("evan.webapp"));//获取服务器根路径，成功，不过需要在web.xml中进行一些配置
        //获取服务器根路径到/static/images/的目录路径
        //文件上传的绝对路径
        String dirPath = initFileBase+"chart/";
        String uploadDir = checkDir(request, dirPath);
        System.out.println("返回的文件路径："+uploadDir);
//        String imagePath= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/static/images/")+imageName;
        // 传递过程中  "+" 变为了 " ".
        String newPicInfo = picInfo.replaceAll(" ", "+");
        decodeBase64(newPicInfo, new File(uploadDir+imageName));
        //log.warn("从echarts中生成图片的的路径为:{}", picPath);
    }

    /**
     * 解析Base64位信息并输出到某个目录下面.
     * @param base64Info base64串
     * @param picPath 生成的文件路径
     * @return 文件地址
     */
    private File decodeBase64(String base64Info, File picPath) {
        if (StringUtils.isEmpty(base64Info)) {
            return null;
        }

        // 数据中：data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAABI4AAAEsCAYAAAClh/jbAAA ...  在"base64,"之后的才是图片信息
        String[] arr = base64Info.split("base64,");

        // 将图片输出到系统某目录.
        OutputStream out = null;
        try {
            // 使用了Apache commons codec的包来解析Base64
            byte[] buffer = org.apache.commons.codec.binary.Base64.decodeBase64(arr[1]);
            out = new FileOutputStream(picPath);
            out.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            //log.error("解析Base64图片信息并保存到某目录下出错!", e);
        } finally {
            IOUtils.closeQuietly(out);
        }

        return picPath;
    }

    @ApiOperation("删除上传文件")
    @GetMapping("/del/{fid}")
    @ResponseBody
    public static ResultObject deleteFile(@PathVariable Integer fid,HttpServletRequest request){
        Fileupload uploadedfile=fileuploadController.fileuploadService.getById(fid);
        if(uploadedfile == null){
            System.out.println("未找到文件！");
            return ResultObject.newError("未找到文件！");
        }
        String realPath = "";
        if(StringUtil.matchReg(uploadedfile.getFilepath(),"^WEB-INF/")){
            realPath = checkDirDown(request,uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        }else {
            realPath = checkDirDown2(uploadedfile.getFilepath());//获取文件上传后的保存绝对路径
        }
        String pic_path=realPath+"/"+uploadedfile.getFilename()+uploadedfile.getFileExtname();//图片路径\
        //文件是单独使用就路径删除
        Long c = fileuploadController.fileuploadService.getCountByMd5(uploadedfile.getFilemd5());
        if(c<2){
            FileUtil.deleteFile(pic_path);
        }
        fileuploadController.fileuploadService.deleteById(fid);
        return ResultObject.newOk("删除成功！");
//        if(FileUtil.deleteFile(pic_path)){
//            fileuploadController.fileuploadService.deleteById(fid);
//            return ResultObject.newOk("删除成功！");
//        }else {
//            return ResultObject.newError("删除失败，文件不存在！");
//        }
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
        System.out.println("获取文件上传后的保存绝对路径："+realPath);
        File file = new File(realPath);
        if (!file.exists())// 如果路径不存在则创建
            file.mkdirs();
        return realPath;
    }

    public static String checkDir2(String uploadDir){
        System.out.println("获取文件上传后的保存绝对路径："+uploadDir);
        File f = new File(uploadDir);
        if (!f.exists())// 如果路径不存在则创建
            f.mkdirs();
//        System.out.println("返回的文件路径："+uploadDir);
        return uploadDir;
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

    public static String checkDirDown2(String dirPath) {
        String realPath = dirPath;// 获取文件上传后的保存绝对路径
//        System.out.println("获取文件上传后的保存绝对路径："+realPath);
        File file = new File(realPath);
        if (!file.exists())// 如果路径不存在则创建
            file.mkdirs();
        return realPath;
    }

    public static String getUrlPath(HttpServletRequest request,Integer fid){
        String path = request.getContextPath();
        String urlpath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        urlpath+= "file/img/"+ fid;
        return urlpath;
    }

    private static boolean createDirectory(String folder) {
        File dir = new File(folder);
        if (dir.exists()) {
            return true;
        } else {
            return dir.mkdirs();
        }
    }
}
