package com.group.libraryapp.dto.user.request

import lombok.AllArgsConstructor
import lombok.Getter

data class UserUpdateRequest(
    val id: Long = 0,
    val name: String,
) {
}
