<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('chapters', function (Blueprint $table) {
            $table->id();
            $table->foreignId('manga_id')->constrained()->cascadeOnDelete(); // Thuộc truyện nào
            $table->integer('number');                    // Số chương (1, 2, 3...)
            $table->string('title')->nullable();          // Tên chương (tuỳ chọn)
            $table->unsignedBigInteger('views')->default(0); // Lượt xem chương
            $table->timestamps();
        });

        Schema::create('chapter_images', function (Blueprint $table) {
            $table->id();
            $table->foreignId('chapter_id')->constrained()->cascadeOnDelete(); // Thuộc chương nào
            $table->string('image_path');                 // Đường dẫn ảnh trang
            $table->integer('order')->default(0);         // Thứ tự trang (trang 1, 2, 3...)
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('chapter_images');
        Schema::dropIfExists('chapters');
    }
};