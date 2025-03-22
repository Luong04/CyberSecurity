package com.rs.servlet;

import java.io.File;


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
@WebServlet(urlPatterns = "/upload")
public class upload extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        Part filePart = request.getPart("file");
        String fileName = filePart.getSubmittedFileName();

        File file = new File(uploadPath + File.separator + fileName);
        filePart.write(file.getAbsolutePath());

        out.println("<h3>File uploaded: <a href='uploads/" + fileName + "'>" + fileName + "</a></h3>");
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/upload.jsp").forward(request, response);

    }
}
