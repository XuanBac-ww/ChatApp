# ChatApp - README Dự Án

Đây là dự án chat mini gồm:

- Frontend ReactJS tại `reactjs/reactjs`
- Backend Spring Boot + Spring Security tại `SpringSecurity/SpringSecurity`

README này được viết dựa trên source hiện có của cả hai phía, không chỉ mô tả ý tưởng chung mà bám vào đúng cấu trúc, endpoint, config và luồng nghiệp vụ đang có trong code.

## 1. Tổng quan

Hệ thống là một ứng dụng chat 1-1 có xác thực tài khoản bằng OTP email, đăng nhập bằng JWT, quản lý hồ sơ cá nhân, kết bạn, và nhắn tin thời gian thực qua WebSocket.

### Tính năng chính

- Đăng ký tài khoản bằng email, họ tên, số điện thoại và mật khẩu
- Gửi OTP qua email để kích hoạt tài khoản
- Đăng nhập bằng JWT
- Xem và cập nhật hồ sơ cá nhân
- Upload/cập nhật avatar lên Cloudinary
- Tìm người dùng theo số điện thoại
- Gửi, chấp nhận, từ chối lời mời kết bạn
- Xem danh sách bạn bè có phân trang
- Tạo hoặc lấy lại cuộc trò chuyện direct message
- Xem lịch sử tin nhắn
- Gửi và nhận tin nhắn realtime qua SockJS + STOMP
- Có sẵn các API admin để xem user active/deleted và xóa mềm tài khoản

## 2. Cấu trúc thư mục thực tế

```text
D:\SpringSecurity
|- README.md
|- reactjs
|  \- reactjs
|     |- package.json
|     \- src
\- SpringSecurity
   \- SpringSecurity
      |- pom.xml
      |- docker
      \- src
```

Lưu ý: source hiện tại đang được đặt trong hai thư mục lồng tên giống nhau:

- Frontend: `reactjs/reactjs`
- Backend: `SpringSecurity/SpringSecurity`

## 3. Tech stack

### Frontend

- React 19
- Vite 7
- React Router 7
- Tailwind CSS 4
- SockJS + STOMPJS
- `jwt-decode`
- `date-fns`
- `lucide-react`

### Backend

- Java 17
- Spring Boot 3.5.4
- Spring Security
- Spring Data JPA
- MySQL
- Redis
- Spring WebSocket
- Spring Mail
- MapStruct
- Lombok
- Hibernate Envers
- springdoc OpenAPI / Swagger UI
- Cloudinary

## 4. Kiến trúc hệ thống

### Frontend

Frontend được tách theo hướng page + hooks + service:

- `pages/`: màn hình theo nghiệp vụ
- `hooks/`: gom business logic cho từng use-case
- `service/`: gọi API theo domain
- `libs/fetchClient.js`: HTTP client dùng chung
- `context/AuthContext.jsx`: quản lý user đang đăng nhập
- `components/`: layout, form, common UI, domain UI

### Backend

Backend đi theo layered architecture:

- `controllers/`: nhận request và expose REST API
- `service/`: xử lý nghiệp vụ
- `repository/`: truy cập dữ liệu bằng Spring Data JPA
- `model/`: entity
- `dto/` + `mapper/`: contract request/response và mapping
- `config/`, `filter/`, `security/`, `aspect/`: các concern cắt ngang như JWT, cache, WebSocket, rate limit

## 5. Luồng nghiệp vụ chính

### 5.1 Đăng ký và kích hoạt tài khoản

1. Frontend gọi `POST /auths/signup`
2. Backend tạo user với `active = false`
3. Backend sinh OTP 6 số, lưu vào bảng `VerifyOTP`, gửi qua email
4. Frontend chuyển sang trang xác thực tài khoản
5. Frontend gọi `POST /auths/verify-otp`
6. Backend kiểm tra OTP còn hạn 5 phút, nếu đúng thì kích hoạt tài khoản

### 5.2 Đăng nhập

1. Frontend gọi `POST /auths/login`
2. Backend xác thực bằng Spring Security + `AuthenticationManager`
3. Backend trả về:
   - `token` JWT
   - `expiresIn`
   - `refreshToken`
4. Frontend hiện tại chỉ lưu:
   - `access_token`
   - `token_expiry`

Lưu ý: backend đã hỗ trợ refresh token, nhưng frontend hiện tại chưa triển khai luồng tự động refresh và cũng chưa lưu `refreshToken` sau khi login.

### 5.3 Kết bạn

1. Tìm user theo số điện thoại qua `POST /users/search`
2. Gửi lời mời qua `POST /friend-ship/send-request`
3. Người nhận xem danh sách pending qua `GET /friend-ship/pending-request`
4. Chấp nhận hoặc từ chối qua:
   - `POST /friend-ship/accept-request`
   - `POST /friend-ship/reject-request`
