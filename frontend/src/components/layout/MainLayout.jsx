
import { Outlet, useLocation } from 'react-router-dom';
import ConversationProvider from '../../context/ConversationProvider';
import { useAuth } from '../../hooks/useAuth';
import { useConversations } from '../../hooks/useConversations';
import SideNav from './SideNav';

const MainLayout = () => {
    const location = useLocation();
    const { user } = useAuth();
    const recipientState = location.state?.recipientUser;
    const conversationsState = useConversations(user, recipientState);

    return (
        <ConversationProvider value={conversationsState}>
            <div className="flex h-screen bg-gray-100">

                {/*  Thanh dieu huong ben trai */}
                <SideNav />

                <main className="flex-1 overflow-y-auto">

                    {/* Render cac trang con o day */}
                    <Outlet />
                </main>
            </div>
        </ConversationProvider>
    );
};

export default MainLayout;
