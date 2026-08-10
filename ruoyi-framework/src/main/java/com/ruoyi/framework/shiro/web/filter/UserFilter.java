package com.ruoyi.framework.shiro.web.filter;

import java.io.IOException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import com.ruoyi.common.utils.ServletUtils;

/**
 * 自定义用户过滤器，对AJAX请求返回JSON而非重定向
 *
 * @author ruoyi
 */
public class UserFilter extends org.apache.shiro.web.filter.authc.UserFilter
{
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception
    {
        if (ServletUtils.isAjaxRequest((jakarta.servlet.http.HttpServletRequest) request))
        {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录超时，请重新登录\"}");
            return false;
        }
        return super.onAccessDenied(request, response);
    }
}
