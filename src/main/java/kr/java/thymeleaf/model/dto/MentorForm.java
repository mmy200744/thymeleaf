package kr.java.thymeleaf.model.dto;

// 1. @Valid -> 검증
// 2, Entity와 Form(DTO)사이에 변화가 중요함.
// 생성 Form -> Entity, 수정 Form <-> Entity
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.java.thymeleaf.model.entity.Mentor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Getter @Setter 쓰거나 @Data를 쓰면 됨

@Data //순환참조 문제가 생길 수 있음 toString 때문에.. 그래서 이 경우엔 getter setter 씀
@NoArgsConstructor

public class MentorForm {
    private Long id;

    //검증은 보통 3단계
    /*
    1단계 : 프런트엔드에서 input (쉽게 회피 가능), js(fetch) 검증 번거롭긴 하지만 end-point에 직접 전송하면 회피 가능
    - 검증보다는 유저를 위한 UI/UX 가이드

    2단계 : 서버에서 검증 -> 원칙적으로 회피할 수 없음 but 서버기 처리에 있어서 부담을 갖게 됨. if 처리가 10,000 or 50,000?.
    - 테이불 구조를 건드리지 않음.
    - 로직 상 에러 발생을 막기 위해 사전적으로, 비즈니스 로직 상의 실제 제약 조건을 구현

    3단계 : DB에서 검증 -> 서버 부담 줄일 수 있는데 DB에 부담. SQLException 인식. 서버에서 프로그래머틱하게 검증하는 것에 비해 검증에 방식이 번거롭고 수정시 DDL 테이블 구조를 건드려야 함.
    - 데이터의 정합성을 지키는 역할 수행함
    */
    //ModelAttribute -> Form 단계에서 처리
    @NotBlank(message = "이름은 필수입니다") // 검증을 서버에서 함. Controller로 들어오면서 검증
    @Size(min = 2, max = 20, message = "이름은 2~20자 사이여야 합니다")
    private String name;

    @NotBlank(message = "전문 분야를 선택해주세요")
    private String specialty;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    // Entity -> DTO 변환
    public static MentorForm from(Mentor mentor) {
        MentorForm form = new MentorForm();
        form.setId(mentor.getId());
        form.setName(mentor.getName());
        form.setSpecialty(mentor.getSpeciality());
        form.setEmail(mentor.getEmail());
        return form;
    }

    // 생성자로 하는 방법
    public MentorForm(Mentor mentor) {
        this.id = mentor.getId();
        this.name = mentor.getName();
        this.specialty = mentor.getSpeciality();
        this.email = mentor.getEmail();
    }

    // DTO(현재 객체) -> Entity
    public Mentor toEntity() {
        return new Mentor(name, specialty, email); // ID, createdAt (생성할 때 자동 생성)
    }
}
