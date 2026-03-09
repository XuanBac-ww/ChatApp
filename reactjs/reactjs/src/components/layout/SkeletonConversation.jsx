import React from 'react';

const SkeletonConversation = () => {
    return (
        <div className="flex items-center p-3 animate-pulse">
            {/* Avatar skeleton */}
            <div className="w-11 h-11 bg-gray-200 rounded-full shrink-0"></div>
            
            {/* Text skeleton */}
            <div className="ml-3 flex-1 space-y-2">
                <div className="h-3 bg-gray-200 rounded w-2/3"></div>
                <div className="h-2 bg-gray-200 rounded w-1/2"></div>
            </div>
        </div>
    );
};

export default SkeletonConversation;