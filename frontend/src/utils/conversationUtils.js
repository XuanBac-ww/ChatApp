import { formatTimeDisplay } from "./dateUtils";
import { getUserAvatar, getUserDisplayName, getUserId } from "./userUtils";

export const mapConversationItem = (item) => {
    return {
        id: item.conversationId,
        otherUserId: item.userId,
        name: item.name,
        message: item.lastMessage || "Chưa có tin nhắn",
        avatar: item.avatar,
        time: formatTimeDisplay(item.lastMessageTimestamp),
        timestamp: item.lastMessageTimestamp,
    };
};

export const mergeRecipientConversation = (conversations, recipientState) => {
    if (!recipientState) {
        return conversations;
    }

    const nextConversations = [...conversations];
    const recipientName = getUserDisplayName(recipientState);
    const existingIndex = nextConversations.findIndex((conversation) => conversation.name === recipientName);

    if (existingIndex > -1) {
        const [existingItem] = nextConversations.splice(existingIndex, 1);
        nextConversations.unshift(existingItem);
        return nextConversations;
    }

    nextConversations.unshift({
        id: `temp-${Date.now()}`,
        otherUserId: getUserId(recipientState),
        name: recipientName,
        message: "Bắt đầu cuộc trò chuyện...",
        time: "Vừa xong",
        avatar: getUserAvatar(recipientState),
    });

    return nextConversations;
};
