import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Component } from "react";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import List from "./pages/List";
import AddPolicy from "./pages/AddPolicy";
import Detail from "./pages/Detail";
import Analytics from "./pages/Analytics";

// ✅ Error Boundary Class
class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, info) {
    console.error("Error caught:", error, info);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div
          style={{ fontFamily: "Arial, sans-serif" }}
          className="flex items-center justify-center min-h-screen bg-gray-100"
        >
          <div className="bg-white p-8 rounded-xl shadow text-center max-w-md">
            <div className="text-5xl mb-4">⚠️</div>
            <h1 className="text-xl font-bold text-gray-800 mb-2">
              Something went wrong
            </h1>
            <p className="text-gray-500 mb-6">
              An unexpected error occurred. Please try again.
            </p>
            <button
              onClick={() => window.location.href = "/dashboard"}
              className="text-white px-6 py-3 rounded-lg font-semibold hover:opacity-90"
              style={{ backgroundColor: "#1B4F8A", minHeight: "44px" }}
            >
              Go to Dashboard
            </button>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}

// ✅ Protected Route
function ProtectedRoute({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/" />;
}

// ✅ App
function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* Login - no protection needed */}
        <Route path="/" element={<Login />} />

        {/* Dashboard */}
        <Route path="/dashboard" element={
          <ProtectedRoute>
            <ErrorBoundary>
              <Dashboard />
            </ErrorBoundary>
          </ProtectedRoute>
        } />

        {/* Policy List */}
        <Route path="/list" element={
          <ProtectedRoute>
            <ErrorBoundary>
              <List />
            </ErrorBoundary>
          </ProtectedRoute>
        } />

        {/* Add / Edit Policy */}
        <Route path="/add" element={
          <ProtectedRoute>
            <ErrorBoundary>
              <AddPolicy />
            </ErrorBoundary>
          </ProtectedRoute>
        } />

        {/* Policy Detail */}
        <Route path="/detail/:id" element={
          <ProtectedRoute>
            <ErrorBoundary>
              <Detail />
            </ErrorBoundary>
          </ProtectedRoute>
        } />

        {/* Analytics */}
        <Route path="/analytics" element={
          <ProtectedRoute>
            <ErrorBoundary>
              <Analytics />
            </ErrorBoundary>
          </ProtectedRoute>
        } />

      </Routes>
    </BrowserRouter>
  );
}

export default App;