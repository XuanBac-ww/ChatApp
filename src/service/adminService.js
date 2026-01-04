import { fetchClient } from "../libs/fetchClient"


export const getAllUsers = () => {
    return fetchClient({
        baseUrl: "/admin/all",
        method: "GET",
        isAuth: true

    })
}

export const getAllDeletedUsers = () => {
    return fetchClient({
        baseUrl: "/admin/all/deleted",
        method: "GET",
        isAuth: true
    })
}

export const deleteAccount = () => {
    return fetchClient({
        baseUrl: "/admin/delete-account",
        method: "DELETE",
        isAuth: true
    })
}