package com.example.data.local

import com.example.data.model.FolioBook
import com.example.data.model.FolioChapterPage
import com.example.data.model.FolioLeafItem

object StoryLibraryRepository {

    val books: List<FolioBook> = listOf(
        FolioBook(
            id = "lantern_keeper",
            title = "The Lantern Keeper",
            author = "Mira Alden",
            symbol = "☾",
            gradientStartHex = 0xFF6E1F2B,
            gradientEndHex = 0xFF2E0C14,
            boardHex = 0xFF3A1D12,
            pages = listOf(
                FolioChapterPage(
                    pageNumber = 1,
                    heading = "Chapter One: The Last Light",
                    paragraphs = listOf(
                        "On the cliffs above Harrow Bay stood a lighthouse that had not guided a ship in forty years. Its keeper, an old woman named Ilse, climbed its three hundred steps every dusk anyway.",
                        "“Someone may still be out there,” she told the gulls, who had heard it a thousand times and never once agreed."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 2,
                    heading = "The Stranger",
                    paragraphs = listOf(
                        "One November night a boy appeared at her door, soaked to the bone, holding a lantern that burned without oil. He said only that the sea had sent him.",
                        "Ilse set the kettle on and said nothing. In forty years she had learned that the sea never sends anything without a reason."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 3,
                    heading = "The Flame",
                    paragraphs = listOf(
                        "The lantern’s flame was pale blue and perfectly still, even in the wind. When Ilse lifted it to the great lens, the whole bay lit up like morning.",
                        "Far out on the black water, a hundred tiny lights answered, one after another, like stars deciding to come home."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 4,
                    heading = "Chapter Two: Home",
                    paragraphs = listOf(
                        "By dawn the fishing boats that had been lost for years drifted into the harbour, their crews asleep and smiling at their oars.",
                        "The boy was gone. On the table lay his lantern, cold now, and a note: “Keep the light. Someone will always be out there.”"
                    )
                )
            )
        ),
        FolioBook(
            id = "quiet_mornings",
            title = "Quiet Mornings",
            author = "Daniel Reyes",
            symbol = "☀",
            gradientStartHex = 0xFF1F5C58,
            gradientEndHex = 0xFF0D2B2B,
            boardHex = 0xFF132D2C,
            pages = listOf(
                FolioChapterPage(
                    pageNumber = 1,
                    heading = "Before the Noise",
                    paragraphs = listOf(
                        "The first hour of the day belongs to you, until you hand it over. Most of us hand it over before our feet touch the floor, reaching for a phone that holds everyone else’s urgent things.",
                        "This book asks for one small change: wait twenty minutes."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 2,
                    heading = "The Twenty-Minute Rule",
                    paragraphs = listOf(
                        "Make tea, open a window, sit down. You do not need a method. You need only to notice what your mind does when nothing is demanded of it.",
                        "At first it will race. By the fifth morning it will slow, and by the tenth you may find you look forward to the silence."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 3,
                    heading = "A Small Ritual",
                    paragraphs = listOf(
                        "Write three lines each morning: one thing you are grateful for, one thing you will do today, and one thing you will let go of.",
                        "Keep the notebook beside your bed. The point is not good writing. The point is arriving in your own day on purpose."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 4,
                    heading = "Begin Tomorrow",
                    paragraphs = listOf(
                        "You will miss mornings, and that is fine. A habit is not a chain; it is a path worn by returning to it.",
                        "Tomorrow, wait twenty minutes. The world will still be there, and you will meet it more kindly."
                    )
                )
            )
        ),
        FolioBook(
            id = "atlas_small_wonders",
            title = "Atlas of Small Wonders",
            author = "Priya Nair",
            symbol = "✦",
            gradientStartHex = 0xFFB8741A,
            gradientEndHex = 0xFF5A2F08,
            boardHex = 0xFF42240A,
            pages = listOf(
                FolioChapterPage(
                    pageNumber = 1,
                    heading = "Why Look Closely",
                    paragraphs = listOf(
                        "A map shows you where things are. This atlas shows you what is easy to miss: a leaf that tells time, a river that flows upward twice a day, a bird that navigates by the magnetic field.",
                        "Wonder is not rare. Attention is."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 2,
                    heading = "The Tide That Runs Backwards",
                    paragraphs = listOf(
                        "In a few wide estuaries, the incoming sea is so strong that it pushes a wave against the river’s current. Locals call it a bore, and surfers ride it for miles upstream.",
                        "It arrives with a low rumble, and then the river simply reverses."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 3,
                    heading = "The Sleeping Leaves",
                    paragraphs = listOf(
                        "Many plants fold their leaves at night, as if tucking themselves in. Charles Darwin studied this and found it was driven by an internal clock, not by light alone.",
                        "Even in a dark room, the leaves keep their schedule."
                    )
                ),
                FolioChapterPage(
                    pageNumber = 4,
                    heading = "Carry a Notebook",
                    paragraphs = listOf(
                        "Wonders do not need distance. The next one may be on your windowsill, in the way frost draws ferns on glass.",
                        "Notice one small thing each day. After a year, you will have written your own atlas."
                    )
                )
            )
        )
    )

    fun getBookById(bookId: String): FolioBook? = books.firstOrNull { it.id == bookId }

    fun buildLeaves(book: FolioBook): List<FolioLeafItem> {
        return buildList {
            add(FolioLeafItem.Cover(book))
            book.pages.forEach { pg -> add(FolioLeafItem.ContentPage(book, pg)) }
            add(FolioLeafItem.EndPage(book))
        }
    }
}
