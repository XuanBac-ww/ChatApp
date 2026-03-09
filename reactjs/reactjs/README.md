# 💬 Frontend Chat App - React + Vite

README này tổng hợp chi tiết kiến trúc và kỹ thuật của phần frontend trong dự án, dựa trên source code thực tế tại `reactjs/reactjs/src`.

---

## 1) 🚀 Giới thiệu

Đây là ứng dụng frontend cho hệ thống chat thời gian thực, tập trung vào trải nghiệm nhắn tin 1-1, quản lý bạn bè và quản lý tài khoản.

### Tính năng chính cho người dùng

- 🔐 Đăng nhập, đăng ký và xác thực tài khoản qua OTP.
- 👤 Quản lý hồ sơ cá nhân (xem/sửa thông tin, cập nhật avatar).
- 👥 Tìm kiếm người dùng theo số điện thoại và gửi lời mời kết bạn.
- ✅ Quản lý lời mời kết bạn (chấp nhận/từ chối) có phân trang.
- 💬 Nhắn tin theo hội thoại với khả năng cập nhật real-time qua WebSocket.
- 🧭 Điều hướng tập trung trong layout chính (`/home`) với sidebar và danh sách hội thoại.

---

## 2) 🧰 Stack công nghệ (Tech Stack)

### Bảng tổng quan

| Nhóm | Công nghệ | Trạng thái trong dự án |
|---|---|---|
| Framework | React 19 | ✅ Có sử dụng |
| Build Tool | Vite 7 | ✅ Có sử dụng |
| Styling | Tailwind CSS v4 (`@tailwindcss/vite`) | ✅ Có sử dụng |
| Routing | React Router DOM v7 | ✅ Có sử dụng |
| State Management | Context API + Custom Hooks + local state | ✅ Có sử dụng |
| Form & Validation | Controlled form thủ công + validation cơ bản | ✅ Có sử dụng |
| Realtime | SockJS + STOMPJS | ✅ Có sử dụng |
| Date Utility | date-fns | ✅ Có sử dụng |
| Icon | lucide-react | ✅ Có sử dụng |

### Chi tiết theo yêu cầu

- **Framework & Build Tool:** ReactJS + Vite.
- **Styling:** Tailwind CSS v4.
  - Plugin UI như **HeadlessUI** / **DaisyUI**: ❌ **Không phát hiện trong source hiện tại**.
- **State Management:**  
  - ❌ Redux Toolkit: không dùng  
  - ❌ Zustand: không dùng  
  - ❌ React Query: không dùng  
  - ✅ **Context API** (`AuthContext`) + custom hooks domain-based
- **Routing:** React Router Dom (nested routing tại `/home`).
- **Form & Validation:**  
  - ❌ React Hook Form / Zod / Yup: không dùng  
  - ✅ Dùng controlled inputs + validate thủ công (`trim`, `required`, kiểm tra MIME file)

### Thư viện có trong dependencies nhưng chưa thấy dùng trực tiếp trong `src`

- `firebase`
- `@heroicons/react`
- `react-icons`

---

## 3) 🏗️ Kiến trúc Frontend

### 3.1 Cấu trúc thư mục

Frontend đang theo hướng **Folder-by-feature kết hợp layer-based** (mức thực dụng, dễ mở rộng cho dự án vừa).

```txt
src/
├─ components/
│  ├─ common/        # Primitive dùng lại (Button, InputField, ErrorBoundary...)
│  ├─ forms/         # Form theo nghiệp vụ auth
│  ├─ layout/        # Khung bố cục chính (MainLayout, SideNav...)
│  └─ ui/            # UI component theo domain (FriendCard, MessageBubble...)
├─ context/          # Auth context toàn cục
├─ hooks/            # Custom hooks chứa business logic theo use-case
├─ libs/             # Hạ tầng gọi API (fetchClient)
├─ pages/            # Màn hình theo tính năng: Auth, Chat, Profile, Friendship...
├─ service/          # Service layer theo domain API
└─ utils/            # Helper thuần (date/user/conversation/ui class constants)
```

### 3.2 Custom Hooks quan trọng

| Hook | Mục đích |
|---|---|
| `useAuth` | Truy cập nhanh `AuthContext` |
| `useUserProfile` | Tải profile, chỉnh sửa thông tin, lưu profile, logout |
| `useToast` | Quản lý toast message + auto hide |
| `useConversations` | Lấy danh sách hội thoại và đồng bộ recipient đang chọn |
| `useRecipientResolver` | Resolve người nhận từ URL param/state điều hướng |
| `useChatSession` | Khởi tạo conversation, lấy messages, kết nối WebSocket, gửi tin nhắn |
| `useAvatarUpload` | Chọn file, preview ảnh, upload/update avatar |
| `useUserSearch` | Tìm user theo số điện thoại và gửi lời mời kết bạn |
| `useFriendRequests` | Load danh sách request phân trang, accept/reject |

