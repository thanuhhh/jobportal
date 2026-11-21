import { Navigate, Route, Routes } from "react-router-dom";
import Navbar from "./components/Navbar.jsx";
import ProtectedRoute from "./ProtectedRoute.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Jobs from "./pages/Jobs.jsx";
import JobDetail from "./pages/JobDetail.jsx";
import SeekerProfile from "./pages/SeekerProfile.jsx";
import SeekerApplications from "./pages/SeekerApplications.jsx";
import SavedJobs from "./pages/SavedJobs.jsx";
import RecruiterDashboard from "./pages/RecruiterDashboard.jsx";
import CompanyForm from "./pages/CompanyForm.jsx";
import JobForm from "./pages/JobForm.jsx";
import Applicants from "./pages/Applicants.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";

export default function App() {
  const seeker = ["JOB_SEEKER"];
  const recruiter = ["RECRUITER"];
  const admin = ["ADMIN"];
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Navigate to="/jobs" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/jobs" element={<Jobs />} />
        <Route path="/jobs/:id" element={<JobDetail />} />

        <Route path="/seeker/profile" element={<ProtectedRoute roles={seeker}><SeekerProfile /></ProtectedRoute>} />
        <Route path="/seeker/applications" element={<ProtectedRoute roles={seeker}><SeekerApplications /></ProtectedRoute>} />
        <Route path="/seeker/saved" element={<ProtectedRoute roles={seeker}><SavedJobs /></ProtectedRoute>} />

        <Route path="/recruiter" element={<ProtectedRoute roles={recruiter}><RecruiterDashboard /></ProtectedRoute>} />
        <Route path="/recruiter/company" element={<ProtectedRoute roles={recruiter}><CompanyForm /></ProtectedRoute>} />
        <Route path="/recruiter/jobs/new" element={<ProtectedRoute roles={recruiter}><JobForm /></ProtectedRoute>} />
        <Route path="/recruiter/jobs/:id/edit" element={<ProtectedRoute roles={recruiter}><JobForm /></ProtectedRoute>} />
        <Route path="/recruiter/jobs/:id/applicants" element={<ProtectedRoute roles={recruiter}><Applicants /></ProtectedRoute>} />

        <Route path="/admin" element={<ProtectedRoute roles={admin}><AdminDashboard /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/jobs" replace />} />
      </Routes>
    </>
  );
}
