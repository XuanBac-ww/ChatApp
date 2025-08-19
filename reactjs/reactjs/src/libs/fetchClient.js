const API_URL = import.meta.env.VITE_API_URL || "";

export async function fetchClient({
  baseUrl = "",
  method = "GET",
  headers = {},
  params = null,
  timeOut = 8000,
  withCredentials = false,
}) {
  // Ghép baseUrl với API_URL
  const endpoint = new URL(baseUrl, API_URL);

  if (method.toUpperCase() === "GET" && params) {
    Object.entries(params).forEach(([key, value]) => {
      endpoint.searchParams.append(key, value);
    });
  }

  const options = {
    method,
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
    credentials: withCredentials ? "include" : "same-origin",
  };

  if (method.toUpperCase() !== "GET" && params) {
    options.body = JSON.stringify(params);
  }

  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), timeOut);
  options.signal = controller.signal;

  try {
    const res = await fetch(endpoint, options);
    clearTimeout(timeoutId);

    if (!res.ok) {
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
      throw new Error("Request timeout");
    }
    throw error;
  }
}
