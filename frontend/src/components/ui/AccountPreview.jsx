
import { memo } from 'react';

const AccountPreview = ({ avatar, name, isActive }) => {

    const statusText = isActive ? 'OnLine' : 'Offline';
    const statusColor = isActive ? 'text-green-500' : 'text-gray-400';

    console.log("Props nhận được:", { avatar, name, isActive }); 

    return (
        <div className="p-4 border-t border-gray-200 ">
            <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                    <img 
                        src={avatar} 
                        alt="Avatar" 
                        className="w-10 h-10 rounded-full" 
                    />
                    <div>
                        <p className="font-semibold text-gray-900">{name}</p>
                        
                        <p className={`text-sm ${statusColor}`}>
                            {statusText}
                        </p>
                    </div>
                </div>
            </div>
        </div> 
    );
}

export default memo(AccountPreview);