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

	@Around("@annotation(waitingQueue)")
	private Object around(ProceedingJoinPoint joinPoint, WaitingQueue waitingQueue) throws Throwable {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
		String token = request.getHeader(QueueSystemConstants.WAITING_QUEUE_HEADER_NAME);

		if(token!= null && queueManager.isAllowed(token)) {
			try {
				return joinPoint.proceed();
			} finally {
				queueManager.removeTokenFromProceedQueue(token);
			}
		}

		// 토큰이 없거나, 아직 입장 허용이 안된 경우
		if(token == null || token.isEmpty()) {
			token = queueManager.enterWaitingQueue();
		}
		long waitingOrder = queueManager.getWaitingOrder(token);
		return ResponseEntity.accepted().body(new WaitingResponse(waitingOrder,"wait" , token));
	}
}