5. Danh sách bạn bè lấy từ `GET /friend-ship/all`

### 5.4 Chat realtime

1. Frontend mở trang chat và gọi `POST /conversations/direct`
2. Backend tạo mới hoặc trả về cuộc trò chuyện direct đã có
3. Frontend lấy lịch sử tin nhắn qua `GET /conversations/{id}/messages`
4. Frontend kết nối WebSocket tới `/ws`
5. Frontend subscribe topic `/topic/conversations/{conversationId}`
6. Khi gửi tin nhắn qua `POST /conversations/{id}/messages`, backend vừa lưu DB vừa broadcast realtime

### 5.5 Avatar

1. Frontend chọn file ảnh
2. Gọi:
   - `POST /image/upload` nếu chưa có avatar
   - `PUT /image/update/{imageId}` nếu đã có avatar
3. Backend upload lên Cloudinary, lưu metadata và hash ảnh
4. Backend có logic tránh dùng trùng nội dung ảnh theo hash

## 6. Routes frontend

Các route chính trong React:

- `/` - landing page
- `/login` - đăng nhập
- `/signup` - đăng ký
- `/verify-account` - nhập OTP kích hoạt tài khoản
- `/home` - layout chính
- `/home/friend-requests` - lời mời kết bạn
- `/home/friends` - danh sách bạn bè
- `/home/profile` - hồ sơ cá nhân
- `/home/message/:fullName` - trang chat

## 7. API backend chính

### Auth - `/auths`

- `POST /auths/signup`
- `POST /auths/login`
- `POST /auths/refresh`
- `POST /auths/verify-otp`
- `POST /auths/logout`
- `POST /auths/reset-password`

### User - `/users`

- `GET /users/me`
- `GET /users/{fullName}`
- `PUT /users/update-account`
- `POST /users/search`

### Friendship - `/friend-ship`

- `POST /friend-ship/send-request`
- `POST /friend-ship/accept-request`
- `POST /friend-ship/reject-request`
- `GET /friend-ship/all`
- `GET /friend-ship/pending-request`

### Conversation - `/conversations`

- `GET /conversations`
- `GET /conversations/{conversationId}/messages`
- `POST /conversations/{conversationId}/messages`
- `POST /conversations/direct`

### Image - `/image`

- `POST /image/upload`
- `PUT /image/update/{imageId}`

### Admin - `/admin`

- `GET /admin/all`
- `GET /admin/all/deleted`
- `DELETE /admin/delete-account`

## 8. Chuẩn response

Backend đang dùng hai wrapper chính:

### `ApiResponse<T>`

```json
{
  "status": 200,
  "success": true,
  "message": "Message",
  "data": {}
}
```

### `PageResponse<T>`

```json
{
  "status": 200,
  "success": true,
  "message": "Message",
  "data": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "last": true
}
```

## 9. Bảo mật và hạ tầng

### JWT

- Backend sinh JWT qua `JwtService`
- JWT chứa thêm các claim:
  - `userId`
  - `name`
  - `avatar`
  - `role`
- `JwtAuthenticationFilter` đọc token từ header `Authorization: Bearer <token>`

### Refresh token

- Refresh token được lưu trong bảng `HistoryLogin`
- API `POST /auths/refresh` đã sẵn sàng ở backend
- Frontend hiện tại chưa dùng luồng refresh này

### Redis

Redis đang được dùng cho:

- Cache dữ liệu user/friend
- Blacklist access token khi logout
- Rate limiting bằng Redis counter + TTL

### Rate limit

Nhiều endpoint đang gắn `@RateLimit(limit = 5, timeWindowSeconds = 60)`.

### WebSocket

- Endpoint: `/ws`
- Broker prefix: `/topic`, `/queue`
- User destination prefix: `/user`
- Frontend đang subscribe theo conversation:
  - `/topic/conversations/{conversationId}`

### Audit và soft delete

- Entity kế thừa `BaseEntity` có:
  - `createdAt`
  - `updatedAt`
  - `createdBy`
  - `updatedBy`
- `User` kế thừa `SoftDelete`
- Dự án dùng Hibernate Envers để lưu audit history

## 10. Cấu hình môi trường

### 10.1 Frontend

Tạo file `.env` trong `reactjs/reactjs`:

```env
VITE_API_URL=http://localhost:8080
```

Lưu ý quan trọng: REST API dùng `VITE_API_URL`, nhưng WebSocket trong `src/hooks/useChat.js` hiện đang hard-code thành:

```js
http://localhost:8080/ws
```

Nếu đổi domain hoặc port backend, cần sửa chỗ này hoặc tách nó thành biến môi trường riêng.

### 10.2 Backend

Backend cần tối thiểu các biến sau:

