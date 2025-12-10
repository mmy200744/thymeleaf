package kr.java.thymeleaf.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@MappedSuperclass //상송 (extends) <- Table을 만들지 않음. 그래도 JPA 관련된 기능은 쓸 수 있음
@Getter //Setter 가 필요없는 속성
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //?
    private Long id;

//    private Instant createdAt; UTC 기준
    private LocalDateTime createdAt; // 서버시간 기준
    
    @PrePersist
    public void prePersist() { //신규 save 되면 (Insert로 실제 반영되기 직전)
        this.createdAt = LocalDateTime.now();
    }
}
