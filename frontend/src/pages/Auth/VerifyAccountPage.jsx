import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { verifyOtp } from '../../service/authService';

const VerifyAccountPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [email, setEmail] = useState(location.state?.email || ""); 
  const [otp, setOtp] = useState("");
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleVerify = async (e) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      // 1. Gọi API
      const response = await verifyOtp({ email, otp });
      if (response && response.success === false) {
          throw new Error(response.message || "Xác thực thất bại");
      }
     
      alert("Xác thực thành công! Vui lòng đăng nhập.");
      navigate("/login"); 
      
    } catch (err) {
      
      console.error("Lỗi xác thực:", err);
      setError(err.message || "Đã có lỗi xảy ra. Vui lòng thử lại.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 p-4">
      <div className="w-full max-w-md bg-white rounded-lg shadow-xl p-8">
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
              className="w-full px-4 py-2 border border-gray-300 rounded-md bg-gray-50 focus:outline-none text-gray-500"
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
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-indigo-500"
              required
            />
          </div>

          {/* Hiển thị lỗi */}
          {error && (
            <div className="text-red-600 text-sm text-center bg-red-50 p-2 rounded border border-red-200">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className={`w-full px-6 py-3 text-white font-semibold rounded-md shadow-md transition-all duration-300
              ${isLoading 
                ? 'bg-gray-400 cursor-not-allowed' 
                : 'bg-gradient-to-r from-purple-500 to-indigo-600 hover:from-purple-600 hover:to-indigo-700 focus:ring-2 focus:ring-purple-500'
              }`}
          >
            {isLoading ? "Đang xử lý..." : "Xác thực"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default VerifyAccountPage;