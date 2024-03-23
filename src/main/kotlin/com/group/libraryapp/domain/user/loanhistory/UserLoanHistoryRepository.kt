package com.group.libraryapp.domain.user.loanhistory

import org.springframework.data.jpa.repository.JpaRepository

interface UserLoanHistoryRepository : JpaRepository<UserLoanHistory, Long> {

//    querydsl로 변경
//    fun findByBookName(bookName: String): UserLoanHistory?
//
//    fun findByBookNameAndStatus(bookName: String, status: UserLoanStatus): UserLoanHistory?
//
//    fun countByStatus(status: UserLoanStatus): Long
}
