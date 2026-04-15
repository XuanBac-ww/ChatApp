import { fetchClient } from "../libs/fetchClient";

export const login = (values) => {
    return fetchClient({
        baseUrl: "/auths/login",
        method: "POST",
        body: {
            email: values.email?.trim(),
            password: values.password,
        },
        isAuth: false,
    });
};

export const signUp = (values) => {
    return fetchClient({
        baseUrl: "/auths/signup",
        method: "POST",
        body: {
            email: values.email?.trim(),
            password: values.password,
            fullName: values.fullName?.trim(),
            numberPhone: values.numberPhone?.trim(),
        },
        isAuth: false,
    });
};

export const logout = (refreshToken) => {
    return fetchClient({
        baseUrl: "/auths/logout",
        method: "POST",
        body: { refreshToken },
        isAuth: true,
    });
};

export const verifyOtp = (values) => {
    return fetchClient({
        baseUrl: "/auths/verify-otp",
        method: "POST",
        body: {
            email: values.email?.trim(),
            otp: values.otp?.trim(),
        },
        isAuth: false,
    });
};
