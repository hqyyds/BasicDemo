package com.h3w.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** 生成dao层代码
 * @author 13018
 * @date 2021/6/25
 */
public class FreeMarkerUtil {
    private static final String TEMPLATE_PATH = "src/main/resources/temp";
    private static final String DAO_PATH = "src/main/java/com/h3w/dao";
    private static final String DAOIMPL_PATH = "src/main/java/com/h3w/dao/impl";

    //初始化dao文件
    public static void createDaoClass(){
        System.out.println("dao生成开始——————————————————————");
        // step1 创建freeMarker配置实例
        Configuration configuration = new Configuration();
        Writer out = null;
        try {
            Set<Class<?>> entitys = ClassUtil.getClassSet("com.h3w.entity");
            for (Class<?> entity : entitys) {
//            System.out.println(entry.getKey());//demo1Controller
                String value = entity.getSimpleName();
                String fileNameDao = value+"Dao";
                String fileNameDaoImpl = value+"DaoImpl";
                configuration.setDirectoryForTemplateLoading(new File(TEMPLATE_PATH));
                // step3 创建数据模型
                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("className", value);
                // step4 加载模版文件
                Template template = configuration.getTemplate("dao.ftl");
                // step5 生成数据
                File docFile = new File(DAO_PATH + "\\" + fileNameDao+".java");
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
                // step6 输出文件
                template.process(dataMap, out);
                System.out.println(fileNameDao+"文件创建成功 !");

                Template template2 = configuration.getTemplate("daoImpl.ftl");
                File docFile2 = new File(DAOIMPL_PATH + "\\" + fileNameDaoImpl+".java");
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile2)));
                template2.process(dataMap, out);
                System.out.println(fileNameDaoImpl+"文件创建成功 !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        System.out.println("dao生成结束——————————————————————");
    }

    public static void main(String[] args) {
        createDaoClass();
    }
}
