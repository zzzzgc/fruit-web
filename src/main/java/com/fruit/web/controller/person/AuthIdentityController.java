package com.fruit.web.controller.person;

import com.fruit.web.base.BaseController;
import com.fruit.web.controller.ProductController;
import com.fruit.web.model.BusinessAuth;
import com.fruit.web.util.Constant;
import com.jfinal.upload.UploadFile;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

public class AuthIdentityController extends BaseController {
    private static Logger log = Logger.getLogger(ProductController.class);

    /**
     * 上传认真图片
     * @param imgL
     */
    public void addAuthInfoImg(String imgL){
        getResponse().addHeader("Access-Control-Allow-Origin","*");
      /*  String test=getPara();
        String ba=getPara("business_license");
        String s= getAttr("imgL");
        String [] sa =getParaValues("imgL");
        HttpServletRequest http= getRequest();
        BusinessAuth a=getBean(BusinessAuth.class,"",true);
        // 获取到BusinessAuth对象
        BusinessAuth businessAuth=getModel(BusinessAuth.class,"",true);*/
        // 获取到图片
        List<UploadFile> listUploadFile=getFiles();
        Map<Integer,String> mapImg=new HashMap<Integer,String>();
        String projectPath=getRequest().getSession().getServletContext().getRealPath("static/images");
        for (int i = 0; i < listUploadFile.size(); i++) {
            UploadFile uploadFile =listUploadFile.get(i);
            String originalFileName =uploadFile.getOriginalFileName();
            String fileName=uploadFile.getFileName();
            File file =uploadFile.getFile();
            if(!new File(projectPath).exists() && new File(projectPath).isDirectory()){ //判断文件夹是否存在
                new File(projectPath).mkdir();
            }
            String fullPath=projectPath+"\\"+UUID.randomUUID().toString()+"."+fileName.split("\\.")[1];
            // 服务器存放图片的路径
            File t=new File(fullPath);
            mapImg.put(i,fullPath);
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
            renderJson(mapImg);
        }
    }

    /**
     * 添加用户店铺认证信息
     */
    public void addAuthInfo(){
        BusinessAuth businessAuth = getModel(BusinessAuth.class,"",true);
        try {
            businessAuth.setCreateTime(new Date());
            businessAuth.setUpdateTime(new Date());
            Object uid=getSessionAttr(Constant.SESSION_UID);
            businessAuth.setUId(Integer.parseInt(uid.toString()));
            businessAuth.setAuthType("1");
            if(businessAuth.getImgOnlineShop()!=null && !"".equals(businessAuth.getImgOnlineShop().trim())){
                businessAuth.setAuthType("2");
            }
            businessAuth.save();
            renderText("1");
        }catch (Exception e){
            renderErrorText("0");
        }
    }

    /**
     * 根据用户ID获取认证信息
     */
    public void getAuthInfoByUid(){
        BusinessAuth businessAuth = getModel(BusinessAuth.class,"",true);
        Object uid=getSessionAttr(Constant.SESSION_UID);
        renderJson(BusinessAuth.dao.getBusinessAuthByUid(Integer.parseInt(uid.toString()),Integer.parseInt(businessAuth.getAuthType())));
    }

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
