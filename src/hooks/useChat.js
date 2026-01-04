import { useState, useEffect, useCallback, useRef } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { getUserByFullName } from '../service/userService';
import { getMessages, sendMessage, startDirectConversation } from '../service/conversationService';


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
                .then(res => setRecipient(res.data || res)) 
                .catch(err => console.error("User fetch error:", err))
                .finally(() => setIsLoading(false));
        } else {
            setIsLoading(false);
        }
    }, [fullName, recipient]);

    return { recipient, isLoading };
};

// Hook Chat Real-time
export const useChatSession = (recipient) => {
    const [conversation, setConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    
    const stompClientRef = useRef(null);

    useEffect(() => {
        if (!recipient) return;

        const initSession = async () => {
            setIsLoading(true);
            try {
                const recipientId = recipient.id || recipient.userId;
                
                const convRes = await startDirectConversation(recipientId);
                const convData = convRes.data 
                setConversation(convData);

             
                if (convData?.id) {
                    const msgRes = await getMessages(convData.id);
                    setMessages(msgRes.data || msgRes);
                }
            } catch (error) {
                console.error("Init chat error:", error);
            } finally {
                setIsLoading(false);
            }
        };

        initSession();
    }, [recipient]);

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