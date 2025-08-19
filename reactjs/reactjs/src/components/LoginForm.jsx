import React from "react";
import InputField from "./InputField";

const LoginForm = React.memo(({ formValues, handleChangeInput, handleLogin }) => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <div className="w-full max-w-md bg-orange-500 rounded-lg shadow-lg p-8">
        <form action="post">
          <div className="space-y-4">
            <div>
              <p className="mb-2 text-sm font-medium text-gray-700">Họ tên</p>
              <InputField
                id={1}
                value={formValues.email}
                onChange={handleChangeInput}
                name="email"
                label="tai khoan"
                type="text"
              />
            </div>
            <div>
              <InputField
                id={2}
                value={formValues.password}
                onChange={handleChangeInput}
                name="password"
                label="mat khau"
                type="password"
              />
            </div>
            <button
              type="submit"
              onClick={handleLogin}
              className="w-full px-4 py-2 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-colors"
            >
              Đăng Nhập
            </button>
          </div>
        </form>
      </div>
    </div>
  );
});

export default LoginForm;