<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Nhập Tên</title>
</head>
<body>
<h2>Nhập tên của bạn</h2>
<form action="UserServlet" method="post">
    <label>Nhập tên:</label>
    <input type="text" name="username" required>
    <button type="submit">Gửi</button>
</form>
</body>
</html>
