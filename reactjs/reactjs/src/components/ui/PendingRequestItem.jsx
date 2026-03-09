
function PendingRequestItem({ request, onAccept, onReject }) {
  
  const { requesterId, requesterUsername, imageUrl } = request;

  const avatar = imageUrl; 
  const name = requesterUsername;

  return (
    <div className="flex items-center p-4 rounded-lg hover:bg-gray-50 border-b border-gray-200 transition-colors">
      <img 
        src={avatar} 
        alt={`Avatar của ${name}`} 
        className="w-12 h-12 rounded-full mr-4 object-cover border border-gray-200" 
      />

      <div className="flex-grow">
        <strong className="text-base font-medium text-gray-800">{name}</strong>
        <span className="block text-sm text-gray-500">Đã gửi lời mời</span> 
      </div>

      <div className="flex gap-2 ml-4">
        <button 
          onClick={() => onAccept(requesterId)}
          className="px-4 py-2 rounded-md bg-blue-600 text-white text-sm font-semibold hover:bg-blue-700 active:bg-blue-800 transition-colors"
        >
          Chấp nhận
        </button>
        <button 
          onClick={() => onReject(requesterId)}
          className="px-4 py-2 rounded-md bg-gray-200 text-gray-800 text-sm font-semibold hover:bg-gray-300 active:bg-gray-400 transition-colors"
        >
          Từ chối
        </button>
      </div>
    </div>
  );
}

export default PendingRequestItem;