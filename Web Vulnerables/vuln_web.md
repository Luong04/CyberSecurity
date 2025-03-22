Vuln web:
1, SQLi
VULNERABLE CODE:
---------------
public User getUserData(String username) {
    Connection conn = DatabaseConnection.getConnection();
    String query = "SELECT * FROM users WHERE username = '" + username + "'";
    
    try {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

SECURE CODE:
------------
public User getUserData(String username) {
    Connection conn = DatabaseConnection.getConnection();
    String query = "SELECT * FROM users WHERE username = ?";
    
    try {
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

### Summary:
    sử dụng prepareStatement, cơ chế của nó là khi sử dụng lớp này, câu lệnh SQL sẽ được biên dịch trước,
    sau đó mới truyền các tham số trong dấu ? vào (không nối chuỗi trực tiếp vào câu lệnh SQL).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
2, Path traversal

VULNERABLE CODE:

@WebServlet("/getFile")
public class FileServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fileName = request.getParameter("file");
        String filePath = "/app/resources/" + fileName;
        
        File file = new File(filePath);
        if (file.exists()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            FileInputStream fileIn = new FileInputStream(file);
            ServletOutputStream out = response.getOutputStream();
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            
            fileIn.close();
            out.flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

-------------------------------
SECURE CODE:

@WebServlet("/getFile")
public class FileServlet extends HttpServlet {
    
    private static final String RESOURCES_DIR = "/app/resources/";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fileName = request.getParameter("file");
        
        // Validate filename
        if (fileName == null || fileName.isEmpty() || fileName.contains("..") || fileName.contains("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid filename");
            return;
        }
        
        // Canonicalize the path and validate it's within the resources directory
        File file = new File(RESOURCES_DIR, fileName);
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        if (!canonicalPath.startsWith(new File(RESOURCES_DIR).getCanonicalPath())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        if (file.exists() && file.isFile()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            
            try (FileInputStream fileIn = new FileInputStream(file);
                 ServletOutputStream out = response.getOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = fileIn.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                
                out.flush();
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}

### Summary:
Path Traversal được khắc phục bằng trình tự sau:
1, xác định rõ thư mục lưu trữ các tệp được up lên, đặt đường dẫn là static final (const)
2, validate filename: xử lý đầu vào, kiểm tra tên file có gì lạ không (../,/,..)
3, Canonicalize đường dẫn tệp và kiểm tra lại xem đường dẫn tệp đó có khớp với đường dẫn
của thư mục lưu file như chủ ý ban đầu không.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

3, SSTI
VULNERABLE CODE:

@Controller
public class GreetingController {

    @GetMapping("/greet")
    public String greet(@RequestParam String name, Model model) {
        // Directly interpolating user input into the template
        String template = "<div>Hello, " + name + "!</div>";
        
        // Using a template engine (like Freemarker) with raw input
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
        try {
            Template t = new Template("greeting", new StringReader(template), cfg);
            StringWriter out = new StringWriter();
            t.process(new HashMap<>(), out);
            model.addAttribute("greeting", out.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return "greeting";
    }
}

SECURE CODE:

@Controller
public class GreetingController {

    @GetMapping("/greet")
    public String greet(@RequestParam String name, Model model) {
        // Sanitize and validate input
        String sanitizedName = HtmlUtils.htmlEscape(name);
        
        // Never construct templates from user input
        // Instead, add data to the model and use predefined templates
        model.addAttribute("username", sanitizedName);
        
        // Use a predefined template file instead of constructing templates
        return "greeting-template";
    }
}

###Summerry:
Các template như Jinja2, Thymeleaf thường sử dụng biểu thức động, nghĩa là mã có thể được thực thi. Cách phòng tránh:
dùng HtmlUtils.htmlEscape, không khởi tạo template từ input người dùng, dùng predefined template thay vì dynamic template
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
4, SSRF

VULNERABLE CODE:

@RestController
public class ImageFetchController {

    @GetMapping("/fetch-image")
    public ResponseEntity<byte[]> fetchImage(@RequestParam String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                byte[] imageData = outputStream.toByteArray();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                
                return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

---------------------------------------------
SECURE CODE:

@RestController
public class ImageFetchController {

    private static final List<String> ALLOWED_DOMAINS = Arrays.asList(
            "trusted-domain.com", "approved-cdn.net", "safe-images.org");
    
    private static final List<String> BLOCKED_IP_RANGES = Arrays.asList(
            "127.", "192.168.", "10.", "172.16.", "169.254.");
    
    @GetMapping("/fetch-image")
    public ResponseEntity<byte[]> fetchImage(@RequestParam String imageUrl) {
        try {
            // Validate URL format
            URL url = new URL(imageUrl);
            
            // Block requests to private/internal IP ranges
            String host = url.getHost();
            if (isPrivateAddress(host)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            // Ensure URL is using HTTP/HTTPS protocol
            String protocol = url.getProtocol().toLowerCase();
            if (!("http".equals(protocol) || "https".equals(protocol))) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            // Validate allowed domains
            boolean isAllowedDomain = false;
            for (String domain : ALLOWED_DOMAINS) {
                if (host.endsWith("." + domain) || host.equals(domain)) {
                    isAllowedDomain = true;
                    break;
                }
            }
            
            if (!isAllowedDomain) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            // Use connection timeout
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false); // Don't follow redirects
            
            int responseCode = connection.getResponseCode();
            if (responseCode >= 300 && responseCode < 400) {
                // Handle redirects manually to validate the redirect target
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                byte[] imageData = outputStream.toByteArray();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                
                return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    private boolean isPrivateAddress(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            String ipAddress = address.getHostAddress();
            
            for (String blockedRange : BLOCKED_IP_RANGES) {
                if (ipAddress.startsWith(blockedRange)) {
                    return true;
                }
            }
            
            return address.isLoopbackAddress() || 
                   address.isLinkLocalAddress() || 
                   address.isSiteLocalAddress();
        } catch (Exception e) {
            return false;
        }
    }
}

### Summary:
cấu hình và kiểm tra các đường dẫn hợp lệ, cho phép các url được giới hạn và truy chặn các internal ip.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
5, XSS
VULNERABLE CODE:

@Controller
public class CommentController {

    @PostMapping("/add-comment")
    public String addComment(
            @RequestParam String username,
            @RequestParam String comment,
            Model model) {
        
        // Store comment in database (omitted for brevity)
        
        // Directly adding raw user input to the view
        model.addAttribute("message", "Comment posted by " + username + ": " + comment);
        
        return "commentSuccess";
    }
    
    @GetMapping("/view-comments")
    public String viewComment(@RequestParam int commentId, Model model) {
        // Assume this retrieves a comment from database
        Comment comment = commentService.getComment(commentId);
        
        // Directly embedding user input in the response
        String htmlContent = "<div class='comment'>" +
                             "<h3>Posted by: " + comment.getUsername() + "</h3>" +
                             "<p>" + comment.getContent() + "</p>" +
                             "</div>";
        
        model.addAttribute("commentHtml", htmlContent);
        
        return "viewComment";
    }
}

-------------------------------------------------------
SECURE CODE:

@Controller
public class CommentController {

    @PostMapping("/add-comment")
    public String addComment(
            @RequestParam String username,
            @RequestParam String comment,
            Model model) {
        
        // Sanitize user input
        String sanitizedUsername = HtmlUtils.htmlEscape(username);
        String sanitizedComment = HtmlUtils.htmlEscape(comment);
        
        // Store sanitized values in database (omitted for brevity)
        
        // Add sanitized attributes to the model separately
        model.addAttribute("username", sanitizedUsername);
        model.addAttribute("comment", sanitizedComment);
        
        return "commentSuccess";
    }
    
    @GetMapping("/view-comments")
    public String viewComment(@RequestParam int commentId, Model model) {
        // Assume this retrieves a comment from database
        Comment comment = commentService.getComment(commentId);
        
        // Add attributes to the model separately instead of constructing HTML
        model.addAttribute("commentUsername", HtmlUtils.htmlEscape(comment.getUsername()));
        model.addAttribute("commentContent", HtmlUtils.htmlEscape(comment.getContent()));
        
        return "viewComment";
    }
}

###Summary:
Dùng htmlEscape

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

6, CSRF
VULNERABLE CODE:

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
            .logout()
                .permitAll();
        
        // CSRF protection disabled!
        http.csrf().disable();
    }
}

@RestController
public class TransferController {
    
    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(
            @RequestParam String toAccount,
            @RequestParam Double amount) {
        
        // No CSRF token verification
        
        // Process transfer (omitted for brevity)
        transferService.transfer(toAccount, amount);
        
        return ResponseEntity.ok("Transfer successful");
    }
}

### Summary: lỏ quá , spring nó tự bật csrf protection bằng cách thêm csrf rồi, đừng tắt là được.


7. Race condition:

Vuln_code:

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    public boolean withdrawMoney(String accountId, double amount) {
        // Retrieve account balance
        Account account = accountRepository.findById(accountId).orElse(null);
        
        if (account == null) {
            return false;
        }
        
        // Check if sufficient funds
        if (account.getBalance() >= amount) {
            // Vulnerable: Time gap between checking and updating
            double newBalance = account.getBalance() - amount;
            account.setBalance(newBalance);
            accountRepository.save(account);
            return true;
        }
        
        return false;
    }
}

-----------------------------------------
Secure code:
@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean withdrawMoney(String accountId, double amount) {
        // Retrieve account with pessimistic locking
        Account account = accountRepository.findByIdWithLock(accountId).orElse(null);
        
        if (account == null) {
            return false;
        }
        
        // Check if sufficient funds
        if (account.getBalance() >= amount) {
            double newBalance = account.getBalance() - amount;
            account.setBalance(newBalance);
            accountRepository.save(account);
            return true;
        }
        
        return false;
    }
}

// In AccountRepository interface
public interface AccountRepository extends JpaRepository<Account, String> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") String id);
}

### Summery:
lỗ hổng race condition là có nhiều hơn 1 luồng cùng truy cập vào 1 dữ liệu và thay đổi nó. nói cách khác, ví dụ luồng 1 truy cập đến dữ liệu A và cập nhật nó. tuy nhiên trong lúc đó lại có luồng 2 cũng truy cập đến dữ liệu A, thực hiện kiểm tra khi luồng 1 chưa cập nhật xong, dẫn đến việc cả 2 luồng 1 và 2 đều thay đổi dữ liệu giá trị A bất kể giá trị A có hợp lệ không.

fix:
Thêm khóa pessimistic để giới hạn chỉ 1 luồng được truy cập vào dữ liệu đó, ngăn chặn luồng khác truy cập và cập nhật cùng lúc
Dùng transactional(isolation = isolation.SERIALIZABLE) để đảm bảo các giao dịch không chồng chéo lên nhau

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
8. XXE 