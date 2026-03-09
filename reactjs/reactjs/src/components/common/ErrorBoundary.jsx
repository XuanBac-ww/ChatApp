import { Component } from 'react';

class ErrorBoundary extends Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false };
    }

    static getDerivedStateFromError() {
        return { hasError: true };
    }

    componentDidCatch() {
        // Intentionally silent in UI; can be wired to logging service later.
    }

    handleReload = () => {
        window.location.reload();
    };

    render() {
        if (this.state.hasError) {
            return (
                <div className="flex min-h-screen items-center justify-center bg-gray-50 p-6">
                    <div className="w-full max-w-md rounded-lg border border-red-200 bg-white p-6 text-center shadow-sm">
                        <h2 className="mb-2 text-xl font-semibold text-red-600">Đã xảy ra lỗi ngoài ý muốn</h2>
                        <p className="mb-4 text-sm text-gray-600">
                            Vui lòng tải lại trang để tiếp tục.
                        </p>
                        <button
                            type="button"
                            onClick={this.handleReload}
                            className="rounded-md bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
                        >
                            Tải lại
                        </button>
                    </div>
                </div>
            );
        }

        return this.props.children;
    }
}

export default ErrorBoundary;
