import { useState, useEffect } from 'react';
import { formatTimeDisplay } from '../utils/dateUtils';
import { getMyConversations } from '../service/conversationService';

export const useConversations = (user, recipientState) => {
    const [conversations, setConversations] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        let isMounted = true; 

        const fetchDataAndMerge = async () => {
            // Nếu chưa có user (chưa login xong), chưa gọi API vội
            if (!user) return;
            
            setIsLoading(true);

            try {
                const response = await getMyConversations();
                
                if (!isMounted) return;

                let mappedData = [];
                
                if (response && response.data) {
                    mappedData = response.data.map(item => {
                       
                        let targetId = item.userId; 
            
                        if (item.senderId && item.receiverId) {
                          
                            targetId = (String(item.senderId) === String(user.id)) 
                                        ? item.receiverId 
                                        : item.senderId;
                        } 
                        else if (item.recipientId) {
                             targetId = item.recipientId;
                        }
                

                        return {
                            id: item.conversationId, 
                            otherUserId: targetId, 
                            name: item.name,
                            message: item.lastMessage || "Chưa có tin nhắn",
                            avatar: item.avatar,
                            time: formatTimeDisplay(item.lastMessageTimestamp),
                            timestamp: item.lastMessageTimestamp 
                        };
                    });
                }

                // --- LOGIC MERGE NGƯỜI MỚI (TỪ TRANG BẠN BÈ/TÌM KIẾM) ---
                if (recipientState) {
                    const recipientName = recipientState.userName || recipientState.fullName;
                    
                    // Tìm xem cuộc trò chuyện này đã có trong danh sách tải về chưa
                    const existingIndex = mappedData.findIndex(c => c.name === recipientName);

                    if (existingIndex > -1) {
                        // Nếu có rồi, đưa nó lên đầu danh sách
                        const [existingItem] = mappedData.splice(existingIndex, 1);
                        mappedData.unshift(existingItem);
                    } else {
                        // Nếu là chat mới hoàn toàn, tạo một item tạm ở đầu
                        mappedData.unshift({
                            id: `temp-${Date.now()}`,
                            otherUserId: recipientState.userId || recipientState.id, 
                            name: recipientName,
                            message: "Bắt đầu cuộc trò chuyện...",
                            time: "Vừa xong",
                            avatar: recipientState.avatar,
                        });
                    }
                }

                setConversations(mappedData);
            } catch (error) {
                console.error("Lỗi tải cuộc trò chuyện:", error);
            } finally {
                if (isMounted) setIsLoading(false);
            }
        };

        fetchDataAndMerge();

        return () => { isMounted = false; };
    }, [user, recipientState]); 

    return { conversations, isLoading };
};