import { useState } from "react";
import { useNavigate } from "react-router-dom";
import RegisterForm from "../../components/forms/RegisterForm";
import { signUp } from "../../service/authService";
import { AUTH_PAGE_WRAPPER_CLASS } from "../../utils/authUiClasses";

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
            await signUp(values);
            navigate("/verify-account", { state: { email: values.email } });

        } catch (err) {
            setError(err.message || "Đăng ký thất bại");
        }
    }

    return (
        <div className={`${AUTH_PAGE_WRAPPER_CLASS} flex items-center justify-center`}>
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