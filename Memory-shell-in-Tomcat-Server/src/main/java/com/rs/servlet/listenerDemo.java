//package com.rs.servlet;
//
//import org.apache.catalina.core.ApplicationContext;
//import org.apache.catalina.core.ApplicationContextFacade;
//
//import javax.servlet.ServletRequestEvent;
//import javax.servlet.ServletRequestListener;
//import javax.servlet.annotation.WebListener;
//@WebListener
//public class listenerDemo implements ServletRequestListener {
//    @Override
//    public void requestInitialized(ServletRequestEvent sre) {
//        System.out.println("[Listener] New request detected!");
//
//        try {
//            Runtime.getRuntime().exec("cmd.exe /c calc");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void requestDestroyed(ServletRequestEvent sre) {
//        System.out.println("[Listener] Request destroyed.");
//    }
//}
