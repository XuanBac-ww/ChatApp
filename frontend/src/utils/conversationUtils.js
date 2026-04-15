import { formatTimeDisplay } from './dateUtils';

export const mapConversationItem = (item, currentUserId) => {
    let otherUserId = item.userId;

    if (item.senderId && item.receiverId) {
        otherUserId = String(item.senderId) === String(currentUserId) ? item.receiverId : item.senderId;
    } else if (item.recipientId) {
        otherUserId = item.recipientId;
    }

    if (item.otherUserId) {
        otherUserId = item.otherUserId;
    }

    return {
        id: item.conversationId,
        otherUserId,
        name: item.name,
        message: item.lastMessage || "Chưa có tin nhắn",
        avatar: item.avatar,
        time: formatTimeDisplay(item.lastMessageTimestamp),
        timestamp: item.lastMessageTimestamp
    };
};

export const mergeRecipientConversation = (conversations, recipientState) => {
    if (!recipientState) {
        return conversations;
    }

    const nextConversations = [...conversations];
    const recipientName = recipientState.fullName;
    const existingIndex = nextConversations.findIndex((conversation) => conversation.name === recipientName);

    if (existingIndex > -1) {
        const [existingItem] = nextConversations.splice(existingIndex, 1);
        nextConversations.unshift(existingItem);
        return nextConversations;
    }

    nextConversations.unshift({
        id: `temp-${Date.now()}`,
        otherUserId: recipientState.userId,
        name: recipientName,
        message: "Bắt đầu cuộc trò chuyện...",
        time: "Vừa xong",
        avatar: recipientState.avatar
    });

    return nextConversations;
};
