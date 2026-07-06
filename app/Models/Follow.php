<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Follow extends Model
{
    protected $fillable = [
        'user_id',
        'comic_slug',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}

