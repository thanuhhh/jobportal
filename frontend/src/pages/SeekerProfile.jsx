import { useEffect, useState } from "react";
import api, { errorMessage } from "../api";

export default function SeekerProfile() {
  const [form, setForm] = useState({ phone: "", skills: "", education: "", experienceYears: "", summary: "" });
  const [info, setInfo] = useState({ name: "", email: "", resumePath: null });
  const [file, setFile] = useState(null);
  const [error, setError] = useState("");
  const [msg, setMsg] = useState("");

  useEffect(() => {
    api.get("/seeker/profile").then((r) => {
      const p = r.data;
      setInfo({ name: p.name, email: p.email, resumePath: p.resumePath });
      setForm({
        phone: p.phone || "",
        skills: p.skills || "",
        education: p.education || "",
        experienceYears: p.experienceYears == null ? "" : p.experienceYears,
        summary: p.summary || "",
      });
    }).catch((e) => setError(errorMessage(e)));
  }, []);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const save = async (e) => {
    e.preventDefault();
    setError("");
    setMsg("");
    try {
      const body = { ...form, experienceYears: form.experienceYears === "" ? null : Number(form.experienceYears) };
      await api.put("/seeker/profile", body);
      setMsg("Profile saved.");
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  const upload = async () => {
    if (!file) return setError("Choose a PDF first");
    setError("");
    setMsg("");
    const fd = new FormData();
    fd.append("file", file);
    try {
      const r = await api.post("/seeker/profile/resume", fd);
      setInfo({ ...info, resumePath: r.data.resumePath });
      setMsg("Resume uploaded.");
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  return (
    <div className="container">
      <h2>My profile</h2>
      <p className="muted">{info.name} &middot; {info.email}</p>
      {error && <div className="error">{error}</div>}
      {msg && <div className="success">{msg}</div>}

      <form className="card" onSubmit={save}>
        <label>Phone</label>
        <input value={form.phone} onChange={set("phone")} />
        <label>Skills (comma separated)</label>
        <input value={form.skills} onChange={set("skills")} />
        <label>Education</label>
        <input value={form.education} onChange={set("education")} />
        <label>Experience (years)</label>
        <input type="number" min="0" value={form.experienceYears} onChange={set("experienceYears")} />
        <label>Summary</label>
        <textarea rows={4} value={form.summary} onChange={set("summary")} />
        <p><button>Save profile</button></p>
      </form>

      <div className="card">
        <h3>Resume (PDF, max 2 MB)</h3>
        <p className="muted">{info.resumePath ? "A resume is uploaded. Uploading again replaces it." : "No resume uploaded yet."}</p>
        <input type="file" accept="application/pdf,.pdf" onChange={(e) => setFile(e.target.files[0])} />
        <p><button onClick={upload}>Upload resume</button></p>
      </div>
    </div>
  );
}
