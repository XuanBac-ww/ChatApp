import { useState, useEffect } from 'react'; 
import { jwtDecode } from 'jwt-decode';
import { useNavigate } from 'react-router-dom'; 
import { AuthContext } from './AuthContextValue';

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const navigate = useNavigate(); 

    useEffect(() => {
        const token = localStorage.getItem("access_token"); 
        
        if(token) {
            try {
                const decodedUser = jwtDecode(token); 
                setUser(decodedUser); 
            } catch {
                localStorage.removeItem("access_token");
            }
        }
    
    }, []);

    const logout = () => {
        localStorage.removeItem("access_token");
        localStorage.removeItem("refreshToken"); 
        
        setUser(null);
        
        navigate("/login");
    };

    return (
       
        <AuthContext.Provider value={{ user, setUser, logout }}>
            {children}
        </AuthContext.Provider>
    );
}