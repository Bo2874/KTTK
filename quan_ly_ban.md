# Module Quản lý Bàn (Table Management)

## 1. Thiết kế giao diện bên client/cho người dùng cuối
Giao diện quản lý bàn dành cho tài khoản quản lý (Manager) và nhân viên phục vụ được thiết kế trực quan, giúp bao quát toàn bộ tình trạng phòng khách thực tế của nhà hàng. Bao gồm các màn hình chính sau:

### 1.1. Màn hình Sơ đồ Bàn (Table Grid/Map View)

### 1.2. Màn hình Thêm/Sửa Bàn (Table Form)
- **Thông tin cơ bản:** Form nhập liệu gồm Mã bàn (tableCode), Khu vực (area - dropdown), Sức chứa tối đa số ghế (capacity).


---

## 2. Thiết kế biểu đồ lớp chi tiết và Phân tích Pattern

Module quản lý bàn tiếp tục kế thừa kiến trúc **MVC** kết hợp với **Facade Pattern** và **DAO Pattern**. Việc quản lý bàn đòi hỏi kiểm soát tính toàn vẹn dữ liệu cực kỳ khắt khe (không được phép xóa hay bảo trì khi đang có khách).

### 2.1. Chi tiết các phương thức của cấu trúc Lớp (Implementation-level)

**Lớp TableController**
- Hàm hiển thị sơ đồ bàn theo khu vực -> `listTables(area: String, session: HttpSession, model: Model): String`
  o Tham số vào: area (String), session (HttpSession), model (Model)
  o Tham số ra: String (Tên file giao diện Thymeleaf dạng lưới)
- Hàm hiển thị Form sửa thông tin bàn -> `showEditForm(id: Long, session: HttpSession, model: Model): String`
  o Tham số vào: id (Long), session, model
  o Tham số ra: String (Hiển thị file form với object đã điền sẵn)
- Hàm hiển thị Form thêm thông tin bàn -> `showAddForm(session: HttpSession, model: Model): String`
  o Tham số vào: session, model
  o Tham số ra: String (Hiển thị file form với object rỗng)
- Hàm xóa một bàn khỏi hệ thống -> `deleteTable(id: Long, session: HttpSession): String`
  o Tham số vào: id (Long), session
  o Tham số ra: String (Lệnh redirect về sơ đồ bàn)
- Hàm lưu thông tin bàn mới/cập nhật từ form, có kiểm tra trùng lặp mã bàn -> `saveTable(table: RestaurantTable, session: HttpSession, model: Model): String`
  o Tham số vào: table (RestaurantTable), session, model
  o Tham số ra: String (Lệnh redirect hoặc tên file form kèm thông báo lỗi `errorMessage` nếu mã trùng)

**Lớp TableFacade**
- Hàm lấy danh sách bàn -> `getTables(area: String): List<RestaurantTable>`
  o Tham số vào: area (String)
  o Tham số ra: List<RestaurantTable>
- Hàm lấy thông tin bàn -> `getById(id: Long): RestaurantTable`
  o Tham số vào: id (Long)
  o Tham số ra: RestaurantTable
- Hàm lưu thông tin bàn -> `save(table: RestaurantTable): RestaurantTable`
  o Tham số vào: table (RestaurantTable)
  o Tham số ra: RestaurantTable
- Hàm xóa bàn -> `delete(id: Long): void`
  o Tham số vào: id (Long)
  o Tham số ra: void
- Hàm kiểm tra trùng mã bàn -> `isTableCodeUnique(tableCode: String, expectedId: Long): boolean`
  o Tham số vào: tableCode (String), expectedId (Long)
  o Tham số ra: boolean

**Lớp TableService**
- Hàm lấy toàn bộ danh sách bàn -> `findAllTables(): List<RestaurantTable>`
  o Tham số vào: không
  o Tham số ra: List<RestaurantTable>
- Hàm lọc bàn theo khu vực và trạng thái -> `findByAreaAndStatus(area: String, status: String): List<RestaurantTable>`
  o Tham số vào: area (String), status (String)
  o Tham số ra: List<RestaurantTable>
