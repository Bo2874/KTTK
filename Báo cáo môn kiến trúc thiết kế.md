CHƯƠNG 1: KIẾN TRÚC HỆ THỐNG
I. Kiến trúc hệ thống theo chiều dọc
Hệ thống Quản lý Nhà hàng được xây dựng theo mô hình ứng dụng web truyền thống (Traditional Web Application), triển khai trên nền tảng Spring Boot kết hợp với Thymeleaf làm template engine phía server.
Theo kiến trúc này, toàn bộ logic xử lý — từ tiếp nhận yêu cầu, thực thi nghiệp vụ đến kết xuất giao diện — đều được thực hiện tập trung tại phía máy chủ (Server-Side Rendering - SSR). Trình duyệt của người dùng (khách hàng hoặc nhân viên) đóng vai trò hiển thị các trang HTML đã được server dựng sẵn và phản hồi thông qua giao thức HTTP.
Hệ thống được phân chia thành ba tầng độc lập, giao tiếp chặt chẽ theo chiều dọc như sau:
1.Tầng Trình diễn (Presentation Tier)
Bao gồm các giao diện người dùng được xây dựng bằng Thymeleaf (.html) và CSS/JS, vận hành trực tiếp trên server.
Cơ chế hoạt động: Khi người dùng gửi yêu cầu (ví dụ: đặt bàn online hoặc chọn món), Controller sẽ tiếp nhận, điều phối dữ liệu qua Model và trả về View tương ứng.
Chức năng chính: Hiển thị danh mục món ăn/combo, giao diện đặt bàn trực quan, form nhập liệu cho nhân viên tại quầy, và các biểu đồ thống kê doanh thu/nhà cung cấp một cách sinh động.
2.Tầng Nghiệp vụ (Business / Application Tier)
Được cài đặt bằng nền tảng Spring Boot, đây là "bộ não" của hệ thống, nơi thực thi các quy tắc nghiệp vụ phức tạp. Tầng này áp dụng mẫu thiết kế Facade Pattern để cung cấp một giao diện cấp cao, độc lập và thống nhất cho tầng Trình diễn:
Quản lý vận hành: Xử lý logic đặt bàn (kiểm tra tình trạng bàn trống), điều phối quy trình gọi món và tính toán hóa đơn thanh toán thông qua BookingFacade.
Quản lý kho & cung ứng: Xử lý quy trình nhập nguyên liệu từ nhà cung cấp, kiểm soát định lượng qua InventoryFacade.
Xử lý dữ liệu thống kê: Tính toán các chỉ số kinh doanh như doanh thu theo thời gian, xếp hạng khách hàng qua ReportFacade.
Kết nối: Giao tiếp với tầng dữ liệu thông qua Spring Data JPA để đảm bảo tính nhất quán của dữ liệu.
3.Tầng Dữ liệu (Data Tier)
Sử dụng hệ quản trị cơ sở dữ liệu quan hệ MySQL. Toàn bộ thông tin của nhà hàng được lưu trữ tập trung trong một schema duy nhất, bao gồm các thực thể chính: Khách hàng, Bàn, Món ăn, Nguyên liệu và Nhà cung cấp.
Ưu điểm: Cấu trúc quan hệ cho phép thực hiện các truy vấn phức tạp (JOIN) giữa nhiều bảng để phục vụ công tác thống kê (ví dụ: kết nối bảng Hóa đơn và bảng Món ăn để thống kê doanh thu theo món).
Tính toàn vẹn: Đảm bảo ràng buộc chặt chẽ giữa các module, ví dụ: không thể xóa một nguyên liệu nếu nó vẫn đang thuộc danh sách nhập hàng từ một nhà cung cấp hiện có.
II. Kiến trúc hệ thống theo chiều ngang
Bên cạnh phân tầng theo chiều dọc, hệ thống áp dụng mẫu kiến trúc MVC (Model - View - Controller) theo chiều ngang nhằm phân tách rõ ràng ba thành phần chức năng: dữ liệu, giao diện và điều phối xử lý.
Trong kiến trúc web truyền thống với Spring Boot + Thymeleaf, ba thành phần MVC của Hệ thống Quản lý Nhà hàng được ánh xạ cụ thể như sau:
1.Model - Thành phần Dữ liệu và Nghiệp vụ
Model đóng vai trò quản lý trạng thái và logic cốt lõi của nhà hàng, bao gồm ba loại lớp chính:
Entity (Thực thể): Là các lớp Java (POJO) được đánh dấu @Entity, ánh xạ trực tiếp với các bảng trong MySQL.
Repository (Truy cập dữ liệu - DAO Pattern): Là các interface kế thừa JpaRepository, đóng gói toàn bộ thao tác CRUD và các truy vấn phức tạp (JPQL).
Facade / Service (Lớp nghiệp vụ trung gian): Sử dụng mẫu thiết kế Facade Pattern để đóng gói logic của các Repository có liên quan thành các dịch vụ đơn giản (như BookingFacade, InventoryFacade). Controller chỉ gọi tới các Facade này thay vì phải gọi trực tiếp từng Repository như DishRepository, OrderRepository...
2.View - Thành phần Giao diện Người dùng
View là các file template Thymeleaf (.html) nằm trong thư mục /resources/templates/. Thành phần này chịu trách nhiệm hiển thị thông tin cho cả khách hàng và nhân viên quản lý.
Cơ chế: Thymeleaf sử dụng các thuộc tính như th:each (duyệt danh sách món ăn), th:if (kiểm tra trạng thái bàn), th:object (liên kết form đặt bàn) để kết hợp dữ liệu từ Model vào cấu trúc HTML.
Tính chất: View hoàn toàn không chứa logic nghiệp vụ. Nó chỉ nhận dữ liệu đã được xử lý từ Controller để hiển thị menu, form đặt món online, hoặc các biểu đồ thống kê doanh thu theo tháng/quý/năm.
3.Controller — Thành phần Điều phối
Controller là các lớp Java được đánh dấu @Controller, đóng vai trò "nhà điều hành" kết nối giữa View và Model (Facade).
Điều hướng yêu cầu: Tiếp nhận HTTP Request từ trình duyệt (ví dụ: qua @PostMapping("/booking/confirm")).
Xử lý nghiệp vụ: Thay vì gọi trực tiếp nhiều Services/Repository rối rắm, Controller chỉ gọi tới đối tượng Facade tương ứng (ví dụ: BookingFacade.processBooking()) để thực hiện logic đa chiều (kiểm tra bàn trống, tính tiền, tạo đơn).
Phản hồi: Đưa kết quả xử lý vào Model, sau đó chỉ định tên View để Thymeleaf kết xuất giao diện và trả về kết quả cho trình duyệt.
CHƯƠNG 2: HOẠT ĐỘNG CỦA CÁC MODULE
I.Module “Khách đặt bàn và/hoặc chọn món online”
Hoạt động của module:
Module này cho phép khách hàng chủ động thực hiện quy trình đặt chỗ và lựa chọn thực đơn trực tuyến thông qua giao diện web:
Khách hàng truy cập vào hệ thống website của nhà hàng -> Trang chủ hiển thị thông tin chung và nút chức năng "Đặt bàn ngay" -> Khách hàng chọn chức năng đặt bàn -> Hệ thống hiển thị giao diện gồm các thông tin lựa chọn: thời gian (ngày, giờ), số lượng khách và Sơ đồ bàn trống (hiển thị trực quan mã bàn, vị trí, sức chứa) -> Khách hàng chọn bàn phù hợp -> Hệ thống tiếp tục hiển thị Danh mục món ăn/combo (bao gồm hình ảnh, đơn giá, mô tả món) -> Khách hàng thêm các món ăn vào danh sách chọn và điều chỉnh số lượng -> Hệ thống tự động tính toán và hiển thị tổng tiền tạm tính dựa trên các món đã chọn.
Sau khi kiểm tra thông tin, khách hàng nhấn "Xác nhận đặt bàn" -> Hệ thống hiển thị form nhập thông tin cá nhân (tên, số điện thoại, email) -> Khách hàng nhấn "Hoàn tất" -> Hệ thống lưu thông tin vào cơ sở dữ liệu với trạng thái PENDING (Chờ xác nhận) và thông báo đặt bàn thành công đến khách hàng.
Tại phía quản trị, nhân viên nhận được thông báo về yêu cầu mới, thực hiện kiểm tra và nhấn "Xác nhận" -> Hệ thống cập nhật trạng thái phiếu đặt thành CONFIRMED và khóa bàn đã chọn trên sơ đồ để tránh trùng lặp. Khi khách hàng đến dùng bữa và hoàn tất thanh toán tại quầy, trạng thái phiếu được cập nhật thành COMPLETED, hệ thống ghi nhận doanh thu thực tế và tự động khấu trừ lượng nguyên liệu tương ứng trong kho dựa trên định lượng của các món ăn đã gọi.


