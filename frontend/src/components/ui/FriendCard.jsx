import { MessageSquareMore } from "lucide-react";
import { useNavigate } from "react-router";


const DEFAULT_AVATAR = "https://i.pravatar.cc/150?u=default"; 

const FriendCard = ({ friend }) => {

    const navigate = useNavigate();

    const avatarUrl = friend.profileImage || DEFAULT_AVATAR;

    const handleChatClick = () => {
        const safeNameUrl = encodeURIComponent(friend.fullName); // ma hoa ten

        navigate(`/home/message/${safeNameUrl}`, {
            state: { recipientUser: friend }
        });
    };

    return (
        <div className="bg-white rounded-lg shadow-md p-6 flex flex-col items-center text-center transition-transform transform hover:-translate-y-1">
            <img 
                src={avatarUrl} 
                alt={friend.userName}
                className="w-20 h-20 rounded-full object-cover border-2 border-gray-200"
            />
            
            <h3 className="mt-4 text-lg font-semibold text-gray-900">
                {friend.userName}
            </h3>
     
            <button
                onClick={handleChatClick}
                className="mt-5 w-full bg-blue-600 text-white font-medium py-2 px-4 rounded-lg flex items-center justify-center space-x-2 hover:bg-blue-700 transition-colors duration-200"
            >
                <MessageSquareMore size={18} />
                <span>Nhắn tin</span>
            </button>
        </div>
    );
};

export default FriendCard;