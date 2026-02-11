# Chat App Frontend (React + Vite)
A modern, responsive user interface for the Chat Application.

## 🛠 Tech Stack
- Framework: React.js (Vite)
- Networking: Axios & SockJS/STOMP
- Styling: Tailwind CSS / CSS Modules

## 🔗 Backend Integration
Create a `.env` file:

```env
VITE_API_BASE_URL=http://localhost:8080

```

## 📂 Project Structure
`src/`
- `main.jsx`: Entry point, mounts the React app.
- `App.jsx`: Root component, high-level layout and routing.
- `App.css`: Global styles.

- `assets/`
  - Static assets such as images and SVGs used in the UI.

- `components/`
  - `common/`: Reusable primitives (`Button`, `ChatInput`, `InputField`, `StateMessage`).
  - `forms/`: Auth-related form components (`LoginForm`, `RegisterForm`).
  - `layout/`: Layout building blocks (`MainLayout`, `SideNav`, `Menu`, `Logo`, `MainImage`, `ChatRedirect`, `SkeletonConversation`).
  - `ui/`: Reusable UI pieces for the chat domain (`ConversationItem`, `MessageBubble`, `FriendCard`, `AccountPreview`, `InfoCard`, `PendingRequestItem`, `ProfileField`, `SearchResultItem`).

- `context/`
  - `AuthContext.jsx`: React context for authentication state and actions.

- `hooks/`
  - Custom hooks for encapsulating logic (`useAuth`, `useChat`, `useConversations`, `useFriendRequest`, `useProfile`, `useUserSearch`, `useAvatarUpload`).

- `libs/`
  - `fetchClient.js`: Pre-configured Axios client (e.g. base URL, interceptors, JWT handling).

- `pages/`
  - Route-level pages grouped by feature:
    - `Auth/`: `LoginPage`, `SignUpPage`, `VerifyAccountPage`.
    - `Home/`: `Homepage`.
    - `Chat/`: `MessagePage`.
    - `FriendRequest/`: `FriendShipRequestPage`.
    - `Friendship/`: `FriendshipPage`.
    - `Profile/`: `ProfilePage`.

- `service/`
  - API service modules for each domain (`authService`, `userService`, `conversationService`, `friendshipService`, `adminService`, `imageService`).

- `utils/`
  - `dateUtils.js`: Helpers for formatting and working with dates in the UI.

## 🚀 Getting Started
1. `npm install`
2. `npm run dev`

## 💡 Key Features
- JWT Authentication with Axios Interceptors
- Real-time messaging via WebSockets
