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
@RequiredArgsConstructor // 의존성 주입
public class MentorController {
    private final MentorService mentorService;

    //등록 폼
    @GetMapping("/mentor/new")
    public String newMentorForm(Model model) {
        //th:object <- form
        model.addAttribute("mentorForm", new MentorForm());
        model.addAttribute("speciality", List.of("백엔드", "프론트엔드", "풀스택", "데이터"));
        return "mentor/form"; //templates/mentor/form.html
    }

    //등록 처리(PRG 패턴)
    @PostMapping("/mentor/new")
    public String createMentor(
           @Valid @ModelAttribute MentorForm form,
           BindingResult bindingResult,
           Model model,
           RedirectAttributes redirectAttributes
           ) {
        //유효성 검증 실패시 form
        if (bindingResult.hasErrors()) {
            model.addAttribute("speciality", List.of("백엔드", "프론트엔드", "풀스택", "데이터"));
            //request(Model) -> MentorForm(th:object) -> form(Model)
            return "mentor/form";
        }
        //저장
        Mentor mentor = form.toEntity();
        mentorService.save(mentor);

        // 성공 메세지 -> redirect -> addFlashAttribute
        redirectAttributes.addFlashAttribute("message", "멘토가 등록되었습니다");

        return "redirect:/mentor/list";
    }

    @GetMapping("/syntax/basic")
    public String basicSyntax(Model model) {
        Mentor mentor = mentorService.findById(1L);

        model.addAttribute("mentor", mentor);
        model.addAttribute("greeting","안녕하세요!");
        model.addAttribute("currentTime", LocalDateTime.now());

        return "syntax/basic"; // resources/templates/syntax/basic.html안에 들어가게 됨
    }

    //http://localhost:8080/mentor/list 로 연결거임
    @GetMapping("/mentor/list")
    public String mentorList(Model model) {
        model.addAttribute("mentors", mentorService.findAllWithMentees());
        model.addAttribute("pageTitle", "멘토 관리 시스템");
        return "mentor/list";
    }

    // http://localhost:8080/mentor/1
    @GetMapping("/mentor/{id}")
//    @ResponseBody // -> JSON Return
    public String mentorDetail(Model model, @PathVariable Long id) {
//    public Mentor mentorDetail(Model model, @PathVariable Long id) {
//        return mentorService.findByIdWithMentees(id);
        model.addAttribute("mentor", mentorService.findByIdWithMentees(id));
        return "mentor/detail";
    }
}
