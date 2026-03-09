import { useFriendRequests } from '../../hooks/useFriendRequest';
import { useUserSearch } from '../../hooks/useUserSearch';
import { useToast } from '../../hooks/useToast';
import FriendRequestHeader from './components/FriendRequestHeader';
import SearchResultsSection from './components/SearchResultsSection';
import PendingRequestsSection from './components/PendingRequestsSection';
import RequestToast from './components/RequestToast';

const FriendShipRequestPage = () => {
  const {
    pendingRequests,
    loadingRequests,
    loadingMore,
    isLastPage,
    error: requestError,
    actionError,
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

  const error = requestError || searchError || actionError;

  const { toast, showToast, hideToast } = useToast();

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
      <FriendRequestHeader
        searchQuery={searchQuery}
        onSearchQueryChange={setSearchQuery}
        onSearchSubmit={handleSearchSubmit}
      />

      {error && <div className="text-center text-red-500 mb-4">{error}</div>}

      {loadingSearch && <div className="text-center text-gray-500">Đang tìm...</div>}

      <SearchResultsSection searchResults={searchResults} onSendRequest={onSendRequestClick} />

      <PendingRequestsSection
        loadingRequests={loadingRequests}
        pendingRequests={pendingRequests}
        isLastPage={isLastPage}
        loadingMore={loadingMore}
        onAccept={handleAccept}
        onReject={handleReject}
        onLoadMore={handleLoadMore}
      />

      <RequestToast toast={toast} onClose={hideToast} />
    </div>
  );
}

export default FriendShipRequestPage;