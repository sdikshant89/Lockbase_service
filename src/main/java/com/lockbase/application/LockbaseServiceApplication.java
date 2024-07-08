package com.lockbase.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LockbaseServiceApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(LockbaseServiceApplication.class, args);

		EnvironmentUtil env_util = context.getBean(EnvironmentUtil.class);
		env_util.getAppStartInfo();
	}
}
