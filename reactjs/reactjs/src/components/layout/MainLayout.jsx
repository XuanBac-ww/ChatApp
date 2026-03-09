
import { Outlet } from 'react-router-dom';
import SideNav from './SideNav';

const MainLayout = () => {
    return (
        <div className="flex h-screen bg-gray-100">

            {/*  Thanh dieu huong ben trai */}
            <SideNav />

            <main className="flex-1 overflow-y-auto">

                {/* Render cac trang con o day */}
                <Outlet /> 
            </main>
        </div>
    );
};

export default MainLayout;