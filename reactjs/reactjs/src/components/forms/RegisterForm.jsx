import React from "react";
import InputField from "../common/InputField";
import { AUTH_CARD_CLASS, AUTH_INPUT_CLASS, AUTH_PRIMARY_BUTTON_CLASS } from "../../utils/authUiClasses";

const RegisterForm = React.memo(({ formValues, handleChangeInput, handleRegister, errorMessage }) => {
  
  return (
    <div className={AUTH_CARD_CLASS}>
      <form onSubmit={handleRegister}> 
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
              className={AUTH_INPUT_CLASS}
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
              className={AUTH_INPUT_CLASS}
            />
          </div>

          <div>
            <p className="mb-2 text-sm font-medium text-gray-700">
              Full Name
            </p> 

            <InputField
              id={3}
              value={formValues.fullName}
              onChange={handleChangeInput}
              name="fullName"
              label="Họ và tên"
              type="text"
              className={AUTH_INPUT_CLASS}
            />
          </div>


           <div>
            <p className="mb-2 text-sm font-medium text-gray-700">
              Số điện thoại
            </p> 

            <InputField
              id={4}
              value={formValues.numberPhone}
              onChange={handleChangeInput}
              name="numberPhone"
              label="Số điện thoại"
              type="text"
              className={AUTH_INPUT_CLASS}
            />
          </div>
          
          {errorMessage && (
              <p className="text-red-700 bg-red-100 p-3 rounded-md text-sm font-medium text-center">
                {errorMessage}
              </p>
          )}

          <button
            type="submit"
            className={AUTH_PRIMARY_BUTTON_CLASS}
          >
            Register
          </button>
        </div>
      </form>
    </div>
  );
});

export default RegisterForm;