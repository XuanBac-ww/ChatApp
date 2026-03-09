import { AlertCircle, CheckCircle, X } from 'lucide-react';

const RequestToast = ({ toast, onClose }) => {
    if (!toast.show) {
        return null;
    }

    const toastClassName = toast.type === 'success' ? 'bg-green-500' : 'bg-red-500';

    return (
        <div className={`fixed bottom-5 right-5 z-50 flex translate-y-0 transform items-center gap-3 rounded-lg px-6 py-3 text-white shadow-lg transition-all duration-500 ${toastClassName}`}>
            {toast.type === 'success' ? <CheckCircle size={24} /> : <AlertCircle size={24} />}
            <span className="font-medium">{toast.message}</span>
            <button onClick={onClose} className="ml-2 opacity-80 hover:opacity-100" type="button">
                <X size={20} />
            </button>
        </div>
    );
};

export default RequestToast;
