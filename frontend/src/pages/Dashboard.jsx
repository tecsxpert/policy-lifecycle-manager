import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid,
  Cell, ResponsiveContainer, Legend
} from "recharts";

// Shared demo data — in real app this comes from API
export const DEMO_POLICIES = [
  { id: 1, name: "Data Protection Policy", status: "Active", category: "Security", effectiveDate: "2024-01-01", expiryDate: "2025-01-01", description: "Covers all data handling procedures." },
  { id: 2, name: "Remote Work Policy", status: "Pending", category: "HR", effectiveDate: "2024-03-01", expiryDate: "2025-03-01", description: "Guidelines for remote employees." },
  { id: 3, name: "Leave Policy", status: "Active", category: "HR", effectiveDate: "2024-01-15", expiryDate: "2025-01-15", description: "Annual and sick leave rules." },
  { id: 4, name: "IT Security Policy", status: "Expired", category: "Security", effectiveDate: "2023-01-01", expiryDate: "2024-01-01", description: "IT infrastructure security guidelines." },
  { id: 5, name: "Travel Reimbursement", status: "Active", category: "Finance", effectiveDate: "2024-02-01", expiryDate: "2025-02-01", description: "Business travel expense rules." },
];

export default function Dashboard() {
  const navigate = useNavigate();

  const policies = JSON.parse(localStorage.getItem("policies") || "null") || DEMO_POLICIES;

  const total = policies.length;
  const active = policies.filter(p => p.status === "Active").length;
  const pending = policies.filter(p => p.status === "Pending").length;
  const expired = policies.filter(p => p.status === "Expired").length;

  const chartData = [
    { name: "Active", value: active },
    { name: "Pending", value: pending },
    { name: "Expired", value: expired },
  ];

  const BAR_COLORS = ["#22c55e", "#eab308", "#ef4444"];

  return (
    <>
      <Navbar />

      <div className="p-6 min-h-screen bg-gray-100" style={{ fontFamily: "Arial, sans-serif" }}>

        <h1 className="text-3xl font-bold mb-6 text-center text-gray-800">
          Dashboard
        </h1>

        {/* 4 KPI Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">

          <div
            className="text-white p-6 rounded-lg shadow"
            style={{ backgroundColor: "#1B4F8A" }}
          >
            <h2 className="text-sm font-medium uppercase tracking-wide opacity-80">
              Total Policies
            </h2>
            <p className="text-4xl font-bold mt-2">{total}</p>
          </div>

          <div className="bg-green-500 text-white p-6 rounded-lg shadow">
            <h2 className="text-sm font-medium uppercase tracking-wide opacity-80">
              Active
            </h2>
            <p className="text-4xl font-bold mt-2">{active}</p>
          </div>

          <div className="bg-yellow-500 text-white p-6 rounded-lg shadow">
            <h2 className="text-sm font-medium uppercase tracking-wide opacity-80">
              Pending
            </h2>
            <p className="text-4xl font-bold mt-2">{pending}</p>
          </div>

          <div className="bg-red-500 text-white p-6 rounded-lg shadow">
            <h2 className="text-sm font-medium uppercase tracking-wide opacity-80">
              Expired
            </h2>
            <p className="text-4xl font-bold mt-2">{expired}</p>
          </div>

        </div>

        {/* Bar Chart — full width */}
        <div className="bg-white p-6 rounded-lg shadow">
          <h2 className="text-xl font-bold mb-4 text-gray-800">
            Policy Status Overview
          </h2>

          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={chartData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis allowDecimals={false} />
              <Tooltip />
              <Legend />
              <Bar dataKey="value" name="Policies" radius={[6, 6, 0, 0]}>
                {chartData.map((entry, index) => (
                  <Cell key={index} fill={BAR_COLORS[index]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

      </div>
    </>
  );
}
