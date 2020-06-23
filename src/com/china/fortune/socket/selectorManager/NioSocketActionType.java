package com.china.fortune.socket.selectorManager;

public enum NioSocketActionType {
    NSA_CLOSE,
    NSA_NULL,
    NSA_READ,
    NSA_WRITE,
    NSA_READ_WRITE,
    NSA_CONNECT,
    NSA_READ_COMPLETED;
}