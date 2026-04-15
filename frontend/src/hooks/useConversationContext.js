import { useContext } from "react";
import { ConversationContext } from "../context/ConversationContext";

export const useConversationContext = () => useContext(ConversationContext);
