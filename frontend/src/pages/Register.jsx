import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth, homeFor } from "../AuthContext.jsx";
import { errorMessage } from "../api";

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: "", email: "", password: "", role: "JOB_SEEKER" });
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      const u = await register(form);
      navigate(homeFor(u.role));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="container auth-box">
      <div className="card">
        <h2>Create account</h2>
        {error && <div className="error">{error}</div>}
        <form onSubmit={submit}>
          <label>Name</label>
          <input value={form.name} onChange={set("name")} required />
          <label>Email</label>
          <input type="email" value={form.email} onChange={set("email")} required />
          <label>Password (min 6 characters)</label>
          <input type="password" value={form.password} onChange={set("password")} required minLength={6} />
          <label>I am a</label>
          <select value={form.role} onChange={set("role")}>
            <option value="JOB_SEEKER">Job seeker</option>
            <option value="RECRUITER">Recruiter</option>
          </select>
          <p><button disabled={busy}>{busy ? "Creating..." : "Register"}</button></p>
        </form>
        <div className="muted">Already registered? <Link to="/login">Login</Link></div>
      </div>
    </div>
  );
}
