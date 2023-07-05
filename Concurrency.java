package concurrency;

import java.util.Random;

public class Concurrency {
    private static final int ARRAY_SIZE = 200000000;
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {
        int[] numbers = generateRandomArray(ARRAY_SIZE);

        long parallelStartTime = System.currentTimeMillis();
        long parallelSum = parallelSum(numbers);
        long parallelEndTime = System.currentTimeMillis();
        long parallelTime = parallelEndTime - parallelStartTime;

        long sequentialStartTime = System.currentTimeMillis();
        long sequentialSum = sequentialSum(numbers);
        long sequentialEndTime = System.currentTimeMillis();
        long sequentialTime = sequentialEndTime - sequentialStartTime;

        System.out.println("Parallel sum: " + parallelSum);
        System.out.println("Parallel time (ms): " + parallelTime);
        System.out.println();
        System.out.println("Sequential sum: " + sequentialSum);
        System.out.println("Sequential time (ms): " + sequentialTime);
    }

    private static int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(11);
        }
        return array;
    }

    private static long parallelSum(int[] numbers) {
        int numThreads = Math.min(NUM_THREADS, numbers.length);
        SumThread[] threads = new SumThread[numThreads];

        int chunkSize = numbers.length / numThreads;
        int startIndex = 0;
        int endIndex = chunkSize;

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new SumThread(numbers, startIndex, endIndex);
            threads[i].start();

            startIndex = endIndex;
            endIndex += chunkSize;
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Sum the partial results
        long sum = 0;
        for (int i = 0; i < numThreads; i++) {
            sum += threads[i].getPartialSum();
        }

        return sum;
    }

    private static long sequentialSum(int[] numbers) {
        long sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

    private static class SumThread extends Thread {
        private final int[] numbers;
        private final int startIndex;
        private final int endIndex;
        private long partialSum;

        public SumThread(int[] numbers, int startIndex, int endIndex) {
            this.numbers = numbers;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public long getPartialSum() {
            return partialSum;
        }

        @Override
        public void run() {
            partialSum = 0;
            for (int i = startIndex; i < endIndex; i++) {
                partialSum += numbers[i];
            }
        }
    }
}