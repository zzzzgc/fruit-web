package com.fruit.web.service.vaildation;

import com.fruit.web.util.Constant;
import com.fruit.web.util.VerifyCodeUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * 用于生成获取图片验证码
 *
 * @author ZGC
 * @date 2018-02-05 18:13
 **/
public class ImageBaseValidationServiceImpl extends BaseValidationService {
    private static int counter = 0;

    @Override
    public String createVerifyCode(HttpServletRequest request) {
        try {
            //文件夹
            String folder = "/verify";
            //文件名
            String dirName = System.currentTimeMillis() + ++counter + ".jpg";
            //文件路径
            String realPath = request.getSession().getServletContext().getRealPath(folder);

            File dir = new File(realPath);
            String verify = VerifyCodeUtils.generateVerifyCode(4);
            request.getSession().setAttribute(Constant.LOGIN_IMAGE_VERIFY_CODE, verify);

            File file = new File(dir, dirName);
            VerifyCodeUtils.outputImage(200, 80, file, verify);
            // /verify/imgName.jsp
            return folder + File.separator + dirName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("图片验证码生成失败");
        }
    }

}
