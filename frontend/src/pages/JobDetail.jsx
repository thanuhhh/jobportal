import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api, { errorMessage, money } from "../api";
import { useAuth } from "../AuthContext.jsx";

export default function JobDetail() {
  const { id } = useParams();
  const { user } = useAuth();
  const [job, setJob] = useState(null);
  const [cover, setCover] = useState("");
  const [error, setError] = useState("");
  const [msg, setMsg] = useState("");

  useEffect(() => {
    api.get(`/jobs/${id}`).then((r) => setJob(r.data)).catch((e) => setError(errorMessage(e)));
  }, [id]);

  const apply = async () => {
    setError("");
    setMsg("");
    try {
      await api.post(`/seeker/jobs/${id}/apply`, { coverLetter: cover });
      setMsg("Application submitted. Track it under My Applications.");
    } catch (e) {
      setError(errorMessage(e));
    }
  };

  const save = async () => {
    setError("");
    setMsg("");
    try {
      await api.post(`/seeker/saved-jobs/${id}`);
      setMsg("Job saved.");
    } catch (e) {
      setError(errorMessage(e));
    }
  };

  if (error && !job) return <div className="container"><div className="error">{error}</div></div>;
  if (!job) return <div className="container"><p className="muted">Loading...</p></div>;

  return (
    <div className="container">
      <p><Link to="/jobs">&larr; Back to jobs</Link></p>
      <div className="card">
        <h2>{job.title}</h2>
        <div className="muted">{job.companyName} &middot; {job.location}</div>
        <div style={{ margin: "10px 0" }}>
          <span className="badge">{job.type}</span>
          <span className="badge">{job.categoryName}</span>
          <span className="badge">Rs {money(job.salaryMin)} - {money(job.salaryMax)}</span>
          {job.experienceYears != null && <span className="badge">{job.experienceYears} yr exp</span>}
        </div>
        <p style={{ whiteSpace: "pre-wrap" }}>{job.description}</p>
      </div>

      {error && <div className="error">{error}</div>}
      {msg && <div className="success">{msg}</div>}

      {!user && (
        <div className="card">
          <Link to="/login">Log in</Link> as a job seeker to apply.
        </div>
      )}
      {user && user.role === "JOB_SEEKER" && (
        <div className="card">
          <h3>Apply</h3>
          <label>Cover letter (optional)</label>
          <textarea rows={4} value={cover} onChange={(e) => setCover(e.target.value)} />
          <p>
            <button onClick={apply}>Apply now</button>{" "}
            <button className="secondary" onClick={save}>Save job</button>
          </p>
        </div>
      )}
    </div>
  );
}
