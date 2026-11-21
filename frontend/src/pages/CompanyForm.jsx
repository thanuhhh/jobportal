import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api, { errorMessage } from "../api";

export default function CompanyForm() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: "", description: "", website: "", location: "" });
  const [error, setError] = useState("");

  useEffect(() => {
    api.get("/recruiter/company").then((r) => {
      const c = r.data;
      setForm({
        name: c.name || "",
        description: c.description || "",
        website: c.website || "",
        location: c.location || "",
      });
    }).catch(() => {});
  }, []);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await api.put("/recruiter/company", form);
      navigate("/recruiter");
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <div className="container">
      <h2>Company profile</h2>
      {error && <div className="error">{error}</div>}
      <form className="card" onSubmit={submit}>
        <label>Company name</label>
        <input value={form.name} onChange={set("name")} required />
        <label>Location</label>
        <input value={form.location} onChange={set("location")} />
        <label>Website</label>
        <input value={form.website} onChange={set("website")} />
        <label>Description</label>
        <textarea rows={4} value={form.description} onChange={set("description")} />
        <p><button>Save</button></p>
      </form>
    </div>
  );
}
