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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class WaitingQueueAspect {
	private final QueueManager queueManager;
	private final String WAITING_QUEUE_HEADER_NAME = "waiting-queue";

	@Around("@annotation(waitingQueue)")
	private Object around(ProceedingJoinPoint joinPoint, WaitingQueue waitingQueue) throws Throwable {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();

		String token = request.getHeader(WAITING_QUEUE_HEADER_NAME);
		//토큰 값이 존재한다면 입장 가능한지 체크
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
