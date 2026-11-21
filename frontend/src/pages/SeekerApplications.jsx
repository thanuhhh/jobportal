import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api, { errorMessage } from "../api";

export default function SeekerApplications() {
  const [data, setData] = useState(null);
  const [page, setPage] = useState(0);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/seeker/applications", { params: { page, size: 10 } })
      .then((r) => setData(r.data))
      .catch((e) => setError(errorMessage(e)));
  }, [page]);

  return (
    <div className="container">
      <h2>My applications</h2>
      {error && <div className="error">{error}</div>}
      {data && data.content.length === 0 && <p>You have not applied to any jobs yet.</p>}
      {data && data.content.map((a) => (
        <div className="card" key={a.id}>
          <h3><Link to={`/jobs/${a.jobId}`}>{a.jobTitle}</Link></h3>
          <div className="muted">{a.companyName} &middot; applied {new Date(a.appliedAt).toLocaleDateString()}</div>
          <p><span className={`badge ${a.status}`}>{a.status}</span></p>
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
