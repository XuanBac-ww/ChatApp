import { useEffect, useRef } from 'react';
import { useParams, useLocation, Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useChatSession, useRecipientResolver } from '../../hooks/useChat';
import ChatInput from '../../components/common/ChatInput';
import MessageBubble from '../../components/ui/MessageBubble';
import { sendMessage, startDirectConversation } from '../../service/conversationService';

const DEFAULT_AVATAR = "https://via.placeholder.com/150";

const MessagePage = () => {
    const { fullName } = useParams();
    const location = useLocation();
    const { user: currentUser } = useAuth();
    const messagesEndRef = useRef(null);

    const { recipient, isLoading: loadingUser } = useRecipientResolver(fullName, location.state?.recipientUser);
    
    const { conversation, setConversation, messages, setMessages, isLoading: loadingChat } = useChatSession(recipient);

    useEffect(() => {
        if (recipient) {
            console.log(">>> [DEBUG] Recipient Object Loaded:", recipient);
            console.log(">>> [DEBUG] Các trường ID có thể có:", {
                id: recipient.id,
                userId: recipient.userId,
                user_id: recipient.user_id,
                accountId: recipient.accountId
            });
        }
    }, [recipient]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const handleSendMessage = async (text) => {
        console.group("--- BẮT ĐẦU GỬI TIN NHẮN ---");
        
        if (!currentUser) {
            console.groupEnd();
            return false;
        }

        if (!recipient) {
            console.groupEnd();
            return false;
        }

        try {
            let currentConversationId = conversation?.id || conversation?.conversationId;

            if (!currentConversationId) {
              
                const recipientId = recipient.id || recipient.userId || recipient.user_id;
                

                if (!recipientId) {
                    console.groupEnd();
                    return false;
                }

                const createRes = await startDirectConversation(recipientId);
                
                if (createRes && (createRes.data || createRes.id)) {
                    const newConvo = createRes.data || createRes;
                    currentConversationId = newConvo.id || newConvo.conversationId;
                    
                    // Cập nhật state hội thoại ngay lập tức
                    if (setConversation) setConversation(newConvo); 
                } else {
                    console.error("Lỗi: API tạo hội thoại trả về dữ liệu không hợp lệ", createRes);
                }
            }

            if (currentConversationId) {
                console.log(`6. Đang gửi tin nhắn vào hội thoại ${currentConversationId}...`);
                
                const sendRes = await sendMessage(currentConversationId, text);
                console.log("7. Response gửi tin nhắn:", sendRes);
                
                if (sendRes && (sendRes.data || sendRes.id)) {
                    const newMsg = sendRes.data || sendRes;
                    if (setMessages) {
                        setMessages(prev => [...prev, newMsg]);
                    }
                    console.log(">>> GỬI THÀNH CÔNG!");
                    console.groupEnd();
                    return true;
                }
            } else {
                console.error("Lỗi: Vẫn không có Conversation ID sau khi thử tạo.");
            }
        } catch (error) {
            console.error(">>> EXCEPTION KHI GỬI TIN:", error);
            console.groupEnd();
            return false;
        }
        console.groupEnd();
        return false;
    };

    if (loadingUser) return <div className="h-full flex items-center justify-center text-gray-500">Đang tìm người dùng...</div>;
    if (!recipient) return <Navigate to="/home" />;
    if (!currentUser) return <div className="h-full flex items-center justify-center text-gray-500">Đang xác thực...</div>;

    const recipientAvatar = recipient.avatar || recipient.profileImage || DEFAULT_AVATAR;

    return (
        <div className="flex flex-col h-full bg-white relative">
            
            {/* Header */}
            <div className="p-4 border-b border-gray-200 flex items-center bg-white shadow-sm shrink-0 z-10">
                <div className="flex items-center space-x-3">
                    <img src={recipientAvatar} alt="Avatar" className="w-10 h-10 rounded-full object-cover border border-gray-200" />
                    <div>
                        <h3 className="font-bold text-gray-800 text-lg">{recipient.fullName || recipient.name}</h3>
                        <div className="flex items-center text-xs text-green-600 font-medium">
                            <span className="w-2 h-2 bg-green-500 rounded-full mr-1 animate-pulse"></span>
                            Đang hoạt động
                        </div>
                    </div>
                </div>
            </div>

            {/* Message List */}
            <div className="flex-grow bg-gray-50 p-4 overflow-y-auto">
                {loadingChat && conversation ? (
                    <div className="flex justify-center mt-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                    </div>
                ) : (!messages || messages.length === 0) ? (
                    <div className="text-center text-gray-400 mt-10">
                        Hãy bắt đầu cuộc trò chuyện với {recipient.fullName || recipient.name}!
                    </div>
                ) : (
                    <div className="space-y-1">
                        {messages.map((msg, index) => {
                            const myId = currentUser.id || currentUser.userId;
                            const senderId = msg.senderId || msg.sender?.id || msg.sender?.userId;
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