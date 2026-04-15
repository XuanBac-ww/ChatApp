import { fetchClient } from "../libs/fetchClient";

export const getMyConversations = () => {
    return fetchClient({
        baseUrl: "/conversations",
        method: "GET",
        isAuth: true,
    });
};

export const getMessages = (conversationId) => {
    return fetchClient({
        baseUrl: `/conversations/${conversationId}/messages`,
        method: "GET",
        isAuth: true,
    });
};

export const sendMessage = (conversationId, content) => {
    return fetchClient({
        baseUrl: `/conversations/${conversationId}/messages`,
        method: "POST",
        body: { content },
        isAuth: true,
    });
};

export const startDirectConversation = (recipientId) => {
    return fetchClient({
        baseUrl: "/conversations/direct",
        method: "POST",
        body: { recipientId },
        isAuth: true,
    });
};
