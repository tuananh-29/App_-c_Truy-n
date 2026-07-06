<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Follow;
use App\Models\User;
use Illuminate\Http\Request;

class UserController extends Controller
{
    // GET /api/admin/users - chỉ admin gọi được
    public function allUsers()
    {
        return response()->json(User::all());
    }

    // POST /api/follow/{slug}
    public function follow($slug)
    {
        $userId = auth('api')->id();

        Follow::firstOrCreate([
            'user_id'     => $userId,
            'comic_slug'  => $slug,
        ]);

        return response()->json(['message' => 'Đã theo dõi truyện', 'slug' => $slug]);
    }

    // DELETE /api/follow/{slug}
    public function unfollow($slug)
    {
        Follow::where('user_id', auth('api')->id())
            ->where('comic_slug', $slug)
            ->delete();

        return response()->json(['message' => 'Đã bỏ theo dõi truyện', 'slug' => $slug]);
    }

    // GET /api/followed
    public function followed()
    {
        $slugs = Follow::where('user_id', auth('api')->id())
            ->pluck('comic_slug');

        return response()->json(['slugs' => $slugs]);
    }
}

