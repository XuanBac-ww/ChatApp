import { fetchClient } from "../libs/fetchClient"



export const getUserInfo = () => {
    return fetchClient({
        baseUrl: "/users/me",
        method: "GET",
        isAuth: true 
    })
}


export const updateUserProfile = (userData) => {
    return fetchClient({
        baseUrl: "/users/update-account",
        method: "PUT",
        params: userData,
        isAuth: true
    })
}

export const searchUsers = (requestBody) => {
    return fetchClient({
        baseUrl: "/users/search",
        method: "POST",
        params: requestBody, 
        isAuth: true
    })
}


export const getUserByFullName = (fullName) => {
    const encodedName = encodeURIComponent(fullName);
    return fetchClient({
        baseUrl: `/users/${encodedName}`,
        method: "GET",
        isAuth: true
    });
};