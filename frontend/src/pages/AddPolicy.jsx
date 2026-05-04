import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Navbar from "../components/Navbar";
import { createPolicy, updatePolicy } from "../services/api";

export default function AddPolicy() {
  const navigate = useNavigate();
  const location = useLocation();
  const editing = location.state;

  const [form, setForm] = useState({
    policyName: editing?.policyName || "",
    policyType: editing?.policyType || "",
    status: editing?.status || "",
    policyHolder: editing?.policyHolder || "",
    expiryDate: editing?.expiryDate || "",
  });

  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [serverError, setServerError] = useState("");

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
    setServerError("");
  };

  const validate = () => {
    const e = {};
    if (!form.policyName.trim()) e.policyName = "Policy name is required";
    if (!form.policyType) e.policyType = "Policy type is required";
    if (!form.status) e.status = "Status is required";
    if (!form.policyHolder.trim()) e.policyHolder = "Policy holder is required";
    if (!form.expiryDate) e.expiryDate = "Expiry date is required";
    return e;
  };

  const handleSubmit = async () => {
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setLoading(true);
    try {
      if (editing?.id) {
        await updatePolicy(editing.id, form);
      } else {
        await createPolicy(form);
      }
      navigate("/list");
    } catch (err) {
      setServerError(
        err.response?.data?.message || "Failed to save policy. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const inputClass = (field) =>
    `border w-full p-3 rounded-lg focus:outline-none focus:ring-2 ${
      errors[field] ? "border-red-500 focus:ring-red-300" : "border-gray-300 focus:ring-blue-300"
    }`;

  return (
    <>
      <Navbar />
      <div className="min-h-screen bg-gray-100 p-6" style={{ fontFamily: "Arial, sans-serif" }}>
        <div className="max-w-2xl mx-auto bg-white rounded-xl shadow-lg p-8">

          <h1 className="text-2xl font-bold mb-6 text-gray-800">
            {editing?.id ? "✏️ Edit Policy" : "➕ Add New Policy"}
          </h1>

          {serverError && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-3 mb-4">
              <p className="text-red-600 text-sm">{serverError}</p>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

            {/* Policy Name */}
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Policy Name *</label>
              <input
                type="text"
                name="policyName"
                value={form.policyName}
                onChange={handleChange}
                placeholder="e.g. AutoShield Premium"
                className={inputClass("policyName")}
                style={{ minHeight: "44px" }}
              />
              {errors.policyName && <p className="text-red-500 text-xs mt-1">{errors.policyName}</p>}
            </div>

            {/* Policy Type */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Policy Type *</label>
              <select
                name="policyType"
                value={form.policyType}
                onChange={handleChange}
                className={inputClass("policyType")}
                style={{ minHeight: "44px" }}
              >
                <option value="">Select Type</option>
                <option value="HR">HR</option>
                <option value="Security">Security</option>
                <option value="Finance">Finance</option>
                <option value="Operations">Operations</option>
                <option value="Legal">Legal</option>
                <option value="Auto Insurance">Auto Insurance</option>
                <option value="Health Insurance">Health Insurance</option>
              </select>
              {errors.policyType && <p className="text-red-500 text-xs mt-1">{errors.policyType}</p>}
            </div>

            {/* Status */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Status *</label>
              <select
                name="status"
                value={form.status}
                onChange={handleChange}
                className={inputClass("status")}
                style={{ minHeight: "44px" }}
              >
                <option value="">Select Status</option>
                <option value="Active">Active</option>
                <option value="Pending">Pending</option>
                <option value="COMPLETED">Completed</option>
              </select>
              {errors.status && <p className="text-red-500 text-xs mt-1">{errors.status}</p>}
            </div>

            {/* Policy Holder */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Policy Holder *</label>
              <input
                type="text"
                name="policyHolder"
                value={form.policyHolder}
                onChange={handleChange}
                placeholder="e.g. John Doe"
                className={inputClass("policyHolder")}
                style={{ minHeight: "44px" }}
              />
              {errors.policyHolder && <p className="text-red-500 text-xs mt-1">{errors.policyHolder}</p>}
            </div>

            {/* Expiry Date */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Expiry Date *</label>
              <input
                type="date"
                name="expiryDate"
                value={form.expiryDate}
                onChange={handleChange}
                className={inputClass("expiryDate")}
                style={{ minHeight: "44px" }}
              />
              {errors.expiryDate && <p className="text-red-500 text-xs mt-1">{errors.expiryDate}</p>}
            </div>

          </div>

          {/* Buttons */}
          <div className="flex gap-3 mt-6">
            <button
              onClick={handleSubmit}
              disabled={loading}
              className="text-white px-6 py-3 rounded-lg font-semibold hover:opacity-90 flex-1 disabled:opacity-60"
              style={{ backgroundColor: "#1B4F8A", minHeight: "44px" }}
            >
              {loading ? "Saving..." : editing?.id ? "Update Policy" : "Add Policy"}
            </button>
            <button
              onClick={() => navigate("/list")}
              className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-6 py-3 rounded-lg font-semibold"
              style={{ minHeight: "44px" }}
            >
              Cancel
            </button>
          </div>

        </div>
      </div>
    </>
  );
}