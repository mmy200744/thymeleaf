package kr.java.thymeleaf.controller;

import jakarta.validation.Valid;
import kr.java.thymeleaf.model.dto.MentorForm;
import kr.java.thymeleaf.model.entity.Mentor;
import kr.java.thymeleaf.service.MentorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    // 공통으로 사용할 전문분야 리스트 (코드 중복 방지용 메서드 추출 권장, 여기서는 직접 사용)
    private List<String> getSpecialties() {
        return List.of("백엔드", "프론트엔드", "풀스택", "데이터");
    }

    // 1. 등록 폼 페이지
    @GetMapping("/mentor/new")
    public String newMentorForm(Model model) {
        model.addAttribute("mentorForm", new MentorForm());
        // [수정] speciality -> specialties 로 통일
        model.addAttribute("specialties", getSpecialties());
        return "mentor/form";
    }

    // 2. 수정 폼 페이지
    @GetMapping("/mentor/{id}/edit")
    public String editMentorForm(@PathVariable Long id, Model model) {
        Mentor mentor = mentorService.findById(id);

        model.addAttribute("mentorForm", MentorForm.from(mentor));
        // [유지] specialties 사용
        model.addAttribute("specialties", getSpecialties());

        return "mentor/form";
    }

    // 3. 등록 처리 (PRG 패턴)
    @PostMapping("/mentor/new")
    public String createMentor(
            @Valid @ModelAttribute MentorForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        // 유효성 검증 실패 시 다시 입력 폼으로 이동
        if (bindingResult.hasErrors()) {
            // [수정] 에러 발생 시에도 리스트가 필요하므로 specialties로 전달
            model.addAttribute("specialties", getSpecialties());
            return "mentor/form";
        }

        // 저장 로직
        Mentor mentor = form.toEntity();
        mentorService.save(mentor);

        redirectAttributes.addFlashAttribute("message", "멘토가 등록되었습니다");
        return "redirect:/mentor/list";
    }

    // 4. 수정 처리
    @PostMapping("/mentor/{id}/edit")
    public String updateMentor(
            @PathVariable Long id,
            @Valid @ModelAttribute MentorForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 유효성 검증 실패 시
        if (bindingResult.hasErrors()) {
            // [유지] specialties 사용
            model.addAttribute("specialties", getSpecialties());
            return "mentor/form";
        }

        Mentor mentor = mentorService.findById(id);
        mentor.setName(form.getName());

        // [수정] setSpeciality -> setSpecialty (Entity 필드명에 맞춤)
        // 만약 Entity에 setSpeciality라고 되어있다면 그에 맞게 고쳐야 함
        mentor.setSpeciality(form.getSpecialty());

        mentor.setEmail(form.getEmail());
        mentorService.save(mentor);

        redirectAttributes.addFlashAttribute("message", "멘토 정보가 수정되었습니다");
        return "redirect:/mentor/%d".formatted(id);
    }

    // 5. 기본 문법 테스트용
    @GetMapping("/syntax/basic")
    public String basicSyntax(Model model) {
        // 테스트용 데이터가 없으면 에러날 수 있으니 주의 (ID 1번이 있다고 가정)
        try {
            Mentor mentor = mentorService.findById(1L);
            model.addAttribute("mentor", mentor);
        } catch (Exception e) {
            // 데이터가 없을 경우를 대비한 빈 객체
            model.addAttribute("mentor", new Mentor());
        }

        model.addAttribute("greeting","안녕하세요!");
        model.addAttribute("currentTime", LocalDateTime.now());

        return "syntax/basic";
    }

    // 6. 목록 조회
    @GetMapping("/mentor/list")
    public String mentorList(Model model) {
        model.addAttribute("mentors", mentorService.findAllWithMentees());
        model.addAttribute("pageTitle", "멘토 관리 시스템");
        return "mentor/list";
    }

    // 7. 상세 조회
    @GetMapping("/mentor/{id}")
    public String mentorDetail(Model model, @PathVariable Long id) {
        model.addAttribute("mentor", mentorService.findByIdWithMentees(id));
        return "mentor/detail";
    }

    // 8. 삭제 처리
    @GetMapping("/mentor/{id}/delete")
    public String deleteMentor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        mentorService.delete(id);
        redirectAttributes.addFlashAttribute("message", "멘토가 삭제되었습니다");
        return "redirect:/mentor/list";
    }
}