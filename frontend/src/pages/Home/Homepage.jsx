import { Link } from "react-router-dom";
import Menu from "../../components/layout/Menu";
import Logo from "../../components/layout/Logo";
import MainImage from "../../components/layout/MainImage";

const Homepage = () => {
  return (
    <div className="min-h-screen bg-white relative overflow-hidden">
      <div className="absolute top-0 left-0 right-0 h-40 bg-purple-100 opacity-50 z-0"></div>
      <div className="absolute bottom-0 left-0 right-0 h-40 bg-purple-100 opacity-50 z-0"></div>
      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        
        <header className="flex items-center justify-between py-4">
          <Logo />
          
          <Menu />

          <div className="flex items-center space-x-4">

            <Link
              to="/signup" 
              className="px-5 py-2 border border-purple-600 text-purple-600 rounded-full hover:bg-purple-50 transition-colors duration-200">
              Sign Up
            </Link>
            <Link
              to="/login"
              className="px-5 py-2 bg-purple-600 text-white rounded-full hover:bg-purple-700 transition-colors duration-200"
            >
              Sign In
            </Link>
          </div>
        </header>

        <MainImage />
      </div>
    </div>
  );
};

export default Homepage;