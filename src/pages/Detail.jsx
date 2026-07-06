import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { getDetail } from '../api/otruyen';
import { coverUrl, handleImgError } from '../utils/image';
import ChapterList from '../components/ChapterList';

export default function Detail() {
    const { slug } = useParams();
    const [comic, setComic] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);

    useEffect(() => {
        const loadDetail = async () => {
            setLoading(true);
            setError(false);
            try {
                const res = await getDetail(slug);
                setComic(res.data.data?.item);
            } catch {
                setError(true);
            } finally {
                setLoading(false);
            }
        };

        loadDetail();
    }, [slug]);

    if (loading) return <div className="loading">⏳ Đang tải...</div>;
    if (error)   return <div className="empty">Đã có lỗi xảy ra, vui lòng thử lại</div>;
    if (!comic)  return <div className="empty">Không tìm thấy truyện</div>;

    return (
        <div className="section">
            <div className="detail-head">
                <img
                    src={coverUrl(comic.thumb_url)}
                    alt={comic.name}
                    onError={handleImgError}
                    className="detail-cover"
                />
                <div className="detail-info">
                    <h1>{comic.name}</h1>
                    <p className="meta">
                        Tác giả: {comic.author?.map(a => a.name).join(', ') || 'Đang cập nhật'}
                    </p>
                    <p className="meta">
                        Thể loại: {comic.category?.map(c => c.name).join(', ') || 'Đang cập nhật'}
                    </p>
                    <p className={comic.status === 'completed' ? 'status-done' : 'status-ongoing'}>
                        {comic.status === 'completed' ? '✅ Hoàn thành' : '🔄 Đang ra'}
                    </p>
                    <p className="desc">{comic.content}</p>
                </div>
            </div>

            <h2>📋 Danh sách chương</h2>
            <ChapterList slug={slug} chapters={comic.chapters?.[0]?.server_data} />
        </div>
    );
}
