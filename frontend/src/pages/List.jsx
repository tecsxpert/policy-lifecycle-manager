import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { getAllPolicies, searchPolicies, deletePolicy } from "../services/api";

const ITEMS_PER_PAGE = 5;

export default function List() {
  const navigate = useNavigate();
  const [policies, setPolicies] = useState([]);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [sortField, setSortField] = useState("id");
  const [sortDir, setSortDir] = useState("asc");

  const fetchPolicies = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getAllPolicies(page, ITEMS_PER_PAGE);
      setPolicies(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      setError("Failed to load policies. Is the backend running?");
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    if (search.trim() === "") {
      fetchPolicies();
    }
  }, [fetchPolicies, search]);

  // Debounced search
  useEffect(() => {
    if (search.trim() === "") return;
    const timer = setTimeout(async () => {
      setLoading(true);
      try {
        const data = await searchPolicies(search);
        setPolicies(data || []);
        setTotalPages(1);
      } catch {
        setError("Search failed.");
      } finally {
        setLoading(false);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [search]);

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this policy?")) return;
    try {
      await deletePolicy(id);
      fetchPolicies();
    } catch (err) {
      alert("Delete failed. You may not have permission.");
    }
  };

  const handleExportCSV = () => {
    const headers = ["ID,Name,Type,Status,Policy Holder,Expiry Date"];
    const rows = policies.map(p =>
      `${p.id},"${p.policyName}","${p.policyType}","${p.status}","${p.policyHolder}","${p.expiryDate || ""}"`
    );
    const csvContent = [...headers, ...rows].join("\n");
    const blob = new Blob([csvContent], { type: "text/csv" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "policies.csv";
    a.click();
    URL.revokeObjectURL(url);
  };

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDir(sortDir === "asc" ? "desc" : "asc");
    } else {
      setSortField(field);
      setSortDir("asc");
    }
  };

  const SortIcon = ({ field }) => {
    if (sortField !== field) return <span className="ml-1 opacity-40">↕</span>;
    return <span className="ml-1">{sortDir === "asc" ? "↑" : "↓"}</span>;
  };

  const statusColor = (status) => {
    if (status === "Active") return "bg-green-500";
    if (status === "Pending") return "bg-yellow-500";
    if (status === "DELETED") return "bg-red-500";
    return "bg-gray-400";
  };

  const filtered = policies.filter(p =>
    filter === "" || p.status === filter
  );

  const SkeletonRow = () => (
    <tr className="border-t animate-pulse">
      {[...Array(6)].map((_, i) => (
        <td key={i} className="p-3">
          <div className="h-4 bg-gray-200 rounded w-full" />
        </td>
      ))}
    </tr>
  );

  return (
    <>
      <Navbar />
      <div className="min-h-screen bg-gray-100 p-6" style={{ fontFamily: "Arial, sans-serif" }}>

        <div className="flex items-center justify-between mb-4">
          <h1 className="text-2xl font-bold text-gray-800">Policy List</h1>
          <div className="flex gap-3">
            <button
              onClick={handleExportCSV}
              className="bg-green-600 hover:bg-green-700 text-white px-5 py-2 rounded-lg font-semibold"
              style={{ minHeight: "44px" }}
            >
              ⬇️ Export CSV
            </button>
            <button
              onClick={() => navigate("/add")}
              className="text-white px-5 py-2 rounded-lg font-semibold hover:opacity-90"
              style={{ backgroundColor: "#1B4F8A", minHeight: "44px" }}
            >
              + Add Policy
            </button>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-4">
            <p className="text-red-600">{error}</p>
          </div>
        )}

        <div className="flex flex-col md:flex-row gap-3 mb-4">
          <input
            type="text"
            placeholder="Search by name or holder..."
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(0); }}
            className="border p-3 rounded-lg w-full md:w-2/3 focus:outline-none focus:ring-2 focus:ring-blue-300"
            style={{ minHeight: "44px" }}
          />
          <select
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            className="border p-3 rounded-lg w-full md:w-1/3 focus:outline-none focus:ring-2 focus:ring-blue-300"
            style={{ minHeight: "44px" }}
          >
            <option value="">All Statuses</option>
            <option value="Active">Active</option>
            <option value="Pending">Pending</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </div>

        <div className="bg-white rounded-lg shadow overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead style={{ backgroundColor: "#1B4F8A" }} className="text-white">
              <tr>
                {[
                  { label: "ID", field: "id" },
                  { label: "Policy Name", field: "policyName" },
                  { label: "Type", field: "policyType" },
                  { label: "Status", field: "status" },
                  { label: "Policy Holder", field: "policyHolder" },
                  { label: "Expiry Date", field: "expiryDate" },
                ].map(col => (
                  <th
                    key={col.field}
                    className="p-3 text-left cursor-pointer select-none hover:opacity-80"
                    onClick={() => handleSort(col.field)}
                  >
                    {col.label}<SortIcon field={col.field} />
                  </th>
                ))}
                <th className="p-3 text-left">Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                [...Array(4)].map((_, i) => <SkeletonRow key={i} />)
              ) : filtered.length === 0 ? (
                <tr>
                  <td colSpan={7} className="p-8 text-center text-gray-400">
                    <div className="text-4xl mb-2">📋</div>
                    No policies found
                  </td>
                </tr>
              ) : (
                filtered.map(p => (
                  <tr
                    key={p.id}
                    className="border-t hover:bg-blue-50 cursor-pointer transition"
                    onClick={() => navigate(`/detail/${p.id}`)}
                  >
                    <td className="p-3 text-gray-600">{p.id}</td>
                    <td className="p-3 font-medium text-gray-800">{p.policyName}</td>
                    <td className="p-3 text-gray-600">{p.policyType}</td>
                    <td className="p-3">
                      <span className={`px-3 py-1 rounded-full text-white text-xs font-semibold ${statusColor(p.status)}`}>
                        {p.status}
                      </span>
                    </td>
                    <td className="p-3 text-gray-600">{p.policyHolder}</td>
                    <td className="p-3 text-gray-600">{p.expiryDate || "-"}</td>
                    <td className="p-3" onClick={e => e.stopPropagation()}>
                      <div className="flex gap-2">
                        <button
                          onClick={() => navigate("/add", { state: p })}
                          className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded text-xs font-semibold"
                          style={{ minHeight: "44px" }}
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDelete(p.id)}
                          className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded text-xs font-semibold"
                          style={{ minHeight: "44px" }}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {!loading && totalPages > 1 && (
          <div className="flex items-center justify-between mt-4 text-sm text-gray-600">
            <span>
              Showing {page * ITEMS_PER_PAGE + 1}–
              {Math.min((page + 1) * ITEMS_PER_PAGE, totalElements)} of {totalElements} policies
            </span>
            <div className="flex gap-2">
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-1 rounded border disabled:opacity-40 hover:bg-gray-100"
                style={{ minHeight: "44px" }}
              >
                ← Prev
              </button>
              {[...Array(totalPages)].map((_, i) => (
                <button
                  key={i}
                  onClick={() => setPage(i)}
                  className={`px-3 py-1 rounded border ${page === i ? "text-white" : "hover:bg-gray-100"}`}
                  style={{ backgroundColor: page === i ? "#1B4F8A" : "", minHeight: "44px" }}
                >
                  {i + 1}
                </button>
              ))}
              <button
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
                className="px-3 py-1 rounded border disabled:opacity-40 hover:bg-gray-100"
                style={{ minHeight: "44px" }}
              >
                Next →
              </button>
            </div>
          </div>
        )}

      </div>
    </>
  );
}