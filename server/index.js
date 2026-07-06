// Backend proxy đơn giản (Node/Express) thay thế cho Laravel proxy còn thiếu.
// Chức năng: nhận request từ frontend (các route tiếng Việt) và forward
// sang OTruyen API thật (https://otruyenapi.com/v1/api), trả JSON nguyên dạng
// {status, message, data: {...}} — đúng shape mà frontend (src/api/otruyen.js
// và các trang Home/Detail/Reader/Search) đang mong đợi.

import express from 'express';
import cors from 'cors';

const app = express();
const PORT = process.env.PORT || 8000;
const OTRUYEN_BASE = 'https://otruyenapi.com/v1/api';

app.use(cors());

// Helper: gọi OTruyen API và trả JSON, có xử lý lỗi gọn
async function fetchJson(url) {
    const res = await fetch(url);
    if (!res.ok) {
        const err = new Error(`Upstream ${res.status}`);
        err.status = res.status;
        throw err;
    }
    return res.json();
}

// GET /api/truyen-moi -> danh sách truyện mới cập nhật (trang chủ)
app.get('/api/truyen-moi', async (req, res) => {
    try {
        const data = await fetchJson(`${OTRUYEN_BASE}/home`);
        res.json(data);
    } catch (e) {
        res.status(e.status || 500).json({ status: 'error', message: e.message });
    }
});

// GET /api/danh-sach/:type -> danh sách theo loại (truyen-moi, dang-phat-hanh, hoan-thanh, sap-ra-mat)
app.get('/api/danh-sach/:type', async (req, res) => {
    try {
        const { type } = req.params;
        const page = req.query.page || 1;
        const data = await fetchJson(`${OTRUYEN_BASE}/danh-sach/${type}?page=${page}`);
        res.json(data);
    } catch (e) {
        res.status(e.status || 500).json({ status: 'error', message: e.message });
    }
});

// GET /api/truyen/:slug -> chi tiết truyện + danh sách chương
app.get('/api/truyen/:slug', async (req, res) => {
    try {
        const { slug } = req.params;
        const data = await fetchJson(`${OTRUYEN_BASE}/truyen-tranh/${slug}`);
        res.json(data);
    } catch (e) {
        res.status(e.status || 500).json({ status: 'error', message: e.message });
    }
});

// GET /api/tim-kiem?keyword=...&page=...
app.get('/api/tim-kiem', async (req, res) => {
    try {
        const { keyword = '', page = 1 } = req.query;
        const data = await fetchJson(
            `${OTRUYEN_BASE}/tim-kiem?keyword=${encodeURIComponent(keyword)}&page=${page}`
        );
        res.json(data);
    } catch (e) {
        res.status(e.status || 500).json({ status: 'error', message: e.message });
    }
});

// GET /api/chuong/:slug/:ch -> nội dung 1 chương
// OTruyen không cho gọi thẳng theo (slug, số chương): phải lấy chi tiết truyện
// trước để tìm URL chapter_api_data khớp với số chương, rồi mới gọi URL đó.
app.get('/api/chuong/:slug/:ch', async (req, res) => {
    try {
        const { slug, ch } = req.params;

        const detail = await fetchJson(`${OTRUYEN_BASE}/truyen-tranh/${slug}`);
        const serverData = detail?.data?.item?.chapters?.[0]?.server_data || [];
        const found = serverData.find((c) => String(c.chapter_name) === String(ch));

        if (!found) {
            return res.status(404).json({ status: 'error', message: 'Không tìm thấy chương' });
        }

        const chapterData = await fetchJson(found.chapter_api_data);
        res.json(chapterData);
    } catch (e) {
        res.status(e.status || 500).json({ status: 'error', message: e.message });
    }
});

app.listen(PORT, () => {
    console.log(`✅ Proxy backend đang chạy tại http://localhost:${PORT}/api`);
});
