import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { searchComics } from '../api/otruyen';
import ComicCard from '../components/ComicCard';

export default function Search() {
    const [params] = useSearchParams();
    const q = params.get('q') || '';
    const [comics, setComics] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(false);

    useEffect(() => {
        const loadSearch = async () => {
            if (!q) { setComics([]); return; }

            setLoading(true);
            setError(false);
            try {
                const res = await searchComics(q);
                setComics(res.data.data?.items || []);
            } catch (err) {
                console.error('Lỗi khi tìm kiếm: ', err);
                setError(true);
            } finally {
                setLoading(false);
            }
        };

        loadSearch();
    }, [q]);

    return (
        <div className="section">
            <h2>🔍 Kết quả tìm kiếm: "{q}"</h2>
            {loading && <div className="loading">Đang tìm...</div>}
            {error && <div className="error-box">⚠️ Không thể tìm kiếm, vui lòng thử lại</div>}
            {!loading && !error && q && comics.length === 0 && (
                <div className="empty">Không tìm thấy truyện nào</div>
            )}
            <div className="comic-grid">
                {comics.map(c => <ComicCard key={c._id} comic={c} />)}
            </div>
        </div>
    );
}
