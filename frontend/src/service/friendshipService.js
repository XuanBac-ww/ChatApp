import { fetchClient } from "../libs/fetchClient";

export const getAllFriends = (page = 0, size = 10) => {
    return fetchClient({
        baseUrl: "/friend-ship/all",
        method: "GET",
        query: { page, size },
        isAuth: true,
    });
};

export const sendFriendRequest = (userId) => {
    return fetchClient({
        baseUrl: "/friend-ship/send-request",
        method: "POST",
        body: { userId },
        isAuth: true,
    });
};

export const acceptFriendRequest = (userId) => {
    return fetchClient({
        baseUrl: "/friend-ship/accept-request",
        method: "POST",
        body: { userId },
        isAuth: true,
    });
};

export const rejectFriendRequest = (userId) => {
    return fetchClient({
        baseUrl: "/friend-ship/reject-request",
        method: "POST",
        body: { userId },
        isAuth: true,
    });
};

export const getPendingFriendRequests = (page = 0, size = 10) => {
    return fetchClient({
        baseUrl: "/friend-ship/pending-request",
        method: "GET",
        query: { page, size },
        isAuth: true,
    });
};