- Hàm tìm thông tin chi tiết một bàn -> `findById(id: Long): Optional<RestaurantTable>`
  o Tham số vào: id (Long)
  o Tham số ra: Optional<RestaurantTable>
- Hàm xóa thực thể bàn -> `deleteTable(id: Long): void`
  o Tham số vào: id (Long)
  o Tham số ra: void
- Hàm lưu thực thể bàn vào cơ sở dữ liệu -> `saveTable(table: RestaurantTable): RestaurantTable`
  o Tham số vào: table (RestaurantTable)
  o Tham số ra: RestaurantTable
- Hàm kiểm tra tính duy nhất của mã bàn -> `isTableCodeUnique(tableCode: String, expectedId: Long): boolean`
  o Tham số vào: tableCode (String), expectedId (Long)
  o Tham số ra: boolean
- Hàm cập nhật trạng thái bàn -> `updateStatus(id: Long, status: String): void`
  o Tham số vào: id (Long), status (String)
  o Tham số ra: void
- Hàm cập nhật trạng thái trực tiếp -> `updateTableStatus(tableId: Long, status: String): void`
  o Tham số vào: tableId (Long), status (String)
  o Tham số ra: void
- Hàm tìm các bàn còn trống theo ngày giờ -> `getAvailableTables(date: String, time: String): List<RestaurantTable>`
  o Tham số vào: date (String), time (String)
  o Tham số ra: List<RestaurantTable>

**Lớp TableDAO (Interface theo chuẩn Repository pattern)**
- Hàm thêm mới bản ghi vào bảng tbl_restaurant_table -> `insert(table: RestaurantTable): void`
  o Tham số vào: table (RestaurantTable)
  o Tham số ra: void
- Hàm cập nhật thông tin chỉnh sửa -> `update(table: RestaurantTable): void`
  o Tham số vào: table (RestaurantTable)
  o Tham số ra: void
- Hàm xóa bản ghi -> `delete(id: Long): void`
  o Tham số vào: id (Long)
  o Tham số ra: void
- Hàm tìm toàn bộ thực thể -> `findAll(): List<RestaurantTable>`
  o Tham số vào: không
  o Tham số ra: List<RestaurantTable>
- Hàm truy vấn bàn kết hợp tiêu chí -> `findByAreaAndStatus(area: String, status: String): List<RestaurantTable>`
  o Tham số vào: area (String), status (String)
  o Tham số ra: List<RestaurantTable>
- Hàm tìm một bản ghi theo khóa chính -> `findById(id: Long): Optional<RestaurantTable>`
  o Tham số vào: id (Long)
  o Tham số ra: Optional<RestaurantTable>
- Hàm tìm bản ghi dựa theo mã bàn (để kiểm tra trùng lặp) -> `findByTableCode(tableCode: String): Optional<RestaurantTable>`
  o Tham số vào: tableCode (String)
  o Tham số ra: Optional<RestaurantTable>

### 2.2. Biểu đồ lớp (Class Diagram)

