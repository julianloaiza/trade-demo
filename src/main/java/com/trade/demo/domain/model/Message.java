package com.trade.demo.domain.model;

/**
 * FIX message object.
 * 17 - execId
 * 48 - securityId
 * 22 - securityIdSource
 * 1 - account
 * 32 - lastShares (positive int)
 * 6 - avgPx (positive double)
 * 8 - side (BUY, SELL)
 */
public class Message {
    
    public String getString(int tag) {
        // Implementation would depend on the actual FIX message parsing
        return null;
    }
    
    public Double getDouble(int tag) {
        // Implementation would depend on the actual FIX message parsing
        return null;
    }
    
    public Integer getInt(int tag) {
        // Implementation would depend on the actual FIX message parsing
        return null;
    }
}
