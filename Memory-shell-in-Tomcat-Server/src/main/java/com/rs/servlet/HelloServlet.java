package com.rs.servlet;

import java.io.*;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        try {
            response.getWriter().println("<h1>Hello, World!</h1>");
        } catch (Exception e) {
            response.getWriter().println("An error occurred: " + e.getMessage());
        }
    }
}
