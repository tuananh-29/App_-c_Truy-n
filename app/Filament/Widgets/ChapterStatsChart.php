<?php

namespace App\Filament\Widgets;

use App\Models\Chapter;
use Filament\Widgets\ChartWidget;

class ChapterStatsChart extends ChartWidget
{
    protected static ?string $heading = 'Chương theo tháng';
    protected static ?string $description = '(+15%) increase this month';
    protected static ?int $sort = 3;
    protected int|string|array $columnSpan = 1;

    protected function getData(): array
    {
        $months = collect(range(8, 0))->map(fn ($i) => now()->subMonths($i));

        $data = $months->map(function ($month) {
            return Chapter::whereMonth('created_at', $month->month)
                ->whereYear('created_at', $month->year)
                ->count();
        });

        $labels = $months->map(fn ($month) => $month->format('M'));

        return [
            'datasets' => [
                [
                    'label' => 'Chương mới',
                    'data' => $data->toArray(),
                    'borderColor' => '#ffffff',
                    'backgroundColor' => 'rgba(255,255,255,0.1)',
                    'fill' => true,
                    'tension' => 0.4,
                    'borderWidth' => 2,
                    'pointBackgroundColor' => '#ffffff',
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