package com.ssssssnake.jvm.gc.benchmark;

/**
 * @author ssssssnake
 **/
public interface GCBenchMark {

    /**
     * @param objectSize object size in bytes.
     */
    void startOnSingleCoreAndFixedSize(int objectSize);

    void startOnSingleCoreAndVaryingSize();

    /**
     * @param objectSize object size in bytes.
     */
    void startOnMultipleCoresAndFixedSize(int objectSize, int threadNum);

    void startOnMultipleCoresAndVaryingSize(int threadNum);
}