II.Module “Quản lý bàn”
Hoạt động của module:
	Module này cho phép quản lý thiết lập cấu hình sơ đồ nhà hàng và theo dõi tình trạng vận hành của các bàn ăn theo thời gian thực:
Người quản lý đăng nhập vào hệ thống -> Chọn chức năng "Quản lý bàn" trên thanh điều hướng -> Hệ thống hiển thị danh sách toàn bộ các bàn trong nhà hàng kèm theo các thông tin: Mã bàn, khu vực (Ví dụ: Tầng 1, Sân thượng, Phòng VIP), sức chứa (số ghế), và Trạng thái hiện tại (Trống, Đã đặt, Đang có khách, hoặc Đang bảo trì).
Khi thêm bàn mới: Quản lý nhấn nút "Thêm bàn" -> Hệ thống hiển thị form nhập liệu bao gồm: số hiệu bàn, chọn khu vực từ danh sách có sẵn, và nhập số chỗ ngồi tối đa -> Quản lý nhấn "Lưu" -> Hệ thống kiểm tra tính duy nhất của mã bàn, ghi nhận vào cơ sở dữ liệu và đặt trạng thái mặc định là AVAILABLE (Trống).
Khi điều chỉnh thông tin: Quản lý chọn một bàn cụ thể để cập nhật lại vị trí hoặc số ghế (ví dụ khi gộp bàn hoặc chuyển đổi công năng phòng) -> Hệ thống cập nhật dữ liệu và đồng bộ hóa ngay lập tức lên giao diện đặt bàn của khách hàng.
Quản lý trạng thái vận hành: * Trong trường hợp bàn bị hỏng hoặc cần vệ sinh chuyên sâu, quản lý chọn bàn và cập nhật trạng thái thành MAINTENANCE (Bảo trì) -> Hệ thống sẽ tự động khóa bàn này trên module "Đặt bàn online" để khách không thể chọn.
oHệ thống tự động liên kết với module "Nhận khách tại quầy" và "Đặt bàn online": Khi một đơn đặt chỗ được xác nhận hoặc khách bắt đầu dùng bữa, trạng thái bàn sẽ tự động chuyển từ AVAILABLE sang RESERVED hoặc OCCUPIED.
oKhi nhân viên nhấn "Thanh toán" tại module hóa đơn, hệ thống sẽ tự động giải phóng bàn, chuyển trạng thái về AVAILABLE để sẵn sàng đón lượt khách tiếp theo.
Logic kỹ thuật đảm bảo:
Tính đồng bộ: Trạng thái bàn là dữ liệu dùng chung giữa khách hàng (đặt online) và nhân viên (tại quầy), đảm bảo không xảy ra tình trạng trùng bàn.
Tính nhất quán: Chỉ những bàn ở trạng thái AVAILABLE mới được xuất hiện trong danh sách lựa chọn của module đặt chỗ.
Quản lý vòng đời: Thể hiện rõ sự thay đổi trạng thái dựa trên các sự kiện (Thêm mới -> Đặt chỗ -> Dùng bữa -> Thanh toán/Giải phóng).
III. Module “Quản lý Món ăn”
Hoạt động của module:
Module này đóng vai trò quản trị nội dung thực đơn (Menu), cho phép nhà hàng linh hoạt điều chỉnh danh mục món ăn, giá bán và các chương trình combo theo tình hình kinh doanh thực tế:
Người quản lý đăng nhập vào hệ thống -> Chọn chức năng "Quản lý Món ăn" -> Hệ thống hiển thị danh sách toàn bộ thực đơn hiện có bao gồm: Hình ảnh món, Tên món, Thuộc nhóm (Khai vị, Món chính, Đồ uống, Combo...), Đơn giá, và Trạng thái phục vụ (Đang bán hoặc Tạm ngưng).
Khi khởi tạo món ăn mới: Quản lý nhấn "Thêm món mới" -> Hệ thống hiển thị form nhập liệu bao gồm: Tên món, mô tả chi tiết, tải lên hình ảnh minh họa, chọn danh mục và thiết lập đơn giá -> Quản lý nhấn "Lưu" -> Hệ thống tạo thực thể Dish trong cơ sở dữ liệu và ngay lập tức đồng bộ hóa để món ăn này xuất hiện trên giao diện "Đặt món online" của khách hàng.
Thiết lập Combo: Quản lý có thể tạo các gói Combo bằng cách chọn nhiều món lẻ và thiết lập một mức giá ưu đãi chung -> Hệ thống lưu thực thể Combo, cho phép khách hàng chọn nhanh các nhóm món ăn thay vì chọn lẻ.
Quản lý trạng thái (Availability): Trong trường hợp nhà bếp báo hết một loại nguyên liệu cụ thể, quản lý tìm kiếm món ăn tương ứng và chuyển trạng thái sang "Hết hàng" (Out of Stock) -> Hệ thống sẽ tự động vô hiệu hóa nút "Chọn món" của món ăn này trên module đặt món online và giao diện tại quầy của nhân viên để tránh việc khách gọi món nhưng không thể phục vụ.
Liên kết định lượng (Recipe Setup): Tại mỗi món ăn, quản lý thiết lập định lượng nguyên liệu cấu thành (Ví dụ: 1 đĩa Bò bít tết gồm 200g Thịt bò, 50g Khoai tây). Đây là dữ liệu nền tảng để hệ thống tự động trừ kho tại Module "Quản lý nguyên liệu" sau khi khách hàng hoàn tất thanh toán hóa đơn.
Logic kỹ thuật đảm bảo:
Tính nhất quán: Dữ liệu món ăn và giá cả là "nguồn sự thật duy nhất" cho cả khách đặt online và nhân viên thu ngân, đảm bảo không có sai lệch về giá.
Tính tương tác: Trạng thái "Đang bán/Hết hàng" tác động trực tiếp đến khả năng đặt hàng của khách theo thời gian thực.
Tính kế thừa: Module này cung cấp thông tin cho Module Thống kê món ăn, giúp hệ thống phân tích được món nào đang mang lại doanh thu cao nhất dựa trên lịch sử giao dịch.
