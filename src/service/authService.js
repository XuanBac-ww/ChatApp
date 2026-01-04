import { fetchClient } from "../libs/fetchClient"

export const login = (values) => {
    return fetchClient({
        baseUrl: "/auths/login",
        method: "POST",
        params: values,
        isAuth: false // Không cần token khi đăng nhập
    })
}

export const signUp = (values) => {
    return fetchClient({
        baseUrl: "/auths/signup",
        method: "POST",
        params: values,
        isAuth: false 
    })
}

export const logout = (refreshToken) => {
  return fetchClient({
    baseUrl: "/auths/logout",
    method: "POST",
    params: { refreshToken: refreshToken }, 
    isAuth: true 
  });
};

export const verifyOtp = (values) => {
    return fetchClient({
        baseUrl: "/auths/verify-otp", 
        method: "POST",
        params: values, 
        isAuth: false
    });
};