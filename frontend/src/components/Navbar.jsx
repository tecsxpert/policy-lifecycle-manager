import { useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  return (
    <nav style={{ backgroundColor: "#1B4F8A" }} className="text-white px-6 py-3 flex items-center justify-between">

      {/* Brand */}
      <span
        className="font-bold text-lg cursor-pointer"
        onClick={() => navigate("/dashboard")}
      >
        Policy Lifecycle Manager
      </span>

      {/* Nav Links */}
      <div className="flex items-center gap-6">
        <button
          onClick={() => navigate("/dashboard")}
          className="hover:underline text-white font-medium"
          style={{ minHeight: "44px" }}
        >
          Dashboard
        </button>

        <button
          onClick={() => navigate("/list")}
          className="hover:underline text-white font-medium"
          style={{ minHeight: "44px" }}
        >
          List
        </button>

        <button
          onClick={() => navigate("/analytics")}
          className="hover:underline text-white font-medium"
          style={{ minHeight: "44px" }}
        >
          Analytics
        </button>

        <button
          onClick={() => navigate("/add")}
          className="hover:underline text-white font-medium"
          style={{ minHeight: "44px" }}
        >
          Add
        </button>

        <button
          onClick={handleLogout}
          className="bg-red-500 hover:bg-red-600 text-white px-4 rounded font-medium"
          style={{ minHeight: "44px" }}
        >
          Logout
        </button>
      </div>
    </nav>
  );
}
