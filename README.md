# ChatApp

ChatApp là ứng dụng chat realtime full-stack, gồm backend Spring Boot và frontend React. Dự án tập trung vào xác thực tài khoản, quản lý bạn bè, hội thoại 1-1 và nhắn tin thời gian thực.

## Cấu Trúc

```text
ChatApp
|- backend
|  |- src
|  |- docker
|  |- pom.xml
|  \- mvnw.cmd
|- frontend
|  |- src
|  |- public
|  |- package.json
|  \- vite.config.js
\- README.md
```

## Chức Năng Đã Triển Khai

### Xác Thực Và Tài Khoản

- Đăng ký tài khoản bằng email, họ tên, số điện thoại và mật khẩu.
- Gửi OTP qua email để xác thực tài khoản.
- Đăng nhập bằng JWT access token.
- Refresh access token bằng refresh token.
- Đăng xuất và vô hiệu hóa phiên đăng nhập hiện tại.
- Reset mật khẩu qua email.
- Lưu lịch sử đăng nhập phục vụ refresh token.

### Hồ Sơ Người Dùng

- Lấy thông tin tài khoản đang đăng nhập.
- Cập nhật thông tin hồ sơ cá nhân.
- Tìm kiếm người dùng theo tên hoặc số điện thoại.
- Upload và cập nhật avatar qua Cloudinary.

### Kết Bạn

- Gửi lời mời kết bạn.
- Chấp nhận lời mời kết bạn.
- Từ chối lời mời kết bạn.
- Xem danh sách bạn bè có phân trang.
- Xem danh sách lời mời đang chờ xử lý.

### Chat Realtime

- Tạo hoặc lấy lại cuộc trò chuyện 1-1.
- Xem danh sách hội thoại của người dùng.
- Xem lịch sử tin nhắn theo hội thoại.
- Gửi tin nhắn qua REST API.
- Broadcast tin nhắn realtime qua WebSocket, SockJS và STOMP.
- Xác thực kết nối WebSocket bằng JWT.

### Quản Trị

- Xem danh sách tài khoản active.
- Xem danh sách tài khoản đã bị xóa mềm.
- Xóa mềm tài khoản người dùng.

### Hạ Tầng Backend

- Bảo vệ API bằng Spring Security và JWT filter.
- Phân quyền route bằng role `USER` và `ADMIN`.
- Rate limit một số endpoint quan trọng bằng custom annotation và Redis.
- Chuẩn hóa response API bằng `ApiResponse` và `PageResponse`.
- Xử lý exception tập trung bằng global exception handler.
- Audit entity bằng Hibernate Envers.
- Soft delete cho tài khoản người dùng.
- Tài liệu API bằng Swagger UI.
- Docker Compose cho backend, MySQL và Redis.

### Frontend

- Trang đăng nhập.
- Trang đăng ký.
- Trang xác thực OTP.
- Trang home sau khi đăng nhập.
- Trang chat theo hội thoại.
- Trang danh sách bạn bè.
- Trang lời mời kết bạn.
- Trang hồ sơ cá nhân.
- Layout chính với sidebar điều hướng.
- Error boundary cho lỗi giao diện.
- Fetch client dùng `VITE_API_URL` và tự xử lý token hết hạn ở mức cơ bản.

## Công Nghệ Triển Khai

### Backend

- Java 17
- Spring Boot 3.5.4
- Spring Security
- Spring Data JPA
- Hibernate Envers
- MySQL
- Redis
- Spring WebSocket
- SockJS và STOMP
- Spring Mail
- Cloudinary
- MapStruct
- Lombok
- Maven
- Docker và Docker Compose
- springdoc OpenAPI / Swagger UI

### Frontend

- React 19
- Vite 7
- React Router 7
- Tailwind CSS 4
- SockJS Client
- STOMPJS
- `jwt-decode`
- `date-fns`
- `lucide-react`
- Fetch API

## Use Case Chính

### UC01 - Đăng Ký Và Xác Thực Tài Khoản

