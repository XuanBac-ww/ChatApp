import { useState } from "react";
import { useNavigate } from "react-router"; // hoặc 'react-router-dom' tùy phiên bản
import RegisterForm from "../../components/forms/RegisterForm";
import { signUp } from "../../service/authService";

const SignUpPage = () => {
    const [values, setValues] = useState({
        email: "",
        password: "",
        fullName: "",
        numberPhone: ""
    });
    const [error, setError] = useState(null);

    const navigate = useNavigate();

    const handleChangeInput = (e) => {
        setError(null);
        setValues({ ...values, [e.target.name]: e.target.value })
    }

    const handleSignUp = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            // 1. Gọi API đăng ký
            const res = await signUp(values);
            console.log("Sign Up success:", res);

            // 2. Chuyển hướng sang trang VerifyAccountPage
            // Quan trọng: Truyền kèm email qua 'state' để trang Verify tự điền
            navigate("/verify-account", { state: { email: values.email } });

        } catch (err) {
            setError(err.message || "Đăng ký thất bại");
        }
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
            <RegisterForm
                formValues={values}
                handleChangeInput={handleChangeInput}
                handleRegister={handleSignUp}
                errorMessage={error}
            />
        </div>
    )
}

export default SignUpPage;