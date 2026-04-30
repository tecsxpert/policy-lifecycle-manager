import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Navbar from "../components/Navbar";

const DEMO_POLICIES = [
  { id: 1, name: "Data Protection Policy", status: "Active", category: "Security", effectiveDate: "2024-01-01", expiryDate: "2025-01-01", description: "Covers all data handling procedures." },
  { id: 2, name: "Remote Work Policy", status: "Pending", category: "HR", effectiveDate: "2024-03-01", expiryDate: "2025-03-01", description: "Guidelines for remote employees." },
  { id: 3, name: "Leave Policy", status: "Active", category: "HR", effectiveDate: "2024-01-15", expiryDate: "2025-01-15", description: "Annual and sick leave rules." },
  { id: 4, name: "IT Security Policy", status: "Expired", category: "Security", effectiveDate: "2023-01-01", expiryDate: "2024-01-01", description: "IT infrastructure security guidelines." },
  { id: 5, name: "Travel Reimbursement", status: "Active", category: "Finance", effectiveDate: "2024-02-01", expiryDate: "2025-02-01", description: "Business travel expense rules." },
];

export default function Detail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [policy, setPolicy] = useState(null);
  const [aiResult, setAiResult] = useState(null);
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState(false);

  useEffect(() => {
    const stored = JSON.parse(localStorage.getItem("policies") || "null") || DEMO_POLICIES;
    const found = stored.find(p => String(p.id) === String(id));
    setPolicy(found || null);
  }, [id]);

  const handleDelete = () => {
    if (!window.confirm("Delete this policy?")) return;
    const stored = JSON.parse(localStorage.getItem("policies") || "null") || DEMO_POLICIES;
    const updated = stored.filter(p => String(p.id) !== String(id));
    localStorage.setItem("policies", JSON.stringify(updated));
    navigate("/list");
  };

  const handleAskAI = async () => {
    setAiLoading(true);
    setAiError(false);
    setAiResult(null);
    try {
      // Replace with real API call later:
      // const res = await axios.post("http://localhost:5000/recommend", { policy_name: policy.name })
      await new Promise(r => setTimeout(r, 1500));
      setAiResult({
        recommendations: [
          { action_type: "Review", description: "Schedule a quarterly review of this policy with all stakeholders.", priority: "High" },
          { action_type: "Update", description: "Ensure compliance with the latest regulatory framework updates.", priority: "Medium" },
          { action_type: "Communicate", description: "Send updated policy summary to all relevant team members.", priority: "Low" },
        ]
      });
    } catch {
      setAiError(true);
    } finally {
      setAiLoading(false);
    }
  };

  const statusColor = (s) => {
    if (s === "Active") return "bg-green-500";
    if (s === "Pending") return "bg-yellow-500";
    if (s === "Expired") return "bg-red-500";
    return "bg-gray-400";
  };

  const priorityColor = (p) => {
    if (p === "High") return "text-red-600 font-semibold";
    if (p === "Medium") return "text-yellow-600 font-semibold";
    return "text-green-600 font-semibold";
  };

  if (!policy) return (
    <>
      <Navbar />
      <div className="flex items-center justify-center min-h-screen text-gray-500">
        Policy not found.
        <button onClick={() => navigate("/list")} className="ml-2 underline text-blue-600">Go back</button>
      </div>
    </>
  );

  return (
    <>
      <Navbar />
      <div className="min-h-screen bg-gray-100 p-6" style={{ fontFamily: "Arial, sans-serif" }}>
        <div className="max-w-3xl mx-auto">

          {/* Back */}
          <button
            onClick={() => navigate("/list")}
            className="text-gray-600 hover:text-gray-900 mb-4 flex items-center gap-1 text-sm"
          >
            ← Back to List
          </button>

          {/* Policy Card */}
          <div className="bg-white rounded-xl shadow p-6 mb-4">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h1 className="text-2xl font-bold text-gray-800">{policy.name}</h1>
                <p className="text-gray-500 text-sm mt-1">Policy ID: #{policy.id}</p>
              </div>
              <span className={`px-4 py-1 rounded-full text-white text-sm font-semibold ${statusColor(policy.status)}`}>
                {policy.status}
              </span>
            </div>

            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <p className="text-gray-500 font-medium">Category</p>
                <p className="text-gray-800 font-semibold">{policy.category}</p>
              </div>
              <div>
                <p className="text-gray-500 font-medium">Effective Date</p>
                <p className="text-gray-800 font-semibold">{policy.effectiveDate}</p>
              </div>
              <div>
                <p className="text-gray-500 font-medium">Expiry Date</p>
                <p className="text-gray-800 font-semibold">{policy.expiryDate}</p>
              </div>
              <div className="col-span-2">
                <p className="text-gray-500 font-medium">Description</p>
                <p className="text-gray-800">{policy.description}</p>
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={() => navigate("/add", { state: policy })}
                className="text-white px-5 py-2 rounded-lg font-semibold hover:opacity-90"
                style={{ backgroundColor: "#1B4F8A", minHeight: "44px" }}
              >
                ✏️ Edit
              </button>
              <button
                onClick={handleDelete}
                className="bg-red-500 hover:bg-red-600 text-white px-5 py-2 rounded-lg font-semibold"
                style={{ minHeight: "44px" }}
              >
                🗑️ Delete
              </button>
            </div>
          </div>

          {/* AI Analysis Card */}
          <div className="bg-white rounded-xl shadow p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-bold text-gray-800">🤖 AI Analysis</h2>
              <button
                onClick={handleAskAI}
                disabled={aiLoading}
                className="text-white px-4 py-2 rounded-lg font-semibold hover:opacity-90 disabled:opacity-60"
                style={{ backgroundColor: "#1B4F8A", minHeight: "44px" }}
              >
                {aiLoading ? "Analysing..." : "Ask AI"}
              </button>
            </div>

            {/* Spinner */}
            {aiLoading && (
              <div className="flex items-center gap-3 text-gray-500 py-4">
                <div className="w-6 h-6 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
                <span>Getting AI recommendations...</span>
              </div>
            )}

            {/* Error */}
            {aiError && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-center justify-between">
                <p className="text-red-600 text-sm">AI service unavailable. Please try again.</p>
                <button onClick={handleAskAI} className="text-red-600 underline text-sm">Retry</button>
              </div>
            )}

            {/* Results */}
            {aiResult && !aiLoading && (
              <div className="space-y-3">
                {aiResult.recommendations.map((rec, i) => (
                  <div key={i} className="border border-gray-200 rounded-lg p-4 bg-gray-50">
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-sm font-bold text-gray-700">{rec.action_type}</span>
                      <span className={`text-xs ${priorityColor(rec.priority)}`}>{rec.priority} Priority</span>
                    </div>
                    <p className="text-sm text-gray-600">{rec.description}</p>
                  </div>
                ))}
              </div>
            )}

            {/* Placeholder */}
            {!aiLoading && !aiResult && !aiError && (
              <p className="text-gray-400 text-sm py-4 text-center">
                Click "Ask AI" to get AI-powered recommendations for this policy.
              </p>
            )}
          </div>

        </div>
      </div>
    </>
  );
}