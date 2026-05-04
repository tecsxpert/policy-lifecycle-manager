import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { getPolicyStats } from "../services/api";
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid,
  Cell, ResponsiveContainer, Legend
} from "recharts";

const BAR_COLORS = ["#22c55e", "#eab308", "#ef4444"];

const KpiSkeleton = () => (
  <div className="bg-gray-200 animate-pulse p-6 rounded-lg shadow h-28" />
);

const ChartSkeleton = () => (
  <div className="bg-white rounded-lg shadow p-6">
    <div className="h-6 bg-gray-200 rounded animate-pulse w-48 mb-4" />
    <div className="h-64 bg-gray-100 rounded animate-pulse" />
  </div>
);

export default function Dashboard() {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await getPolicyStats();
        setStats(data);
      } catch (err) {
        setError("Failed to load stats. Please try again.");
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const total = stats?.totalPolicies || 0;
  const active = stats?.totalActivePolicies || 0;
  const pending = total - active;

  const chartData = [
    { name: "Active", value: active },
    { name: "Pending", value: pending },
  ];

  const kpiCards = [
    { label: "TOTAL POLICIES", value: total, color: "#1B4F8A" },
    { label: "ACTIVE", value: active, color: "#22c55e" },
    { label: "PENDING", value: pending, color: "#eab308" },
  ];

  return (
    <>
      <Navbar />
      <div className="p-6 min-h-screen bg-gray-100" style={{ fontFamily: "Arial, sans-serif" }}>

        <h1 className="text-3xl font-bold mb-6 text-center text-gray-800">
          Dashboard
        </h1>

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-4">
            <p className="text-red-600">{error}</p>
          </div>
        )}

        {/* KPI Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          {loading ? (
            <>
              <KpiSkeleton />
              <KpiSkeleton />
              <KpiSkeleton />
            </>
          ) : (
            kpiCards.map((card, i) => (
              <div
                key={i}
                className="text-white p-6 rounded-lg shadow"
                style={{ backgroundColor: card.color }}
              >
                <h2 className="text-sm font-medium uppercase tracking-wide opacity-80">
                  {card.label}
                </h2>
                <p className="text-4xl font-bold mt-2">{card.value}</p>
              </div>
            ))
          )}
        </div>

        {/* Chart */}
        {loading ? (
          <ChartSkeleton />
        ) : (
          <div className="bg-white p-6 rounded-lg shadow">
            <h2 className="text-xl font-bold mb-4 text-gray-800">
              Policy Status Overview
            </h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Legend />
                <Bar dataKey="value" name="Policies" radius={[6, 6, 0, 0]}>
                  {chartData.map((_, index) => (
                    <Cell key={index} fill={BAR_COLORS[index]} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}

      </div>
    </>
  );
}