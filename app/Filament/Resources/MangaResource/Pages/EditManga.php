<?php

namespace App\Filament\Resources\MangaResource\Pages;

use App\Filament\Resources\MangaResource;
use Filament\Actions;
use Filament\Resources\Pages\EditRecord;

class EditManga extends EditRecord
{
    protected static string $resource = MangaResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }
}