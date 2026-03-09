import { Pencil } from "lucide-react";


const InfoCard =({ title, children }) => {
    
    return(
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-semibold text-gray-800">
                    {title}
                </h3>
                <button className="text-gray-500 hover:text-blue-600">
                    <Pencil size={18} />
                </button>
            </div>
            <div className="space-x-4">
                {children}
            </div>
        </div>  
    )
}

export default InfoCard;