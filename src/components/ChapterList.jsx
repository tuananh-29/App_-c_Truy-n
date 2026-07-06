import { useNavigate } from 'react-router-dom';

/**
 * Danh sách chương của một truyện.
 * props:
 *  - slug: slug truyện (dùng để điều hướng)
 *  - chapters: mảng server_data lấy từ comic.chapters[0].server_data
 */
export default function ChapterList({ slug, chapters = [] }) {
    const nav = useNavigate();

    if (!chapters.length) {
        return <div className="empty">Chưa có chương nào</div>;
    }

    return (
        <div className="chapter-list">
            {chapters.map(ch => (
                <button
                    key={ch.chapter_api_data || ch.chapter_name}
                    className="chapter-item"
                    onClick={() => nav(`/truyen/${slug}/chuong/${ch.chapter_name}`)}
                >
                    Chương {ch.chapter_name}
                </button>
            ))}
        </div>
    );
}
