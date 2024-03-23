package com.group.libraryapp.domain.user

// querydsl 적용방식 1
interface UserRepositoryCustom {
    fun findAllWithHistories(): List<User>
}