```mermaid
classDiagram
    %% --- TẦNG GIAO DIỆN (VIEWS) ---
    class table_grid_html {
        <<View>>
    }
    class table_form_html {
        <<View>>
    }

    %% --- TẦNG CONTROLLER ---
    class TableController {
        <<Controller>>
        +listTables(area, session, model): String
        +showAddForm(session, model): String
        +showEditForm(id: Long, session, model): String
        +saveTable(table, session, model): String
        +deleteTable(id: Long, session): String
    }

    %% --- TẦNG FACADE ---
    class TableFacade {
        <<Facade>>
        -tableService: TableService
        +getTables(area: String) List~RestaurantTable~
        +getById(id: Long) RestaurantTable
        +save(table: RestaurantTable) RestaurantTable
        +delete(id: Long) void
        +isTableCodeUnique(tableCode: String, expectedId: Long) boolean
    }
    
    %% --- TẦNG SERVICE ---
    class TableService {
        <<Service>>
        -tableDAO: TableDAO
        +findAllTables() List~RestaurantTable~
        +findByAreaAndStatus(area, status) List~RestaurantTable~
        +findById(id: Long) Optional~RestaurantTable~
        +deleteTable(id: Long) void
        +updateStatus(id: Long, status: String) void
        +updateTableStatus(tableId: Long, status: String) void
        +saveTable(table: RestaurantTable) RestaurantTable
        +isTableCodeUnique(tableCode: String, expectedId: Long) boolean
        +getAvailableTables(date: String, time: String) List~RestaurantTable~
    }

    %% --- TẦNG REPOSITORY/DAO ---
    class TableDAO {
        <<Repository>>
        +insert(table: RestaurantTable) void
        +update(table: RestaurantTable) void
        +delete(id: Long) void
        +findAll() List~RestaurantTable~
        +findByAreaAndStatus(area, status) List~RestaurantTable~
        +findById(id: Long) Optional~RestaurantTable~
        +findByTableCode(tableCode: String) Optional~RestaurantTable~
    }

    %% --- TẦNG ENTITY ---
    class RestaurantTable {
        <<Entity>>
        -id: Long
        -tableCode: String
        -area: String
        -capacity: Integer
        -status: String
    }

    %% --- QUAN HỆ KẾT NỐI (RELATIONSHIPS) ---
    TableController --> table_grid_html : render view \n"table-grid.html"
    TableController --> table_form_html : render view \n"table-form.html"
    
    TableController --> TableFacade : xử lý đổi trạng thái/xóa bàn an toàn
    
    TableFacade --> TableService : tương tác với Table Entity
    
    TableService --> TableDAO : ủy quyền thao tác DB
    TableDAO --> RestaurantTable : maps to

    %% --- CHÚ THÍCH (NOTES MÔ PHỎNG GIỐNG ẢNH VISUAL PARADIGM) ---
    note for table_grid_html "Hiển thị Grid/Map View:\n- Các block bàn đổi màu theo status\n- Popup danh sách món (nếu OCCUPIED)\n- Nhấp chuột phải để đổi trạng thái"
    
    note for table_form_html "Hiển thị form thiết lập bàn:\n- Input: tableCode, capacity\n- Dropdown: Area, Status"
    
    note for TableService "Xử lý logic cấp Entity:\n- Không quan tâm logic Booking\n- Thao tác CRUD thuần túy với TableDAO."

    note for TableDAO "Tương tác trực tiếp JDBC/JPA lấy từ tbl_restaurant_table."
```

### Phân tích ưu điểm Pattern đã sử dụng:
1. **Facade Pattern (Lớp `TableFacade`):** 
   - **Bối cảnh:** Việc thao tác liên quan tới Bàn yêu cầu kết hợp giữa việc kiểm tra trùng mã bàn, lưu và xóa. Cần một đầu mối chung để có thể tích hợp với các tính năng lớn hơn như Booking sau này.
   - **Ưu điểm:** Bằng cách tập trung giao tiếp vào `TableFacade`, tầng Controller được giải phóng khỏi việc gọi trực tiếp nhiều service khác nhau. `TableFacade` đóng vai trò "người gác cổng" để xử lý nghiệp vụ thông qua `TableService`.
2. **DAO / Repository Pattern (`TableDAO`):** 
   - Trừu tượng hóa thao tác tương tác với Database. Bất kỳ sự thay đổi nào đối với cơ sở dữ liệu đều chỉ cần tác động tại tầng của `TableDAO` mà không ảnh hưởng tới logic kiểm tra ràng buộc chéo trên tầng `TableService` hay `TableFacade`. Qua đó giúp đảm bảo tính linh hoạt và dễ nâng cấp cho code.

---

## 3. Các Biểu đồ Tuần tự (Sequence Diagrams)

Dưới đây là các biểu đồ tuần tự trọng tâm của module, mô phỏng chính xác luồng chạy của code MVC, luồng Validation và Facade Pattern kiểm soát tính vẹn toàn dữ liệu.

