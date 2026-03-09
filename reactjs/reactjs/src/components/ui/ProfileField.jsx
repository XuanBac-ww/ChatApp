
const ProfileField = ({ label, name, value, isEditing, onChange, readOnly = false }) => {
    return (
        <div>
            <label className='text-sm font-medium text-gray-500 block mb-1'>{label}</label>
            {isEditing && !readOnly ? (
                <input
                    type="text"
                    name={name}
                    value={value}
                    onChange={onChange}
                    className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm transition-colors"
                />
            ) : (
                <p className='text-gray-900 font-medium py-2'>{value}</p>
            )}
        </div>
    );
};

export default ProfileField;