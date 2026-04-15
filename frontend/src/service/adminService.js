import { fetchClient } from "../libs/fetchClient";

export const getAllUsers = (page = 0, size = 10) => {
    return fetchClient({
        baseUrl: "/admin/all",
        method: "GET",
        query: { page, size },
        isAuth: true,
    });
};

export const getAllDeletedUsers = (page = 0, size = 10) => {
    return fetchClient({
        baseUrl: "/admin/all/deleted",
        method: "GET",
        query: { page, size },
        isAuth: true,
    });
};

export const deleteAccount = () => {
    return fetchClient({
        baseUrl: "/admin/delete-account",
        method: "DELETE",
        isAuth: true,
    });
};
