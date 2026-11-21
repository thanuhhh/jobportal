import { Link } from "react-router-dom";
import { money } from "../api";

export default function JobCard({ job, children }) {
  return (
    <div className="card">
      <h3><Link to={`/jobs/${job.id}`}>{job.title}</Link></h3>
      <div className="muted">{job.companyName} &middot; {job.location}</div>
      <div style={{ margin: "8px 0" }}>
        <span className="badge">{job.type}</span>
        <span className="badge">{job.categoryName}</span>
        <span className="badge">Rs {money(job.salaryMin)} - {money(job.salaryMax)}</span>
        {job.experienceYears != null && <span className="badge">{job.experienceYears} yr exp</span>}
      </div>
      {children}
    </div>
  );
}
