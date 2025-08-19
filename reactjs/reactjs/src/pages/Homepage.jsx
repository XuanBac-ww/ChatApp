import { Link } from "react-router-dom";

const Homepage = () => {
  return (
    <div className="p-4">
      <h1 className="text-3xl font-bold underline mb-4">
        Welcome to the Homepage!
      </h1>
      <Link 
        to="/login" 
        className="text-blue-600 hover:underline"
      >
        Đăng nhập
      </Link>
    </div>
  );
};

export default Homepage;
