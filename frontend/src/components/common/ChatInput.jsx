import { Send } from "lucide-react";
import { useState } from "react";


const ChatInput = ({ onSend, disabled }) => {
    const [text, setText] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!text.trim()) return;
        
        const success = await onSend(text);
        if (success) setText(""); // Chỉ xóa input nếu gửi thành công
    };

    return (
        <form onSubmit={handleSubmit} className="flex items-center bg-gray-100 rounded-full px-4 py-2">
            <input 
                type="text" 
                placeholder={disabled ? "Đang kết nối..." : "Nhập tin nhắn..."}
                className="flex-grow bg-transparent outline-none text-gray-700 px-2"
                value={text}
                onChange={(e) => setText(e.target.value)}
                disabled={disabled} 
            />
            <button 
                type="submit" 
                className={`ml-2 p-2 rounded-full transition-colors ${!text.trim() ? 'text-gray-400' : 'text-blue-600 hover:bg-blue-50'}`}
                disabled={disabled || !text.trim()}
            >
                <Send size={20} />
            </button>
        </form>
    );
};

export default ChatInput;