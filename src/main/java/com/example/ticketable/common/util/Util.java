package com.example.ticketable.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Util {
	public static Pageable convertPageable(Pageable pageable){
		return PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize());
	}
}
