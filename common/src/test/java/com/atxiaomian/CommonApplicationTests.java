package com.atxiaomian;

import org.junit.jupiter.api.Test;
import com.atxiaomian.lease.common.mybatisplus.MybatisPlusConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

class CommonApplicationTests {

	@Test
	void configurationIsAvailable() {
		assertThat(MybatisPlusConfiguration.class).isNotNull();
	}

}
