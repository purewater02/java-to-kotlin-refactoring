package com.group.libraryapp.dto.user.request

import lombok.AllArgsConstructor
import lombok.Getter

data class UserCreateRequest(
    val name: String? = null,
    val age: Int? = null
) {
}
