import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const navigate = useNavigate();

  const [form, setForm] = useState({ username: "", password: "" });
  const [errors, setErrors] = useState({ username: "", password: "" });

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" }); // clear error on type
  };

  const handleLogin = () => {
    const newErrors = {};
    if (!form.username) newErrors.username = "Username is required";
    if (!form.password) newErrors.password = "Password is required";

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    localStorage.setItem("token", "dummy-token");
    navigate("/dashboard");
  };

  return (
    <div
      className="flex items-center justify-center min-h-screen"
      style={{ backgroundColor: "#eef2f7", fontFamily: "Arial, sans-serif" }}
    >

      {/* Card */}
      <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-sm">

        {/* Logo / Brand */}
        <div className="text-center mb-6">
          <div
            className="inline-flex items-center justify-center w-12 h-12 rounded-full text-white font-bold text-xl mb-3"
            style={{ backgroundColor: "#1B4F8A" }}
          >
            P
          </div>
          <h2 className="text-sm font-medium text-gray-500 uppercase tracking-wide">
            Policy Lifecycle Manager
          </h2>
          <h1 className="text-2xl font-bold text-gray-800 mt-1">
            Sign in to your account
          </h1>
        </div>

        {/* Username */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Username
          </label>
          <input
            type="text"
            name="username"
            placeholder="Enter username"
            value={form.username}
            onChange={handleChange}
            className={`border w-full p-3 rounded-lg focus:outline-none focus:ring-2 ${
              errors.username
                ? "border-red-500 focus:ring-red-300"
                : "border-gray-300 focus:ring-blue-300"
            }`}
            style={{ minHeight: "44px" }}
          />
          {errors.username && (
            <p className="text-red-500 text-xs mt-1">{errors.username}</p>
          )}
        </div>

        {/* Password */}
        <div className="mb-6">
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>
          <input
            type="password"
            name="password"
            placeholder="Enter password"
            value={form.password}
            onChange={handleChange}
            className={`border w-full p-3 rounded-lg focus:outline-none focus:ring-2 ${
              errors.password
                ? "border-red-500 focus:ring-red-300"
                : "border-gray-300 focus:ring-blue-300"
            }`}
            style={{ minHeight: "44px" }}
          />
          {errors.password && (
            <p className="text-red-500 text-xs mt-1">{errors.password}</p>
          )}
        </div>

        {/* Submit */}
        <button
          onClick={handleLogin}
          className="w-full text-white py-3 rounded-lg font-semibold hover:opacity-90 transition"
          style={{ backgroundColor: "#1B4F8A", minHeight: "44px" }}
        >
          Login
        </button>

      </div>
    </div>
  );
}
