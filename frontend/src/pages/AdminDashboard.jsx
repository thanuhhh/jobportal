import { useEffect, useState } from "react";
import api, { errorMessage } from "../api";

export default function AdminDashboard() {
  const [tab, setTab] = useState("jobs");
  const [stats, setStats] = useState(null);
  const [error, setError] = useState("");
  const [msg, setMsg] = useState("");

  const [jobs, setJobs] = useState(null);
  const [approvedFilter, setApprovedFilter] = useState("false");
  const [users, setUsers] = useState(null);
  const [roleFilter, setRoleFilter] = useState("");
  const [categories, setCategories] = useState([]);
  const [newCat, setNewCat] = useState("");

  const loadStats = () => api.get("/admin/stats").then((r) => setStats(r.data)).catch((e) => setError(errorMessage(e)));
  const loadJobs = () => {
    const params = { page: 0, size: 50 };
    if (approvedFilter !== "") params.approved = approvedFilter;
    api.get("/admin/jobs", { params }).then((r) => setJobs(r.data)).catch((e) => setError(errorMessage(e)));
  };
  const loadUsers = () => {
    const params = { page: 0, size: 50 };
    if (roleFilter) params.role = roleFilter;
    api.get("/admin/users", { params }).then((r) => setUsers(r.data)).catch((e) => setError(errorMessage(e)));
  };
  const loadCategories = () => api.get("/categories").then((r) => setCategories(r.data));

  useEffect(() => { loadStats(); }, []);
  useEffect(() => { if (tab === "jobs") loadJobs(); }, [tab, approvedFilter]);
  useEffect(() => { if (tab === "users") loadUsers(); }, [tab, roleFilter]);
  useEffect(() => { if (tab === "categories") loadCategories(); }, [tab]);

  const run = async (fn, after) => {
    setError("");
    setMsg("");
    try {
      await fn();
      after();
      loadStats();
    } catch (e) {
      setError(errorMessage(e));
    }
  };

  return (
    <div className="container">
      <h2>Admin dashboard</h2>
      {stats && (
        <div className="row">
          {Object.entries(stats).map(([k, v]) => (
            <div className="card" key={k} style={{ flex: "1 1 120px", textAlign: "center" }}>
              <div style={{ fontSize: 24, fontWeight: 700 }}>{v}</div>
              <div className="muted">{k}</div>
            </div>
          ))}
        </div>
      )}

      <p className="row">
        {["jobs", "users", "categories"].map((t) => (
          <button key={t} className={tab === t ? "" : "secondary"} onClick={() => setTab(t)}>
            {t[0].toUpperCase() + t.slice(1)}
          </button>
        ))}
      </p>

      {error && <div className="error">{error}</div>}
      {msg && <div className="success">{msg}</div>}

      {tab === "jobs" && (
        <>
          <select style={{ maxWidth: 220 }} value={approvedFilter} onChange={(e) => setApprovedFilter(e.target.value)}>
            <option value="false">Waiting for approval</option>
            <option value="true">Approved</option>
            <option value="">All</option>
          </select>
          {jobs && jobs.content.length === 0 && <p>No jobs here.</p>}
          {jobs && jobs.content.map((j) => (
            <div className="card" key={j.id}>
              <h3>{j.title}</h3>
              <div className="muted">{j.companyName} &middot; {j.location}</div>
              <p>
                <span className="badge">{j.status}</span>
                <span className="badge" style={{ background: j.approved ? "#dcfce7" : "#fef9c3" }}>
                  {j.approved ? "Approved" : "Pending"}
                </span>
              </p>
              <div className="row">
                {!j.approved && (
                  <button onClick={() => run(() => api.patch(`/admin/jobs/${j.id}/approve`), loadJobs)}>Approve</button>
                )}
                <button className="danger" onClick={() => {
                  if (window.confirm("Delete this job?")) run(() => api.delete(`/admin/jobs/${j.id}`), loadJobs);
                }}>Delete</button>
              </div>
            </div>
          ))}
        </>
      )}

      {tab === "users" && (
        <>
          <select style={{ maxWidth: 220 }} value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)}>
            <option value="">All roles</option>
            <option value="JOB_SEEKER">Job seekers</option>
            <option value="RECRUITER">Recruiters</option>
            <option value="ADMIN">Admins</option>
          </select>
          {users && users.content.map((u) => (
            <div className="card" key={u.id}>
              <h3>{u.name}</h3>
              <div className="muted">{u.email}</div>
              <p>
                <span className="badge">{u.role}</span>
                <span className="badge" style={{ background: u.enabled ? "#dcfce7" : "#fee2e2" }}>
                  {u.enabled ? "Active" : "Blocked"}
                </span>
              </p>
              {u.role !== "ADMIN" && (u.enabled
                ? <button className="danger" onClick={() => run(() => api.patch(`/admin/users/${u.id}/block`), loadUsers)}>Block</button>
                : <button onClick={() => run(() => api.patch(`/admin/users/${u.id}/unblock`), loadUsers)}>Unblock</button>)}
            </div>
          ))}
        </>
      )}

      {tab === "categories" && (
        <>
          <form className="row" onSubmit={(e) => {
            e.preventDefault();
            if (!newCat.trim()) return;
            run(() => api.post("/admin/categories", { name: newCat }), () => { setNewCat(""); loadCategories(); });
          }}>
            <input placeholder="New category name" value={newCat} onChange={(e) => setNewCat(e.target.value)} />
            <button>Add</button>
          </form>
          <p className="muted">A category used by a job cannot be deleted.</p>
          {categories.map((c) => (
            <div className="card" key={c.id}>
              <div className="row" style={{ justifyContent: "space-between", alignItems: "center" }}>
                <span>{c.name}</span>
                <button className="danger" onClick={() => run(() => api.delete(`/admin/categories/${c.id}`), loadCategories)}>Delete</button>
              </div>
            </div>
          ))}
        </>
      )}
    </div>
  );
}
