import { fetchClient } from "../libs/fetchClient";

export const uploadImage = (file) => {
    const formData = new FormData();
    formData.append("image", file); 
    return fetchClient({
        baseUrl: "/image/upload",
        method: "POST",
        params: formData,
        isAuth: true
    });
};

export const updateImage = (imageId, file) => {
    const formData = new FormData();
    formData.append("image", file);

    return fetchClient({
        baseUrl: `/image/update/${imageId}`,
        method: "PUT",
        params: formData,
        isAuth: true
    });
};