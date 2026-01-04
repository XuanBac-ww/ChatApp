import { formatDistanceToNow } from 'date-fns';
import { vi } from 'date-fns/locale';

export const formatTimeDisplay = (timestamp) => {
    if (!timestamp) return '';
    
    try {
        const date = new Date(timestamp);
        
        return formatDistanceToNow(date, { 
            addSuffix: true, 
            locale: vi       
        });
    } catch (error) {
        console.error("Lỗi format thời gian:", error);
        return '';
    }
};