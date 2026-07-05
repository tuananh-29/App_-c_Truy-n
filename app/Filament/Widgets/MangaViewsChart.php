<?php

namespace App\Filament\Widgets;

use App\Models\Manga;
use Filament\Widgets\ChartWidget;

class MangaViewsChart extends ChartWidget
{
    protected static ?string $heading = 'Truyện mới theo ngày';
    protected static ?string $description = 'Last 7 days performance';
    protected static ?int $sort = 2;
    protected int|string|array $columnSpan = 1;

    protected function getData(): array
    {
        $days = collect(range(6, 0))->map(fn ($i) => now()->subDays($i));

        $data = $days->map(function ($day) {
            return Manga::whereDate('created_at', $day->toDateString())->count();
        });

        $labels = $days->map(fn ($day) => $day->format('D'));

        return [
            'datasets' => [
                [
                    'label' => 'Truyện mới',
                    'data' => $data->toArray(),
                  'backgroundColor' => '#ec4899', // hồng  
'borderColor' => '#db2777',
                    'borderWidth' => 2,
                ],
            ],
            'labels' => $labels->toArray(),
        ];
    }

    protected function getType(): string
    {
        return 'bar';
    }

    protected function getOptions(): array
    {
        return [
            'plugins' => [
                'legend' => ['display' => false],
            ],
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