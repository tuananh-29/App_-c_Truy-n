import { useNavigate } from 'react-router-dom';
import { coverUrl, handleImgError } from '../utils/image';

export default function ComicCard({ comic }) {
    const nav = useNavigate();
    return (
        <div className="comic-card" onClick={() => nav(`/truyen/${comic.slug}`)}>
            <img
                src={coverUrl(comic.thumb_url)}
                alt={comic.name}
                onError={handleImgError}
                loading="lazy"
            />
            <div className="info">
                <p className="title">{comic.name}</p>
                <p className="sub">
                    {comic.chaptersLatest?.[0]?.chapter_name
                        ? `Chương ${comic.chaptersLatest[0].chapter_name}`
                        : 'Cập nhật mới'}
                </p>
            </div>
        </div>
    );
}
