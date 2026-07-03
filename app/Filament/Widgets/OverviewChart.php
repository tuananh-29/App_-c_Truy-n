<?php

namespace App\Filament\Widgets;

use App\Models\Manga;
use App\Models\Chapter;
use Filament\Widgets\ChartWidget;

class OverviewChart extends ChartWidget
{
    protected static ?string $heading = 'Tổng quan hệ thống';
    protected static ?string $description = 'Truyện & Chương theo tháng';
    protected static ?int $sort = 4;
    protected int|string|array $columnSpan = 1;

    protected function getData(): array
    {
        $months = collect(range(8, 0))->map(fn ($i) => now()->subMonths($i));

        $mangaData = $months->map(fn ($month) => Manga::whereMonth('created_at', $month->month)
            ->whereYear('created_at', $month->year)->count());

        $chapterData = $months->map(fn ($month) => Chapter::whereMonth('created_at', $month->month)
            ->whereYear('created_at', $month->year)->count());

        $labels = $months->map(fn ($month) => $month->format('M'));

        return [
            'datasets' => [
                [
                    'label' => 'Truyện',
                    'data' => $mangaData->toArray(),
                    'borderColor' => '#e91e63',
                    'backgroundColor' => 'rgba(233,30,99,0.1)',
                    'fill' => true,
                    'tension' => 0.4,
                    'borderWidth' => 2,
                ],
                [
                    'label' => 'Chương',
                    'data' => $chapterData->toArray(),
                    'borderColor' => '#ffffff',
                    'backgroundColor' => 'rgba(255,255,255,0.05)',
                    'fill' => true,
                    'tension' => 0.4,
                    'borderWidth' => 2,
                ],
            ],
            'labels' => $labels->toArray(),
        ];
    }

    protected function getType(): string
    {
        return 'line';
    }

    protected function getOptions(): array
    {
        return [
            'scales' => [
                'y' => [
                    'ticks' => ['color' => 'rgba(255,255,255,0.7)'],
                    'grid' => ['color' => 'rgba(255,255,255,0.1)'],
                ],
                'x' => [
                    'ticks' => ['color' => 'rgba(255,255,255,0.7)'],
                    'grid' => ['display' => false],
                ],
            ],
        ];
    }
}