const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

const ACCESS_TOKEN_KEY = "access_token";
const REFRESH_TOKEN_KEY = "refreshToken";
const TOKEN_EXPIRY_KEY = "token_expiry";

let refreshTokenPromise = null;

const clearAuthStorage = () => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(TOKEN_EXPIRY_KEY);
};

const handleSessionExpired = () => {
    clearAuthStorage();
    if (window.location.pathname !== "/login") {
        window.location.href = "/login?sessionExpired=true";
    }
};

const getTokenExpiry = (token) => {
    try {
        const [, payload] = token.split(".");
        if (!payload) return null;
        const normalizedPayload = payload
            .replace(/-/g, "+")
            .replace(/_/g, "/")
            .padEnd(Math.ceil(payload.length / 4) * 4, "=");
        const parsedPayload = JSON.parse(window.atob(normalizedPayload));
        return parsedPayload.exp ? parsedPayload.exp * 1000 : null;
    } catch {
        return null;
    }
};

const saveAccessToken = (token) => {
    localStorage.setItem(ACCESS_TOKEN_KEY, token);

    const expiryTime = getTokenExpiry(token);
    if (expiryTime) {
        localStorage.setItem(TOKEN_EXPIRY_KEY, expiryTime.toString());
    }
};

const refreshAccessToken = async () => {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    if (!refreshToken) {
        return null;
    }

    const endpoint = new URL("/auths/refresh", API_URL);
    let response;

    try {
        response = await fetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "same-origin",
            body: JSON.stringify({ refreshToken }),
        });
    } catch {
        return null;
    }

    if (!response.ok) {
        return null;
    }

    const payload = await response.json();
    const token = payload?.data;
    if (!payload?.success || !token) {
        return null;
    }

    saveAccessToken(token);
    return token;
};

export const getValidAccessToken = async () => {
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    if (!token) {
        return null;
    }

    const expiryTime = localStorage.getItem(TOKEN_EXPIRY_KEY) || getTokenExpiry(token);
    if (!expiryTime || Date.now() <= Number(expiryTime)) {
        return token;
    }

    if (!refreshTokenPromise) {
        refreshTokenPromise = refreshAccessToken().finally(() => {
            refreshTokenPromise = null;
        });
    }

    return refreshTokenPromise;
};

export async function fetchClient({
    baseUrl = "",
    method = "GET",
    headers = {},
    query = null,
    body = null,
    formData = null,
    params = null,
    timeout = 8000,
    timeOut,
    isAuth = false,
}) {
    const requestTimeout = typeof timeOut === "number" ? timeOut : timeout;
    const requestMethod = method.toUpperCase();

    const requestQuery = query ?? (requestMethod === "GET" ? params : null);
    const requestBody = body ?? (requestMethod !== "GET" && !(params instanceof FormData) ? params : null);
    const requestFormData = formData ?? (params instanceof FormData ? params : null);

    const requestHeaders = { ...headers };

    if (isAuth) {
        const token = await getValidAccessToken();
        if (!token) {
            handleSessionExpired();
            throw new Error("Session expired. Please log in again.");
        }

        requestHeaders.Authorization = `Bearer ${token}`;
    }

    const endpoint = new URL(baseUrl, API_URL);

    if (requestQuery) {
        Object.entries(requestQuery).forEach(([key, value]) => {
            if (value !== null && value !== undefined && value !== "") {
                endpoint.searchParams.append(key, value);
            }
        });
    }

    const options = {
        method: requestMethod,
        headers: {
            ...requestHeaders,
            ...(!requestFormData && requestBody !== null && { "Content-Type": "application/json" }),
        },
        credentials: "same-origin",
    };

    if (requestMethod !== "GET" && requestFormData) {
        options.body = requestFormData;
    } else if (requestMethod !== "GET" && requestBody !== null) {
        options.body = JSON.stringify(requestBody);
    }

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), requestTimeout);
    options.signal = controller.signal;

    try {
        const res = await fetch(endpoint, options);
        clearTimeout(timeoutId);

        if (!res.ok) {
            if (res.status === 401 || res.status === 403) {
                handleSessionExpired();
                throw new Error("Session expired or access denied.");
            }

            let errorBody;
            try {
                errorBody = await res.json();
            } catch {
                throw new Error(`HTTP Error ${res.status}: ${res.statusText}`);
            }

            if (errorBody?.message) {
                throw new Error(errorBody.message);
            }

            throw new Error(`HTTP Error ${res.status}: ${res.statusText}`);
        }

        const text = await res.text();
        if (!text) {
            return null;
        }

        try {
            return JSON.parse(text);
        } catch {
            return text;
        }
    } catch (error) {
        clearTimeout(timeoutId);
        if (error.name === "AbortError") {
            throw new Error("Request timeout.");
        }
        throw error;
    }
}
