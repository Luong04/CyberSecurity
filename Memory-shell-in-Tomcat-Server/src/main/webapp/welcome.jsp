<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Chào Mừng</title>
</head>
<body>
<h2>Xin chào, <%= request.getAttribute("user") %>!</h2>
<a href="hello.jsp">Nhập lại</a>
</body>
</html>