### 3.1. Biểu đồ: Lọc bàn theo điều kiện (Khu vực / Trạng thái)
**Các bước gọi luồng xử lý:**
1. Tại màn hình giao diện table-grid.html, Manager chọn khu vực (Area) và ấn nút Lọc Bàn
2. Giao diện table-grid.html gọi lớp TableController
3. Lớp TableController gọi phương thức listTables()
4. Phương thức listTables() gọi lớp TableFacade
5. Lớp TableFacade gọi hàm getTables()
6. Phương thức getTables() gọi phương thức findByAreaAndStatus() hoặc findAllTables() của lớp TableService
7. Lớp TableService gọi gọi lớp TableDAO
8. Lớp TableDAO gọi phương thức findByAreaAndStatus() hoặc findAll() tới cơ sở dữ liệu
9. Cơ sở dữ liệu trả dữ liệu về cho DAO
10. Phương thức của DAO gọi lớp RestaurantTable đóng gói dữ liệu
11. Lớp RestaurantTable lần lượt đóng gói thành các đối tượng RestaurantTable
12. Lớp RestaurantTable trả kết quả tập hợp danh sách các đối tượng RestaurantTable về DAO
13. Phương thức của TableDAO trả kết quả về cho phương thức của TableService
14. Phương thức của TableService trả kết quả về cho phương thức getTables() của Facade
15. Lớp TableFacade trả kết quả về cho phương thức listTables() của TableController
16. Phương thức listTables() gọi tới lớp Model đóng gói dữ liệu
17. Lớp Model đóng gói dữ liệu danh sách Tables
18. Lớp Model trả danh sách về cho phương thức listTables()
19. Phương thức listTables() trả kết quả về giao diện table-grid.html
20. Giao diện table-grid.html hiển thị lưới sơ đồ bàn trực quan cho Manager

```mermaid
sequenceDiagram
    actor Manager
    participant UI as <<Boundary>><br>table-grid.html
    participant Ctrl as <<Control>><br>TableController
    participant Fac as <<Control>><br>TableFacade
    participant Svc as <<Service>><br>TableService
    participant DAO as <<Repository>><br>TableDAO
    participant DB as <<Entity>><br>Database
    participant TableEnt as <<Entity>><br>RestaurantTable
    participant Model as <<Entity>><br>Model

    Manager->>UI: 1. chọn khu vực (Area) và ấn nút Lọc Bàn
    activate UI
    UI->>Ctrl: 2. gọi lớp TableController
    activate Ctrl
    Ctrl->>Ctrl: 3. gọi phương thức listTables()
    
    Ctrl->>Fac: 4. gọi lớp TableFacade
    activate Fac
    Fac->>Fac: 5. gọi hàm getTables()
    
    Fac->>Svc: 6. gọi phương thức findByAreaAndStatus()
    activate Svc
    
    Svc->>DAO: 7. gọi lớp TableDAO
    activate DAO
    DAO->>DB: 8. gọi phương thức findByAreaAndStatus()
    
    activate DB
    DB-->>DAO: 9. trả dữ liệu về
    deactivate DB
    
    DAO->>TableEnt: 10. đóng gói dữ liệu
    activate TableEnt
    TableEnt-->>TableEnt: 11. đóng gói thành các đối tượng RestaurantTable
    TableEnt-->>DAO: 12. trả kết quả tập hợp danh sách các đối tượng
    deactivate TableEnt
    
    DAO-->>Svc: 13. trả kết quả về
    deactivate DAO
    
    Svc-->>Fac: 14. trả kết quả về
    deactivate Svc
    
    Fac-->>Ctrl: 15. trả kết quả về
    deactivate Fac
    
    Ctrl->>Model: 16. gọi tới lớp Model đóng gói dữ liệu
    activate Model
    Model-->>Model: 17. đóng gói dữ liệu danh sách Tables
    Model-->>Ctrl: 18. trả danh sách về
    deactivate Model
    
    Ctrl-->>UI: 19. trả kết quả về giao diện table-grid.html
    deactivate Ctrl
    
    UI-->>Manager: 20. hiển thị lưới sơ đồ bàn trực quan cho Manager
    deactivate UI
```

