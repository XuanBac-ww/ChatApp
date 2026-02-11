Chat App Frontend (React + Vite)
A modern, responsive frontend for a real-time Chat application, optimized for great user experience and performance.

🛠 Tech Stack

- Networking: Fetch API (custom fetch client) & SockJS/STOMP (WebSockets)
- Styling: Tailwind CSS / CSS Modules
- State Management: React Context API & custom hooks

🔗 Backend Integration
This project is designed to work with a Spring Boot backend. Create a `.env` file at the project root:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Make sure the backend is configured with CORS to allow `http://localhost:5173`.

📂 Project Structure

```text
src/
├── components/
│   ├── common/      # Core UI components (Button, Input, StateMessage)
│   ├── forms/       # Authentication form logic (Login, Register)
│   ├── layout/      # Main layout building blocks (MainLayout, SideNav, Skeleton)
│   └── ui/          # Chat-specific UI (MessageBubble, FriendCard, ProfileField)
├── context/         # AuthContext for global authentication state
├── hooks/           # Custom hooks separating logic (useAuth, useChat, useConversations)
├── libs/            # fetchClient.js: Fetch configuration, JWT handling, interceptors
├── pages/           # Route-level pages (Auth, Home, Chat, Profile)
├── service/         # Service layer for API calls per domain (auth, user, message)
└── utils/           # Utility helpers (dateUtils.js)
```
💡 Key Features
- Custom Fetch Client: Uses the native Fetch API to perform HTTP requests.
- Automatically attaches JWT tokens to request headers.
- Centralized handling of error responses (401 Unauthorized, etc.).
- Real-time Messaging: Instant chat experience via STOMP over WebSockets.
- Responsive Layout: Adapts well to both desktop and mobile screens.