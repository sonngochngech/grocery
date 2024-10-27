package com.grocery.app;

import com.grocery.app.entities.Role;
import com.grocery.app.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;

import static com.grocery.app.config.constant.AppConstants.ADMIN_ID;
import static com.grocery.app.config.constant.AppConstants.USER_ID;


@SpringBootApplication
@EnableConfigurationProperties
public class AppApplication implements CommandLineRunner {

	@Autowired
	private RoleRepo roleRepo;


	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try{
			Role adminRole=new Role(ADMIN_ID,"ADMIN");
			Role userRole=new Role(USER_ID,"USER");
			List<Role> roles=List.of(adminRole,userRole);
			List<Role> savedRoles=roleRepo.saveAll(roles);
			savedRoles.forEach(System.out::println);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
