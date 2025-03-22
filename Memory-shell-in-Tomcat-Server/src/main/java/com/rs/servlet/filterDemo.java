//package com.rs.servlet;
//
//import org.apache.catalina.core.StandardWrapper;
//
//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//
//@WebFilter(urlPatterns = "/*")
//public class filterDemo implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        System.out.println("[Filter] FilterDemo initialized!");
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        System.out.println("[Filter] FilterDemo is filtering request...");
//
//        chain.doFilter(request, response);
//
//        System.out.println("[Filter] FilterDemo processed response.");
//    }
//
//
//    @Override
//    public void destroy() {
//        System.out.println("[Filter] FilterDemo destroyed!");
//    }
//}
