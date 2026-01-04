import { useState } from "react";
import { useNavigate } from "react-router-dom"; 
import LoginForm from "../../components/forms/LoginForm";
import { login } from "../../service/authService";

import { jwtDecode } from "jwt-decode"; 

const LoginPage = () => {

    const [values, setValues] = useState({
        email : "",
        password : ""
    });
    const [error, setError] = useState(null);     

    const handleChangeInput = (e) => {
        setError(null); 
        setValues({ ...values, [e.target.name]: e.target.value })
    }

    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null); 
            
        try {
            const res  = await login(values);

            if (res && res.success) {

                const { token } = res.data;

               
                localStorage.setItem("access_token", token);
                
                try {
                    const decodedToken = jwtDecode(token);
                    const expiryTime = decodedToken.exp * 1000; 
                    localStorage.setItem("token_expiry", expiryTime.toString());
                } catch (decodeError) {
                    console.error("Lỗi giải mã token:", decodeError);
                    setError("Token không hợp lệ, không thể đăng nhập.");
                    localStorage.removeItem("access_token"); 
                    return; 
                }
                navigate("/home");
                console.log("Login success:", res);
            } else {
                setError(res.message || "Đăng nhập thất bại");
            }

        } catch (err) {
            setError(err.message);
        }
    }
    
    return(
       <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
           <LoginForm
               formValues={values}
               handleChangeInput={handleChangeInput}
               handleLogin={handleLogin}
               errorMessage={error}
           />
       </div>
    )
}

export default LoginPage;