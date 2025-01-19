package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServiceTests {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Test
    void testSendMail(){
        emailService.sendEmail(
                "sejaljadhav0508@gmail.com",
                "Test Mail",
                "Dear Sejal, pleasure to meet you");
    }

    @Test
    public void getUsersForSATest(){
        System.out.println(userRepository.getUsersForSA());
    }
}