### 3.2. Biểu đồ: Thêm bàn mới
**Các bước gọi luồng xử lý:**
1. Tại màn hình giao diện table-grid.html, Manager ấn nút Thêm bàn mới
2. Giao diện table-grid.html gọi lớp TableController
3. Lớp TableController gọi phương thức showAddForm()
4. Phương thức showAddForm() gọi lớp Model đóng gói dữ liệu
5. Lớp Model đóng gói đối tượng RestaurantTable rỗng
6. Lớp Model trả thuộc tính về cho phương thức showAddForm()
7. Phương thức showAddForm() trả về giao diện table-form.html
8. Tại màn hình giao diện table-form.html, Manager điền các thông tin bàn mới và ấn nút Lưu Thông Tin
9. Giao diện table-form.html gọi lớp TableController
10. Lớp TableController gọi phương thức saveTable()
11. Phương thức saveTable() gọi lớp TableFacade kiểm tra trùng lặp
12. Lớp TableFacade gọi hàm isTableCodeUnique()
13. Hàm isTableCodeUnique() trả về true/false hợp lệ
14. Phương thức saveTable() của Controller gọi tiếp lớp TableFacade để lưu
15. Lớp TableFacade gọi phương thức save()
16. Phương thức save() gọi tới lớp TableService
17. Lớp TableService gọi phương thức saveTable()
18. Phương thức saveTable() nhận dạng đối tượng tạo mới nên gọi lớp TableDAO
19. Lớp TableDAO gọi phương thức insert()
20. Phương thức insert() gọi tới cơ sở dữ liệu (Database) để lưu dữ liệu
21. Cơ sở dữ liệu trả kết quả lưu thành công về cho phương thức insert() của TableDAO
22. Phương thức insert() của TableDAO trả kết quả về cho phương thức saveTable() của TableService
23. Phương thức saveTable() gọi lớp RestaurantTable đóng gói đối tượng vừa lưu (kèm ID)
24. Lớp RestaurantTable đóng gói đối tượng
25. Lớp RestaurantTable trả đối tượng về cho phương thức saveTable() của TableService
26. Phương thức saveTable() của TableService trả kết quả về cho phương thức save() của TableFacade
27. Phương thức save() của TableFacade trả kết quả về cho Controller
28. Phương thức saveTable() của Controller lệnh điều hướng (redirect) tải lại danh sách
29. Giao diện table-grid.html hiển thị lưới bàn mới cập nhật cho Manager

```mermaid
sequenceDiagram
    actor Manager
    participant Grid as <<Boundary>><br/>table-grid.html
    participant UI as <<Boundary>><br/>table-form.html
    participant Ctrl as <<Control>><br/>TableController
    participant Model as <<Entity>><br/>Model
    participant Fac as <<Control>><br/>TableFacade
    participant Svc as <<Service>><br/>TableService
    participant DAO as <<Repository>><br/>TableDAO
    participant DB as <<Entity>><br/>Database
    participant TableEnt as <<Entity>><br/>RestaurantTable

    Manager->>Grid: 1. ấn nút Thêm bàn mới
    activate Grid
    Grid->>Ctrl: 2. gọi lớp TableController
    activate Ctrl
    Ctrl->>Ctrl: 3. gọi phương thức showAddForm()
    
    Ctrl->>Model: 4. gọi lớp Model đóng gói dữ liệu
    activate Model
    Model-->>Model: 5. đóng gói đối tượng rỗng
    Model-->>Ctrl: 6. trả thuộc tính về
    deactivate Model
    
    Ctrl-->>UI: 7. trả về giao diện table-form.html
    deactivate Ctrl
    deactivate Grid
    
    activate UI
    Manager->>UI: 8. điền các thông tin bàn mới và ấn nút Lưu
    UI->>Ctrl: 9. gọi lớp TableController
    deactivate UI
    
    activate Ctrl
    Ctrl->>Ctrl: 10. gọi phương thức saveTable()
    
    Ctrl->>Fac: 11. gọi lớp TableFacade kiểm tra trùng lặp
    activate Fac
    Fac->>Fac: 12. gọi hàm isTableCodeUnique()
    Fac-->>Ctrl: 13. trả về hợp lệ (true/false)
    
    Ctrl->>Fac: 14. gọi gọi tiếp lớp TableFacade để lưu
    Fac->>Fac: 15. gọi phương thức save()
    
    Fac->>Svc: 16. gọi tới lớp TableService
    activate Svc
    Svc->>Svc: 17. gọi phương thức saveTable()
    Svc->>Svc: 18. nhận dạng đối tượng tạo mới
    
    Svc->>DAO: 18. gọi lớp TableDAO
    activate DAO
    DAO->>DAO: 19. gọi phương thức insert()
    
    DAO->>DB: 20. gọi tới cơ sở dữ liệu để lưu dữ liệu
    activate DB
    DB-->>DAO: 21. trả kết quả lưu thành công về
    deactivate DB
    
    DAO-->>Svc: 22. trả kết quả về
    deactivate DAO
    
    Svc->>TableEnt: 23. gọi lớp RestaurantTable đóng gói đối tượng
    activate TableEnt
    TableEnt-->>TableEnt: 24. đóng gói đối tượng
    TableEnt-->>Svc: 25. trả đối tượng về
    deactivate TableEnt
    
    Svc-->>Fac: 26. trả kết quả về
    deactivate Svc
    
    Fac-->>Ctrl: 27. trả kết quả về cho Controller
    deactivate Fac
    
    Ctrl-->>Grid: 28. lệnh điều hướng (redirect) tải lại danh sách
    deactivate Ctrl
    
    activate Grid
    Grid-->>Manager: 29. hiển thị lưới bàn mới cập nhật cho Manager
    deactivate Grid
```

