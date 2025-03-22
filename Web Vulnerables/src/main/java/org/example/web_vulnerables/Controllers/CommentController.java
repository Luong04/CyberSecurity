package org.example.web_vulnerables.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.web_vulnerables.Entities.UserEntity;
import org.example.web_vulnerables.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/addComment")
    public ResponseEntity<String> patchComment(@RequestParam("comment") String comment, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập!");
        }

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng!");
        }

        try {
            String query = "INSERT INTO comment (username, content, created_at) VALUES ('" +
                    username + "', '" + comment + "', CURRENT_TIMESTAMP)";
            jdbcTemplate.execute(query);
            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi thêm bình luận: " + e.getMessage());
        }

    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllComments() {
        try {
            String sql = "SELECT comment_id, username, content, created_at FROM comment ORDER BY created_at DESC";

            List<Map<String, Object>> comments = jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("id", rs.getLong("comment_id"));
                    comment.put("username", rs.getString("username"));
                    comment.put("content", rs.getString("content"));
                    comment.put("createdAt", rs.getTimestamp("created_at"));
                    return comment;
                }
            });

            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy bình luận: " + e.getMessage());
        }
    }
}
