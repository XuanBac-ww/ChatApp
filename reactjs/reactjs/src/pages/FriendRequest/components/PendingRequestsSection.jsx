import PendingRequestItem from '../../../components/ui/PendingRequestItem';

const loadMoreButtonClassName = "rounded-md bg-blue-600 px-6 py-2 font-semibold text-white hover:bg-blue-700 disabled:bg-gray-400";

const PendingRequestsSection = ({
    loadingRequests,
    pendingRequests,
    isLastPage,
    loadingMore,
    onAccept,
    onReject,
    onLoadMore
}) => {
    if (loadingRequests) {
        return <div className="text-center text-gray-500">Đang tải lời mời...</div>;
    }

    if (pendingRequests.length === 0) {
        return <div className="text-center text-gray-500">Bạn không có lời mời kết bạn nào.</div>;
    }

    return (
        <>
            <div className="flex flex-col gap-2">
                {pendingRequests.map((request) => (
                    <PendingRequestItem
                        key={request.friendshipId}
                        request={request}
                        onAccept={onAccept}
                        onReject={onReject}
                    />
                ))}
            </div>

            {!isLastPage && (
                <div className="mt-6 text-center">
                    <button
                        onClick={onLoadMore}
                        disabled={loadingMore}
                        className={loadMoreButtonClassName}
                        type="button"
                    >
                        {loadingMore ? 'Đang tải...' : 'Xem thêm'}
                    </button>
                </div>
            )}
        </>
    );
};

export default PendingRequestsSection;
