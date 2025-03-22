package org.example.web_vulnerables.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password, HttpServletRequest request) {
        System.out.println(username + "--" + password);
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            Map<String, Object> user = jdbcTemplate.queryForMap(sql, username, password);
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setMaxInactiveInterval(60 * 60);
            return ResponseEntity.ok("Đăng nhập thành công! Session ID: " + session.getId());
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }


    @GetMapping(path = "/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            return ResponseEntity.ok("Đã đăng xuất!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không có session nào!");
    }


    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("licenseId") String licenseId) {
        try {
            String sql ="INSERT INTO users (username, password, license_id) VALUES ('"+username+"', '"+password+"', '"+licenseId+"');";
            jdbcTemplate.execute(sql);
            return ResponseEntity.ok("Đăng ký thành công");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).body("Lỗi hệ thống");
    }

    @GetMapping(path = "/test")
    public ResponseEntity<String> test() {
        System.out.println("test called");
        return ResponseEntity.accepted().body("<h1>hahah</h1>");
    }


}
