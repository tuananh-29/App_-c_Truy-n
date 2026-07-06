<?php

namespace App\Filament\Widgets;

use App\Models\Follow;
use App\Models\User;
use Filament\Widgets\StatsOverviewWidget as BaseWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;

class StatsOverview extends BaseWidget
{
    protected static ?int $sort = 1;

    protected function getStats(): array
    {
        $totalUsers = User::count();

        $newUsersThisMonth = User::whereMonth('created_at', now()->month)
            ->whereYear('created_at', now()->year)
            ->count();

        $newUsersLastMonth = User::whereMonth('created_at', now()->subMonth()->month)
            ->whereYear('created_at', now()->subMonth()->year)
            ->count();

        $growth = $newUsersLastMonth > 0
            ? round((($newUsersThisMonth - $newUsersLastMonth) / $newUsersLastMonth) * 100, 1)
            : 0;

        $totalFollows = Follow::count();
        $totalActiveUsers = User::where('is_active', true)->count();

        return [
            Stat::make('Tổng người dùng', $totalUsers)
                ->description($growth >= 0 ? "+{$growth}% tháng này" : "{$growth}% tháng này")
                ->descriptionIcon($growth >= 0 ? 'heroicon-m-arrow-trending-up' : 'heroicon-m-arrow-trending-down')
                ->color('danger')
                ->icon('heroicon-o-users'),

            Stat::make('User mới tháng này', $newUsersThisMonth)
                ->description('Trong tháng ' . now()->format('m/Y'))
                ->descriptionIcon('heroicon-m-plus-circle')
                ->color('success')
                ->icon('heroicon-o-sparkles'),

            Stat::make('Tài khoản đang hoạt động', $totalActiveUsers)
                ->description('is_active = true')
                ->descriptionIcon('heroicon-m-check-circle')
                ->color('warning')
                ->icon('heroicon-o-shield-check'),

            Stat::make('Tổng lượt theo dõi truyện', $totalFollows)
                ->description('Bảng follows (comic_slug từ OTruyen)')
                ->descriptionIcon('heroicon-m-heart')
                ->color('info')
                ->icon('heroicon-o-bookmark'),
        ];
    }
}
