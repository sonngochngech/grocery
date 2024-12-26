package com.grocery.app;

import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.entities.Role;
import com.grocery.app.entities.User;
import com.grocery.app.repositories.RoleRepo;
import com.grocery.app.repositories.UserRepo;
import com.grocery.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

import static com.grocery.app.config.constant.AppConstants.ADMIN_ID;
import static com.grocery.app.config.constant.AppConstants.USER_ID;


@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
@EnableScheduling
public class AppApplication implements CommandLineRunner {

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;


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


//            set up default user and delete when testing
//			User storedUser=userRepo.findByUsername("test").orElse(null);
//			if(storedUser == null){
//				UserDetailDTO user=UserDetailDTO.builder().id(1L).username("test").password(passwordEncoder.encode("12345678")).role(userRole).build();
//				UserDetailDTO user1=UserDetailDTO.builder().id(2L).username("test1").password(passwordEncoder.encode("12345678")).role(userRole).build();
//				UserDetailDTO user2=UserDetailDTO.builder().id(3L).username("test2").password(passwordEncoder.encode("12345678")).role(userRole).build();
//				UserDetailDTO user3=UserDetailDTO.builder().id(4L).username("test3").password(passwordEncoder.encode("12345678")).role(userRole).build();
//				UserDetailDTO admin=UserDetailDTO.builder().id(5L).username("admin").password(passwordEncoder.encode("12345678")).role(adminRole).build();
//				UserDetailDTO admin1=UserDetailDTO.builder().id(6L).username("admin1").password(passwordEncoder.encode("12345678")).role(adminRole).build();
//
//				userService.registerUser(user);
//				userService.registerUser(user1);
//				userService.registerUser(user2);
//				userService.registerUser(user3);
//				userService.registerUser(admin);
//				userService.registerUser(admin1);
//				System.out.println("SAVE");
//			}

		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
