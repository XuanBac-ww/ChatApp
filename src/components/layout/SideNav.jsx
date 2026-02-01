import { MessageSquare, User, UserPlus, Users } from 'lucide-react';
import { useCallback } from 'react';
import { NavLink, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useConversations } from '../../hooks/useConversations';
import AccountPreview from '../ui/AccountPreview';
import ConversationItem from '../ui/ConversationItem';
import SkeletonConversation from './SkeletonConversation';

const NAV_ITEMS = [
    { text: 'Tin nhắn', icon: MessageSquare, path: '/home' },
    { text: 'Tìm bạn bè', icon: Users, path: 'friends' },
    { text: 'Lời mời kết bạn', icon: UserPlus, path: 'friend-requests' },
    { text: 'Hồ sơ', icon: User, path: 'profile' },
];

const SideNav = () => {
    const { user } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();
    const recipientState = location.state?.recipientUser;

    const { conversations, isLoading } = useConversations(user, recipientState);

    const handleChatClick = useCallback((conv) => {
        const safeNameUrl = encodeURIComponent(conv.name);
        console.log("DEBUG CLICK CONV:", conv);
        navigate(`/home/message/${safeNameUrl}`, {
            state: { 
                recipientUser: { 
                    id: conv.otherUserId, 
                    fullName: conv.name,    
                    profileImage: conv.avatar 
                },
                // pass conversationId when available to avoid relying on name-to-id resolution
                conversationId: conv.id
            }
        });
    }, [navigate]);

    return (
        <div className="flex flex-col h-screen w-80 bg-white border-r border-gray-100 font-sans shadow-[4px_0_24px_rgba(0,0,0,0.02)]">
            <div className="px-6 py-6 pb-2">
                <h1 className="text-2xl font-bold text-slate-800 tracking-tight">ChatApp</h1>
            </div>

            <nav className="px-3 mt-4 space-y-1">
                {NAV_ITEMS.map((item) => {
                    const Icon = item.icon;
                    return (
                        <NavLink
                            key={item.text}
                            to={item.path}
                          
                            className={({ isActive }) => {
                            
                                let isItemActive = isActive;
                
                                if (item.path === '/home') {
                                    // Nó sẽ Active nếu đường dẫn là /home HOẶC chứa /home/message
                                    const isMessageRoute = location.pathname.includes('/home/message');
                                    const isHomeExact = location.pathname === '/home';
                                    // Chỉ active khi ở Home hoặc đang chat, không active khi ở /home/friends
                                    isItemActive = isHomeExact || isMessageRoute;
                                }

                                return `flex items-center space-x-3 px-4 py-3 rounded-xl transition-all duration-200 font-medium ${
                                    isItemActive
                                        ? 'bg-blue-50 text-blue-600 shadow-sm'
                                        : 'text-gray-500 hover:bg-gray-50 hover:text-gray-900'
                                }`;
                            }}
                        >
                            <Icon size={20} strokeWidth={2.5} className="shrink-0" />
                            <span className="text-[15px]">{item.text}</span>
                        </NavLink>
                    );
                })}
            </nav>

            {/* Phần dưới giữ nguyên */}
            <div className="mt-6 flex-1 flex flex-col min-h-0">
                <div className="px-6 mb-2 flex items-center justify-between">
                    <h3 className="text-xs font-bold text-gray-400 uppercase tracking-wider">
                        Cuộc trò chuyện
                    </h3>
                </div>

                <div className="flex-1 overflow-y-auto px-3 pb-2 space-y-1 [&::-webkit-scrollbar]:w-1.5 [&::-webkit-scrollbar-track]:bg-transparent [&::-webkit-scrollbar-thumb]:bg-gray-200 [&::-webkit-scrollbar-thumb]:rounded-full hover:[&::-webkit-scrollbar-thumb]:bg-gray-300">
                    {isLoading ? (
                        <>
                            <SkeletonConversation />
                            <SkeletonConversation />
                            <SkeletonConversation />
                        </>
                    ) : conversations.length === 0 ? (
                        <div className="text-center py-8 text-gray-400 text-sm">
                            Chưa có tin nhắn nào
                        </div>
                    ) : (
                        conversations.map((conv) => {
                            const isSelected = 
                                location.pathname.includes(encodeURIComponent(conv.name)) || 
                                (recipientState && (recipientState.userName === conv.name || recipientState.fullName === conv.name));
                            
                            return (
                                <ConversationItem 
                                    key={conv.id} 
                                    conv={conv} 
                                    isSelected={isSelected} 
                                    onClick={handleChatClick} 
                                />
                            );
                        })
                    )}
                </div>
            </div>

            {user && (
                <div className="p-4 border-t border-gray-100 bg-gray-50/50">
                    <AccountPreview 
                        name={user.fullName || user.name} 
                        avatar={user.avatar} 
                        isActive={true} 
                    />
                </div>
            )}
        </div>
    );
};

export default SideNav;