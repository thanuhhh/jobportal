import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { errorMessage } from "../api";

export default function RecruiterDashboard() {
  const [company, setCompany] = useState(null);
  const [jobs, setJobs] = useState(null);
  const [page, setPage] = useState(0);
  const [error, setError] = useState("");

  const load = () => {
    api.get("/recruiter/company").then((r) => setCompany(r.data)).catch(() => setCompany(false));
    api.get("/recruiter/jobs", { params: { page, size: 10 } })
      .then((r) => setJobs(r.data))
      .catch(() => setJobs({ content: [], totalPages: 0, page: 0 }));
  };

  useEffect(load, [page]);

  const close = async (id) => {
    try {
      await api.patch(`/recruiter/jobs/${id}/close`);
      load();
    } catch (e) {
      setError(errorMessage(e));
    }
  };

  const remove = async (id) => {
    if (!window.confirm("Delete this job and all its applications?")) return;
    try {
      await api.delete(`/recruiter/jobs/${id}`);
      load();
    } catch (e) {
      setError(errorMessage(e));
    }
  };

  return (
    <div className="container">
      <h2>Recruiter dashboard</h2>
      {error && <div className="error">{error}</div>}

      <div className="card">
        {company === null && <p className="muted">Loading...</p>}
        {company === false && (
          <p>You have no company profile yet. <Link to="/recruiter/company">Create it</Link> before posting jobs.</p>
        )}
        {company && (
          <>
            <h3>{company.name}</h3>
            <div className="muted">{company.location} {company.website && <>&middot; {company.website}</>}</div>
            <p>{company.description}</p>
            <Link to="/recruiter/company">Edit company</Link>
          </>
        )}
      </div>

      <p><Link to="/recruiter/jobs/new"><button>Post a new job</button></Link></p>

      <h3>My jobs</h3>
      {jobs && jobs.content.length === 0 && <p>No jobs posted yet.</p>}
      {jobs && jobs.content.map((j) => (
        <div className="card" key={j.id}>
          <h3>{j.title}</h3>
          <div className="muted">{j.location} &middot; {j.type}</div>
          <p>
            <span className="badge">{j.status}</span>
            <span className="badge" style={{ background: j.approved ? "#dcfce7" : "#fef9c3" }}>
              {j.approved ? "Approved" : "Waiting for admin approval"}
            </span>
          </p>
          <div className="row">
            <Link to={`/recruiter/jobs/${j.id}/applicants`}><button>Applicants</button></Link>
            <Link to={`/recruiter/jobs/${j.id}/edit`}><button className="secondary">Edit</button></Link>
            {j.status === "OPEN" && <button className="secondary" onClick={() => close(j.id)}>Close</button>}
            <button className="danger" onClick={() => remove(j.id)}>Delete</button>
          </div>
        </div>
      ))}

      {jobs && jobs.totalPages > 1 && (
        <div className="pager">
          <button className="secondary" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <span>Page {jobs.page + 1} of {jobs.totalPages}</span>
          <button className="secondary" disabled={page + 1 >= jobs.totalPages} onClick={() => setPage(page + 1)}>Next</button>
        </div>
      )}
    </div>
  );
}
