package com.example.backend.controller.member;

import com.example.backend.domain.Member;
import com.example.backend.service.EmailService;
import com.example.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Random;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/user")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/verify")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        try {
            System.out.println(email+"컨트롤러 이메일");
            emailService.sendEmail(email);
            return ResponseEntity.ok("이메일 발송에 성공했습니다.");
        } catch (MessagingException e) {
            // 이메일 발송 실패 처리
            e.printStackTrace();
            // 적절한 오류 응답을 클라이언트에게 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이메일 발송 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        // 사용자가 입력한 코드와 Redis에 저장된 코드를 비교하여 인증을 수행
        boolean isVerified = emailService.verifyCode(email, code);

        if (isVerified) {
            return ResponseEntity.ok("인증이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드가 일치하지 않습니다.");
        }
    }


    @PostMapping("/findId")
    public ResponseEntity<String>  findId(@RequestParam String email) {
        try {
            System.out.println(email+"컨트롤러 이메일");
            emailService.findId(email);
            return ResponseEntity.ok("이메일 발송에 성공했습니다.");
        } catch (MessagingException e) {
            // 이메일 발송 실패 처리
            e.printStackTrace();
            // 적절한 오류 응답을 클라이언트에게 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이메일 발송 중 오류가 발생했습니다.");
        }
    }
    // 랜덤한 6자리 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        int verificationCode = random.nextInt(max - min + 1) + min;
        return String.valueOf(verificationCode);
    }

    @PostMapping("/findPwd")
    public ResponseEntity<String>  findPwd(@RequestParam String email) {
        try {
            System.out.println(email+"컨트롤러 이메일");

            Member member = new Member();

            String verificationCode = generateVerificationCode();
            member.setUser_email(email);
            String encodedPassword = passwordEncoder.encode(verificationCode);
                member.setUser_pass(encodedPassword);
                memberService.updatePwd(member);
            System.out.println("updatePwd?");
            if( passwordEncoder.matches(verificationCode, encodedPassword)) {
                    member.setUser_pass(verificationCode);
                    emailService.findPwd(member);
                    System.out.println("findPwd몇번?");
                }
            return ResponseEntity.ok("이메일 발송에 성공했습니다.");
        } catch (MessagingException e) {
            // 이메일 발송 실패 처리
            e.printStackTrace();
            // 적절한 오류 응답을 클라이언트에게 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이메일 발송 중 오류가 발생했습니다.");
        }
    }

}
