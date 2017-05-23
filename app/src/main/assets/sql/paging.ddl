CREATE TABLE IF NOT EXISTS Paging (
BookCode INTEGER NOT NULL,
Code INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL,
ChapterIndex INTEGER,
NumberOfPagesInChapter INTEGER,
FontName TEXT,
FontSize INTEGER,
LineSpacing INTEGER,
Width INTEGER,
Height INTEGER,
VerticalGapRatio REAL,
HorizontalGapRatio REAL,
IsPortrait INTEGER,
IsDoublePagedForLandscape INTEGER
);