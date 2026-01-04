import { useState, useRef, useEffect } from 'react';
import { uploadImage, updateImage } from '../../service/imageService';

export const useAvatarUpload = (profile) => {
    const [isUploading, setIsUploading] = useState(false);
    const [previewImage, setPreviewImage] = useState(null);
    const fileInputRef = useRef(null);

    useEffect(() => {
        return () => previewImage && URL.revokeObjectURL(previewImage);
    }, [previewImage]);

    const triggerFileInput = () => {
        if (!isUploading && fileInputRef.current) fileInputRef.current.click();
    };

    const handleFileChange = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('Vui lòng chọn file định dạng hình ảnh!');
            return;
        }

       
        const objectUrl = URL.createObjectURL(file);
        setPreviewImage(objectUrl);
        setIsUploading(true);

        try {
            if (profile.imageId) {
                await updateImage(profile.imageId, file);
            } else {
                await uploadImage(file);
            }
            console.log("Upload thành công!");
        } catch (error) {
            console.error("Lỗi upload:", error);
            alert('Có lỗi xảy ra, vui lòng thử lại.');
            setPreviewImage(null); 
        } finally {
            setIsUploading(false);
            if (fileInputRef.current) fileInputRef.current.value = '';
        }
    };

    const currentAvatar = previewImage || profile?.profileImage ;

    return {
        isUploading,
        currentAvatar,
        fileInputRef,
        triggerFileInput,
        handleFileChange
    };
};