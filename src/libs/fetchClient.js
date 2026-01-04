const API_URL = import.meta.env.VITE_API_URL;

const handleSessionExpired = () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('token_expiry');
    if (window.location.pathname !== "/login") {
        window.location.href = '/login?sessionExpired=true';
    }
}


export async function fetchClient({
    baseUrl = "",
    method = "GET",
    headers = {},
    params = null,
    timeOut = 8000, 
    isAuth = false, 
}) {
    if (isAuth) {
        const expiryTime = localStorage.getItem('token_expiry');
        if (expiryTime && Date.now() > parseInt(expiryTime, 10)) {
            console.warn("Token đã hết hạn. Đang tự động đăng xuất...");
            handleSessionExpired();
            throw new Error("Phiên đăng nhập đã hết hạn.");
        }

        const token = localStorage.getItem('access_token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        } else {
            console.warn(`Request ${baseUrl} cần token nhưng không tìm thấy.`);
            handleSessionExpired();
            throw new Error("Bạn chưa đăng nhập. Vui lòng đăng nhập lại.");
        }
    }

    const endpoint = new URL(baseUrl, API_URL);

    if (method.toUpperCase() === "GET" && params) {
        Object.entries(params).forEach(([key, value]) => {
            if (value !== null && value !== undefined) {
                endpoint.searchParams.append(key, value);
            }
        });
    }
  
    const isFormData = params instanceof FormData;

    const options = {
        method,
        headers: {
            ...headers,
            ...(!isFormData && { "Content-Type": "application/json" }),
        },
        credentials: "same-origin", // Gửi kèm cookie nếu cùng domain
    };

    if (method.toUpperCase() !== "GET" && params) {
        options.body = isFormData ? params : JSON.stringify(params);
    }

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), timeOut);
    options.signal = controller.signal;

    try {
        const res = await fetch(endpoint, options);
        clearTimeout(timeoutId); 

        if (!res.ok) {
            if (res.status === 401 || res.status === 403) {
                console.error("Lỗi 401/403 từ server. Đang tự động đăng xuất...");
                handleSessionExpired();
                throw new Error("Phiên đăng nhập hết hạn hoặc không có quyền truy cập.");
            }

            let errorBody;
            try {
                errorBody = await res.json();
            } catch {
                throw new Error(`HTTP Error ${res.status}: ${res.statusText}`);
            }

            if (errorBody && errorBody.message) {
                throw new Error(errorBody.message);
            }
            throw new Error(`HTTP Error ${res.status}: ${res.statusText}`);
        }

        const text = await res.text();
        try {
            return JSON.parse(text);
        } catch {
            return text;
        }

    } catch (error) {
        if (error.name === "AbortError") {
            throw new Error("Kết nối quá thời gian quy định (Request timeout).");
        }
        throw error;
    }
}