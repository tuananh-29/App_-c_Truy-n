<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Str;

class Manga extends Model
{
    use HasFactory;

    protected $fillable = [
        'title',
        'slug',
        'author',
        'description',
        'cover_image',
        'status',
        'views',
    ];

    protected static function booted(): void
    {
        static::creating(function (Manga $manga) {
            if (empty($manga->slug)) {
                $manga->slug = Str::slug($manga->title);
            }
        });
    }

    // Quan hệ nhiều-nhiều với Genre
    public function genres()
    {
        return $this->belongsToMany(Genre::class, 'genre_manga');
    }

    // Quan hệ một-nhiều với Chapter (sẽ dùng sau)
    public function chapters()
    {
        return $this->hasMany(Chapter::class);
    }
}