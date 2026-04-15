import { ConversationContext } from "./ConversationContext";

const ConversationProvider = ({ children, value }) => (
    <ConversationContext.Provider value={value}>
        {children}
    </ConversationContext.Provider>
);

export default ConversationProvider;
