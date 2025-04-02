package com.example.ticketable.domain.member.repository;

import com.example.ticketable.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
