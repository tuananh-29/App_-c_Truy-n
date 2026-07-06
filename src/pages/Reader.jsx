import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getChapter } from '../api/otruyen';
import { handleImgError } from '../utils/image';

export default function Reader() {
    const { slug, ch } = useParams();
    const [pages, setPages] = useState([]);
    const [title, setTitle] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(false);
    const nav = useNavigate();

    useEffect(() => {
        const loadChapter = async () => {
            setLoading(true);
            setError(false);
            window.scrollTo(0, 0);
            try {
                const res = await getChapter(slug, ch);
                const data = res.data.data?.item;
                setPages(data?.chapter_image || []);
                setTitle(data?.chapter_name || ch);
            } catch (err) {
                console.error('Lỗi khi tải dữ liệu chương: ', err);
                setError(true);
            } finally {
                setLoading(false);
            }
        };

        loadChapter();
    }, [slug, ch]);

    if (loading) return <div className="loading">⏳ Đang tải chương...</div>;
    if (error)   return <div className="error-box">⚠️ Không tải được chương này, vui lòng thử lại</div>;

    const prevCh = parseInt(ch) - 1;
    const nextCh = parseInt(ch) + 1;

    return (
        <>
            <div className="reader-head">
                <button onClick={() => nav(`/truyen/${slug}`)} className="back-link">
                    ← Quay lại
                </button>
                <span className="reader-title">Chương {title}</span>
            </div>

            <div className="reader-page">
                {pages.map((p, i) => (
                    <img
                        key={i}
                        src={p.image_file}
                        alt={`Trang ${i + 1}`}
                        loading="lazy"
                        onError={handleImgError}
                    />
                ))}
            </div>

            <div className="reader-nav">
                <button
                    onClick={() => nav(`/truyen/${slug}/chuong/${prevCh}`)}
                    disabled={prevCh < 1}>
                    ← Chương trước
                </button>
                <button onClick={() => nav(`/truyen/${slug}`)}>
                    📋 Mục lục
                </button>
                <button onClick={() => nav(`/truyen/${slug}/chuong/${nextCh}`)}>
                    Chương sau →
                </button>
            </div>
        </>
    );
}
