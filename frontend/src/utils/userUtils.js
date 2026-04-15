export const getUserId = (user) => {
    if (!user) return null;
    return user.id ?? user.userId ?? user.user_id ?? user.accountId ?? null;
};

export const getUserDisplayName = (user) => {
    if (!user) return "";
    return user.fullName ?? user.userName ?? user.name ?? "";
};

export const getUserAvatar = (user) => {
    if (!user) return "";
    return user.profileImage ?? user.avatar ?? user.imageUrl ?? "";
};

export const getConversationId = (conversation) => {
    if (!conversation) return null;
    return conversation.id ?? conversation.conversationId ?? null;
};

export const isTemporaryConversationId = (conversationId) => {
    return Boolean(conversationId && String(conversationId).startsWith("temp-"));
};
