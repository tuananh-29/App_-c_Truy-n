<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;
use Tymon\JWTAuth\Facades\JWTAuth;

class AuthController extends Controller
{
    // POST /api/register
    public function register(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'name'     => 'required|string|max:100',
            'email'    => 'required|string|email|max:150|unique:users',
            'password' => 'required|string|min:6|confirmed',
            // yêu cầu gửi kèm password_confirmation
        ]);

        if ($validator->fails()) {
            return response()->json(['errors' => $validator->errors()], 422);
        }

        $user = User::create([
            'name'     => $request->name,
            'email'    => $request->email,
            'password' => Hash::make($request->password),
            'coins'    => 0,
        ]);

        $token = JWTAuth::fromUser($user);

        return response()->json([
            'message' => 'Đăng ký thành công',
            'user'    => $user,
            'token'   => $token,
        ], 201);
    }

    // POST /api/login
    public function login(Request $request)
    {
        $credentials = $request->only('email', 'password');

        if (!$token = auth('api')->attempt($credentials)) {
            return response()->json(['message' => 'Sai email hoặc mật khẩu'], 401);
        }

        return response()->json([
            'user'  => auth('api')->user(),
            'token' => $token,
        ]);
    }

    // GET /api/me
    public function me()
    {
        return response()->json(auth('api')->user());
    }

    // POST /api/logout
    public function logout()
    {
        auth('api')->logout();
        return response()->json(['message' => 'Đã đăng xuất']);
    }

    // POST /api/refresh
    public function refresh()
    {
        return response()->json([
            'token' => auth('api')->refresh(),
        ]);
    }
}

