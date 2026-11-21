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

export default function App() {
  const seeker = ["JOB_SEEKER"];
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
        <Route path="*" element={<Navigate to="/jobs" replace />} />
      </Routes>
    </>
  );
}
