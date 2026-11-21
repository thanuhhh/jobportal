import { useEffect, useState } from "react";
import api, { errorMessage } from "../api";
import JobCard from "../components/JobCard.jsx";

export default function SavedJobs() {
  const [data, setData] = useState(null);
  const [error, setError] = useState("");

  const load = () =>
    api.get("/seeker/saved-jobs", { params: { page: 0, size: 50 } })
      .then((r) => setData(r.data))
      .catch((e) => setError(errorMessage(e)));

  useEffect(() => {
    load();
  }, []);

  const remove = async (id) => {
    try {
      await api.delete(`/seeker/saved-jobs/${id}`);
      load();
    } catch (e) {
      setError(errorMessage(e));
    }
  };

  return (
    <div className="container">
      <h2>Saved jobs</h2>
      {error && <div className="error">{error}</div>}
      {data && data.content.length === 0 && <p>No saved jobs.</p>}
      {data && data.content.map((j) => (
        <JobCard key={j.id} job={j}>
          <button className="danger" onClick={() => remove(j.id)}>Remove</button>
        </JobCard>
      ))}
    </div>
  );
}
