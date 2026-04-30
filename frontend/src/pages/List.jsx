import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import { DEMO_POLICIES } from "./Dashboard";

const ITEMS_PER_PAGE = 5;

export default function List() {
  const navigate = useNavigate();

  const [policies, setPolicies] = useState([]);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("");
  const [loading, setLoading] = useState(true);
  const [sortField, setSortField] = useState("id");
  const [sortDir, setSortDir] = useState("asc");
  const [page, setPage] = useState(1);

  // Load from localStorage or demo data
  useEffect(() => {
    setTimeout(() => {
      const stored = JSON.parse(localStorage.getItem("policies") || "null");
      setPolicies(stored || DEMO_POLICIES);
      setLoading(false);
    }, 800);
  }, []);

  const handleDelete = (id) => {
    if (!window.confirm("Delete this policy?")) return;
    const updated = policies.filter(p => p.id !== id);
    setPolicies(updated);
    localStorage.setItem("policies", JSON.stringify(updated));
  };

  const handleSort = (field) => {
    if (sortField === field) {
      setSortDir(sortDir === "asc" ? "desc" : "asc");
    } else {
      setSortField(field);
      setSortDir("asc");
    }
    setPage(1);
  };

  const SortIcon = ({ field }) => {
    if (sortField !== field) return <span className="ml-1 opacity-40">↕</span>;
    return <span className="ml-1">{sortDir === "asc" ? "↑" : "↓"}</span>;
  };

  const statusColor = (status) => {
    if (status === "Active") return "bg-green-500";
    if (status === "Pending") return "bg-yellow-500";
    if (status === "Expired") return "bg-red-500";
    return "bg-gray-400";
  };

  // Filter + search
  const filtered = policies
    .filter(p =>
      p.name.toLowerCase().includes(search.toLowerCase()) &&
      (filter === "" || p.status === filter)
    )
    .sort((a, b) => {
      const valA = String(a[sortField] || "").toLowerCase();
      const valB = String(b[sortField] || "").toLowerCase();
      return sortDir === "asc"
        ? valA.localeCompare(valB)
        : valB.localeCompare(valA);
    });

  const totalPages = Math.ceil(filtered.length / ITEMS_PER_PAGE);
  const paginated = filtered.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE);

  // Loading skeleton rows
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
          <button
            onClick={() => navigate("/add")}
            className="text-white px-5 py-2 rounded-lg font-semibold hover:opacity-90"
            style={{ backgroundColor: "#1B4F8A", minHeight: "44px" }}
          >
            + Add Policy
          </button>
        </div>

        {/* Search + Filter Row */}
        <div className="flex flex-col md:flex-row gap-3 mb-4">
          <input
            type="text"
            placeholder="Search by name..."
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(1); }}
            className="border p-3 rounded-lg w-full md:w-2/3 focus:outline-none focus:ring-2 focus:ring-blue-300"
            style={{ minHeight: "44px" }}
          />
          <select
            value={filter}
            onChange={(e) => { setFilter(e.target.value); setPage(1); }}
            className="border p-3 rounded-lg w-full md:w-1/3 focus:outline-none focus:ring-2 focus:ring-blue-300"
            style={{ minHeight: "44px" }}
          >
            <option value="">All Statuses</option>
            <option value="Active">Active</option>
            <option value="Pending">Pending</option>
            <option value="Expired">Expired</option>
          </select>
        </div>

        {/* Table */}
        <div className="bg-white rounded-lg shadow overflow-x-auto">
          <table className="min-w-full text-sm">
            <thead style={{ backgroundColor: "#1B4F8A" }} className="text-white">
              <tr>
                {[
                  { label: "ID", field: "id" },
                  { label: "Name", field: "name" },
                  { label: "Category", field: "category" },
                  { label: "Status", field: "status" },
                  { label: "Effective Date", field: "effectiveDate" },
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
              ) : paginated.length === 0 ? (
                <tr>
                  <td colSpan={7} className="p-8 text-center text-gray-400">
                    <div className="text-4xl mb-2">📋</div>
                    No policies found
                  </td>
                </tr>
              ) : (
                paginated.map(p => (
                  <tr
                    key={p.id}
                    className="border-t hover:bg-blue-50 cursor-pointer transition"
                    onClick={() => navigate(`/detail/${p.id}`, { state: { policies } })}
                  >
                    <td className="p-3 text-gray-600">{p.id}</td>
                    <td className="p-3 font-medium text-gray-800">{p.name}</td>
                    <td className="p-3 text-gray-600">{p.category}</td>
                    <td className="p-3">
                      <span className={`px-3 py-1 rounded-full text-white text-xs font-semibold ${statusColor(p.status)}`}>
                        {p.status}
                      </span>
                    </td>
                    <td className="p-3 text-gray-600">{p.effectiveDate}</td>
                    <td className="p-3 text-gray-600">{p.expiryDate}</td>
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
              Showing {(page - 1) * ITEMS_PER_PAGE + 1}–
              {Math.min(page * ITEMS_PER_PAGE, filtered.length)} of {filtered.length} policies
            </span>
            <div className="flex gap-2">
              <button
                onClick={() => setPage(p => Math.max(1, p - 1))}
                disabled={page === 1}
                className="px-3 py-1 rounded border disabled:opacity-40 hover:bg-gray-100"
                style={{ minHeight: "44px" }}
              >
                ← Prev
              </button>
              {[...Array(totalPages)].map((_, i) => (
                <button
                  key={i}
                  onClick={() => setPage(i + 1)}
                  className={`px-3 py-1 rounded border ${page === i + 1 ? "text-white" : "hover:bg-gray-100"}`}
                  style={{
                    backgroundColor: page === i + 1 ? "#1B4F8A" : "",
                    minHeight: "44px"
                  }}
                >
                  {i + 1}
                </button>
              ))}
              <button
                onClick={() => setPage(p => Math.min(totalPages, p + 1))}
                disabled={page === totalPages}
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
