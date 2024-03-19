package com.group.libraryapp.service.user;

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import com.group.libraryapp.domain.user.UserRepository;
import com.group.libraryapp.dto.user.request.UserCreateRequest
import com.group.libraryapp.dto.user.request.UserUpdateRequest
import com.group.libraryapp.user.User
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
class UserServiceTest @Autowired constructor(
		private val userRepository: UserRepository,
		private val userService: UserService,
) {

	@AfterEach
	fun clean() {
		userRepository.deleteAll()
	}

	@Test
	@DisplayName("유저 저장이 정상 동작한다.")
	fun saveUser() {
		//given
		val request = UserCreateRequest("pure", null)
		//when
		userService.saveUser(request)
		//then
		val results = userRepository.findAll()
		assertThat(results).hasSize(1)
		assertThat(results[0].name).isEqualTo("pure")
		assertThat(results[0].age).isNull()
	}

	@Test
	@DisplayName("유저 조회가 정상 동작한다.")
	fun getUserTest() {
		//given
		userRepository.saveAll(listOf(
			User("A", 20),
			User("B", null)
		))
		//when
		val results = userService.getUsers()

		//then
		assertThat(results).hasSize(2)
		assertThat(results).extracting("name").containsExactlyInAnyOrder("A", "B")
		assertThat(results).extracting("age").containsExactlyInAnyOrder(20, null)
	}

	@Test
	@DisplayName("유저 수정이 정상 동작한다.")
	fun updateUserNameTest() {
		//given
		val savedUser = userRepository.save(User("A", null))
		val request = UserUpdateRequest(savedUser.id!!, "B")

		//when
		userService.updateUserName(request)

		//then
		val result = userRepository.findAll()[0]
		assertThat(result.name).isEqualTo("B")
	}

	@Test
	@DisplayName("유저 삭제가 정상 동작한다.")
	fun deleteUserNameTest() {
		//given
		userRepository.save(User("A", null))

		//when
		userService.deleteUser("A")

		//then
		assertThat(userRepository.findAll()).isEmpty()
	}
}