package kr.java.thymeleaf.model.repository;

import kr.java.thymeleaf.model.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//save,findAll, --- 이런 기본 메서드가 내장되어있음
public interface MentorRepository extends JpaRepository<Mentor, Long> {
    @Query("select distinct m from Mentor m left join fetch m.mentees") 
    //left join은 값이 없어도 다 불러오겠다.
    // N+1 문제 해결 위한 쿼리임
    List<Mentor> findAllWithMentees();

    @Query("SELECT m FROM Mentor m LEFT JOIN FETCH m.mentees Where m.id = :id")
    Optional<Mentor> findByIdWithMentees(@Param("id") Long id);
}