package com.clicksolutions.taskmanagement.util;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitRequestRate {

    private static final Logger logger = LoggerFactory.getLogger(LimitRequestRate.class);

    private final int maxRequests;
    private final long timeWindowMs;

    private final ConcurrentHashMap<String, RateLimitInfo> clientRequests = new ConcurrentHashMap<>();

    public LimitRequestRate(int maxRequests, long timeWindowMs) {
        this.maxRequests = maxRequests;
        this.timeWindowMs = timeWindowMs;
        logger.info("Rate limiter initialized with maxRequests={} and timeWindowMs={}", maxRequests, timeWindowMs);
    }

    public boolean isAllowed(String clientId) {
        Instant now = Instant.now();
        clientRequests.computeIfAbsent(clientId, k -> {
            logger.debug("New client added to rate limiter: {}", clientId);
            return new RateLimitInfo(maxRequests, now.toEpochMilli() + timeWindowMs);
        });

        RateLimitInfo rateLimitInfo = clientRequests.get(clientId);

        synchronized (rateLimitInfo) {
            if (now.toEpochMilli() > rateLimitInfo.getResetTime()) {
                logger.info("Resetting rate limit for clientId={} at {}", clientId, now);
                rateLimitInfo.reset(maxRequests, now.toEpochMilli() + timeWindowMs);
            }

            if (rateLimitInfo.getRemainingRequests() > 0) {
                rateLimitInfo.decrementRequests();
                logger.debug("Request allowed for clientId={}, remainingRequests={}", clientId, rateLimitInfo.getRemainingRequests());
                return true;
            } else {
                logger.warn("Request denied for clientId={}, rate limit exceeded. RemainingRequests={}", clientId, rateLimitInfo.getRemainingRequests());
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
