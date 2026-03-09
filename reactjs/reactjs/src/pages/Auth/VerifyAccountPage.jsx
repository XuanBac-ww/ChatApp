import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { verifyOtp } from '../../service/authService';
import {
  AUTH_CARD_CLASS,
  AUTH_DISABLED_BUTTON_CLASS,
  AUTH_INPUT_CLASS,
  AUTH_PAGE_WRAPPER_CLASS,
  AUTH_PRIMARY_BUTTON_CLASS
} from '../../utils/authUiClasses';

const VerifyAccountPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState(location.state?.email || ""); 
  const [otp, setOtp] = useState("");
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const handleVerify = async (e) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const response = await verifyOtp({ email, otp });
      if (response && response.success === false) {
          throw new Error(response.message || "Xác thực thất bại");
      }
      setIsSuccess(true);
      setTimeout(() => navigate("/login"), 1200);
      
    } catch (err) {
      setError(err.message || "Đã có lỗi xảy ra. Vui lòng thử lại.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={`${AUTH_PAGE_WRAPPER_CLASS} flex items-center justify-center`}>
      <div className={AUTH_CARD_CLASS}>
        <h2 className="text-2xl font-bold text-center text-gray-800 mb-6">Xác thực tài khoản</h2>
        
        <p className="text-sm text-gray-600 mb-6 text-center">
          Vui lòng nhập mã OTP đã được gửi đến email: <br/>
          <span className="font-semibold text-indigo-600">{email}</span>
        </p>

        <form onSubmit={handleVerify} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className={`${AUTH_INPUT_CLASS} bg-gray-50 text-gray-500`}
              readOnly={!!location.state?.email}
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Mã OTP</label>
            <input
              type="text"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              placeholder="Nhập mã 6 số"
              className={AUTH_INPUT_CLASS}
              required
            />
          </div>

          {/* Hiển thị lỗi */}
          {error && (
            <div className="text-red-600 text-sm text-center bg-red-50 p-2 rounded border border-red-200">
              {error}
            </div>
          )}
          {isSuccess && (
            <div className="rounded border border-green-200 bg-green-50 p-2 text-center text-sm text-green-700">
              Xác thực thành công, đang chuyển sang trang đăng nhập...
            </div>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className={isLoading ? AUTH_DISABLED_BUTTON_CLASS : AUTH_PRIMARY_BUTTON_CLASS}
          >
            {isLoading ? "Đang xử lý..." : "Xác thực"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default VerifyAccountPage;