package com.example.data.model

data class FolioChapterPage(
    val pageNumber: Int,
    val heading: String,
    val paragraphs: List<String>
)

data class FolioBook(
    val id: String,
    val title: String,
    val author: String,
    val symbol: String,
    val gradientStartHex: Long,
    val gradientEndHex: Long,
    val boardHex: Long,
    val pages: List<FolioChapterPage>
) {
    val totalLeaves: Int get() = pages.size + 2
}

sealed class FolioLeafItem {
    data class Cover(val book: FolioBook) : FolioLeafItem()
    data class ContentPage(val book: FolioBook, val page: FolioChapterPage) : FolioLeafItem()
    data class EndPage(val book: FolioBook) : FolioLeafItem()
}
