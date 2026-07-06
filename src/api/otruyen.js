import axios from 'axios';

/**
 * Lớp gọi API tập trung — TOÀN BỘ request đều đi qua Laravel proxy,
 * KHÔNG bao giờ gọi thẳng sang OTruyen API từ trình duyệt.
 * Cấu hình URL proxy trong file .env: VITE_API_BASE_URL=http://localhost:8000/api
 */
const API = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8000/api',
    timeout: 15000,
});

// Interceptor xử lý lỗi tập trung (log gọn, có thể mở rộng để hiện toast...)
API.interceptors.response.use(
    (res) => res,
    (err) => {
        console.error('[API Error]', err?.config?.url, err?.message);
        return Promise.reject(err);
    }
);

export const getHome      = ()          => API.get('/truyen-moi');
export const getList      = (type, p=1) => API.get(`/danh-sach/${type}`, { params: { page: p } });
export const getDetail    = (slug)      => API.get(`/truyen/${slug}`);
export const getChapter   = (slug, ch)  => API.get(`/chuong/${slug}/${ch}`);
export const searchComics = (q, p=1)    => API.get('/tim-kiem', { params: { keyword: q, page: p } });

export default API;
