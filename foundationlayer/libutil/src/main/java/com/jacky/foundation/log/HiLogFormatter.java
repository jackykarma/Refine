package com.jacky.foundation.log;

public interface HiLogFormatter<T> {

    String format(T data);
}