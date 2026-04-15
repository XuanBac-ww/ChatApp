import { getUserAvatar, getUserDisplayName } from "../../utils/userUtils";

const DEFAULT_AVATAR = "https://i.pravatar.cc/150?u=default";

const SearchResultItem = ({ user, onSendRequest }) => {
  const handleSend = () => {
    onSendRequest(user.userId);
  };

  const renderAction = () => {
    switch (user.status) {
      case "FRIEND":
      case "ACCEPTED":
        return (
          <span className="px-4 py-2 bg-blue-50 text-blue-600 rounded-md text-sm font-semibold border border-blue-100">
            Đã là bạn bè
          </span>
        );

      case "PENDING_SENT":
      case "PENDING":
        return (
          <span className="text-orange-500 font-medium text-sm">
            Đang chờ chấp nhận
          </span>
        );

      case "PENDING_RECEIVED":
        return (
          <span className="text-orange-500 font-medium text-sm">
            Đang chờ bạn phản hồi
          </span>
        );

      case "BLOCKED":
        return (
          <span className="text-gray-500 font-medium text-sm">
            Đã chặn
          </span>
        );

      default:
        return (
          <button
            onClick={handleSend}
            className="px-4 py-2 rounded-md bg-green-500 text-white text-sm font-semibold hover:bg-green-600 focus:outline-none transition-colors"
          >
            Gửi kết bạn
          </button>
        );
    }
  };

  const displayName = getUserDisplayName(user);
  const avatar = getUserAvatar(user) || DEFAULT_AVATAR;

  return (
    <div className="flex items-center p-4 rounded-lg bg-gray-50 border border-gray-200 hover:shadow-md transition-shadow duration-200">
      <img
        src={avatar}
        alt={`Avatar ${displayName}`}
        className="w-12 h-12 rounded-full mr-4 object-cover border border-gray-300"
      />

      <div className="flex-grow">
        <strong className="text-base font-medium text-gray-800 block">
          {displayName}
        </strong>
      </div>

      <div className="ml-4">
        {renderAction()}
      </div>
    </div>
  );
};

export default SearchResultItem;
