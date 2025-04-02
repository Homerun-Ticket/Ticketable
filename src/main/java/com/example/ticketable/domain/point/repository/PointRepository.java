package com.example.ticketable.domain.point.repository;

import com.example.ticketable.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<Point, Long> {
}
