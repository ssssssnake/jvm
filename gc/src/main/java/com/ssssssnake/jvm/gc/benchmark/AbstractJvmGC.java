package com.ssssssnake.jvm.gc.benchmark;

import com.ssssssnake.jvm.gc.benchmark.memory.MemoryAllocTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ssssssnake
 **/
public abstract class AbstractJvmGC {

    /**
     *
     * @param memoryAllocTask
     * @param threadNum
     * @param numberOfAllocs
     */
    public void execute(MemoryAllocTask memoryAllocTask, int threadNum, int numberOfAllocs) {
        ExecutorService executorService = Executors.newFixedThreadPool(200);

        for (int i = 0; i < numberOfAllocs; i++) {
            executorService.execute(memoryAllocTask);
        }

        executorService.shutdown();
    }
}
