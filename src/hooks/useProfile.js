import { useState, useEffect } from 'react';
import { getUserInfo, updateUserProfile } from '../service/userService';
import { logout as logoutAPI } from '../service/authService'; 
import { useAuth } from './useAuth';

export const useUserProfile = () => {
    const { logout: contextLogout } = useAuth(); 
    
    const [profile, setProfile] = useState(null);
    const [formData, setFormData] = useState({ fullName: '', numberPhone: '' });
    
    const [status, setStatus] = useState({
        isLoading: true,
        isEditing: false,
        isSaving: false,
        error: null,
        saveError: null
    });

    // Fetch data
    useEffect(() => {
        const fetchUserInfo = async () => {
            try {
                const response = await getUserInfo();
                if (response?.success) {
                    setProfile(response.data);
                    setFormData({
                        fullName: response.data.fullName || '',
                        numberPhone: response.data.numberPhone || ''
                    });
                    setStatus(prev => ({ ...prev, isLoading: false }));
                } else {
                    setStatus(prev => ({ ...prev, isLoading: false, error: response.message }));
                }
            } catch (err) {
                setStatus(prev => ({ ...prev, isLoading: false, error: err.response?.message || "Lỗi kết nối" }));
            }
        };
        fetchUserInfo();
    }, []);

    // Xử lý input change
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    // Bật chế độ sửa
    const startEditing = () => setStatus(prev => ({ ...prev, isEditing: true, saveError: null }));

    // Hủy sửa
    const cancelEditing = () => {
        setFormData({
            fullName: profile.fullName,
            numberPhone: profile.numberPhone
        });
        setStatus(prev => ({ ...prev, isEditing: false, saveError: null }));
    };

    // Lưu thay đổi
    const saveChanges = async () => {
        setStatus(prev => ({ ...prev, isSaving: true, saveError: null }));
        try {
            const response = await updateUserProfile(formData);
            if (response?.success) {
                setProfile(prev => ({ ...prev, ...formData }));
                setStatus(prev => ({ ...prev, isEditing: false, isSaving: false }));
            } else {
                setStatus(prev => ({ ...prev, isSaving: false, saveError: response.message }));
            }
        } catch (err) {
            setStatus(prev => ({ ...prev, isSaving: false, saveError: err.response?.message || "Lỗi khi lưu" }));
        }
    };

    const handleLogout = async () => {
        try {
            const refreshToken = localStorage.getItem("refreshToken"); 
            
            if (refreshToken) {
                await logoutAPI(refreshToken);
            }
        } catch (error) {
            console.error("Lỗi khi gọi API logout:", error);
        } finally {
            contextLogout();
        }
    };

    return {
        profile,
        formData,
        status,
        actions: { 
            handleChange, 
            startEditing, 
            cancelEditing, 
            saveChanges, 
            logout: handleLogout 
        }
    };
};