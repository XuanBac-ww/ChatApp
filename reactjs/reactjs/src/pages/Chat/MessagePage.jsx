import { useEffect, useRef } from 'react';
import { Navigate, useLocation, useParams } from 'react-router-dom';
import ChatInput from '../../components/common/ChatInput';
import MessageBubble from '../../components/ui/MessageBubble';
import { useAuth } from '../../hooks/useAuth';
import { useChatSession, useRecipientResolver } from '../../hooks/useChat';
import { getUserAvatar, getUserDisplayName, getUserId, isTemporaryConversationId } from '../../utils/userUtils';


const MessagePage = () => {
    const { fullName } = useParams();
    const location = useLocation();
    const { user: currentUser } = useAuth();
    const messagesEndRef = useRef(null);

    const {
        recipient,
        isLoading: loadingUser,
        error: recipientError
    } = useRecipientResolver(fullName, location.state?.recipientUser);

    const rawConvId = location.state?.conversationId;
    const conversationIdFromState = isTemporaryConversationId(rawConvId) ? null : rawConvId;

    const {
        conversation,
        messages,
        isLoading: loadingChat,
        error: chatError,
        onSendMessage
    } = useChatSession(recipient, conversationIdFromState);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const handleSendMessage = async (text) => onSendMessage(text);

    if (loadingUser) return <div className="h-full flex items-center justify-center text-gray-500">Đang tìm người dùng...</div>;
    if (!recipient) return <Navigate to="/home" />;
    if (!currentUser) return <div className="h-full flex items-center justify-center text-gray-500">Đang xác thực...</div>;

    const recipientAvatar = getUserAvatar(recipient);
    const recipientName = getUserDisplayName(recipient);

    return (
        <div className="flex flex-col h-full bg-white relative">
            
            {/* Header */}
            <div className="p-4 border-b border-gray-200 flex items-center bg-white shadow-sm shrink-0 z-10">
                <div className="flex items-center space-x-3">
                    <img src={recipientAvatar} alt="Avatar" className="w-10 h-10 rounded-full object-cover border border-gray-200" />
                    <div>
                        <h3 className="font-bold text-gray-800 text-lg">{recipientName}</h3>
                        <div className="flex items-center text-xs text-green-600 font-medium">
                            <span className="w-2 h-2 bg-green-500 rounded-full mr-1 animate-pulse"></span>
                            Đang hoạt động
                        </div>
                    </div>
                </div>
            </div>

            {/* Message List */}
            <div className="flex-grow bg-gray-50 p-4 overflow-y-auto">
                {(recipientError || chatError) && (
                    <div className="mb-3 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
                        {recipientError || chatError}
                    </div>
                )}
                {loadingChat && conversation ? (
                    <div className="flex justify-center mt-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    </div>
                ) : (!messages || messages.length === 0) ? (
                    <div className="text-center text-gray-400 mt-10">
                        Hãy bắt đầu cuộc trò chuyện với {recipientName}!
                    </div>
                ) : (
                    <div className="space-y-1">
                        {messages.map((msg, index) => {
                            const myId = getUserId(currentUser);
                            const senderId = msg.senderId || getUserId(msg.sender);
                            const isMe = String(senderId) === String(myId);

                            return (
                                <MessageBubble 
                                    key={msg.id || index} 
                                    msg={msg} 
                                    isMe={isMe} 
                                    recipientAvatar={recipientAvatar} 
                                />
                            );
                        })}
                        <div ref={messagesEndRef} />
                    </div>
                )}
            </div>

            {/* Input */}
            <div className="p-4 bg-white border-t shrink-0">
                <ChatInput 
                    onSend={handleSendMessage} 
                    disabled={loadingUser} 
                />
            </div>
        </div>
    );
};

export default MessagePage;