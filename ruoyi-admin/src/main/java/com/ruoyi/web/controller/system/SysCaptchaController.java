package com.ruoyi.web.controller.system;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import jakarta.annotation.Resource;
import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 图片验证码（支持算术形式）
 * 
 * @author ruoyi
 */
@Controller
public class SysCaptchaController extends BaseController
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 验证码生成（返回base64格式，供前端API调用）
     */
    @GetMapping(value = {"/captchaImage", "/captcha/captchaImage"})
    @ResponseBody
    public AjaxResult getKaptchaImageBase64(HttpServletRequest request)
    {
        try
        {
            HttpSession session = request.getSession();
            String type = request.getParameter("type");
            String capStr = null;
            String code = null;
            BufferedImage bi = null;
            
            if ("char".equals(type))
            {
                capStr = code = captchaProducer.createText();
                bi = captchaProducer.createImage(capStr);
            }
            else
            {
                // 默认使用math类型
                String capText = captchaProducerMath.createText();
                capStr = capText.substring(0, capText.lastIndexOf("@"));
                code = capText.substring(capText.lastIndexOf("@") + 1);
                bi = captchaProducerMath.createImage(capStr);
            }
            
            session.setAttribute(Constants.KAPTCHA_SESSION_KEY, code);
            
            // 转换为base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", baos);
            byte[] bytes = baos.toByteArray();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            
            AjaxResult ajax = AjaxResult.success();
            ajax.put("uuid", UUID.randomUUID().toString());
            ajax.put("img", base64);
            return ajax;
        }
        catch (Exception e)
        {
            return AjaxResult.error("验证码生成失败");
        }
    }

    /**
     * 验证码生成（返回图片格式，供页面直接使用）
     */
    @GetMapping(value = "/captcha/captchaImage/image")
    public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response)
    {
        ServletOutputStream out = null;
        try
        {
            HttpSession session = request.getSession();
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");

            String type = request.getParameter("type");
            String capStr = null;
            String code = null;
            BufferedImage bi = null;
            if ("char".equals(type))
            {
                capStr = code = captchaProducer.createText();
                bi = captchaProducer.createImage(capStr);
            }
            else
            {
                // 默认使用math类型
                String capText = captchaProducerMath.createText();
                capStr = capText.substring(0, capText.lastIndexOf("@"));
                code = capText.substring(capText.lastIndexOf("@") + 1);
                bi = captchaProducerMath.createImage(capStr);
            }
            session.setAttribute(Constants.KAPTCHA_SESSION_KEY, code);
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.flush();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
