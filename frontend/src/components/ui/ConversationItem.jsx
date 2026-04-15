import  { memo } from 'react';

const ConversationItem = ({ conv, isSelected, onClick }) => {
    return (
        <div
            onClick={() => onClick(conv)}
            className={`group flex items-center p-3 rounded-2xl cursor-pointer transition-all duration-200 relative ${
                isSelected ? 'bg-blue-50/80' : 'hover:bg-gray-50'
            }`}
        >
            {/* Avatar Area */}
            <div className="relative shrink-0">
                <img 
                    src={conv.avatar} 
                    alt={conv.name} 
                    className="w-11 h-11 rounded-full object-cover border border-gray-100 shadow-sm group-hover:scale-105 transition-transform"
                    loading="lazy"
                />
            </div>

            {/* Content Area */}
            <div className="ml-3 flex-1 min-w-0">
                <div className="flex justify-between items-center mb-0.5">
                    <h4 className={`text-[15px] font-semibold truncate ${
                        isSelected ? 'text-slate-900' : 'text-slate-700'
                    }`}>
                        {conv.name}
                    </h4>
                    <span className={`text-[11px] font-medium ${
                        isSelected ? 'text-blue-500' : 'text-gray-400'
                    }`}>
                        {conv.time}
                    </span>
                </div>
                <p className={`text-[13px] truncate leading-relaxed ${
                    isSelected ? 'text-blue-600 font-medium' : 'text-gray-500 group-hover:text-gray-600'
                }`}>
                    {isSelected && conv.message === "Bắt đầu cuộc trò chuyện..." 
                        ? "Đang soạn tin..." 
                        : conv.message}
                </p>
            </div>
            
            {/* Active Indicator  */}
            {isSelected && (
                <div className="absolute left-0 top-1/2 -translate-y-1/2 h-8 w-1 bg-blue-600 rounded-r-full"></div>
            )}
        </div>
    );
};

export default memo(ConversationItem);