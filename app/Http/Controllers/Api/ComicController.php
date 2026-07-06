<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Cache;

class ComicController extends Controller
{
    private string $base = 'https://otruyenapi.com/v1/api';

    // GET /api/truyen-moi
    public function home()
    {
        $res = Cache::remember('otruyen_home', 300, function () {
            return Http::get("{$this->base}/home")->json();
        });

        return response()->json($res);
    }

    // GET /api/danh-sach/{type}  (truyen-moi, dang-phat-hanh, hoan-thanh)
    public function list($type)
    {
        $page = request('page', 1);
        $cacheKey = "otruyen_list_{$type}_{$page}";

        $res = Cache::remember($cacheKey, 300, function () use ($type, $page) {
            return Http::get("{$this->base}/danh-sach/{$type}", ['page' => $page])->json();
        });

        return response()->json($res);
    }

    // GET /api/truyen/{slug}
    public function detail($slug)
    {
        $res = Cache::remember("otruyen_detail_{$slug}", 300, function () use ($slug) {
            return Http::get("{$this->base}/truyen-tranh/{$slug}")->json();
        });

        return response()->json($res);
    }

    // GET /api/chuong/{slug}/{chapter}
    public function chapter($slug, $chapter)
    {
        $res = Http::get("{$this->base}/truyen-tranh/{$slug}/chuong-{$chapter}")->json();
        return response()->json($res);
    }

    // GET /api/tim-kiem?q=...
    public function search()
    {
        $res = Http::get("{$this->base}/tim-kiem", [
            'keyword' => request('q'),
            'page'    => request('page', 1),
        ])->json();

        return response()->json($res);
    }
}