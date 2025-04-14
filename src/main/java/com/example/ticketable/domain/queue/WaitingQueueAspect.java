package com.example.ticketable.domain.queue;

import com.example.ticketable.domain.queue.dto.WaitingResponse;
import com.example.ticketable.domain.queue.service.QueueManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WaitingQueueAspect {
	private final QueueManager queueManager;

	@Around("@annotation(com.example.ticketable.domain.queue.WaitingQueue)")
	private Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request =
			((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		String token = request.getHeader("waiting-token");

		if(token!= null && queueManager.isAllowed(token)) {
			Object proceed = joinPoint.proceed();
			queueManager.removeTokenFromProceedQueue(token);
			return proceed;
		} else {
			if(token == null || token.isEmpty()) {
				token = queueManager.enterWaitingQueue();
			}
			long waitingOrder = queueManager.getWaitingOrder(token);
			return ResponseEntity.accepted().body(new WaitingResponse(waitingOrder,"wait" , token));
		}
	}
}
