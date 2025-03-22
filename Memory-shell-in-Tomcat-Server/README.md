# Memory-shell-in-Tomcat-Server

## Mục đích
* Mô tả lỗ hổng file upload trong jsp servlet chạy Tomcat server cho phép upload và thực thi shell
* Kiểm thử memory shell trong tomcat server
## Download
>gitclone https://github.com/HoangVietAnh09/Memory-shell-in-Tomcat-Server.git
## Run
Chạy với IDE intellij trên hệ điều hành window
## Quá trình kiểm thử
* Upload memshell và thực thi như với web shell bình thương
* Vào đường dẫn lưu trữ shell vừa upload rồi xóa shell
* Kiểm tra tồn tại của shell trên web thông qua URL
* Thực thi lại lệnh nếu vẫn đạt được kết quả mong muốn là đã thành công

Các shell bao gồm:
* shell.jsp: shell bình thương
* listenerDemo.jsp: memshell được inject thông qua listener
* filterDemo.jsp: memshell được inject thông qua filter
* servletDemo.jsp: memshell được inject thông qua servlet