### 3.3. Biểu đồ: Sửa thông tin bàn (Tích hợp Validation chống trùng Mã bàn)
**Các bước gọi luồng xử lý:**
1. Tại trang sơ đồ bàn table-grid.html, Manager ấn nút Sửa tương ứng với một bàn cụ thể
2. Giao diện table-grid.html gọi lớp TableController
3. Lớp TableController gọi phương thức showEditForm()
4. Phương thức showEditForm() gọi tới lớp TableFacade
5. Lớp TableFacade gọi hàm getById()
6. Hàm getById() gọi lớp TableService
7. Lớp TableService gọi phương thức findById()
8. Phương thức findById() gọi lớp TableDAO
9. Lớp TableDAO gọi phương thức findById() tới cơ sở dữ liệu
10. Cơ sở dữ liệu trả tập kết quả bản ghi về cho TableDAO
11. Lớp TableDAO gọi khởi tạo đóng gói đối tượng RestaurantTable
12. Đối tượng RestaurantTable trả về thực thể cho TableDAO
13. Lớp TableDAO trả kết quả đối tượng về cho TableService
14. Lớp TableService trả kết quả về cho TableFacade
15. Lớp TableFacade trả kết quả về cho TableController
16. Phương thức showEditForm() gọi lớp Model
17. Lớp Model đóng gói đối tượng vừa tìm thấy và trả thuộc tính về
18. Phương thức showEditForm() trả về giao diện table-form.html
19. Giao diện table-form.html được điền sẵn dữ liệu và hiển thị cho Manager
20. Manager chỉnh sửa các thông tin trên form và ấn nút Lưu Thông Tin
21. Giao diện table-form.html gọi lớp TableController
22. Lớp TableController gọi phương thức saveTable()
23. Phương thức saveTable() gọi lớp TableFacade kiểm tra trùng lặp mã bàn
24. Lớp TableFacade gọi hàm isTableCodeUnique()
25. Hàm isTableCodeUnique() trả về tính hợp lệ (true)
26. Phương thức saveTable() nhận diện thông tin mã bàn hợp lệ và bản ghi đã có ID (hành động sửa) nên gọi lớp TableFacade tìm bản gốc bằng getById()
27. Lớp TableFacade thông qua TableService gọi tới cơ sở dữ liệu
28. Dữ liệu bản ghi cũ trả về kết quả cho TableFacade
29. Lớp TableFacade trả kết quả bản ghi cũ về cho TableController
30. TableController lấy Trạng thái (Status) cũ gán vào đối tượng đang lưu để tránh mất dữ liệu
31. Phương thức saveTable() gọi lớp TableFacade để thực thi lưu
32. Lớp TableFacade gọi phương thức save()
33. Phương thức save() gọi tới lớp TableService lưu xuống Database
34. Lớp TableService gọi phương thức saveTable()
35. Phương thức saveTable() nhận dạng đối tượng đã tồn tại ID nên gọi lớp TableDAO cập nhật
36. Lớp TableDAO gọi phương thức update()
37. Cơ sở dữ liệu trả kết quả lưu thành công về cho TableDAO
38. Lớp TableDAO trả kết quả về cho TableService
39. Lớp TableService trả kết quả về cho TableFacade
40. Lớp TableFacade trả kết quả về cho TableController
41. Phương thức saveTable() lệnh điều hướng (redirect) chuyển trang về lại danh sách
42. Giao diện table-grid.html tải lại và hiển thị sơ đồ bàn đã cập nhật cho Manager

