import { createContext } from "react";

export const ConversationContext = createContext({
    conversations: [],
    isLoading: false,
    error: null,
});
