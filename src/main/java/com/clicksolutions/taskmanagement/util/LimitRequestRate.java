package com.clicksolutions.taskmanagement.util;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

	public class LimitRequestRate {
		
	    private final int maxRequests;
	    private final long timeWindowMs;

	    private final ConcurrentHashMap<String, RateLimitInfo> clientRequests = new ConcurrentHashMap<>();

	    public LimitRequestRate(int maxRequests, long timeWindowMs) {
	        this.maxRequests = maxRequests;
	        this.timeWindowMs = timeWindowMs;
	    }

	    public boolean isAllowed(String clientId) {
	        Instant now = Instant.now();
	        clientRequests.computeIfAbsent(clientId, k -> new RateLimitInfo(maxRequests, now.toEpochMilli() + timeWindowMs));

	        RateLimitInfo rateLimitInfo = clientRequests.get(clientId);

	        synchronized (rateLimitInfo) {
	            if (now.toEpochMilli() > rateLimitInfo.getResetTime()) {
	                rateLimitInfo.reset(maxRequests, now.toEpochMilli() + timeWindowMs);
	            }

	            if (rateLimitInfo.getRemainingRequests() > 0) {
	                rateLimitInfo.decrementRequests();
	                return true;
	            } else {
	                return false;
	            }
	        }
	    }

	    private static class RateLimitInfo {
	        private AtomicInteger remainingRequests;
	        private long resetTime;

	        public RateLimitInfo(int maxRequests, long resetTime) {
	            this.remainingRequests = new AtomicInteger(maxRequests);
	            this.resetTime = resetTime;
	        }

	        public int getRemainingRequests() {
	            return remainingRequests.get();
	        }

	        public long getResetTime() {
	            return resetTime;
	        }

	        public void decrementRequests() {
	            remainingRequests.decrementAndGet();
	        }

	        public void reset(int maxRequests, long resetTime) {
	            this.remainingRequests.set(maxRequests);
	            this.resetTime = resetTime;
	        }
	    }
	}


