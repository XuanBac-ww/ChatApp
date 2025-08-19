import { useState } from "react"
import LoginForm from "../components/LoginForm"
import { fetchClient } from "../libs/fetchClient"

const LoginPage = () => {

    const [values, setValues] = useState({
        email : "",
        password : ""
    })

    const handleChangeInput = (e) => {
        setValues({ ...values, [e.target.name]: e.target.value })
    }

    const handleLogin = (e) => {
         e.preventDefault();
    try {
      // Gọi API login từ BE
      const res = fetchClient({
        baseUrl: "/auth/login",   // ví dụ endpoint BE
        method: "POST",
        params: values,               // body { email, password }
      });

      console.log("Login success:", res);

    } catch (err) {
        console.error("Login failed:", err.message);
        alert("Đăng nhập thất bại: " + err.message);
        }
    }

    return(
       <LoginForm
            formValues={values}
            handleChangeInput={handleChangeInput}
            handleLogin={handleLogin}
        />

    )
}

export default LoginPage;