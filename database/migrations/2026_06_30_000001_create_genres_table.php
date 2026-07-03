<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('mangas', function (Blueprint $table) {
            $table->id();
            $table->string('title');                    // Tên truyện
            $table->string('slug')->unique();           // URL thân thiện
            $table->string('author')->nullable();       // Tên tác giả (text đơn giản)
            $table->text('description')->nullable();    // Tóm tắt nội dung
            $table->string('cover_image')->nullable();  // Đường dẫn ảnh bìa
            $table->enum('status', ['ongoing', 'completed', 'dropped'])
                ->default('ongoing');                   // Trạng thái: đang ra / hoàn thành / đã drop
            $table->unsignedBigInteger('views')->default(0); // Lượt xem
            $table->timestamps();
        });

        // Bảng trung gian: 1 truyện có nhiều thể loại
        Schema::create('genre_manga', function (Blueprint $table) {
            $table->id();
            $table->foreignId('manga_id')->constrained()->cascadeOnDelete();
            $table->foreignId('genre_id')->constrained()->cascadeOnDelete();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('genre_manga');
        Schema::dropIfExists('mangas');
    }
};