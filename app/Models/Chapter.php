<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Chapter extends Model
{
    use HasFactory;

    protected $fillable = [
        'manga_id',
        'number',
        'title',
        'views',
    ];

    // Thuộc về Manga nào
    public function manga()
    {
        return $this->belongsTo(Manga::class);
    }

    // Có nhiều ảnh trang
    public function images()
    {
        return $this->hasMany(ChapterImage::class)->orderBy('order');
    }
}