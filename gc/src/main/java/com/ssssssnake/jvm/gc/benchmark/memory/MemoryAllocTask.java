package com.ssssssnake.jvm.gc.benchmark.memory;

/**
 * @author ssssssnake
 **/
public final class MemoryAllocTask implements Runnable {

    private AllocatorType allocatorType;

    private int sizeOfObject;

    private int boundOfSize;

    public MemoryAllocTask() {
        this(AllocatorType.FIXED, 1024 * 1024 * 10, -1);
    }

    /**
     * @param allocatorType fixed or varying object size.
     * @param sizeOfObject  object size in bytes.
     * @param boundOfSize   the bound of object size in bytes, between zero (inclusive) and {@code bound} (exclusive).
     */
    public MemoryAllocTask(AllocatorType allocatorType, int sizeOfObject, int boundOfSize) {
        if ((AllocatorType.FIXED == allocatorType && sizeOfObject < 0)
                || (AllocatorType.VARYING == allocatorType && boundOfSize <= 0)) {
            throw new IllegalArgumentException();
        }
        this.sizeOfObject = sizeOfObject;
        this.allocatorType = allocatorType;
        this.boundOfSize = boundOfSize;
    }

    @Override
    public void run() {
        if (AllocatorType.FIXED == this.allocatorType) {
            MemoryAllocator.alloc(this.sizeOfObject);
        } else if (AllocatorType.VARYING == this.allocatorType) {
            MemoryAllocator.allocRandom(this.boundOfSize);
        } else {
            throw new RuntimeException("unsupported allocator type.");
        }
    }
}
