import { MoveRight } from "lucide-react";

const MainImage = () => {
    return (  
        <main className="mt-16 md:mt-24 flex flex-col md:flex-row items-center justify-between gap-12">
          {/* Left Side: Text Content */}
          <div className="w-full md:w-1/2 text-center md:text-left">
            <h1 className="text-5xl md:text-6xl font-extrabold text-gray-900 leading-tight mb-6">
              MOBILE CHAT <br /> APPLICATION
            </h1>
            <p className="text-lg text-gray-600 mb-8 max-w-md mx-auto md:mx-0">
              Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
              eiusmod tempor incididunt ut labore et dolore magna aliqua.
            </p>
            <button className="px-8 py-4 bg-purple-600 text-white font-semibold rounded-full shadow-lg hover:bg-purple-700 transition-all duration-300 flex items-center justify-center md:justify-start mx-auto md:mx-0">
              GET STARTED
              <MoveRight className="ml-3 h-5 w-5" />
            </button>
          </div>

          <div className="w-full md:w-1/2 relative flex justify-center items-center">
            <img 
                src="/src/assets/digital-connection-technology-social-media.jpg" 
                alt="Mobile Chat Application Illustration" 
                className="max-w-full h-auto rounded-lg shadow-2xl"
            />
          </div>
        </main>
    );
}
 
export default MainImage;