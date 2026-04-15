
const StateMessage = ({ message, isError }) => (
    <div className='flex-1 bg-gray-50 flex items-center justify-center h-full'>
        <p className={`text-lg ${isError ? 'text-red-600' : 'text-gray-600'}`}>{message}</p>
    </div>
);

export default StateMessage;