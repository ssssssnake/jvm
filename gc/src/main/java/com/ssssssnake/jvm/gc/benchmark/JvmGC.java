package com.ssssssnake.jvm.gc.benchmark;

import com.ssssssnake.jvm.gc.benchmark.memory.MemoryAllocTask;
import com.ssssssnake.jvm.gc.benchmark.memory.AllocatorType;

/**
 * @author ssssssnake
 */
public final class JvmGC extends AbstractJvmGC implements GCBenchMark {

    private int numberOfAllocs;

    /**
     * @param numberOfAllocs the number of objects to be allocated.
     */
    public JvmGC(int numberOfAllocs) {
        this.numberOfAllocs = numberOfAllocs;
    }

    @Override
    public void startOnSingleCoreAndFixedSize(int objectSize) {
        MemoryAllocTask memoryAllocTask = new MemoryAllocTask(AllocatorType.FIXED, objectSize, -1);
        execute(memoryAllocTask, 1, numberOfAllocs);
    }

    @Override
    public void startOnSingleCoreAndVaryingSize() {
        MemoryAllocTask memoryAllocTask = new MemoryAllocTask(AllocatorType.VARYING, -1, 1024 * 1024 * 10);
        execute(memoryAllocTask, 1, numberOfAllocs);
    }

    @Override
    public void startOnMultipleCoresAndFixedSize(int objectSize, int threadNum) {
        MemoryAllocTask memoryAllocTask = new MemoryAllocTask(AllocatorType.FIXED, objectSize, -1);
        execute(memoryAllocTask, threadNum, numberOfAllocs);
    }

    @Override
    public void startOnMultipleCoresAndVaryingSize(int threadNum) {
        MemoryAllocTask memoryAllocTask = new MemoryAllocTask(AllocatorType.VARYING, -1, 1024 * 1024 * 10);
        execute(memoryAllocTask, threadNum, numberOfAllocs);
    }

}
