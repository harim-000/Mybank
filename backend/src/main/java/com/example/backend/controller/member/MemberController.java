package com.example.backend.controller.member;

import com.example.backend.domain.Member;
import com.example.backend.service.MemberService;
import com.zaxxer.hikari.util.SuspendResumeLock;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * The type Member controller.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/user")
public class MemberController {


    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment; // Environment 객체 주입

    /**
     * Check id duplicate response entity.
     *
     * @param user_id the user id
     * @return the response entity
     */
    @PostMapping("/checkIdDuplicate")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestParam String user_id) {
        try {
            boolean isDuplicate = memberService.isIdDuplicate(user_id);
            return ResponseEntity.ok(isDuplicate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);
        }
    }

    /**
     * Check email duplicate response entity.
     *
     * @param user_email the user email
     * @return the response entity
     */
    @PostMapping("/checkEmailDuplicate")
    public ResponseEntity<Boolean> checkEmailDuplicate(@RequestParam String user_email) {
        try {
            boolean isDuplicate = memberService.isEmailDuplicate(user_email);
            System.out.println(isDuplicate);
            return ResponseEntity.ok(isDuplicate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);
        }
    }

    @PostMapping("/checkIdEmailDuplicate")
    public ResponseEntity<Member> checkIdEmailDuplicate(@RequestParam String user_id) {
        try {

            Member member = memberService.isIdEmailDuplicate(user_id);

            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.notFound().build(); // 사용자가 없는 경우 404 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }
    }

    /**
     * Join response entity.
     *
     * @param user_id      the user id
     * @param user_pass    the user pass
     * @param user_email   the user email
     * @param user_profile the user profile
     * @return the response entity
     */
    @PostMapping("/join")
    public ResponseEntity<String> join(String user_id, String user_pass, String user_email, MultipartFile user_profile) {
        try {

            // 중복 체크
            if (memberService.isIdDuplicate(user_id)) {
                return ResponseEntity.badRequest().body("이미 사용 중인 아이디입니다.");
            }
            if (memberService.isEmailDuplicate(user_email)) {
                return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
            }

            System.out.println(user_id + "들어옴?");
            // 회원 가입 정보를 Member 객체로 변환
            Member member = new Member();
            member.setUser_id(user_id);
            member.setUser_email(user_email);

            // 비밀번호를 암호화하여 저장
            String encodedPassword = passwordEncoder.encode(user_pass);
            member.setUser_pass(encodedPassword);

            if (user_profile != null) {
                // 프로필 이미지를 파일로 저장하고 파일 경로를 저장
                String imagePath = saveProfileImage(user_profile);
                member.setUser_profile(imagePath);

                System.out.println(user_profile);

                System.out.println(imagePath);
            }

            // 회원 정보 저장
            memberService.insertMember(member);

            return ResponseEntity.ok("회원 가입이 완료되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원 가입 중에 오류가 발생했습니다.");
        }
    }

    // 프로필 이미지를 저장하는 메서드
    private String saveProfileImage(MultipartFile profile_image) throws IOException {
        String originalFileName = profile_image.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 랜덤한 유니크 파일 이름 생성
        String newFileName = generateUniqueFileName(fileExtension);
        
        String uploadPath = environment.getProperty("spring.servlet.multipart.location") + "/profile"; // 저장 경로 가져오기
        File targetFile = new File(uploadPath, newFileName); // 전체 경로 생성
        // 이미지 이름 바꾸기
        profile_image.transferTo(targetFile);
        System.out.println(targetFile);
        // 이미지 이름을 갱신
        String imageName = newFileName;
        System.out.println(imageName);
        return imageName;
    }

    // 같은 이름이 프로필 이미지로 들어올 경우를 고려해 뒤에 파일 이름 유니크하게 설정
    private String generateUniqueFileName(String fileExtension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        Random random = new Random();
        int randomNumber = random.nextInt(10000); // Generate a random number between 0 and 9999

        String uniqueFileName = timestamp + "_" + randomNumber + fileExtension;
        System.out.println(uniqueFileName);
        return uniqueFileName;
    }

    /**
     * Gets user info.
     *
     * @param user_id the user id
     * @return the user info
     */
    @PostMapping("/getUserInfo")
    public ResponseEntity<Member> getUserInfo(@RequestParam String user_id) {
        try {
            System.out.println(user_id + "아이디");
            Member member = memberService.selectOneById(user_id);
            System.out.println(member + "겟멤버 객체");
            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.notFound().build(); // 사용자가 없는 경우 404 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Modify response entity.
     *
     * @param user_id         the user id
     * @param user_pass       the user pass
     * @param user_originPass the user origin pass
     * @param user_email      the user email
     * @param user_profile    the user profile
     * @return the response entity
     */
    @PostMapping("/mod")
    public ResponseEntity<String> modify(
            String user_id,
            String user_pass,
            String user_originPass,
            String user_email,
            MultipartFile user_profile) {
        try {
            System.out.println("이메일수정 들어옴?" + user_email);
            System.out.println("아이디수정 들어옴?" + user_id);
            System.out.println("비번수정 들어옴?" + user_pass);
            System.out.println("기존비번 들어옴?" + user_originPass);

            System.out.println("프로필수정 들어옴?" + user_profile);

            // 기존 회원 정보 조회
            Member existingMember = memberService.selectOneById(user_id);

            System.out.println(existingMember + "회원객체");

            // 현재 비밀번호 확인
            boolean isCurrentPasswordCorrect = passwordEncoder.matches(user_originPass, existingMember.getUser_pass());
            if (!isCurrentPasswordCorrect) {
                return ResponseEntity.badRequest().body("현재 비밀번호가 일치하지 않습니다.");
            }

            boolean isModified = false; // 변경 여부를 저장하는 변수 추가

            if (user_email != null && !user_email.isEmpty() && !user_email.equals(existingMember.getUser_email())) {
                if (memberService.isEmailDuplicate(user_email)) {
                    return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
                } else {
                    existingMember.setUser_email(user_email);
                    isModified = true; // 이메일이 변경된 경우 변경 여부를 설정
                }
            }

            if (user_pass != null) {
                // 비밀번호 변경 로직을 수행
                String encodedPassword = passwordEncoder.encode(user_pass);
                existingMember.setUser_pass(encodedPassword);
                isModified = true; // 비밀번호가 변경된 경우 변경 여부를 설정
            }
            if (user_profile != null && !user_profile.isEmpty()) {
                System.out.println("프로필수정 들어옴?" + user_profile);
                String imagePath = saveProfileImage(user_profile);
                existingMember.setUser_profile(imagePath);
                isModified = true; // 프로필 이미지가 변경된 경우 변경 여부를 설정
            }

            // 변경된 경우에만 200 상태 코드 반환
            if (isModified) {
                // 회원 정보 저장
                memberService.updateMember(existingMember);
                return ResponseEntity.ok("회원 수정이 완료되었습니다.");
            } else {
                // 변경된 내용이 없으면 204 No Content 상태 코드 반환
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원 수정 중에 오류가 발생했습니다.");
        }
    }

    /**
     * Del.
     *
     * @param user_id the user id
     */
    @PostMapping("/del")
    @Transactional
    public void del(@RequestParam String user_id) {
        memberService.deleteAccount(user_id);
        memberService.deleteFaq(user_id);
        memberService.deleteFaq_Reply(user_id);
        memberService.deleteById(user_id);
    }


}