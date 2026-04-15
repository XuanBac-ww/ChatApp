import { fetchClient } from "../libs/fetchClient";

export const getUserInfo = () => {
    return fetchClient({
        baseUrl: "/users/me",
        method: "GET",
        isAuth: true,
    });
};

export const updateUserProfile = (userData) => {
    return fetchClient({
        baseUrl: "/users/update-account",
        method: "PUT",
        body: {
            fullName: userData.fullName?.trim(),
            numberPhone: userData.numberPhone?.trim(),
        },
        isAuth: true,
    });
};

export const searchUsers = (requestBody) => {
    return fetchClient({
        baseUrl: "/users/search",
        method: "POST",
        body: {
            numberPhone: requestBody.numberPhone?.trim(),
        },
        isAuth: true,
    });
};

export const getUserByFullName = (fullName) => {
    return fetchClient({
        baseUrl: `/users/${encodeURIComponent(fullName)}`,
        method: "GET",
        isAuth: true,
    });
};
