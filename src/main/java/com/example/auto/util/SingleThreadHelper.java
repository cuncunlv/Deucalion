package com.example.auto.util;

import com.example.auto.util.core.VoidHandle;

import java.util.concurrent.locks.ReentrantLock;

public class SingleThreadHelper {

    private static final ReentrantLock SINGLE_PROCESS_LOCK = new ReentrantLock();

    public static void singleProcess(VoidHandle voidHandle) {
        if (!SINGLE_PROCESS_LOCK.tryLock()) {
            throw new RuntimeException("CDK process is running");
        } else {
            try {
                voidHandle.execute();
            } finally {
                SINGLE_PROCESS_LOCK.unlock();
            }
        }

    }
}
