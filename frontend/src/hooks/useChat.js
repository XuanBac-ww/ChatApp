import { useCallback, useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { getMessages, sendMessage, startDirectConversation } from '../service/conversationService';
import { getUserByFullName } from '../service/userService';
import { getConversationId, getUserId, isTemporaryConversationId } from '../utils/userUtils';


const getToken = () => {
    return localStorage.getItem("access_token") || ""; 
};

export const useRecipientResolver = (fullName, initialRecipient) => {
    const [recipient, setRecipient] = useState(initialRecipient);
    const [isLoading, setIsLoading] = useState(!initialRecipient);
    const [error, setError] = useState(null);

    useEffect(() => {
        let isMounted = true;

        const resolveRecipient = async () => {
            if (initialRecipient) {
                setRecipient(initialRecipient);
                setIsLoading(false);
                return;
            }

            if (!fullName) {
                setRecipient(null);
                setIsLoading(false);
                return;
            }

            setIsLoading(true);
            setError(null);
            try {
                const decodedName = decodeURIComponent(fullName);
                const response = await getUserByFullName(decodedName);
                if (isMounted) {
                    setRecipient(response?.data || null);
                }
            } catch (err) {
                if (isMounted) {
                    setRecipient(null);
                    setError(err.message || 'Không thể tải thông tin người nhận.');
                }
            } finally {
                if (isMounted) {
                    setIsLoading(false);
                }
            }
        };

        resolveRecipient();

        return () => {
            isMounted = false;
        };
    }, [fullName, initialRecipient]);

    return { recipient, isLoading, error };
};

export const useChatSession = (recipient, initialConversationId = null) => {
    const [conversation, setConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    
    const stompClientRef = useRef(null);

    useEffect(() => {
        if (!recipient && !initialConversationId) {
            return;
        }

        let isMounted = true;

        const initSession = async () => {
            setIsLoading(true);
            setError(null);
            try {
                let activeConversationId = null;
                let activeConversation = null;

                if (initialConversationId && !isTemporaryConversationId(initialConversationId)) {
                    activeConversationId = initialConversationId;
                    activeConversation = { id: activeConversationId, conversationId: activeConversationId };
                } else if (recipient) {
                    const recipientId = getUserId(recipient);
                    if (!recipientId) {
                        throw new Error('Không tìm thấy ID người nhận hợp lệ.');
                    }
                    const convResponse = await startDirectConversation(recipientId);
                    activeConversation = convResponse?.data || convResponse;
                    activeConversationId = getConversationId(activeConversation);
                }

                if (isMounted) {
                    setConversation(activeConversation);
                }

                if (activeConversationId) {
                    const msgRes = await getMessages(activeConversationId);
                    if (isMounted) {
                        setMessages(msgRes?.data || []);
                    }
                } else if (isMounted) {
                    setMessages([]);
                }
            } catch (initError) {
                if (isMounted) {
                    setError(initError.message || 'Không thể khởi tạo cuộc trò chuyện.');
                    setConversation(null);
                    setMessages([]);
                }
            } finally {
                if (isMounted) {
                    setIsLoading(false);
                }
            }
        };

        initSession();

        return () => {
            isMounted = false;
        };
    }, [recipient, initialConversationId]);

    const conversationId = getConversationId(conversation);

    useEffect(() => {
        const token = getToken();

        if (conversationId && token) {
            const socket = new SockJS('http://localhost:8080/ws'); 
            const stompClient = Stomp.over(socket);
           
            stompClient.debug = null; 

            const headers = {
                'Authorization': `Bearer ${token}`
            };

            stompClient.connect(headers, () => {
                stompClient.subscribe(`/topic/conversations/${conversationId}`, (payload) => {
                    const newMessage = JSON.parse(payload.body);
                   
                    setMessages((prev) => {
                        if (prev.some(m => m.id === newMessage.id)) return prev;
                        return [...prev, newMessage];
                    });
                });
            });

            stompClientRef.current = stompClient;
        }

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.disconnect();
            }
        };
    }, [conversationId]);


    const onSendMessage = useCallback(async (content) => {
        const text = content?.trim();
        if (!text) {
            return false;
        }

        try {
            let activeConversationId = getConversationId(conversation);

            if (!activeConversationId && recipient) {
                const recipientId = getUserId(recipient);
                if (!recipientId) {
                    throw new Error('Không tìm thấy ID người nhận.');
                }

                const createdConversationResponse = await startDirectConversation(recipientId);
                const createdConversation = createdConversationResponse?.data || createdConversationResponse;
                activeConversationId = getConversationId(createdConversation);
                setConversation(createdConversation);
            }

            if (!activeConversationId) {
                return false;
            }

            const sendResponse = await sendMessage(activeConversationId, text);
            const nextMessage = sendResponse?.data || sendResponse;

            if (nextMessage) {
                setMessages((prev) => {
                    if (nextMessage.id && prev.some((message) => message.id === nextMessage.id)) {
                        return prev;
                    }
                    return [...prev, nextMessage];
                });
            }

            return true;
        } catch (sendError) {
            setError(sendError.message || 'Không thể gửi tin nhắn.');
            return false;
        }
    }, [conversation, recipient]);

    return {
        conversation,
        setConversation,
        messages,
        setMessages,
        isLoading,
        error,
        onSendMessage
    };
};