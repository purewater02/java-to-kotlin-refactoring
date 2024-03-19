package com.group.libraryapp.service.book;

import com.group.libraryapp.domain.book.Book
import com.group.libraryapp.domain.book.BookRepository
import com.group.libraryapp.domain.user.UserRepository
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository
import com.group.libraryapp.dto.book.request.BookLoanRequest
import com.group.libraryapp.dto.book.request.BookRequest
import com.group.libraryapp.dto.book.request.BookReturnRequest
import com.group.libraryapp.user.User
import com.group.libraryapp.user.loanhistory.UserLoanHistory
import org.junit.jupiter.api.Test;
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JavaBookServiceTest @Autowired constructor(
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
		//given
		val request = BookRequest("이상한 나라의 엘리스")

		//when
		bookService.saveBook(request)

		//then
		val books = bookRepository.findAll()
		assertThat(books).hasSize(1)
		assertThat(books[0].name).isEqualTo("이상한 나라의 엘리스")
	}

	@Test
	@DisplayName("책 대출이 정상 동작한다.")
	fun loanBook() {
		//given
		bookRepository.save(Book("코틀린 마스터"))
		val savedUser = userRepository.save(
			User(
				"pure",
				null
			)
		)
		val request = BookLoanRequest("pure", "코틀린 마스터")

		//when
		bookService.loanBook(request)

		//then
		val results = userLoanHistoryRepository.findAll()
		println(">>>>>>>>>>>>>>>>>>>>>>>>>> ${results[0].bookName} <<<<<<<<<<<<<<<<<<<<<<<<<")
		assertThat(results).hasSize(1)
		assertThat(results[0].bookName).isEqualTo("코틀린 마스터")
		assertThat(results[0].user.id).isEqualTo(savedUser.id)
		assertThat(results[0].isReturn).isFalse()
	}

	@Test
	@DisplayName("책이 진작 대출되어 있다면, 신규 대출이 실패한다.")
	fun loanBookFailTest() {
		//given
		bookRepository.save(Book("코틀린 마스터"))
		val savedUser = userRepository.save(
			User(
				"pure",
				null
			)
		)
		userLoanHistoryRepository.save(
			UserLoanHistory(
				savedUser,
				"코틀린 마스터",
				false
			)
		)
		val request = BookLoanRequest("pure", "코틀린 마스터")

		//when & then
		val message = assertThrows<IllegalArgumentException> {
            bookService.loanBook(request)
        }.message

        assertThat(message).isEqualTo("진작 대출되어 있는 책입니다")
	}

	@Test
	@DisplayName("책 반납이 정상 동작한다")
	fun returnBook() {
		//given
		bookRepository.save(Book("코틀린 마스터"))
		val savedUser = userRepository.save(
			User(
				"pure",
				null
			)
		)
		userLoanHistoryRepository.save(
			UserLoanHistory(
				savedUser,
				"코틀린 마스터",
				false
			)
		)
		val request = BookReturnRequest("pure", "코틀린 마스터")

		//when
		bookService.returnBook(request)

		//then
		val results = userLoanHistoryRepository.findAll()
        assertThat(results).hasSize(1)
        assertThat(results[0].isReturn).isTrue()
	}
}