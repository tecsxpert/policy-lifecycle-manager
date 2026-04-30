import { useState, useEffect } from "react";
import Navbar from "../components/Navbar";
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid,
  ResponsiveContainer, LineChart, Line, PieChart, Pie,
  Cell, Legend
} from "recharts";

const DEMO_POLICIES = [
  { id: 1, name: "Data Protection Policy", status: "Active", category: "Security", effectiveDate: "2024-01-01", expiryDate: "2025-01-01", description: "Covers all data handling procedures." },
  { id: 2, name: "Remote Work Policy", status: "Pending", category: "HR", effectiveDate: "2024-03-01", expiryDate: "2025-03-01", description: "Guidelines for remote employees." },
  { id: 3, name: "Leave Policy", status: "Active", category: "HR", effectiveDate: "2024-01-15", expiryDate: "2025-01-15", description: "Annual and sick leave rules." },
  { id: 4, name: "IT Security Policy", status: "Expired", category: "Security", effectiveDate: "2023-01-01", expiryDate: "2024-01-01", description: "IT infrastructure security guidelines." },
  { id: 5, name: "Travel Reimbursement", status: "Active", category: "Finance", effectiveDate: "2024-02-01", expiryDate: "2025-02-01", description: "Business travel expense rules." },
];

const COLORS = ["#22c55e", "#eab308", "#ef4444", "#3b82f6", "#a855f7"];

export default function Analytics() {
  const [policies, setPolicies] = useState([]);

  useEffect(() => {
    try {
      const stored = JSON.parse(localStorage.getItem("policies") || "null");
      setPolicies(stored && stored.length > 0 ? stored : DEMO_POLICIES);
    } catch {
      setPolicies(DEMO_POLICIES);
    }
  }, []);

  const categoryCount = policies.reduce((acc, p) => {
    acc[p.category] = (acc[p.category] || 0) + 1;
    return acc;
  }, {});
  const categoryData = Object.entries(categoryCount).map(([name, value]) => ({ name, value }));

  const statusCount = policies.reduce((acc, p) => {
    acc[p.status] = (acc[p.status] || 0) + 1;
    return acc;
  }, {});
  const pieData = Object.entries(statusCount).map(([name, value]) => ({ name, value }));

  const now = new Date();
  const lineData = [...Array(6)].map((_, i) => {
    const d = new Date(now.getFullYear(), now.getMonth() - (5 - i), 1);
    const label = d.toLocaleString("default", { month: "short", year: "2-digit" });
    const count = policies.filter(p => {
      try {
        const created = new Date(p.effectiveDate);
        return created.getFullYear() === d.getFullYear() && created.getMonth() === d.getMonth();
      } catch { return false; }
    }).length;
    return { month: label, policies: count };
  });

  if (policies.length === 0) {
    return (
      <>
        <Navbar />
        <div className="flex items-center justify-center min-h-screen">
          <p className="text-gray-400 text-lg">Loading analytics...</p>
        </div>
      </>
    );
  }

  return (
    <>
      <Navbar />
      <div className="min-h-screen bg-gray-100 p-6" style={{ fontFamily: "Arial, sans-serif" }}>
        <h1 className="text-3xl font-bold mb-6 text-gray-800">📊 Analytics</h1>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

          {/* Bar Chart */}
          <div className="bg-white rounded-xl shadow p-6">
            <h2 className="text-lg font-bold mb-4 text-gray-800">Policies by Category</h2>
            <ResponsiveContainer width="100%" height={260}>
              <BarChart data={categoryData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="value" name="Policies" radius={[6, 6, 0, 0]}>
                  {categoryData.map((_, i) => (
                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>

          {/* Pie Chart */}
          <div className="bg-white rounded-xl shadow p-6">
            <h2 className="text-lg font-bold mb-4 text-gray-800">Policies by Status</h2>
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie
                  data={pieData}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={90}
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                >
                  {pieData.map((_, i) => (
                    <Cell key={i} fill={COLORS[i % COLORS.length]} />
                  ))}
                </Pie>
                <Legend />
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </div>

          {/* Line Chart */}
          <div className="bg-white rounded-xl shadow p-6 lg:col-span-2">
            <h2 className="text-lg font-bold mb-4 text-gray-800">Policies Added (Last 6 Months)</h2>
            <ResponsiveContainer width="100%" height={260}>
              <LineChart data={lineData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Legend />
                <Line
                  type="monotone"
                  dataKey="policies"
                  stroke="#1B4F8A"
                  strokeWidth={3}
                  dot={{ r: 5 }}
                  activeDot={{ r: 8 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>

        </div>
      </div>
    </>
  );
}
