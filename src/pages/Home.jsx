import { useEffect, useState } from 'react';
import { getHome } from '../api/otruyen';
import ComicCard from '../components/ComicCard';

export default function Home() {
    const [comics, setComics] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    useEffect(() => {
        getHome()
            .then(res => setComics(res.data.data?.items || []))
            .catch(() => setError(true))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div className="loading">⏳ Đang tải truyện...</div>;
    if (error)   return <div className="error-box">⚠️ Không thể tải danh sách truyện, vui lòng thử lại sau</div>;

    return (
        <div className="section">
            <h2>📚 Truyện mới cập nhật</h2>
            {comics.length === 0
                ? <div className="empty">Chưa có truyện nào</div>
                : (
                    <div className="comic-grid">
                        {comics.map(c => <ComicCard key={c._id} comic={c} />)}
                    </div>
                )}
        </div>
    );
}
