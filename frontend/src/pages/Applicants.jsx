import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api, { errorMessage } from "../api";

const STATUSES = ["APPLIED", "SHORTLISTED", "REJECTED", "HIRED"];

export default function Applicants() {
  const { id } = useParams();
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [error, setError] = useState("");

  const load = () =>
    api.get(`/recruiter/jobs/${id}/applications`, { params: { page, size: 10 } })
      .then((r) => setData(r.data))
      .catch((e) => setError(errorMessage(e)));

  useEffect(() => {
    load();
  }, [id, page]);

  const changeStatus = async (appId, status) => {
    setError("");
    try {
      await api.patch(`/recruiter/applications/${appId}/status`, { status });
      load();
    } catch (e) {
      setError(errorMessage(e));
    }
  };

  const viewResume = async (appId) => {
    setError("");
    try {
      const r = await api.get(`/recruiter/applications/${appId}/resume`, { responseType: "blob" });
      const url = URL.createObjectURL(new Blob([r.data], { type: "application/pdf" }));
      window.open(url, "_blank");
    } catch (e) {
      setError("Could not open the resume. The applicant may not have uploaded one.");
    }
  };

  return (
    <div className="container">
      <p><Link to="/recruiter">&larr; Back to dashboard</Link></p>
      <h2>Applicants</h2>
      {error && <div className="error">{error}</div>}
      {data && data.content.length === 0 && <p>No applications yet.</p>}
      {data && data.content.map((a) => (
        <div className="card" key={a.applicationId}>
          <h3>{a.seekerName}</h3>
          <div className="muted">{a.seekerEmail} {a.phone && <>&middot; {a.phone}</>}</div>
          <p>
            {a.skills && <><b>Skills:</b> {a.skills}<br /></>}
            {a.education && <><b>Education:</b> {a.education}<br /></>}
            {a.experienceYears != null && <><b>Experience:</b> {a.experienceYears} yr<br /></>}
            {a.coverLetter && <><b>Cover letter:</b> {a.coverLetter}</>}
          </p>
          <div className="row" style={{ alignItems: "center" }}>
            <span className={`badge ${a.status}`}>{a.status}</span>
            <select style={{ maxWidth: 180 }} value={a.status} onChange={(e) => changeStatus(a.applicationId, e.target.value)}>
              {STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
            </select>
            {a.hasResume
              ? <button className="secondary" onClick={() => viewResume(a.applicationId)}>View resume</button>
              : <span className="muted">No resume</span>}
          </div>
        </div>
      ))}
      {data && data.totalPages > 1 && (
        <div className="pager">
          <button className="secondary" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <span>Page {data.page + 1} of {data.totalPages}</span>
          <button className="secondary" disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
        </div>
      )}
    </div>
  );
}
