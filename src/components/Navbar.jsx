import { Link, useNavigate } from 'react-router-dom';
import { useState } from 'react';

export default function Navbar() {
    const [q, setQ] = useState('');
    const nav = useNavigate();

    const handleSearch = (e) => {
        e.preventDefault();
        if (q.trim()) nav(`/tim-kiem?q=${encodeURIComponent(q.trim())}`);
    };

    return (
        <div className="navbar">
            <Link to="/" className="logo">📖 TruyenMoiNgay</Link>
            <nav>
                <Link to="/">Trang chủ</Link>
                <Link to="/tim-kiem">Thể loại</Link>
            </nav>
            <form className="search-form" onSubmit={handleSearch}>
                <input
                    value={q}
                    onChange={e => setQ(e.target.value)}
                    placeholder="🔍 Tìm kiếm truyện..."
                />
                <button type="submit">Tìm</button>
            </form>
        </div>
    );
}