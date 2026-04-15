package com.example.SpringSecurity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpringSecurityApplicationTests {

	@Test
	void applicationClassShouldExist() {
		assertThat(SpringSecurityApplication.class).isNotNull();
	}

}
