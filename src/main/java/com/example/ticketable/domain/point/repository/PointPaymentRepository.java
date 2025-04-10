package com.example.ticketable.domain.point.repository;

import com.example.ticketable.domain.point.entity.PointPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointPaymentRepository extends JpaRepository<PointPayment, Long> {
}