```mermaid
sequenceDiagram
    actor Manager
    participant Grid as <<Boundary>><br/>table-grid.html
    participant UI as <<Boundary>><br/>table-form.html
    participant Ctrl as <<Control>><br/>TableController
    participant Model as <<Entity>><br/>Model
    participant Fac as <<Control>><br/>TableFacade
    participant Svc as <<Service>><br/>TableService
    participant DAO as <<Repository>><br/>TableDAO
    participant DB as <<Entity>><br/>Database
    participant TableEnt as <<Entity>><br/>RestaurantTable

    Manager->>Grid: 1. ấn nút Sửa
    activate Grid
    Grid->>Ctrl: 2. gọi lớp TableController
    activate Ctrl
    Ctrl->>Ctrl: 3. gọi phương thức showEditForm()
    
    Ctrl->>Fac: 4. gọi tới lớp TableFacade
    activate Fac
    Fac->>Fac: 5. gọi hàm getById()
    
    Fac->>Svc: 6. gọi lớp TableService
    activate Svc
    Svc->>Svc: 7. gọi phương thức findById()
    
    Svc->>DAO: 8. gọi lớp TableDAO
    activate DAO
    DAO->>DB: 9. gọi phương thức findById()
    activate DB
    DB-->>DAO: 10. trả tập kết quả bản ghi về
    deactivate DB
    
    DAO->>TableEnt: 11. gọi khởi tạo đóng gói đối tượng
    activate TableEnt
    TableEnt-->>DAO: 12. trả về thực thể cho TableDAO
    deactivate TableEnt
    
    DAO-->>Svc: 13. trả kết quả đối tượng về
    deactivate DAO
    
    Svc-->>Fac: 14. trả kết quả về
    deactivate Svc
    
    Fac-->>Ctrl: 15. trả kết quả về
    deactivate Fac
    
    Ctrl->>Model: 16. gọi lớp Model
    activate Model
    Model-->>Ctrl: 17. trả thuộc tính đối tượng về
    deactivate Model
    
    Ctrl-->>UI: 18. trả về giao diện table-form.html
    deactivate Ctrl
    deactivate Grid
    
    activate UI
    UI-->>Manager: 19. điền sẵn dữ liệu và hiển thị
    
    Manager->>UI: 20. chỉnh sửa thông tin và ấn Lưu
    UI->>Ctrl: 21. gọi lớp TableController
    deactivate UI
    
    activate Ctrl
    Ctrl->>Ctrl: 22. gọi phương thức saveTable()
    
    Ctrl->>Fac: 23. gọi lớp TableFacade kiểm tra trùng lặp mã bàn
    activate Fac
    Fac->>Fac: 24. gọi hàm isTableCodeUnique()
    Fac-->>Ctrl: 25. trả về tính hợp lệ (true)
    deactivate Fac
    
    Ctrl->>Fac: 26. gọi lớp TableFacade tìm bản gốc bằng getById()
    activate Fac
    Fac->>Svc: 27. thông qua TableService gọi tới cơ sở dữ liệu
    activate Svc
    Svc-->>Fac: 28. trả kết quả bản ghi cũ về
    deactivate Svc
    Fac-->>Ctrl: 29. trả kết quả bản ghi cũ về
    deactivate Fac
    
    Ctrl->>Ctrl: 30. lấy Trạng thái (Status) cũ gán vào đối tượng
    
    Ctrl->>Fac: 31. gọi TableFacade để thực thi lưu
    activate Fac
    Fac->>Fac: 32. gọi phương thức save()
    
    Fac->>Svc: 33. gọi tới lớp TableService lưu xuống Database
    activate Svc
    Svc->>Svc: 34. gọi phương thức saveTable()
    
    Svc->>DAO: 35. gọi lớp TableDAO cập nhật
    activate DAO
    DAO->>DB: 36. gọi phương thức update()
    activate DB
    DB-->>DAO: 37. trả kết quả lưu thành công
    deactivate DB
    
    DAO-->>Svc: 38. trả kết quả về
    deactivate DAO
    
    Svc-->>Fac: 39. trả kết quả về
    deactivate Svc
    
    Fac-->>Ctrl: 40. trả kết quả về cho Controller
    deactivate Fac
    
    Ctrl-->>Grid: 41. lệnh điều hướng (redirect) chuyển trang
    deactivate Ctrl
    
    activate Grid
    Grid-->>Manager: 42. hiển thị sơ đồ bàn đã cập nhật
    deactivate Grid
```

