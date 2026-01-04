import React, { useRef, useState, useEffect } from 'react';
import InfoCard from '../../components/ui/InfoCard';
import ProfileField from '../../components/ui/ProfileField';
import StateMessage from '../../components/common/StateMessage';
import Button from '../../components/common/Button';
import { useUserProfile } from '../../hooks/useProfile';
import { LogOut, SquarePen, Camera } from 'lucide-react';

import { uploadImage, updateImage } from '../../service/imageService';

const ProfilePage = () => {
    
    const { profile, formData, status, actions } = useUserProfile();
    const { isLoading, isEditing, isSaving, error, saveError } = status;
    
    // --- STATE MỚI CHO CÁCH 2 ---
    const [isUploading, setIsUploading] = useState(false);
    const [previewImage, setPreviewImage] = useState(null); // Lưu ảnh hiển thị tạm thời
    const fileInputRef = useRef(null);


    useEffect(() => {
        return () => {
            if (previewImage) {
                URL.revokeObjectURL(previewImage);
            }
        };
    }, [previewImage]);

    // Hàm xử lý click vào avatar
    const handleAvatarClick = () => {
        if (!isUploading && fileInputRef.current) {
            fileInputRef.current.click();
        }
    };

    // Hàm xử lý chính
    const handleFileChange = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // 1. Validate file
        if (!file.type.startsWith('image/')) {
            alert('Vui lòng chọn file định dạng hình ảnh!');
            return;
        }

        // 2. HIỂN THỊ ẢNH NGAY LẬP TỨC (Preview Mode)
        // Tạo một URL tạm thời từ file người dùng vừa chọn
        const objectUrl = URL.createObjectURL(file);
        setPreviewImage(objectUrl); 

        setIsUploading(true);
        
        try {
            // 3. Gửi lên Server (Upload ngầm)
            if (profile.imageId) {
                await updateImage(profile.imageId, file);
            } else {
                await uploadImage(file);
            }
            
            // Upload thành công: Không cần làm gì cả vì ảnh đã hiện rồi (previewImage)
            // Có thể hiện thông báo nhỏ (Toast) nếu muốn
            console.log("Upload thành công!");

        } catch (error) {
            console.error("Lỗi upload ảnh:", error);
            alert('Có lỗi xảy ra, vui lòng thử lại.');
            
            // 4. Nếu lỗi -> Hoàn tác lại ảnh cũ
            setPreviewImage(null); 
        } finally {
            setIsUploading(false);
            // Reset input để cho phép chọn lại cùng 1 file nếu muốn
            if (fileInputRef.current) fileInputRef.current.value = '';
        }
    };

    if (isLoading) return <StateMessage message="Đang tải thông tin hồ sơ..." />;
    if (error) return <StateMessage message={`Lỗi: ${error}`} isError />;
    if (!profile) return <StateMessage message="Không có dữ liệu hồ sơ." />;

    // Logic chọn nguồn ảnh hiển thị:
    // Ưu tiên 1: Ảnh vừa chọn (previewImage)
    // Ưu tiên 2: Ảnh từ server (profile.profileImage)
    // Ưu tiên 3: Ảnh mặc định
    const currentAvatar = previewImage || profile.profileImage || "https://via.placeholder.com/150";

    return (
        <div className='flex-1 bg-gray-50 overflow-y-auto'>
            
            {/* --- HEADER SECTION --- */}
            <div className='bg-blue-600 text-white p-8 shadow-md'>
                <div className='flex items-center space-x-6'>
                    
                    {/* --- AVATAR AREA --- */}
                    <div className="relative group cursor-pointer" onClick={handleAvatarClick}>
                        <input 
                            type="file" 
                            ref={fileInputRef} 
                            onChange={handleFileChange} 
                            accept="image/*" 
                            className="hidden" 
                        />

                        <img
                            src={currentAvatar}
                            alt="Avatar"
                            className={`w-24 h-24 rounded-full border-4 border-white shadow-lg object-cover transition-opacity ${isUploading ? 'opacity-70' : ''}`}
                        />

                        {/* Overlay icon Camera */}
                        <div className="absolute inset-0 bg-black bg-opacity-40 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                            <Camera className="text-white" size={24} />
                        </div>

                        {/* Loading spinner */}
                        {isUploading && (
                            <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-20 rounded-full">
                                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
                            </div>
                        )}
                    </div>

                    <div>
                        <h1 className='text-3xl font-bold'>
                            {isEditing ? "Chỉnh sửa hồ sơ" : profile.fullName}
                        </h1>
                        <p className="text-blue-100 mt-1 opacity-90">{profile.email}</p>
                    </div>
                </div>
            </div>

            {/* --- INFO FORM SECTION --- */}
            {/* ... (Phần bên dưới giữ nguyên không thay đổi) ... */}
            <div className='p-8 max-w-3xl'>
                <InfoCard
                    title="Thông tin cơ bản"
                    iconAction={!isEditing ? actions.startEditing : null}
                    icon={!isEditing ? <SquarePen size={20} /> : null}
                >
                    <div className='grid grid-cols-1 md:grid-cols-2 gap-y-6 gap-x-8'>
                        <ProfileField 
                            label="Tên người dùng" 
                            name="fullName" 
                            value={isEditing ? formData.fullName : profile.fullName} 
                            isEditing={isEditing} 
                            onChange={actions.handleChange} 
                        />
                        <ProfileField 
                            label="Số điện thoại" 
                            name="numberPhone" 
                            value={isEditing ? formData.numberPhone : profile.numberPhone} 
                            isEditing={isEditing} 
                            onChange={actions.handleChange} 
                        />
                        <ProfileField 
                            label="Email" 
                            value={profile.email} 
                            isEditing={false} 
                            readOnly={true} 
                        />
                        <div>
                            <label className='text-sm font-medium text-gray-500 block mb-1'>Trạng thái</label>
                            <div className='flex items-center space-x-2 py-2'>
                                <span className='w-2.5 h-2.5 bg-green-500 rounded-full animate-pulse'></span>
                                <p className='text-green-600 font-medium'>Đang hoạt động</p>
                            </div>
                        </div>
                        <ProfileField 
                            label="Ngày tham gia" 
                            value={profile.createdAt ? new Date(profile.createdAt).toLocaleDateString('vi-VN') : ''} 
                            isEditing={false} 
                            readOnly={true} 
                        />
                    </div>
                </InfoCard>
            </div>

            {/* --- BUTTONS --- */}
            <div className='px-8 pb-12 flex items-center space-x-4'>
                {isEditing ? (
                    <>
                        <Button onClick={actions.saveChanges} disabled={isSaving} variant="primary">
                            {isSaving ? 'Đang lưu...' : 'Lưu Thay Đổi'}
                        </Button>
                        <Button onClick={actions.cancelEditing} disabled={isSaving} variant="secondary">
                            Hủy
                        </Button>
                    </>
                ) : (
                    <>
                        <Button onClick={actions.startEditing} variant="primary">
                            Chỉnh Sửa Hồ Sơ
                        </Button>
                        <button onClick={actions.logout} className="flex items-center space-x-2 px-4 py-2 text-gray-600 hover:text-red-600 font-medium transition-colors rounded-lg hover:bg-red-50">
                            <LogOut size={20} />
                            <span>Đăng xuất</span>
                        </button>
                    </>
                )}
            </div>
            
            {saveError && (
                <div className="px-8 pb-8 animate-fade-in">
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded relative">
                        <span className="block sm:inline">{saveError}</span>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ProfilePage;