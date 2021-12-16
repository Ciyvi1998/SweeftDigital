package ge.springboot.sweeftdigital;

import ge.springboot.sweeftdigital.entity.Role;
import ge.springboot.sweeftdigital.entity.User;
import ge.springboot.sweeftdigital.enums.Gender;
import ge.springboot.sweeftdigital.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;


@SpringBootApplication
public class SweeftdigitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SweeftdigitalApplication.class, args);
    }

//    @Bean
//    CommandLineRunner run(UserService userService) {
//        return args -> {
//            userService.registerRole(new Role(null,"ADMIN"));
//            userService.registerRole(new Role(null,"USER"));
//            userService.registerRole(new Role(null,"SUPER_ADMIN"));
//
//            userService.saveUser(new User(null,"Admin",
//                    "Admin","admin",
//                    "admin",0,
//                    Gender.MALE,
//                    new Date("02/02/2002"),
//                    true,
//                    false, null, null));
//            userService.saveUser(new User(null,"Nika",
//                    "Kakauridze","kakauridze.nika@gtu.ge",
//                    "12345",23,
//                    Gender.MALE,
//                    new Date("02/14/1998"),
//                    true,
//                    false, null, null));
//            userService.saveUser(new User(null,"Super",
//                    "Admin","superAdmin",
//                    "superAdmin",30,
//                    Gender.MALE,
//                    new Date("04/24/2000"),
//                    true,
//                    false, null, null));
//
//            userService.addRoleToUser("kakauridze.nika@gtu.ge","USER");
//            userService.addRoleToUser("admin","ADMIN");
//            userService.addRoleToUser("superAdmin","SUPER_ADMIN");
//        };
//    }
}
