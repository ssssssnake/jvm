package com.ssssssnake.jvm.gc.benchmark.memory;

import java.util.Random;

/**
 * @author ssssssnake
 **/
public final class MemoryAllocator {

    private static Random random = new Random();

    /**
     * Allocate heap memory in bytes.
     * @param size object size in bytes.
     * @return byte array.
     */
    public static byte[] alloc(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Bound should be greater or equal than 0.");
        }
        return new byte[size];
    }

    /**
     * Allocate randomly sized heap memory in bytes, between zero (inclusive) and {@code bound} (exclusive).
     * @param bound the bound of object size in bytes.
     * @return byte array.
     */
    public static byte[] allocRandom(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound should be greater than 0.");
        }
        return new byte[random.nextInt(bound)];
    }

}
