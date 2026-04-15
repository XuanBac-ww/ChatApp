import { Navigate } from 'react-router-dom';
import { useConversationContext } from '../../hooks/useConversationContext';

const ChatRedirect = () => {
    const { conversations, isLoading } = useConversationContext();

    if (isLoading) {
        return <div className="h-full flex items-center justify-center text-gray-500">Đang tải...</div>;
    }

    if (conversations && conversations.length > 0) {
        const firstConv = conversations[0];
        const safeName = encodeURIComponent(firstConv.name);

        return <Navigate 
            to={`message/${safeName}`} 
            state={{ 
                conversationId: firstConv.id,
                recipientUser: { 
                    id: firstConv.otherUserId, 
                    fullName: firstConv.name, 
                    profileImage: firstConv.avatar 
                } 
            }} 
            replace 
        />;
    }

    return <Navigate to="friends" replace />;
};

export default ChatRedirect;