### 3.4. Biểu đồ: Xóa Bàn
**Các bước gọi luồng xử lý:**
1. Tại màn hình giao diện table-grid.html, Manager nhấn nút Xóa tương ứng với một bàn cụ thể
2. Giao diện xác nhận xóa xuất hiện, Manager chọn đồng ý (OK)
3. Giao diện table-grid.html gọi lớp TableController
4. Lớp TableController gọi phương thức deleteTable()
5. Phương thức deleteTable() gọi tới lớp TableFacade
6. Lớp TableFacade gọi phương thức delete()
7. Phương thức delete() gọi tới lớp TableService
8. Lớp TableService gọi phương thức deleteTable()
9. Phương thức deleteTable() gọi lớp TableDAO
10. Lớp TableDAO gọi phương thức delete()
11. Phương thức delete() gọi tới cơ sở dữ liệu để thực thi lệnh xóa bản ghi
12. Cơ sở dữ liệu trả kết quả xóa thành công về cho lệnh delete() của TableDAO
13. Phương thức delete() của TableDAO trả kết quả về cho phương thức deleteTable() của TableService
14. Phương thức deleteTable() của TableService trả kết quả về cho phương thức delete() của TableFacade
15. Phương thức delete() của TableFacade trả kết quả về cho phương thức deleteTable() của TableController
16. Phương thức deleteTable() lệnh điều hướng (redirect) chuyển trang về lại danh sách
17. Giao diện table-grid.html tải lại và hiển thị danh sách đã cập nhật cho Manager

```mermaid
sequenceDiagram
    actor Manager
    participant UI as <<Boundary>><br/>table-grid.html
    participant Ctrl as <<Control>><br/>TableController
    participant Fac as <<Control>><br/>TableFacade
    participant Svc as <<Service>><br/>TableService
    participant DAO as <<Repository>><br/>TableDAO
    participant DB as <<Entity>><br/>Database

    Manager->>UI: 1. nhấn nút Xóa tương ứng với bàn
    activate UI
    UI->>UI: 2. xác nhận xóa xuất hiện, Manager đồng ý
    UI->>Ctrl: 3. gọi lớp TableController
    activate Ctrl
    Ctrl->>Ctrl: 4. gọi phương thức deleteTable()
    
    Ctrl->>Fac: 5. gọi tới lớp TableFacade
    activate Fac
    Fac->>Fac: 6. gọi phương thức delete()
    
    Fac->>Svc: 7. gọi tới lớp TableService
    activate Svc
    Svc->>Svc: 8. gọi phương thức deleteTable()
    
    Svc->>DAO: 9. gọi lớp TableDAO
    activate DAO
    DAO->>DAO: 10. gọi phương thức delete()
    
    DAO->>DB: 11. gọi tới cơ sở dữ liệu thực thi xóa bản ghi
    activate DB
    DB-->>DAO: 12. trả kết quả xóa thành công về
    deactivate DB
    
    DAO-->>Svc: 13. trả kết quả về
    deactivate DAO
    
    Svc-->>Fac: 14. trả kết quả về
    deactivate Svc
    
    Fac-->>Ctrl: 15. trả kết quả về
    deactivate Fac
    
    Ctrl-->>UI: 16. lệnh điều hướng (redirect) chuyển trang
    deactivate Ctrl
    
    activate UI
    UI-->>Manager: 17. hiển thị danh sách đã cập nhật
    deactivate UI
```
