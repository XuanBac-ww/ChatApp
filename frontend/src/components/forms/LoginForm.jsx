import React from "react";
import InputField from "../common/InputField";

const LoginForm = React.memo(({ formValues, handleChangeInput, handleLogin, errorMessage }) => {
  
  return (
    <div className="w-full max-w-md bg-white rounded-lg shadow-xl p-8 md:p-12">
      <form onSubmit={handleLogin}> 
        <div className="space-y-6">
          <div>
            <p className="mb-2 text-sm font-medium text-gray-700">
              Email 
            </p> 
            <InputField
              id={1}
              value={formValues.email}
              onChange={handleChangeInput}
              name="email"
              label="Email" 
              type="text"
              className="pl-4 pr-4 py-2 w-full border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <p className="mb-2 text-sm font-medium text-gray-700">
              Password
            </p> 

            <InputField
              id={2}
              value={formValues.password}
              onChange={handleChangeInput}
              name="password"
              label="Mật khẩu"
              type="password"
              className="pl-4 pr-4 py-2 w-full border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          {errorMessage && (
              <p className="text-red-700 bg-red-100 p-3 rounded-md text-sm font-medium text-center">
                {errorMessage}
              </p>
          )}

          <button
            type="submit"
            className="w-full px-6 py-3 bg-gradient-to-r from-purple-500 to-indigo-600 text-white font-semibold rounded-md shadow-md hover:from-purple-600 hover:to-indigo-700 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-offset-2 transition-all duration-300"
          >
            LOGIN
          </button>
        </div>
      </form>
    </div>
  );
});

export default LoginForm;