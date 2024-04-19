package org.example.oj.thread;

import java.util.concurrent.*;

/**
 * @author SummCoder
 * @desc 线程池
 * @date 2024/4/18 18:51
 */

public class ThreadPool {

    private static final int poolSize = 5;
    private final Worker[] workers;
    private final BlockingQueue<FutureTask<Integer>> taskQueue;

    public ThreadPool() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new Worker[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    public Future<Integer> submit(Callable<Integer> task) {
        FutureTask<Integer> futureTask = new FutureTask<>(task);
        try {
            taskQueue.put(futureTask);
        } catch (InterruptedException e) {
            // 处理任务添加异常
            e.printStackTrace();
        }
        return futureTask;
    }

    private class Worker extends Thread {
        public void run() {
            while (true) {
                try {
                    FutureTask<Integer> futureTask = taskQueue.take();
                    futureTask.run();
                } catch (InterruptedException e) {
                    // 处理任务执行异常
                    e.printStackTrace();
                }
            }
        }
    }
}