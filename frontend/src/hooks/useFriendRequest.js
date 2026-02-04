import { useState, useEffect, useCallback } from 'react';
import { getPendingFriendRequests, acceptFriendRequest, rejectFriendRequest } from '../service/friendshipService';

export const useFriendRequests = (pageSize = 10) => {
  const [pendingRequests, setPendingRequests] = useState([]);
  const [loadingRequests, setLoadingRequests] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [currentPage, setCurrentPage] = useState(0);
  const [isLastPage, setIsLastPage] = useState(false);
  const [error, setError] = useState(null);

  const fetchPendingRequests = useCallback(async (pageToFetch) => {
    if (pageToFetch === 0) {
      setLoadingRequests(true);
    } else {
      setLoadingMore(true);
    }
    setError(null);

    try {
      const response = await getPendingFriendRequests(pageToFetch, pageSize);
      
      if (response.success) {
        const incomingList = response.data || []; 
        
        setPendingRequests(prev =>
          pageToFetch === 0
            ? incomingList
            : [...prev, ...incomingList]
        );

        setCurrentPage(response.page); 
        setIsLastPage(response.last);
        
      } else {
        throw new Error(response.message);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      if (pageToFetch === 0) {
        setLoadingRequests(false);
      } else {
        setLoadingMore(false);
      }
    }
  }, [pageSize]); 

  useEffect(() => {
    fetchPendingRequests(0);
  }, [fetchPendingRequests]); 

  const handleLoadMore = () => {
    if (!isLastPage && !loadingMore) {
      fetchPendingRequests(currentPage + 1);
    }
  };

  const handleAccept = async (requesterId) => {
    try {
      const response = await acceptFriendRequest(requesterId);
      
      if (response && response.success) {
        setPendingRequests(prev => 
          prev.filter(req => req.requesterId !== requesterId)
        );
      } else {
        alert('Lỗi: ' + (response.message || 'Không thể chấp nhận lời mời.'));
      }
    } catch (err) {
      console.error(err);
      alert('Đã xảy ra lỗi hệ thống: ' + err.message);
    }
  };

  const handleReject = async (requesterId) => {
    try {
      const response = await rejectFriendRequest(requesterId);
      
      if (response && response.success) {
        setPendingRequests(prev => 
          prev.filter(req => req.requesterId !== requesterId)
        );
      } else {
        alert('Lỗi: ' + (response.message || 'Không thể từ chối lời mời.'));
      }
    } catch (err) {
      console.error(err);
      alert('Đã xảy ra lỗi hệ thống: ' + err.message);
    }
  };

  return {
    pendingRequests,
    loadingRequests,
    loadingMore,
    isLastPage,
    error,
    handleAccept,
    handleReject,
    handleLoadMore,
  };
};