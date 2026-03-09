import { useCallback, useRef, useState } from 'react';

export const useToast = (duration = 3000) => {
    const [toast, setToast] = useState({ show: false, message: '', type: 'success' });
    const timerRef = useRef(null);

    const hideToast = useCallback(() => {
        setToast((prev) => ({ ...prev, show: false }));
        if (timerRef.current) {
            clearTimeout(timerRef.current);
            timerRef.current = null;
        }
    }, []);

    const showToast = useCallback((message, type = 'success') => {
        setToast({ show: true, message, type });
        if (timerRef.current) {
            clearTimeout(timerRef.current);
        }
        timerRef.current = setTimeout(() => {
            setToast((prev) => ({ ...prev, show: false }));
            timerRef.current = null;
        }, duration);
    }, [duration]);

    return { toast, showToast, hideToast };
};
