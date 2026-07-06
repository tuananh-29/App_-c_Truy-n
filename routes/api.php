<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\ComicController;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\UserController;

/*
|--------------------------------------------------------------------------
| API Routes - TruyenMoiNgay
|--------------------------------------------------------------------------
| Đây là toàn bộ route mà nhóm Android (Retrofit) và Web (axios) sẽ gọi vào.
| Base URL khi chạy local: http://127.0.0.1:8000/api
| Android emulator gọi bằng: http://10.0.2.2:8000/api
*/

// ── Public: dữ liệu truyện (proxy sang OTruyen API) ─────────────
Route::get('/truyen-moi', [ComicController::class, 'home']);
Route::get('/danh-sach/{type}', [ComicController::class, 'list']);
Route::get('/truyen/{slug}', [ComicController::class, 'detail']);
Route::get('/chuong/{slug}/{chapter}', [ComicController::class, 'chapter']);
Route::get('/tim-kiem', [ComicController::class, 'search']);

// ── Public: đăng ký / đăng nhập ──────────────────────────────────
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

// ── Private: cần JWT token (Header: Authorization: Bearer <token>) ─  
Route::middleware('auth:api')->group(function () {
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me', [AuthController::class, 'me']);
    Route::post('/refresh', [AuthController::class, 'refresh']);

    Route::post('/follow/{slug}', [UserController::class, 'follow']);
    Route::delete('/follow/{slug}', [UserController::class, 'unfollow']);
    Route::get('/followed', [UserController::class, 'followed']);
});

// ── Private: chỉ role = admin mới gọi được ──────────────────────
Route::middleware(['auth:api', 'role:admin'])->group(function () {
    Route::get('/admin/users', [UserController::class, 'allUsers']);
});


