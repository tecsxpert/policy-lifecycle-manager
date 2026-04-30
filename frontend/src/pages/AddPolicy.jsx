import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Navbar from "../components/Navbar";

const DEMO_POLICIES = [
  { id: 1, name: "Data Protection Policy", status: "Active", category: "Security", effectiveDate: "2024-01-01", expiryDate: "2025-01-01", description: "Covers all data handling procedures." },
  { id: 2, name: "Remote Work Policy", status: "Pending", category: "HR", effectiveDate: "2024-03-01", expiryDate: "2025-03-01", description: "Guidelines for remote employees." },
  { id: 3, name: "Leave Policy", status: "Active", category: "HR", effectiveDate: "2024-01-15", expiryDate: "2025-01-15", description: "Annual and sick leave rules." },
  { id: 4, name: "IT Security Policy", status: "Expired", category: "Security", effectiveDate: "2023-01-01", expiryDate: "2024-01-01", description: "IT infrastructure security guidelines." },
  { id: 5, name: "Travel Reimbursement", status: "Active", category: "Finance", effectiveDate: "2024-02-01", expiryDate: "2025-02-01", description: "Business travel expense rules." },
];

export default function AddPolicy() {
  const navigate = useNavigate();
  const location = useLocation();
  const editing = location.state;

  const [form, setForm] = useState({
    id: editing?.id || null,
    name: editing?.name || "",
    category: editing?.category || "",
    status: editing?.status || "",
    effectiveDate: editing?.effectiveDate || "",
    expiryDate: editing?.expiryDate || "",
    description: editing?.description || "",
  });

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
  };

  const validate = () => {
    const e = {};
    if (!form.name.trim()) e.name = "Policy name is required";
    if (!form.category) e.category = "Category is required";
    if (!form.status) e.status = "Status is required";
    if (!form.effectiveDate) e.effectiveDate = "Effective date is required";
    if (!form.expiryDate) e.expiryDate = "Expiry date is required";
    if (!form.description.trim()) e.description = "Description is required";
    if (form.effectiveDate && form.expiryDate && form.expiryDate <= form.effectiveDate) {
      e.expiryDate = "Expiry date must be after effective date";
    }
    return e;
  };

  const handleSubmit = () => {
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }
    const existing = JSON.parse(localStorage.getItem("policies") || "null") || DEMO_POLICIES;
    let updated;
    if (form.id) {
      updated = existing.map(p => p.id === form.id ? { ...form } : p);
    } else {
      const newId = Math.max(...existing.map(p => p.id), 0) + 1;
      updated = [...existing, { ...form, id: newId }];
    }
    localStorage.setItem("policies", JSON.stringify(updated));
    navigate("/list");
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
            {form.id ? "✏️ Edit Policy" : "➕ Add New Policy"}
          </h1>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

            {/* Policy Name */}
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Policy Name *</label>
              <input
                type="text"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="Enter policy name"
                className={inputClass("name")}
                style={{ minHeight: "44px" }}
              />
              {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name}</p>}
            </div>

            {/* Category */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Category *</label>
              <select
                name="category"
                value={form.category}
                onChange={handleChange}
                className={inputClass("category")}
                style={{ minHeight: "44px" }}
              >
                <option value="">Select Category</option>
                <option value="HR">HR</option>
                <option value="Security">Security</option>
                <option value="Finance">Finance</option>
                <option value="Operations">Operations</option>
                <option value="Legal">Legal</option>
              </select>
              {errors.category && <p className="text-red-500 text-xs mt-1">{errors.category}</p>}
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
                <option value="Expired">Expired</option>
              </select>
              {errors.status && <p className="text-red-500 text-xs mt-1">{errors.status}</p>}
            </div>

            {/* Effective Date */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Effective Date *</label>
              <input
                type="date"
                name="effectiveDate"
                value={form.effectiveDate}
                onChange={handleChange}
                className={inputClass("effectiveDate")}
                style={{ minHeight: "44px" }}
              />
              {errors.effectiveDate && <p className="text-red-500 text-xs mt-1">{errors.effectiveDate}</p>}
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

            {/* Description */}
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Description *</label>
              <textarea
                name="description"
                value={form.description}
                onChange={handleChange}
                rows={3}
                placeholder="Brief description of this policy..."
                className={`border w-full p-3 rounded-lg focus:outline-none focus:ring-2 resize-none ${
                  errors.description ? "border-red-500 focus:ring-red-300" : "border-gray-300 focus:ring-blue-300"
                }`}
              />
              {errors.description && <p className="text-red-500 text-xs mt-1">{errors.description}</p>}
            </div>

          </div>

          {/* Buttons */}
          <div className="flex gap-3 mt-6">
            <button
              onClick={handleSubmit}
              className="text-white px-6 py-3 rounded-lg font-semibold hover:opacity-90 flex-1"
              style={{ backgroundColor: "#1B4F8A", minHeight: "44px" }}
            >
              {form.id ? "Update Policy" : "Add Policy"}
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