### 3.3 Cách quản lý API

API được tổ chức theo mô hình **infrastructure client + service layer**:

1. **`libs/fetchClient.js`** là HTTP client dùng chung:
   - Base URL từ `VITE_API_URL`
   - Hỗ trợ `GET params`, JSON body, `FormData`
   - Timeout với `AbortController`
   - Chuẩn hóa error handling
2. **Xử lý auth tập trung** trong `fetchClient`:
   - Gắn `Authorization: Bearer <token>` khi `isAuth = true`
   - Check `token_expiry` từ `localStorage`
   - Tự logout/redirect khi gặp `401/403` hoặc token hết hạn
3. **Service layer theo domain** (`authService`, `userService`, `conversationService`, `friendshipService`, `imageService`, `adminService`) giúp tách biệt UI và logic gọi API.

---

## 4) ⚙️ Kỹ thuật nổi bật (Technical Highlights)

### 4.1 Tối ưu hiệu suất

Đang có:

- ✅ `React.memo` cho một số component thường render lại (`LoginForm`, `RegisterForm`, `InputField`, `ConversationItem`...).
- ✅ `useCallback` cho các handler quan trọng.
- ✅ `loading="lazy"` cho ảnh avatar trong danh sách hội thoại.
- ✅ Skeleton loading (`SkeletonConversation`) để cải thiện perceived performance.

Chưa thấy trong code hiện tại:

- ❌ Code splitting với `React.lazy` / `Suspense`
- ❌ Data caching layer (React Query/SWR)
- ❌ Virtualized list cho danh sách tin nhắn dài
- ❌ Debounce/throttle cho một số input tìm kiếm

### 4.2 Tổ chức Reusable Components

Chiến lược tái sử dụng khá rõ:

- **Common primitives:** `Button`, `InputField`, `StateMessage`, `ErrorBoundary`, `ChatInput`
- **Domain UI components:** `FriendCard`, `ConversationItem`, `MessageBubble`, `ProfileField`, `SearchResultItem`, `PendingRequestItem`
- **Layout components:** `MainLayout`, `SideNav`, `ChatRedirect`, `Logo`, `MainImage`, `Menu`

=> Cách chia này giúp page-level code mỏng hơn và dễ maintain.

### 4.3 Responsive Design & Dark Mode

- 📱 **Responsive:** Có dùng breakpoints Tailwind (`sm`, `md`, `lg`, `xl`) ở nhiều page.
- 🌙 **Dark Mode:** ❌ Chưa thấy triển khai (`dark:` classes hoặc cơ chế theme toggle chưa có).

### 4.4 UI Components / Icon Libraries

- UI styling chính: **Tailwind CSS**
- Icon chính trong source: **lucide-react**
- Các package icon/UI khác có trong dependencies nhưng chưa thấy dùng trực tiếp: `@heroicons/react`, `react-icons`

---

## 5) 🧪 Hướng dẫn phát triển (Development)

### 5.1 Cài đặt dependencies

```bash
npm install
```

Hoặc:

```bash
yarn
```

### 5.2 Chạy môi trường development

```bash
npm run dev
```

### 5.3 Build production

```bash
npm run build
```

### 5.4 Preview bản build

```bash
npm run preview
```

### 5.5 Lint code

```bash
npm run lint
```

### 5.6 Biến môi trường cần thiết (`.env`)

Hiện tại frontend detect biến môi trường bắt buộc:

| Biến | Bắt buộc | Mục đích |
|---|---|---|
| `VITE_API_URL` | ✅ Có | Base URL cho toàn bộ API request |

Ví dụ file `.env`:

```env
VITE_API_URL=http://localhost:8080
```

---

## 📌 Gợi ý cải tiến kỹ thuật

- Bổ sung route-level code splitting (`React.lazy`) để giảm bundle initial.
- Chuẩn hóa validation với `React Hook Form + Zod` nếu form nghiệp vụ tăng.
- Cân nhắc thêm React Query cho cache/invalidation dữ liệu API.
- Thêm dark mode token/theme để đồng bộ trải nghiệm UI.

---

## 👨‍💻 Ghi chú

Tài liệu này phản ánh trạng thái source code frontend hiện tại trong thư mục `reactjs/reactjs`.
