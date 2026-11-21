import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api, { errorMessage } from "../api";

const blank = {
  title: "", description: "", location: "", salaryMin: "", salaryMax: "",
  type: "FULL_TIME", experienceYears: "", categoryId: "",
};

export default function JobForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(blank);
  const [categories, setCategories] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/categories").then((r) => {
      setCategories(r.data);
      if (!id && r.data.length) setForm((f) => ({ ...f, categoryId: r.data[0].id }));
    });
    if (id) {
      api.get(`/recruiter/jobs/${id}`).then((r) => {
        const j = r.data;
        setForm({
          title: j.title, description: j.description, location: j.location,
          salaryMin: j.salaryMin ?? "", salaryMax: j.salaryMax ?? "",
          type: j.type, experienceYears: j.experienceYears ?? "", categoryId: j.categoryId,
        });
      }).catch((e) => setError(errorMessage(e)));
    }
  }, [id]);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });
  const num = (v) => (v === "" ? null : Number(v));

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    const body = {
      ...form,
      salaryMin: num(form.salaryMin),
      salaryMax: num(form.salaryMax),
      experienceYears: num(form.experienceYears),
      categoryId: Number(form.categoryId),
    };
    try {
      if (id) await api.put(`/recruiter/jobs/${id}`, body);
      else await api.post("/recruiter/jobs", body);
      navigate("/recruiter");
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <div className="container">
      <h2>{id ? "Edit job" : "Post a job"}</h2>
      {!id && <p className="muted">New jobs appear in search after an admin approves them.</p>}
      {error && <div className="error">{error}</div>}
      <form className="card" onSubmit={submit}>
        <label>Title</label>
        <input value={form.title} onChange={set("title")} required />
        <label>Description</label>
        <textarea rows={5} value={form.description} onChange={set("description")} required />
        <label>Location</label>
        <input value={form.location} onChange={set("location")} required />
        <div className="row">
          <div style={{ flex: 1 }}>
            <label>Min salary</label>
            <input type="number" min="0" value={form.salaryMin} onChange={set("salaryMin")} />
          </div>
          <div style={{ flex: 1 }}>
            <label>Max salary</label>
            <input type="number" min="0" value={form.salaryMax} onChange={set("salaryMax")} />
          </div>
          <div style={{ flex: 1 }}>
            <label>Experience (years)</label>
            <input type="number" min="0" value={form.experienceYears} onChange={set("experienceYears")} />
          </div>
        </div>
        <label>Type</label>
        <select value={form.type} onChange={set("type")}>
          <option value="FULL_TIME">Full time</option>
          <option value="PART_TIME">Part time</option>
          <option value="INTERNSHIP">Internship</option>
          <option value="CONTRACT">Contract</option>
        </select>
        <label>Category</label>
        <select value={form.categoryId} onChange={set("categoryId")} required>
          {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <p>
          <button>{id ? "Save changes" : "Post job"}</button>{" "}
          <button type="button" className="secondary" onClick={() => navigate("/recruiter")}>Cancel</button>
        </p>
      </form>
    </div>
  );
}
