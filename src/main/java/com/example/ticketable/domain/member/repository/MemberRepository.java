package com.example.ticketable.domain.member.repository;

import com.example.ticketable.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
	
	boolean existsByEmail(@Param("email") String email);
	
	@Query("select m from Member m where m.email = :email and m.deletedAt is null")
	Optional<Member> findByEmail(@Param("email") String email);
	
	@Query("select m from Member m where m.id = :memberId and m.deletedAt is null")
	Optional<Member> findMemberById(@Param("memberId") Long memberId);
}
