import { Bell, Search } from 'lucide-react';

const searchInputClassName = "rounded-full border border-gray-300 py-2 pl-10 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500";

const FriendRequestHeader = ({ searchQuery, onSearchQueryChange, onSearchSubmit }) => {
    return (
        <header className="mb-6 flex items-center justify-between">
            <div>
                <h2 className="text-2xl font-semibold text-gray-800">Lời mời kết bạn</h2>
                <p className="text-sm text-gray-500">Quản lý các lời mời kết bạn của bạn</p>
            </div>

            <div className="flex items-center gap-4">
                <form onSubmit={onSearchSubmit} className="relative">
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(event) => onSearchQueryChange(event.target.value)}
                        placeholder="Tìm bạn bằng SĐT..."
                        className={searchInputClassName}
                    />
                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                        <Search size={16} />
                    </span>
                </form>

                <button className="rounded-full p-2 text-gray-600 hover:bg-gray-100 focus:outline-none" type="button">
                    <Bell size={20} />
                </button>
            </div>
        </header>
    );
};

export default FriendRequestHeader;
