package com.group.libraryapp.service.book

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.User
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.dto.book.response.BookStatResponse
import com.group.libraryapp.type.BookType
import com.group.libraryapp.type.UserLoanStatus
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BookServiceTest @Autowired constructor(
    private val bookRepository: BookRepository,
    private val bookService: BookService,
    private val userRepository: UserRepository,
    private val userLoanHistoryRepository: UserLoanHistoryRepository,
) {

    @AfterEach
    fun clean() {
        bookRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    @DisplayName("책 등록이 정상 동작한다.")
    fun saveBook() {
        // given
        val request = BookRequest("코틀린 마스터", BookType.COMPUTER)

        // when
        bookService.saveBook(request)

        // then
        val books = bookRepository.findAll()
        assertThat(books).hasSize(1)
        assertThat(books[0].name).isEqualTo("코틀린 마스터")
    }

    @Test
    @DisplayName("책 대출이 정상 동작한다.")
    fun loanBook() {
        // given
        bookRepository.save(Book.fixture("코틀린 마스터"))
        val savedUser = userRepository.save(
            User(
                "pure",
                null,
            ),
        )
        val request = BookLoanRequest("pure", "코틀린 마스터")

        // when
        bookService.loanBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        println(">>>>>>>>>>>>>>>>>>>>>>>>>> ${results[0].bookName} <<<<<<<<<<<<<<<<<<<<<<<<<")
        assertThat(results).hasSize(1)
        assertThat(results[0].bookName).isEqualTo("코틀린 마스터")
        assertThat(results[0].user.id).isEqualTo(savedUser.id)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.LOANED)
    }

    @Test
    @DisplayName("책이 진작 대출되어 있다면, 신규 대출이 실패한다.")
    fun loanBookFailTest() {
        // given
        bookRepository.save(Book.fixture("코틀린 마스터"))
        val savedUser = userRepository.save(
            User(
                "pure",
                null,
            ),
        )
        userLoanHistoryRepository.save(
            UserLoanHistory.fixture(
                savedUser,
                "코틀린 마스터",
                UserLoanStatus.LOANED,
            ),
        )
        val request = BookLoanRequest("pure", "코틀린 마스터")

        // when & then
        val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(request)
        }.message

        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
    }

    @Test
    @DisplayName("책 반납이 정상 동작한다")
    fun returnBook() {
        // given
        bookRepository.save(Book.fixture("코틀린 마스터"))
        val savedUser = userRepository.save(
            User(
                "pure",
                null,
            ),
        )
        userLoanHistoryRepository.save(
            UserLoanHistory.fixture(
                savedUser,
                "코틀린 마스터",
                UserLoanStatus.LOANED,

            ),
        )
        val request = BookReturnRequest("pure", "코틀린 마스터")

        // when
        bookService.returnBook(request)

        // then
        val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].status).isEqualTo(UserLoanStatus.RETURNED)
    }

    @Test
    @DisplayName("책 대여 권수를 정상 응답한다")
    fun countLoanedBookTest() {
        // given
        val savedUser = userRepository.save(User("pure", null))
        userLoanHistoryRepository.saveAll(
            listOf(
                UserLoanHistory.fixture(savedUser, "A"),
                UserLoanHistory.fixture(savedUser, "B", UserLoanStatus.RETURNED),
            ),
        )

        // when
        val result = bookService.countLoanedBook()

        // then
        assertThat(result).isEqualTo(1)
    }

    @Test
    @DisplayName("분야별 책 권수를 정상 확인한다")
    fun getBookStatisticsTest() {
        // given
        bookRepository.saveAll(
            listOf(
                Book.fixture("A", BookType.COMPUTER),
                Book.fixture("B", BookType.COMPUTER),
                Book.fixture("C", BookType.SOCIETY),
            ),
        )

        // when
        val results = bookService.getBookStatistics()

        // then
        assertCount(results, BookType.COMPUTER, 2)
        assertCount(results, BookType.SOCIETY, 1)
    }

    private fun assertCount(results: List<BookStatResponse>, type: BookType, count: Long) {
        assertThat(results.first { result -> result.type == type }.count).isEqualTo(count)
    }
}
