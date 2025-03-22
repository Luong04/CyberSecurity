
# Memory Webshell trong Tomcat Servlet

![6a47baa076a4ce9135fd4a6d724c4933](https://github.com/user-attachments/assets/fdf8f7c4-17e3-4748-a201-882543afb39b)
>Cấu trúc của tomcat server



![image](https://github.com/user-attachments/assets/94fb3827-f2bc-4070-8ab2-adb6fc9ba0d5)


## Inject Memory Webshell thông qua Listener

**Listener:** Theo dõi các sự kiện khởi tạo hay hủy bỏ của những đối tượng như application, session hay request để thực thi các đoạn mã tương ứng với các sự kiện.

Một số hàm để gọi listener trong servlet:

* **ServletContextListener:** Dùng để theo dõi các sự kiện liên quan đến ServletContext (ứng dụng web).

  ![image](https://github.com/user-attachments/assets/c33704d9-f604-4407-bea5-8d8fc1e91442)

* **HttpSessionListener:** Dùng để theo dõi các sự kiện liên quan đến HttpSession.

  ![image](https://github.com/user-attachments/assets/abc432d1-7ca3-4d4e-9664-d86b7473f4ca)

* **ServletRequestListener:** Dùng để theo dõi các sự kiện liên quan đến ServletRequest.

  ![image](https://github.com/user-attachments/assets/53b9d0ee-6768-4f3c-968f-a749c4dfd2c7)

* **Attribute Listener:** Dùng để theo dõi các thay đổi của các thuộc tính trong ServletContext, HttpSession, hoặc ServletRequest.
  * **ServletContextAttributeListener:**
    
    ![image](https://github.com/user-attachments/assets/71830c12-cdaa-4849-bf4a-1e3771fbeeb0)
    
  * **HttpSessionAttributeListener:**
    
    ![image](https://github.com/user-attachments/assets/f9bc682f-89a8-4b54-84fe-bb73e804d7aa)

* **AsyncListener:** Dùng để theo dõi các sự kiện liên quan đến xử lý asynchronous (xử lý bất đồng bộ) trong Servlet.

  ![image](https://github.com/user-attachments/assets/1bf10076-1bf8-4997-8a55-52616e787ff0)


Quan sát các class có thể gọi listener ta thấy rằng hầu hết các class đều có tồn tại method với chức năng là khởi tạo hoặc là kết thúc.

Lợi dụng điều này ta sẽ chèn shell vào quá trình khởi tạo của một class nào đó có thực hiện qua lớp listener.

Ở đây mình sẽ chọn class ServletRequestListener. Tức là khi thực hiện 1 request mới sẽ gọi đến method requestInitialized()

![image](https://github.com/user-attachments/assets/cd72dac3-18f4-4ad9-8f43-0c11898976ea)

Method requestInitialized() nhận vào tham số dạng ServletRequestEvent.

Đoạn code trên sẽ nhận vào 1 tham số với tên là "cmd". Nếu như giá trị của tham số khác giỗng thì sẽ thực thi giá trị truyền vào.

***Trước hết hãy cùng tìm hiểu về Reflection API***

Reflection API là một phần quan trọng của Java, giúp thao tác với các lớp, phương thức, và thuộc tính ngay cả khi bạn không biết về chúng tại thời điểm biên dịch.

Reflection cho phép khám phá và sử dụng các thành phần của một chương trình trong quá trình chạy (runtime).

Class java.lang.Class là một entry point của mọi thao tác của reflection API.

Trong java có 2 cách lấy Object class:
* Sử dụng Object.getClass() method => method này sẽ trả về runtime Class object type
*  Sử dụng .class => ClassName.class trả về Class object static

![image](https://github.com/user-attachments/assets/a3689662-ee04-421b-a9ed-e773718b3e43)
> bypass blacklist runtime

![image](https://github.com/user-attachments/assets/681e5154-9e96-41f9-b1e4-fe992bc0b063)
> Lấy method getRuntime

![image](https://github.com/user-attachments/assets/e1852cfc-82b8-47d7-8cab-387750ba64ba)
> Láy method exec() và thực thi

Công dụng chính của Reflection API:

* Khám phá thông tin của các lớp: có thể lấy thông tin về tên lớp, các phương thức, các trường (fields) và các hàm tạo (constructors) của một lớp.
* Tạo đối tượng: Reflection cho phép tạo ra các đối tượng của một lớp mà không cần biết trước tên của lớp đó.
* Gọi phương thức: có thể gọi các phương thức của một đối tượng, ngay cả khi bạn không biết tên phương thức đó tại thời điểm biên dịch.
* Thay đổi giá trị của các trường: có thể truy cập và thay đổi giá trị của các trường của một đối tượng.

![image](https://github.com/user-attachments/assets/0406c840-73ca-4e91-a568-e53cb83b4fc9)
> Một ví dụ về reflect lấy ra tất cả các method.


Trong servlet ta có thể thêm các listener thông qua hàm addListener() của ServletContext

![image](https://github.com/user-attachments/assets/0bd6346b-d24e-4a79-a966-d033e80cb33e)

Giải thích qua về ServletContext thì ServletContext là một interface trong Java Servlet API, được sử dụng để đại diện cho một môi trường ứng dụng web duy nhất trên máy chủ. Mỗi ứng dụng web được triển khai trên máy chủ ứng dụng sẽ có một đối tượng ServletContext duy nhất, được chia sẻ giữa tất cả các servlet trong ứng dụng đó.

Chức năng của ServletContext:

* Lưu trữ thông tin chung cho toàn bộ ứng dụng
* Cung cấp các phương thức để lấy thông tin cấu hình
* Tương tác với máy chủ và tài nguyên ứng dụng
* Cung cấp khả năng giao tiếp giữa các servlet

Bến cạnh ServletContext cần tìm hiểu thêm 2 cái class là ApplicationContext và ApplicationContextFacade

***ApplicationContext***

* Được kế thừa lại từ class ServletContext nên thực hiện các chức năng sử lí logic của ServletContext

***ApplicationContextFacade***

* ApplicationContextFacade là một lớp proxy hoặc wrapper cho ApplicationContext.
* Nó đóng vai trò như một lớp bảo vệ giữa các servlet và logic nội bộ của Tomcat.
* Thay vì cho phép các servlet tương tác trực tiếp với ApplicationContext, Tomcat cung cấp đối tượng ServletContext thông qua ApplicationContextFacade.
* Cách hoạt động: Khi bạn gọi getServletContext() trong một servlet, Tomcat không trả về trực tiếp một đối tượng ApplicationContext. Thay vào đó, nó trả về một đối tượng ApplicationContextFacade. ApplicationContextFacade sẽ chuyển tiếp (delegate) lời gọi phương thức của bạn tới ApplicationContext.

Ta có stack strace như sau

![image](https://github.com/user-attachments/assets/d8a00afa-c06b-49f3-a5a0-fcfbf2da6a4e)

Từ đây ta thấy rằng để có thể thực hiện thêm 1 listener mới ta phải thực hiện qua class ApplicationContextFacade

Trong ApplicationContextFacade có hàm addListener() gọi đến hàm addListener() của ApplicationContext.

![image](https://github.com/user-attachments/assets/4097a208-df29-4c6e-97b2-4da38f36d912)

![image](https://github.com/user-attachments/assets/b7e65cdb-e907-4b3b-ab75-09bcdc05378a)

Trong ApplicationContext ta thấy hàm addListener gọi đến hàm addApplicationLifecycleListener() của StandardContext

![image](https://github.com/user-attachments/assets/4f327543-a5bf-4d78-9aab-a0f4ad11a9c0)


![image](https://github.com/user-attachments/assets/81bd0d26-52da-4214-86bb-3b07e2c626cd)

![image](https://github.com/user-attachments/assets/db960994-8d79-43be-870d-0abc6a4856a9)

Lúc này thì Listener thực sự được add vào trong Context của Web Server theo thứ tự sau 

![image](https://github.com/user-attachments/assets/0f98d22d-ead2-497c-baae-806d8b6cf9d3)

Trong quá trính debug ta thấy method addApplicationLifecycleListener trong class StandardContext được gọi do mới tạo do chúng ta tạo mới một session. Chính vì vậy mà ta
sẽ không sử dụng hàm này để thêm listener vào context. Chúng ta tạo một filter kế thừa ServletRequestListener nên hàm addApplicationEventListener sẽ được gọi


> Đúc kết lại thì ý tưởng sẽ là thêm listener ở class StandardContext bằng cách thông qua Reflect

![image](https://github.com/user-attachments/assets/a30a443b-f2f1-4912-ac48-6a2a48bec5d3)


![image](https://github.com/user-attachments/assets/a126fc4b-1b83-409b-a638-6c4edc5ce9c1)

Thông qua 2 lần ánh xạ ta đã có thể thêm được 1 listener mới

Bằng các lỗ hổng trên trang web cho phép upload file độc hại ta có thể ta có thể inject một file .jsp làm shell thực thi lệnh

![image](https://github.com/user-attachments/assets/eadd8199-03dd-4d18-b5b6-54416d950735)

Sau khi xóa shell ta thấy vẫn có thể thực thi được lệnh

![image](https://github.com/user-attachments/assets/0d60eddd-57bb-4aa6-9090-7b2603d42100)


![image](https://github.com/user-attachments/assets/dcc9c6f6-df84-45c2-9fc2-8581f102b4c4)

## Inject Memory Webshell thông quan Filter

**Filter:** Khi request đến servlet và trả về response có thể đi qua một hoặc nhiều filter(còn gọi là filter chain). Filter có thể thực hiện kiểm tra, sửa đổi các thuộc tính của request hay response.

![image](https://github.com/user-attachments/assets/99f012f0-b9c7-45a2-9502-8b6609d5b0a0)

Khi ứng dụng thực hiện filter thì sẽ gọi đến method doFilter() của class filterDemo do class này kế thừa interface Filter

Chính vì vậy ta sẽ đặt break point tại method doFilter()

![image](https://github.com/user-attachments/assets/0cbe3e0a-94af-47fa-8dfa-32cac6977323)

Từ đây ta có một đoạn stack trace

![image](https://github.com/user-attachments/assets/c3a145e1-d99a-4e08-af31-ab90512fd818)


Từ quá trình debug ta thấy rằng trước khi gọi đến method doFilter() của class filterDemo thì method internalDoFilter trong class ApplicationFilterChain được gọi.

![image](https://github.com/user-attachments/assets/4c910d41-1108-455f-a48e-204f4986afa7)

Trước khi gọi đến method internalDoFilter() thì method doFilter() của class ApplicationFilterChain trong  được gọi.

```
public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (Globals.IS_SECURITY_ENABLED) {
            ServletRequest req = request;
            ServletResponse res = response;

            try {
                AccessController.doPrivileged(() -> {
                    this.internalDoFilter(req, res);
                    return null;
                });
            } catch (PrivilegedActionException var7) {
                PrivilegedActionException pe = var7;
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw (ServletException)e;
                }

                if (e instanceof IOException) {
                    throw (IOException)e;
                }

                if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }

                throw new ServletException(e.getMessage(), e);
            }
        } else {
            this.internalDoFilter(request, response);
        }
```
Trên đây là một đoạn code trong class ApplicationFilterChain

Trước khi gọi đến doFilter() trong ApplicationFilterChain thì hàm invoke() trong StandardWrapperValve được gọi

![image](https://github.com/user-attachments/assets/08ec7783-c870-4178-8d4f-3181b06301f0)

Các method sẽ được gọi theo hướng sau 

![image](https://github.com/user-attachments/assets/6cbc31d4-2353-4e38-9c3d-961829a233c6)

Trước hết chúng ta cần phải biết một số khái niệm sau:

* Biến filterMaps: ánh xạ các URL với tất cả các filter
* filterDefs: chứa tất cả các biến , bao gồm các biến ở bên trong các đối tượng
* Biến filterConfigs: chứa tất cả thông tin trong filterDef và các đối tượng filer tương ứng với các filter và quản lí filter

![image](https://github.com/user-attachments/assets/08ec7783-c870-4178-8d4f-3181b06301f0)

Ta thử đi vào trường filterChain thì thấy rằng trường này được tạo ra từ ApplicationFilterFactory.createFilterChain(request, wrapper, servlet);

![image](https://github.com/user-attachments/assets/dc6137bf-995e-4af8-922e-80310cf4cfc7)

Thử vào class ApplicationFilter, ta thấy rằng một mảng FilterMap được tạo ra lấy giá trị từ StandardContext 

![image](https://github.com/user-attachments/assets/f9542b9a-6962-419f-8f5b-3c269c1f6df2)

Giá trị của các phần từ trong FilterMap chứa các thông tin về tên, đường dẫn đến path truy cập và path được filter

![image](https://github.com/user-attachments/assets/8d1a3f1e-337b-4b82-952b-d10afa9b45cc)

Tiếp đến sẽ kiểm tra xem path hiện tại của ứng dụng có giống với path được filter hay không và kiểm tra xem filter này có phù hợp với kiểu dispatcher hiện tại không.

![image](https://github.com/user-attachments/assets/b2376c75-a441-4c7c-b604-c4b66b8b1823)

![Screenshot 2025-02-10 134206](https://github.com/user-attachments/assets/4c649a49-ec0b-4351-b676-7c9e6b9376fb)

Nếu phù hợp thì sẽ thêm filterConfig được tạo ở trên vào filterChain 

![image](https://github.com/user-attachments/assets/aafed92c-9659-4470-b364-4457f93b73d0)

Debug ta thu được các trường trong ApplicationFilterConfig(config). Trong đó có bao gồm trường filterDef chứa các thông tin về filter như filterName hay filterClass

***Giải thích về Dispatcher***

Trong Java Servlet, dispatcher là cơ chế để chuyển tiếp hoặc chuyển hướng request từ một servlet này sang một servlet khác hoặc JSP.

Trong Tomcat, Dispatcher đề cập đến cơ chế điều phối yêu cầu (request dispatching), tức là cách Tomcat xử lý và chuyển tiếp các yêu cầu HTTP bên trong ứng dụng web.

Có hai cơ chế chính liên quan đến Dispatcher trong Tomcat:
* RequestDispatcher – Chuyển tiếp hoặc include request trong một ứng dụng web.
* Mapper & Pipeline Dispatcher – Điều phối request trong nội bộ Tomcat.
  
  |DispatcherType|Ý nghĩa|
  |:---:|:----:|
  |REQUEST|Request trực tiếp từ client (mặc định)|
  |FORWARD|Request được chuyển tiếp từ RequestDispatcher.forward()|
  |INCLUDE|Request được bao gồm từ RequestDispatcher.include()|
  |ERROR|Request được xử lý bởi trang lỗi|
  |ASYNC|Request đang xử lý bất đồng bộ (async)|


![image](https://github.com/user-attachments/assets/a6af5997-f38b-401b-a598-c4fa04f7fdd6)


Từ đây ta có thể thấy được hướng khai thác sẽ là:
* Tạo một filterDef sau đó thêm nào StandardContext
* Tạo ra một ApplicationFilterConfig mới từ filterDef ở trên bằng method filterStart() trong StandardContext

![image](https://github.com/user-attachments/assets/f75bf030-ae38-45a3-a27c-b61bdec6608b)

* Tạo filterMap rồi thêm vào StandardContext để filter thực hiện

![image](https://github.com/user-attachments/assets/a32a5968-4b48-418a-bd26-30e811da5879)


Sau khi upload shell mình xóa file shell đi và kết quả mình vẫn thực hiện được lệnh tức là đã thành công

![image](https://github.com/user-attachments/assets/62ca7dff-b184-4e36-86f1-21648b72e023)

![image](https://github.com/user-attachments/assets/fee23c8d-dbf2-4e53-9514-1f999190eaff)


## Inject Memory Webshell thông qua Servlet
### Tìm hiểu về Wrapper
#### Giới thiệu về StandardWrapper
Wrapper là một lớp trong Apache Tomcat, đóng vai trò quản lý vòng đời của một Servlet trong ứng dụng web. Nó là giúp Tomcat quản lý từng Servlet riêng lẻ theo chuẩn Java Servlet API.

Container là một interface trong Tomcat dùng để chứa các Wrapper hoặc Context
#### Vai trò của Wrapper trong Tomcat
* Quản lý vòng đời Servlet
  * Gọi init() khi Servlet được khởi tạo
  * Xử lý request bằng service()
  * Gọi destroy() khi Servlet bị dỡ bỏ
  * Lấy thông tin của servlet getServletInfo()
  * Cấu hình servlet getServletConfig()
* Quản lý instance Servlet
* Hỗ trợ tải Servlet theo yêu cầu (Lazy Loading)
* Ghi log & xử lý lỗi Servlet

#### Cách Wrapper hoạt động
* Khi Tomcat khởi động
  * Wrapper được tạo ra cho mỗi Servlet được định nghĩa trong ứng dụng. Nếu không có wrapper thì sẽ sử dụng StandardWrapper
  * Nếu load-on-startup >= 0, nó sẽ khởi tạo Servlet ngay lập tức.
* Khi có request đến Servlet
  * Nếu Servlet chưa được khởi tạo, Wrapper sẽ gọi loadServlet().
  * service() của Servlet được gọi để xử lý request.
* Khi Tomcat dừng hoặc ứng dụng bị undeploy
  * Wrapper gọi destroy() để dọn dẹp tài nguyên
 
Trong class StandardContext ta thấy có method là addChild() với tham số chuyền vào là một conntainer với chức năng chính là thay thế Servlet JSP cũ bằng một Servlet JSP mới và đảm bảo việc ánh xạ URL đến servlet mới.

  ![Screenshot 2025-02-11 091850](https://github.com/user-attachments/assets/cd6878c4-5e5e-4a3f-b083-a33823825dd0)

Không chỉ vậy trong StandardContext còn hộ trợ tạo một wrapper mới thông qua method createWrapper(). Nếu wrapper chống thì sẽ tự động tạo StandardContext

![image](https://github.com/user-attachments/assets/a7b9e5af-d0bd-4ff5-b938-f30b03cd5b1c)



![image](https://github.com/user-attachments/assets/4dc66750-72e8-4242-8a73-875d3e95aac9)


Từ đấy ta hình dung được quá quá trình inject sẽ là 
* Tạo một wrapper mới
* Set các giá trị cho các thuộc tính của wrapper này
* Thay thế các servlet cũ bằng servlet mới vừa tạo

![image](https://github.com/user-attachments/assets/e85772c8-7b8a-418e-b603-2d968bf580dd)


![image](https://github.com/user-attachments/assets/3560ebdb-ae00-4e0b-af9d-65f8f42e1a9b)

![image](https://github.com/user-attachments/assets/3b99528f-4ef7-4238-bdcc-a3d3be06da61)


![image](https://github.com/user-attachments/assets/6c98e547-6271-46fa-9dbb-b27619b29a2d)

![image](https://github.com/user-attachments/assets/fc086582-d449-4104-a655-dbaf14f38704)









 























 






