<?php

namespace App\Filament\Widgets;

use App\Models\Manga;
use App\Models\Genre;
use App\Models\Chapter;
use Filament\Widgets\StatsOverviewWidget as BaseWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;

class StatsOverview extends BaseWidget
{
    protected static ?int $sort = 1;

    protected function getCards(): array
    {
        $totalMangas = Manga::count();
        $totalGenres = Genre::count();
        $totalChapters = Chapter::count();

        $newMangasThisMonth = Manga::whereMonth('created_at', now()->month)
            ->whereYear('created_at', now()->year)
            ->count();

        $newMangasLastMonth = Manga::whereMonth('created_at', now()->subMonth()->month)
            ->whereYear('created_at', now()->subMonth()->year)
            ->count();

        $growth = $newMangasLastMonth > 0
            ? round((($newMangasThisMonth - $newMangasLastMonth) / $newMangasLastMonth) * 100, 1)
            : 0;

        return [
            Stat::make('Tổng truyện', $totalMangas)
                ->description($growth >= 0 ? "+{$growth}% tháng này" : "{$growth}% tháng này")
                ->descriptionIcon($growth >= 0 ? 'heroicon-m-arrow-trending-up' : 'heroicon-m-arrow-trending-down')
                ->color('danger')
                ->icon('heroicon-o-book-open'),

            Stat::make('Truyện mới tháng này', $newMangasThisMonth)
                ->description('Trong tháng ' . now()->format('m/Y'))
                ->descriptionIcon('heroicon-m-plus-circle')
                ->color('success')
                ->icon('heroicon-o-sparkles'),

            Stat::make('Thể loại', $totalGenres)
                ->description('Đang hoạt động')
                ->descriptionIcon('heroicon-m-tag')
                ->color('warning')
                ->icon('heroicon-o-tag'),

            Stat::make('Tổng chương', $totalChapters)
                ->description('Tất cả chương')
                ->descriptionIcon('heroicon-m-document-text')
                ->color('info')
                ->icon('heroicon-o-document-text'),
        ];
    }
}