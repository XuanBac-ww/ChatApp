import { fetchClient } from "../libs/fetchClient";

export const getAllFriends = (page = 0, size = 10) => {
    return fetchClient({
        baseUrl: "/friend-ship/all", 
        method: "GET",
        params: {
            page,
            size
        },
        isAuth: true 
    });
};


export const sendFriendRequest = (payload) => {
    return fetchClient({
        baseUrl: "/friend-ship/send-request", 
        method: "POST",
        params: payload,
        isAuth: true
    })
}

export const acceptFriendRequest = async (requesterId) => {
  return await fetchClient({
    baseUrl: `/friend-ship/accept-request`, 
    method: 'POST', 
    isAuth: true,
    params: { 
        userId: requesterId 
    }
  });
};

export const rejectFriendRequest = async (requesterId) => {
  return await fetchClient({
    baseUrl: `/friend-ship/reject-request`, 
    method: 'POST', 
    isAuth: true,
    params: { 
        userId: requesterId 
    }
  });
};


export const getPendingFriendRequests = (page = 0, size = 10) => {
  return fetchClient({
    baseUrl: "/friend-ship/pending-request",
    method: "GET",
    params: {
      page,
      size,
    },
    isAuth: true,
  });
};