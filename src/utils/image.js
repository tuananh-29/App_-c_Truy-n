// Ảnh bìa truyện được host trên img.otruyenapi.com
const COVER_BASE = 'https://img.otruyenapi.com/uploads/comics/';

// Placeholder dạng data-URI (SVG) — không phụ thuộc file tĩnh, luôn sẵn có kể cả khi build/deploy
export const FALLBACK_COVER =
    'data:image/svg+xml;charset=UTF-8,' +
    encodeURIComponent(`
        <svg xmlns="http://www.w3.org/2000/svg" width="300" height="400" viewBox="0 0 300 400">
            <rect width="300" height="400" fill="#1a1a1a"/>
            <text x="50%" y="50%" fill="#555" font-size="18" font-family="sans-serif"
                  text-anchor="middle" dominant-baseline="middle">Không có ảnh</text>
        </svg>
    `);

// Ghép URL ảnh bìa đầy đủ từ thumb_url trả về bởi API
export function coverUrl(thumb) {
    if (!thumb) return FALLBACK_COVER;
    return COVER_BASE + thumb;
}

// Handler dùng chung cho thẻ <img onError={...}> — tránh loop lỗi vô hạn
export function handleImgError(e) {
    if (e.target.src !== FALLBACK_COVER) {
        e.target.onerror = null;
        e.target.src = FALLBACK_COVER;
    }
}
