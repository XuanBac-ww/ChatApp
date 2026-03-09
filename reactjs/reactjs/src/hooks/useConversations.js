import { useEffect, useState } from 'react';
import { getMyConversations } from '../service/conversationService';
import { mapConversationItem, mergeRecipientConversation } from '../utils/conversationUtils';
import { getUserId } from '../utils/userUtils';

export const useConversations = (user, recipientState) => {
    const [conversations, setConversations] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        let isMounted = true; 

        const fetchDataAndMerge = async () => {
            if (!user) {
                setIsLoading(false);
                return;
            }
            
            setIsLoading(true);
            setError(null);

            try {
                const response = await getMyConversations();
                
                if (!isMounted) return;

                const items = response?.data || [];
                const mappedConversations = items.map((item) => mapConversationItem(item, getUserId(user)));
                setConversations(mergeRecipientConversation(mappedConversations, recipientState));
            } catch (fetchError) {
                setError(fetchError.message || "Lỗi tải cuộc trò chuyện.");
                setConversations([]);
            } finally {
                if (isMounted) setIsLoading(false);
            }
        };

        fetchDataAndMerge();

        return () => { isMounted = false; };
    }, [user, recipientState]); 

    return { conversations, isLoading, error };
};