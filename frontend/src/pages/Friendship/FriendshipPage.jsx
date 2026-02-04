import { useEffect, useState } from "react";
import { getAllFriends } from "../../service/friendshipService";
import FriendCard from "../../components/ui/FriendCard";
import { Search } from "lucide-react";

const FriendsPage = () => {
    const [friends, setFriends] = useState([]); 
    const [searchTerm, setSearchTerm] = useState("");
    
    const [currentPage, setCurrentPage] = useState(0); 
    const [hasMore, setHasMore] = useState(true); 
    const [isLoading, setIsLoading] = useState(false); 
    const [error, setError] = useState(null);

    
    const fetchFriends = async (pageToFetch) => {
        setIsLoading(true);
        setError(null);
        
        try {
            const response = await getAllFriends(pageToFetch, 10); 
            
            if (response && response.success && Array.isArray(response.data)) {
                
                setFriends(prevFriends => [...prevFriends, ...response.data]);
                
                setCurrentPage(response.page);
                setHasMore(!response.last);
            } else {
                setHasMore(false); 
                setError(response.message);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchFriends(0);
    }, []); 

    const handleLoadMore = () => {
        if (!isLoading) {
            fetchFriends(currentPage + 1);
        }
    };

    
    const filteredFriends = friends.filter(friend => 
        friend.userName.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="flex-1 bg-gray-100 p-8 overflow-y-auto">
            
            {/* Tiêu đề trang */}
            <div className="mb-6">
                <h1 className="text-3xl font-bold text-gray-900">Bạn bè</h1>
                <p className="text-gray-500 mt-1">Danh sách bạn bè đã kết nối của bạn.</p>
            </div>

            {/* Thanh tìm kiếm */}
            <div className="relative mb-6">
                <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">
                    <Search size={18} />
                </span>
                <input 
                    type="text"
                    placeholder="Tìm kiếm bạn bè..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full pl-12 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            </div>

            {/* Lưới danh sách bạn bè */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {filteredFriends.map((friend) => (
                    <FriendCard key={friend.id} friend={friend} />
                ))}
            </div>

            <div className="text-center mt-8">
                {error && (
                    <p className="text-red-500">Lỗi: {error}</p>
                )}
                
                {!error && hasMore && (
                    <button
                        onClick={handleLoadMore}
                        disabled={isLoading}
                        className="bg-blue-600 text-white font-semibold py-2 px-6 rounded-lg shadow hover:bg-blue-700 transition duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isLoading ? 'Đang tải...' : 'Xem thêm'}
                    </button>
                )}
                
                {!hasMore && friends.length > 0 && (
                    <p className="text-gray-500">Bạn đã xem hết danh sách bạn bè.</p>
                )}

                {!isLoading && friends.length === 0 && !error && (
                    <p className="text-gray-500">Bạn chưa có bạn bè nào.</p>
                )}
            </div>

        </div>
    );
}

export default FriendsPage;