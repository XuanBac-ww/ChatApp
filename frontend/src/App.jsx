import { Navigate, Route, Routes } from "react-router-dom"; 
import "./App.css";
import Homepage from "./pages/Home/Homepage";
import LoginPage from "./pages/Auth/LoginPage";
import SignUpPage from "./pages/Auth/SignUpPage";
import MainLayout from "./components/layout/MainLayout";
import ProfilePage from "./pages/Profile/ProfilePage";
import FriendsPage from "./pages/Friendship/FriendshipPage";
import FriendShipRequestPage from "./pages/FriendRequest/FriendShipRequestPage";
import MessagePage from "./pages/Chat/MessagePage";
import ChatRedirect from "./components/layout/ChatRedirect";
import VerifyAccountPage from "./pages/Auth/VerifyAccountPage";

function App() {
  return (
      <Routes>
        <Route path="/" element={<Homepage />} /> 
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="/verify-account" element={<VerifyAccountPage />} />

        <Route path="/home" element={<MainLayout />}>
          
          <Route index element={<ChatRedirect />} /> 
          
          <Route path="friend-requests" element={<FriendShipRequestPage />} />
          <Route path="friends" element={<FriendsPage />} />
          <Route path="profile" element={<ProfilePage />} />
          <Route path="message/:fullName" element={<MessagePage />} />
        </Route>

      </Routes>
  );
}

export default App;