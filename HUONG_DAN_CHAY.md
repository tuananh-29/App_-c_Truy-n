# Hướng dẫn chạy project

Project gồm 2 phần chạy song song:

## 1. Backend proxy (thay cho Laravel còn thiếu)
Nằm trong thư mục `server/`, gọi thẳng OTruyen API thật (otruyenapi.com).

```bash
cd server
npm install
npm start
```
Mặc định chạy ở `http://localhost:8000/api` — đúng với `VITE_API_BASE_URL` trong `.env`.

## 2. Frontend (React + Vite)
Ở thư mục gốc:

```bash
npm install
npm run dev
```
Mở địa chỉ Vite in ra (thường là http://localhost:5173).

## Lưu ý
- Phải chạy backend TRƯỚC (hoặc cùng lúc), nếu không trang sẽ báo "Không thể tải danh sách truyện".
- Nếu đổi port backend, sửa `VITE_API_BASE_URL` trong file `.env` cho khớp.
