package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
//import com.sun.net.httpserver.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//import javax.servlet.*;

@WebFilter(filterName="loginCheckFilter",urlPatterns="/*")//所有的url都要进入这个判断，
@Slf4j//开启日志
public class LoginCheckFilter implements Filter {//实现接口后需要实现方法才可以的
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, IOException, ServletException {//注意这里的ServletRequest类型不是HTTP类型的
        HttpServletRequest request =(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;//第一步直接强转
        //1
        String requestUri=request.getRequestURI();//获取本次的uri
        //2定义不需要处理的url
        String []urls=new String[]{
                "/employee/login",
                "/employee/logout",//登录登出放行
                "/backend/**",
                "/front/**",//前后端静态页面都放
                "/common/**",
                "/user/sendMsg",//验证码放行
                "/user/login",//手机端登录页面
                "/doc.html",//swagger生成json页面
                "/swagger-resources",
                "/v2/api-docs"//这个地方是v2/api-docs!!!之前这里错了，所以到不了doc.html页面
        };
        //判断
        boolean check=check(urls,requestUri);
        //
        if(check){//true->成功，放行doFilter
            filterChain.doFilter(request,response);
            return;//退出方法
        }
        //4-1客户端登录过
        if(request.getSession().getAttribute("employee")!=null){//已经登录了
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        //4-2移动端登陆过
        if(request.getSession().getAttribute("user")!=null){
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return ;
        }
        //到了这里就是需要过滤的浏览器了
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * @param urls
     * @param requestUri
     * @return
     */
    public boolean check(String []urls,String requestUri){
        for(String url:urls){
            boolean match=PATH_MATCHER.match(url,requestUri);//调用资源比较器的match方法
            if(match)return true;//比对成功，放行，true
        }
        return false;
    }

}