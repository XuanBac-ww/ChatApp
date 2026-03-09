
const Button = ({ children, onClick, disabled, variant }) => {
    const baseStyle = "font-semibold py-2 px-6 rounded-lg shadow transition duration-200 disabled:opacity-50 disabled:cursor-not-allowed";
    const variants = {
        primary: "bg-blue-600 text-white hover:bg-blue-700",
        secondary: "bg-gray-200 text-gray-800 hover:bg-gray-300"
    };
    return (
        <button onClick={onClick} disabled={disabled} className={`${baseStyle} ${variants[variant]}`}>
            {children}
        </button>
    );
};

export default Button;