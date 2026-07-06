<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;

class CheckRole
{
    public function handle(Request $request, Closure $next, string $role)
    {
        $user = auth('api')->user();

        if (!$user || $user->role !== $role) {
            return response()->json([
                'message' => 'Bạn không có quyền truy cập chức năng này',
            ], 403);
        }

        return $next($request);
    }
}