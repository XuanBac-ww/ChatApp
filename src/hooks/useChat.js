import { useCallback, useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { getMessages, getMyConversations, sendMessage, startDirectConversation } from '../service/conversationService';
import { getUserByFullName } from '../service/userService';


const getToken = () => {
    return localStorage.getItem("access_token") || ""; 
};

// Hook tìm người nhận
export const useRecipientResolver = (fullName, initialRecipient) => {
    const [recipient, setRecipient] = useState(initialRecipient);
    const [isLoading, setIsLoading] = useState(!initialRecipient);

    useEffect(() => {
        if (!recipient && fullName) {
            setIsLoading(true);
            const decodedName = decodeURIComponent(fullName);
            getUserByFullName(decodedName)
                .then(res => setRecipient(res.data)) 
                .catch(err => console.error("User fetch error:", err))
                .finally(() => setIsLoading(false));
        } else {
            setIsLoading(false);
        }
    }, [fullName, recipient]);

    return { recipient, isLoading };
};

// Hook Chat Real-time
export const useChatSession = (recipient, initialConversationId = null) => {
    const [conversation, setConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    
    const stompClientRef = useRef(null);

    useEffect(() => {
        if (!recipient && !initialConversationId) return;

        const initSession = async () => {
            setIsLoading(true);
            try {
                // If we already have a concrete conversationId from navigation, load it directly
                if (initialConversationId) {
                    const convId = initialConversationId;
                    setConversation({ id: convId, conversationId: convId });
                    const msgRes = await getMessages(convId);
                    setMessages(msgRes.data);
                    setIsLoading(false);
                    return;
                }

                const recipientId = recipient.id || recipient.userId;

                const convRes = await startDirectConversation(recipientId);
                console.log("DEBUG: startDirectConversation response:", convRes);
                let convData = convRes.data;

                // If the server returned a conversation that doesn't match the target recipient,
                // try to find a correct conversation among the user's conversations as a fallback.
                if (convData && convData.otherUserId && String(convData.otherUserId) !== String(recipientId)) {
                    console.warn("Conversation's otherUserId doesn't match recipientId. Trying fallback search...");
                    try {
                        const all = await getMyConversations();
                        console.log("DEBUG: getMyConversations for fallback:", all);
                        const list = (all && all.data) || all || [];
                        const found = list.find(c => (
                            String(c.userId) === String(recipientId) ||
                            String(c.recipientId) === String(recipientId) ||
                            String(c.otherUserId) === String(recipientId) ||
                            String(c.senderId) === String(recipientId) ||
                            String(c.receiverId) === String(recipientId)
                        ));

                        if (found) {
                            
                            convData = {
                                id: found.conversationId,
                                ...found
                            };
                        }
                    } catch (e) {
                        console.error("Fallback search failed:", e);
                    }
                }

                setConversation(convData);

                const convId = convData?.conversationId;
                if (convId) {
                    const msgRes = await getMessages(convId);
                    console.log("DEBUG: messages loaded:", msgRes);
                    setMessages(msgRes.data);
                }
            } catch (error) {
                console.error("Init chat error:", error);
            } finally {
                setIsLoading(false);
            }
        };

        initSession();
    }, [recipient, initialConversationId]);

    useEffect(() => {
        const token = getToken();

        
        if (conversation?.id && token) {
            
            const socket = new SockJS('http://localhost:8080/ws'); 
            const stompClient = Stomp.over(socket);
           
            stompClient.debug = null; 

            const headers = {
                'Authorization': `Bearer ${token}`
            };

            stompClient.connect(headers, () => {

                stompClient.subscribe(`/topic/conversations/${conversation.id}`, (payload) => {
                    const newMessage = JSON.parse(payload.body);
                   
                    setMessages((prev) => {
                        if (prev.some(m => m.id === newMessage.id)) return prev;
                        return [...prev, newMessage];
                    });
                });

            }, (error) => {
                console.error(" WebSocket Error:", error);
               
            });

            stompClientRef.current = stompClient;
        }

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.disconnect();
            }
        };
    }, [conversation?.id]);


    const onSendMessage = useCallback(async (content) => {
        if (!conversation?.id || !content.trim()) return;

        try {
            await sendMessage(conversation.id, content);
            return true;
        } catch (error) {
            console.error("Send message error:", error);
            return false;
        }
    }, [conversation]);

    return { conversation, messages, isLoading, onSendMessage };
};