1. Người dùng nhập email, họ tên, số điện thoại và mật khẩu.
2. Backend tạo tài khoản ở trạng thái chưa active.
3. Hệ thống gửi OTP qua email.
4. Người dùng nhập OTP để kích hoạt tài khoản.
5. Tài khoản được active và có thể đăng nhập.

### UC02 - Đăng Nhập

1. Người dùng nhập email và mật khẩu.
2. Backend xác thực thông tin đăng nhập.
3. Backend trả về access token và refresh token.
4. Frontend lưu token và chuyển người dùng vào khu vực chính của ứng dụng.

### UC03 - Refresh Token

1. Frontend gửi refresh token tới backend.
2. Backend kiểm tra refresh token trong lịch sử đăng nhập.
3. Nếu hợp lệ, backend cấp access token mới.

### UC04 - Cập Nhật Hồ Sơ

1. Người dùng mở trang hồ sơ.
2. Frontend gọi API lấy thông tin tài khoản hiện tại.
3. Người dùng chỉnh sửa thông tin cá nhân.
4. Backend validate và lưu thay đổi.

### UC05 - Upload Avatar

1. Người dùng chọn ảnh đại diện.
2. Frontend gửi file ảnh lên backend.
3. Backend upload ảnh lên Cloudinary.
4. Backend lưu thông tin ảnh và trả về dữ liệu avatar mới.

### UC06 - Tìm Kiếm Và Gửi Lời Mời Kết Bạn

1. Người dùng tìm kiếm người khác bằng tên hoặc số điện thoại.
2. Frontend hiển thị kết quả tìm kiếm.
3. Người dùng gửi lời mời kết bạn.
4. Backend tạo friendship ở trạng thái pending.

### UC07 - Duyệt Lời Mời Kết Bạn

1. Người dùng mở trang lời mời kết bạn.
2. Frontend tải danh sách request đang chờ.
3. Người dùng chấp nhận hoặc từ chối.
4. Backend cập nhật trạng thái friendship.

### UC08 - Bắt Đầu Chat 1-1

1. Người dùng chọn một bạn bè để nhắn tin.
2. Frontend gọi API tạo hoặc lấy lại direct conversation.
3. Backend trả về thông tin hội thoại.
4. Frontend mở màn hình chat tương ứng.

### UC09 - Gửi Và Nhận Tin Nhắn Realtime

1. Người dùng nhập nội dung tin nhắn.
2. Frontend gửi message tới backend.
3. Backend lưu tin nhắn vào MySQL.
4. Backend broadcast message qua WebSocket topic của hội thoại.
5. Các client đang mở hội thoại nhận tin nhắn realtime.

### UC10 - Quản Trị Tài Khoản

1. Admin đăng nhập bằng tài khoản có role phù hợp.
2. Admin xem danh sách user active hoặc deleted.
3. Admin thực hiện xóa mềm tài khoản khi cần.

## API Chính

| Nhóm | Endpoint |
| --- | --- |
| Auth | `POST /auths/signup`, `POST /auths/login`, `POST /auths/refresh`, `POST /auths/verify-otp`, `POST /auths/logout`, `POST /auths/reset-password` |
| User | `GET /users/me`, `GET /users/{fullName}`, `PUT /users/update-account`, `POST /users/search` |
| Image | `POST /image/upload`, `PUT /image/update/{imageId}` |
| Friendship | `POST /friend-ship/send-request`, `POST /friend-ship/accept-request`, `POST /friend-ship/reject-request`, `GET /friend-ship/all`, `GET /friend-ship/pending-request` |
| Conversation | `GET /conversations`, `GET /conversations/{conversationId}/messages`, `POST /conversations/{conversationId}/messages`, `POST /conversations/direct` |
| Admin | `GET /admin/all`, `GET /admin/all/deleted`, `DELETE /admin/delete-account` |
| WebSocket | SockJS endpoint `/ws`, broker topic `/topic`, app prefix `/app` |

## Kiểm Tra

- Backend: `.\mvnw.cmd test`
- Frontend: `npm run build`

