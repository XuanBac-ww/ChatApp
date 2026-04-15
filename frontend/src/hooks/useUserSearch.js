import { useState } from 'react';
import { searchUsers } from '../service/userService';
import { sendFriendRequest } from '../service/friendshipService';

export const useUserSearch = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [loadingSearch, setLoadingSearch] = useState(false);
  const [error, setError] = useState(null);

  const handleSearchSubmit = async (e) => {
    e.preventDefault();
    if (searchQuery.trim() === '') {
      setSearchResults([]);
      return;
    }
    setLoadingSearch(true);
    setError(null);
    try {
      const payload = { numberPhone: searchQuery }; 
      const response = await searchUsers(payload);
      if (response.success) {
        if (response.data) {
            setSearchResults([response.data]); 
        } else {
            setSearchResults([]); 
        }
      } else {
        setSearchResults([]);
        throw new Error(response.message);
      }
    } catch (err) {
      setSearchResults([]);
      setError(err.message);
    } finally {
      setLoadingSearch(false);
    }
  };

  const handleSendRequest = async (identifier) => {
    if (!identifier) return { success: false, message: "Không tìm thấy người dùng" };

    try {
      const payload = { userId: identifier }; 
      const response = await sendFriendRequest(payload);

      return { success: response.success, message: response.message };
    } catch (err) {
      return { success: false, message: err.message };
    }
};
  
  return {
    searchQuery,
    setSearchQuery,
    setSearchResults,
    searchResults,
    loadingSearch,
    error,
    handleSearchSubmit,
    handleSendRequest,
  };
};