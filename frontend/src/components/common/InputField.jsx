import { memo } from 'react'; 

const InputField = memo(({
    id,
    value,
    onChange,
    name,
    label,
    type,
    className = "w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
}) => {

    console.log(1);

    return (
        <input 
            id={id} 
            value={value} 
            onChange={onChange} 
            name={name} 
            placeholder={label} 
            type={type}
            className={className}
        />
    );
});

export default InputField;