import { useEffect, useState } from "react";
import api, { errorMessage } from "../api";
import JobCard from "../components/JobCard.jsx";

const empty = { keyword: "", location: "", categoryId: "", type: "", minSalary: "" };

export default function Jobs() {
  const [filters, setFilters] = useState(empty);
  const [applied, setApplied] = useState(empty);
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [categories, setCategories] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api.get("/categories").then((r) => setCategories(r.data)).catch(() => {});
  }, []);

  useEffect(() => {
    const params = { page, size: 5 };
    Object.entries(applied).forEach(([k, v]) => {
      if (v !== "") params[k] = v;
    });
    setLoading(true);
    setError("");
    api
      .get("/jobs", { params })
      .then((r) => setData(r.data))
      .catch((e) => setError(errorMessage(e)))
      .finally(() => setLoading(false));
  }, [applied, page]);

  const set = (k) => (e) => setFilters({ ...filters, [k]: e.target.value });

  const search = (e) => {
    e.preventDefault();
    setPage(0);
    setApplied(filters);
  };

  const clear = () => {
    setFilters(empty);
    setApplied(empty);
    setPage(0);
  };

  return (
    <div className="container">
      <h2>Find jobs</h2>
      <form className="card" onSubmit={search}>
        <div className="row">
          <input placeholder="Keyword (title or skill)" value={filters.keyword} onChange={set("keyword")} />
          <input placeholder="Location" value={filters.location} onChange={set("location")} />
          <select value={filters.categoryId} onChange={set("categoryId")}>
            <option value="">All categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
          <select value={filters.type} onChange={set("type")}>
            <option value="">All types</option>
            <option value="FULL_TIME">Full time</option>
            <option value="PART_TIME">Part time</option>
            <option value="INTERNSHIP">Internship</option>
            <option value="CONTRACT">Contract</option>
          </select>
          <input type="number" placeholder="Min salary" value={filters.minSalary} onChange={set("minSalary")} />
        </div>
        <p>
          <button>Search</button>{" "}
          <button type="button" className="secondary" onClick={clear}>Clear</button>
        </p>
      </form>

      {error && <div className="error">{error}</div>}
      {loading && <p className="muted">Loading...</p>}
      {data && data.content.length === 0 && !loading && <p>No jobs found.</p>}
      {data && data.content.map((j) => <JobCard key={j.id} job={j} />)}

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
