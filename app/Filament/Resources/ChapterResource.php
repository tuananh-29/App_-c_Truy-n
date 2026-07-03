<?php

namespace App\Filament\Resources;

use App\Filament\Resources\ChapterResource\Pages;
use App\Models\Chapter;
use App\Models\Manga;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Table;

class ChapterResource extends Resource
{
    protected static ?string $model = Chapter::class;

    protected static ?string $navigationIcon = 'heroicon-o-document-text';

    protected static ?string $navigationLabel = 'Chương';

    protected static ?string $modelLabel = 'chương';

    protected static ?string $pluralModelLabel = 'Chương';

    public static function form(Form $form): Form
    {
        return $form
            ->schema([
                Forms\Components\Section::make('Thông tin chương')
                    ->schema([
                        Forms\Components\Select::make('manga_id')
                            ->label('Truyện')
                            ->relationship('manga', 'title')
                            ->searchable()
                            ->preload()
                            ->required(),

                        Forms\Components\TextInput::make('number')
                            ->label('Số chương')
                            ->numeric()
                            ->required()
                            ->minValue(0),

                        Forms\Components\TextInput::make('title')
                            ->label('Tên chương (tuỳ chọn)')
                            ->maxLength(255)
                            ->placeholder('Ví dụ: Khởi đầu mới'),
                    ])->columns(3),

                Forms\Components\Section::make('Ảnh trang truyện')
                    ->schema([
                        Forms\Components\FileUpload::make('images')
                            ->label('Upload ảnh trang (có thể chọn nhiều ảnh cùng lúc)')
                            ->image()
                            ->multiple()
                            ->reorderable()
                            ->directory('chapters')
                            ->helperText('Kéo thả để sắp xếp thứ tự trang. Hỗ trợ JPG, PNG, WebP.')
                            ->columnSpanFull(),
                    ]),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                Tables\Columns\TextColumn::make('manga.title')
                    ->label('Truyện')
                    ->searchable()
                    ->sortable(),

                Tables\Columns\TextColumn::make('number')
                    ->label('Chương')
                    ->sortable()
                    ->formatStateUsing(fn ($state) => 'Chương ' . $state),

                Tables\Columns\TextColumn::make('title')
                    ->label('Tên chương')
                    ->placeholder('(Không có tên)')
                    ->searchable(),

                Tables\Columns\TextColumn::make('views')
                    ->label('Lượt xem')
                    ->numeric()
                    ->sortable(),

                Tables\Columns\TextColumn::make('created_at')
                    ->label('Ngày đăng')
                    ->dateTime('d/m/Y H:i')
                    ->sortable(),
            ])
            ->defaultSort('manga_id')
            ->filters([
                Tables\Filters\SelectFilter::make('manga')
                    ->label('Lọc theo truyện')
                    ->relationship('manga', 'title'),
            ])
            ->actions([
                Tables\Actions\EditAction::make(),
                Tables\Actions\DeleteAction::make(),
            ])
            ->bulkActions([
                Tables\Actions\BulkActionGroup::make([
                    Tables\Actions\DeleteBulkAction::make(),
                ]),
            ]);
    }

    public static function getPages(): array
    {
        return [
            'index'  => Pages\ListChapters::route('/'),
            'create' => Pages\CreateChapter::route('/create'),
            'edit'   => Pages\EditChapter::route('/{record}/edit'),
        ];
    }
}