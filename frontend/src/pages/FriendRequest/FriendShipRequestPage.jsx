import  { useState } from 'react';

import { useFriendRequests } from '../../hooks/useFriendRequest';
import { useUserSearch } from '../../hooks/useUserSearch';
import PendingRequestItem from '../../components/ui/PendingRequestItem';
import SearchResultItem from '../../components/ui/SearchResultItem';
import { AlertCircle, Bell, CheckCircle, Search, X } from 'lucide-react';

const FriendShipRequestPage = () => {
  const {
    pendingRequests,
    loadingRequests,
    loadingMore,
    isLastPage,
    error: requestError,
    handleAccept,
    handleReject,
    handleLoadMore,
  } = useFriendRequests(10);

  const {
    searchQuery,
    setSearchQuery,
    searchResults,
    setSearchResults, 
    loadingSearch,
    error: searchError,
    handleSearchSubmit,
    handleSendRequest: callApiSendRequest,
  } = useUserSearch();

  const error = requestError || searchError;

  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  const showToast = (message, type = 'success') => {
    setToast({ show: true, message, type });

    setTimeout(() => {
      setToast((prev) => ({ ...prev, show: false }));
    }, 3000);
  };

  const onSendRequestClick = async (userId) => {
    const result = await callApiSendRequest(userId);

    if (result.success) {
      showToast("Gửi lời mời kết bạn thành công!", "success");

    
      setSearchResults((prevResults) =>
        prevResults.map((user) => {
          if (user.userId === userId) {
            return { ...user, status: 'PENDING' };
          }
          return user;
        })
      );
    } else {
      showToast(result.message || "Gửi thất bại", "error");

      if (result.message && (result.message.includes("Đã gửi") || result.message.includes("exist"))) {
         setSearchResults((prevResults) =>
            prevResults.map((user) => 
               user.userId === userId ? { ...user, status: 'PENDING' } : user
            )
         );
      }
    }
  };

  return (
    <div className="w-full h-full p-6 bg-white overflow-y-auto relative"> 
      
      <header className="flex justify-between items-center mb-6">
        <div className="header-title">
          <h2 className="text-2xl font-semibold text-gray-800">Lời mời kết bạn</h2>
          <p className="text-sm text-gray-500">Quản lý các lời mời kết bạn của bạn</p>
        </div>

        <div className="flex items-center gap-4">
          <form onSubmit={handleSearchSubmit} className="relative">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm bạn bằng SĐT..."
              className="border border-gray-300 rounded-full py-2 px-4 pl-10 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
              <Search size={16} />
            </span>
          </form>
          <button className="p-2 rounded-full text-gray-600 hover:bg-gray-100 focus:outline-none">
            <Bell size={20} />
          </button>
        </div>
      </header>

      {error && <div className="text-center text-red-500 mb-4">{error}</div>}

      {loadingSearch && <div className="text-center text-gray-500">Đang tìm...</div>}

      {searchResults.length > 0 && (
        <div className="mb-8">
          <h3 className="text-lg font-semibold text-gray-700 mb-4">Kết quả tìm kiếm</h3>
          <div className="flex flex-col gap-4">
            {searchResults.map((user) => (
              <SearchResultItem
                key={user.userId} 
                user={user}
                onSendRequest={onSendRequestClick} 
              />
            ))}
          </div>
          <hr className="my-6" />
        </div>
      )}

      {loadingRequests && <div className="text-center text-gray-500">Đang tải lời mời...</div>}

      {!loadingRequests && pendingRequests.length === 0 && (
        <div className="text-center text-gray-500">Bạn không có lời mời kết bạn nào.</div>
      )}

      {pendingRequests.length > 0 && (
          <div className="flex flex-col gap-2">
              {pendingRequests.map((request) => (
              <PendingRequestItem
                  key={request.friendshipId} 
                  request={request}
                  onAccept={handleAccept} 
                  onReject={handleReject} 
              />
              ))}
          </div>
      )}

      {/* Nút Xem thêm */}
      {!loadingRequests && !isLastPage && pendingRequests.length > 0 && (
        <div className="text-center mt-6">
          <button
            onClick={handleLoadMore}
            disabled={loadingMore}
            className="px-6 py-2 rounded-md bg-blue-600 text-white font-semibold hover:bg-blue-700 disabled:bg-gray-400"
          >
            {loadingMore ? 'Đang tải...' : 'Xem thêm'}
          </button>
        </div>
      )}

      {/* --- UI THÔNG BÁO (TOAST) --- */}
      {toast.show && (
        <div
          className={`fixed bottom-5 right-5 flex items-center gap-3 px-6 py-3 rounded-lg shadow-lg text-white transition-all duration-500 transform translate-y-0 z-50 
            ${toast.type === 'success' ? 'bg-green-500' : 'bg-red-500'}`}
        >
          {toast.type === 'success' ? <CheckCircle size={24} /> : <AlertCircle size={24} />}
          <span className="font-medium">{toast.message}</span>
          <button onClick={() => setToast({ ...toast, show: false })} className="ml-2 opacity-80 hover:opacity-100">
            <X size={20} />
          </button>
        </div>
      )}
    </div>
  );
}

export default FriendShipRequestPage;