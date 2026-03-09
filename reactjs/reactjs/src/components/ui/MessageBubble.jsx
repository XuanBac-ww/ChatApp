
const MessageBubble = ({ msg, isMe, recipientAvatar }) => {
    return (
        <div className={`flex w-full mb-4 ${isMe ? 'justify-end' : 'justify-start'}`}>
            
            {/* Avatar (Chỉ hiện cho người khác - bên trái) */}
            {!isMe && (
                <img 
                    src={recipientAvatar} 
                    alt="avatar" 
                    className="w-8 h-8 rounded-full mr-2 self-end mb-1 object-cover shadow-sm" 
                />
            )}

            {/* Bong bóng chat */}
            <div 
                className={`max-w-[70%] px-4 py-2 shadow-sm text-sm break-words relative ${
                    isMe 
                        ? 'bg-blue-600 text-white rounded-2xl rounded-br-sm' // Style của Mình
                        : 'bg-white text-gray-800 border border-gray-200 rounded-2xl rounded-bl-sm' // Style người khác
                }`}
            >
                <p>{msg.content}</p>
                <span className={`text-[10px] block mt-1 text-right font-medium opacity-80 ${
                    isMe ? 'text-blue-100' : 'text-gray-400'
                }`}>
                    {msg.createdAt 
                        ? new Date(msg.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) 
                        : ''}
                </span>
            </div>
        </div>
    );
};

export default MessageBubble;