import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext.jsx";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const out = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className="nav">
      <Link className="brand" to="/jobs">JobPortal</Link>
      <Link to="/jobs">Jobs</Link>
      {user && user.role === "JOB_SEEKER" && (
        <>
          <Link to="/seeker/applications">My Applications</Link>
          <Link to="/seeker/saved">Saved</Link>
          <Link to="/seeker/profile">Profile</Link>
        </>
      )}
      {user && user.role === "RECRUITER" && <Link to="/recruiter">Recruiter</Link>}
      {user && user.role === "ADMIN" && <Link to="/admin">Admin</Link>}
      {user ? (
        <>
          <span className="muted" style={{ color: "#cbd5e1" }}>{user.name} ({user.role})</span>
          <button className="secondary" onClick={out}>Logout</button>
        </>
      ) : (
        <>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
        </>
      )}
    </div>
  );
}
