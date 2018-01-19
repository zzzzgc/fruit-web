package com.fruit.web.controller.person;

import com.fruit.web.base.BaseController;
import com.fruit.web.controller.ProductController;
import com.fruit.web.util.FileService;
import com.jfinal.upload.UploadFile;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthIdentityController extends BaseController {
    private static Logger log = Logger.getLogger(ProductController.class);

    public void uploadImgs(){
        getResponse().addHeader("Access-Control-Allow-Origin","*");
        System.out.println("test");
        List<UploadFile> listUploadFile= getFiles();
        for (int i = 0; i < listUploadFile.size(); i++) {
            UploadFile uploadFile=listUploadFile.get(i);
            String originalFileName =uploadFile.getOriginalFileName();
            String fileName=uploadFile.getFileName();
            File file =uploadFile.getFile();
            File t=new File("C:\\Users\\Administrator\\Desktop\\wav(0db)\\"+UUID.randomUUID().toString()+"."+fileName.split("\\.")[1]);
            FileInputStream ins= null;
            FileOutputStream out= null;
            try {
                ins = new FileInputStream(file);
                out = new FileOutputStream(t);
            byte[] b = new byte[1024];
            int n=0;
            while((n=ins.read(b))!=-1){
                out.write(b, 0, n);
            }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try{
                    ins.close();
                    out.close();
                }catch (Exception e){
                }
                file.delete();
            }
        }
        Map map=new HashMap();
        map.put("error",0);
        map.put("url","D:\\ccz\\devsoft\\tomcat-fruit-web\\apache-tomcat-9.0.1\\webapps\\ROOT/authIdentity/uploadImgs.html");
        Map [] maps=new Map[2];
        maps[0]=map;
        maps[1]=map;
        renderJson(maps);
    }
}
