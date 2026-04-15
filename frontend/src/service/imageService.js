import { fetchClient } from "../libs/fetchClient";

const createImageFormData = (file) => {
    const formData = new FormData();
    formData.append("image", file);
    return formData;
};

export const uploadImage = (file) => {
    return fetchClient({
        baseUrl: "/image/upload",
        method: "POST",
        formData: createImageFormData(file),
        isAuth: true,
    });
};

export const updateImage = (imageId, file) => {
    return fetchClient({
        baseUrl: `/image/update/${imageId}`,
        method: "PUT",
        formData: createImageFormData(file),
        isAuth: true,
    });
};
