import SearchResultItem from '../../../components/ui/SearchResultItem';

const SearchResultsSection = ({ searchResults, onSendRequest }) => {
    if (searchResults.length === 0) {
        return null;
    }

    return (
        <div className="mb-8">
            <h3 className="mb-4 text-lg font-semibold text-gray-700">Kết quả tìm kiếm</h3>
            <div className="flex flex-col gap-4">
                {searchResults.map((user) => (
                    <SearchResultItem
                        key={user.userId}
                        user={user}
                        onSendRequest={onSendRequest}
                    />
                ))}
            </div>
            <hr className="my-6" />
        </div>
    );
};

export default SearchResultsSection;
