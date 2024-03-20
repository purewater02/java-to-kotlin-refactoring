package com.group.libraryapp.dto.book.request

import lombok.AllArgsConstructor
import lombok.Getter

data class BookReturnRequest(
    val userName: String,
    val bookName: String,
) {
}