```env
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
CLOUDINARY_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
DB_URL=jdbc:mysql://localhost:3306/chat?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&zeroDateTimeBehavior=convertToNull
DB_USERNAME=root
DB_PASSWORD=your_password
```

Trong source hiện tại:

- `application.yml` mặc định thiên về môi trường Docker:
  - MySQL host: `mysql`
  - Redis host: `redis`
- `application-dev-local.yml` chỉ override datasource sang `localhost:3306`
- Redis host trong profile local chưa override về `localhost`

## 11. Chạy dự án

### 11.1 Cách khuyến nghị: backend bằng Docker, frontend chạy local

Đây là cách khớp nhất với source hiện có.

### Bước 1: tạo Docker network

```bash
docker network create spring-network
```

### Bước 2: tạo file `.env` cho backend

Đặt file tại:

```text
SpringSecurity/SpringSecurity/.env
```

### Bước 3: chạy backend + MySQL + Redis

Từ thư mục:

```text
SpringSecurity/SpringSecurity
```

chạy:

```bash
docker compose -f docker/docker-compose.yml up --build
```

Backend sẽ chạy ở:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

### Bước 4: chạy frontend

Từ thư mục:

```text
reactjs/reactjs
```

chạy:

```bash
npm install
npm run dev
```

Frontend mặc định chạy ở:

```text
http://localhost:5173
```

### 11.2 Chạy backend local bằng Maven

Từ thư mục:

```text
SpringSecurity/SpringSecurity
```

chạy:

```bash
mvn -Pdev-local spring-boot:run
```

Hoặc dùng Maven Wrapper:

```bash
./mvnw -Pdev-local spring-boot:run
# Windows PowerShell
.\mvnw.cmd -Pdev-local spring-boot:run
```

### Điều cần biết khi chạy local

- MySQL local phải có database `chat`
- `application-dev-local.yml` đang dùng `localhost:3306`
- Redis trong base config vẫn đang là host `redis`

Nếu chạy local hoàn toàn, bạn nên override Redis host về `localhost`, ví dụ:

```bash
mvn -Pdev-local spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.data.redis.host=localhost"
```

## 12. Docker compose hiện có

Backend có sẵn các file:

- `docker/docker-compose.yml`
- `docker/docker-compose.app.yml`
- `docker/docker-compose.db.yml`
- `docker/docker-compose.redis.yml`
- `docker/Dockerfile`

Một vài thông số đang dùng:

- App: `8080:8080`
- Redis: `6379:6379`
- MySQL: `3307:3306`

Lưu ý: MySQL Docker public ra cổng `3307`, trong khi profile `dev-local` của backend đang dùng `3306`. Vì vậy nếu chạy backend local nhưng tái dùng MySQL từ Docker, bạn cần sửa `DB_URL` hoặc override lại port.

## 13. Tài khoản seed

Khi khởi động, `DataInitializer` sẽ tạo sẵn admin nếu chưa có:

- Email: `admin@gmail.com`
- Password: `123`

Tuy nhiên theo đúng logic `createUser(...)` hiện tại, tài khoản này vẫn được tạo với `active = false` và đi qua luồng OTP email giống user thường.

## 14. Testing

Backend hiện có một số test trong `src/test/java`, gồm:

- `AuthServiceTest`
- `JwtServiceTest`
- `UserValidationServiceTest`
- `VerifyOTPServiceTest`
- `CustomUserDetailServiceTest`

Frontend hiện chưa thấy test setup riêng trong source.

## 15. Ghi chú quan trọng theo source hiện tại

- Frontend chưa có màn hình quản trị, dù backend đã có API admin
- Frontend chưa lưu và chưa dùng `refreshToken` sau khi đăng nhập
- WebSocket URL đang hard-code `http://localhost:8080/ws`
- Xác thực UI phía frontend chủ yếu dựa vào token trong `localStorage`; khi API trả `401/403`, app sẽ xóa token và đẩy người dùng về `/login`
- Swagger đã mở sẵn qua Springdoc
- CORS mặc định cho phép `http://localhost:5173`, có thể override bằng `app.cors.allowed-origins`

## 16. Lệnh nhanh

### Frontend

```bash
npm install
npm run dev
npm run build
npm run lint
```

### Backend

```bash
mvn -Pdev-local spring-boot:run
mvn test
mvn clean package
```

### Docker

```bash
docker network create spring-network
docker compose -f docker/docker-compose.yml up --build
```

## 17. Đề xuất cải thiện tiếp theo

- Đưa WebSocket URL sang biến môi trường frontend
- Triển khai lưu và refresh access token bằng `refreshToken`
- Thêm route guard rõ ràng cho frontend
- Bổ sung test cho frontend
- Đồng bộ lại profile local của Redis để chạy local mượt hơn
- Tách secret JWT ra môi trường thay vì hard-code trong `application.yml`
