package com.kafka.springjwt.exceptions;

public class InavlidTokenAuthentication extends Exception{
    public InavlidTokenAuthentication(String message)
    {
        super(message);
    }
}
