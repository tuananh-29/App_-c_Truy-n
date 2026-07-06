<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('follows', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained()->cascadeOnDelete();
            // comic_slug tham chiếu tới slug của OTruyen API (không có bảng comics riêng
            // vì dữ liệu truyện được proxy trực tiếp, không lưu lại trong DB của mình)
            $table->string('comic_slug');
            $table->timestamps();

            $table->unique(['user_id', 'comic_slug']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('follows');
    }
};

