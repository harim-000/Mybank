package com.example.backend.service;

import com.example.backend.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MemberService memberService;
    public void sendEmail(String toEmail) throws MessagingException {
        String key = "verificationCode:" + toEmail;

        // 랜덤한 인증번호 생성
        String verificationCode = generateVerificationCode();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        System.out.println(toEmail + " 컨트롤러 이메일");
        // 이메일 수신자 설정
        helper.setTo(toEmail);

        // 이메일 제목 설정
        helper.setSubject("MyBank 회원가입 인증 이메일입니다.");

        // 이메일 본문에 인증번호 포함
        String message = "MyBank 인증 코드: " + verificationCode;
        helper.setText(message, true);

        // 이메일 발송
        javaMailSender.send(mimeMessage);

        // Redis에 인증 코드 저장하면서 만료 시간 설정 (5분)
        redisService.saveDataWithExpiration(key, verificationCode, 180);
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        String key = "verificationCode:" + email;
        System.out.println(key+"redis키값");
        String storedCode = redisTemplate.opsForValue().get(key);
        return code.equals(storedCode);
    }

    // 랜덤한 6자리 인증번호 생성
    private String generateVerificationCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        int verificationCode = random.nextInt(max - min + 1) + min;
        return String.valueOf(verificationCode);
    }


    public void findId(String toEmail) throws MessagingException {
        String user_id = memberService.findId(toEmail);


        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        System.out.println(toEmail + " 컨트롤러 이메일");
        // 이메일 수신자 설정
        helper.setTo(toEmail);

        // 이메일 제목 설정
        helper.setSubject("MyBank 아이디 찾기 이메일입니다.");

        // user_id에서 처음 3글자 추출
        String firstThreeCharacters = user_id.substring(0, Math.min(user_id.length(), 3));

        // 나머지 부분을 '*'로 채우기
        String maskedUserId = firstThreeCharacters + "*".repeat(user_id.length() - 3);

        // 이메일 본문에 숨겨진 아이디 포함
        String message = "MyBank 아이디 힌트: " + maskedUserId;
        helper.setText(message, true);

        // 이메일 발송
        javaMailSender.send(mimeMessage);
    }

    public void findPwd(Member member) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        System.out.println(member.getUser_email() + " 컨트롤러 이메일");

        // 이메일 수신자 설정
        helper.setTo(member.getUser_email());

        // 이메일 제목 설정
        helper.setSubject("MyBank 비밀번호 찾기 이메일입니다.");

        // 이메일 본문에 인증번호 포함
        String message = "MyBank 임시 비밀번호: " + member.getUser_pass();
        helper.setText(message, true);

        // 이메일 발송
        javaMailSender.send(mimeMessage);
    }
}