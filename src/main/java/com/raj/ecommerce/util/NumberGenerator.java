package com.raj.ecommerce.util;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NumberGenerator {

    public String generate12DigitNumber(){
        long randomPart= Math.abs(UUID.randomUUID().getLeastSignificantBits());
        long twelveDigits= randomPart % 1_000_000_000_000L;
        return String.format("%012d",twelveDigits);
    }

    public String generate4DigitNumber(){
        long randomPart=Math.abs(UUID.randomUUID().getLeastSignificantBits());
        long fourDigits= randomPart % 1_000_0L;
        return String.format("%04d",fourDigits);
    }
}
