import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import Home from './pages/Home';
import Detail from './pages/Detail';
import Reader from './pages/Reader';
import Search from './pages/Search';
import './styles/main.css';

export default function App() {
    return (
        <BrowserRouter>
            <Navbar />
            <Routes>
                <Route path="/"                          element={<Home />} />
                <Route path="/truyen/:slug"              element={<Detail />} />
                <Route path="/truyen/:slug/chuong/:ch"   element={<Reader />} />
                <Route path="/tim-kiem"                  element={<Search />} />
                <Route path="*" element={<div className="empty">404 - Không tìm thấy trang</div>} />
            </Routes>
        </BrowserRouter>
    );
}
