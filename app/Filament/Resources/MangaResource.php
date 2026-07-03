<?php

namespace App\Filament\Resources;

use App\Filament\Resources\MangaResource\Pages;
use App\Models\Manga;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Table;
use Illuminate\Support\Str;

class MangaResource extends Resource
{
    protected static ?string $model = Manga::class;

    protected static ?string $navigationIcon = 'heroicon-o-book-open';

    protected static ?string $navigationLabel = 'Truyện';

    protected static ?string $modelLabel = 'truyện';

    protected static ?string $pluralModelLabel = 'Truyện';

    public static function form(Form $form): Form
    {
        return $form
            ->schema([
                Forms\Components\Section::make('Thông tin cơ bản')
                    ->schema([
                        Forms\Components\TextInput::make('title')
                            ->label('Tên truyện')
                            ->required()
                            ->maxLength(255)
                            ->live(onBlur: true)
                            ->afterStateUpdated(function (string $operation, $state, Forms\Set $set) {
                                if ($operation === 'create') {
                                    $set('slug', Str::slug($state));
                                }
                            }),

                        Forms\Components\TextInput::make('slug')
                            ->label('Slug (URL)')
                            ->required()
                            ->maxLength(255)
                            ->unique(ignoreRecord: true),

                        Forms\Components\TextInput::make('author')
                            ->label('Tác giả')
                            ->maxLength(255),

                        Forms\Components\Select::make('status')
                            ->label('Trạng thái')
                            ->options([
                                'ongoing'   => '🟢 Đang ra',
                                'completed' => '✅ Hoàn thành',
                                'dropped'   => '🔴 Đã drop',
                            ])
                            ->default('ongoing')
                            ->required(),
                    ])->columns(2),

                Forms\Components\Section::make('Nội dung & Thể loại')
                    ->schema([
                        Forms\Components\Textarea::make('description')
                            ->label('Tóm tắt')
                            ->rows(4)
                            ->columnSpanFull(),

                        Forms\Components\Select::make('genres')
                            ->label('Thể loại')
                            ->relationship('genres', 'name')
                            ->multiple()
                            ->preload()
                            ->searchable()
                            ->columnSpanFull(),
                    ]),

               Forms\Components\Section::make('Ảnh bìa')
    ->schema([
        Forms\Components\TextInput::make('cover_image')
            ->label('URL ảnh bìa')
            ->placeholder('https://example.com/anh-bia.jpg')
            ->columnSpanFull(),
    ]),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                Tables\Columns\ImageColumn::make('cover_image')
                    ->label('Bìa')
                    ->width(50)
                    ->height(70),

                Tables\Columns\TextColumn::make('title')
                    ->label('Tên truyện')
                    ->searchable()
                    ->sortable(),

                Tables\Columns\TextColumn::make('author')
                    ->label('Tác giả')
                    ->searchable(),

                Tables\Columns\TextColumn::make('genres.name')
                    ->label('Thể loại')
                    ->badge()
                    ->separator(','),

                Tables\Columns\BadgeColumn::make('status')
                    ->label('Trạng thái')
                    ->colors([
                        'success' => 'ongoing',
                        'primary' => 'completed',
                        'danger'  => 'dropped',
                    ])
                    ->formatStateUsing(fn (string $state): string => match ($state) {
                        'ongoing'   => 'Đang ra',
                        'completed' => 'Hoàn thành',
                        'dropped'   => 'Đã drop',
                        default     => $state,
                    }),

                Tables\Columns\TextColumn::make('views')
                    ->label('Lượt xem')
                    ->numeric()
                    ->sortable(),

                Tables\Columns\TextColumn::make('created_at')
                    ->label('Ngày tạo')
                    ->dateTime('d/m/Y')
                    ->sortable()
                    ->toggleable(isToggledHiddenByDefault: true),
            ])
            ->filters([
                Tables\Filters\SelectFilter::make('status')
                    ->label('Trạng thái')
                    ->options([
                        'ongoing'   => 'Đang ra',
                        'completed' => 'Hoàn thành',
                        'dropped'   => 'Đã drop',
                    ]),

                Tables\Filters\SelectFilter::make('genres')
                    ->label('Thể loại')
                    ->relationship('genres', 'name')
                    ->multiple(),
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
            'index'  => Pages\ListMangas::route('/'),
            'create' => Pages\CreateManga::route('/create'),
            'edit'   => Pages\EditManga::route('/{record}/edit'),
        ];
    }
}