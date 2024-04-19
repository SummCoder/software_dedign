package org.example.oj.strategy.score.program.impl;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author SummCoder
 * @desc
 * @date 2024/4/19 23:09
 */
public class CompileTaskImpl implements Callable<Integer> {

    private final String outputPath;
    private final String sourcePath;

    public CompileTaskImpl(String outputPath, String sourcePath) {
        this.outputPath = outputPath;
        this.sourcePath = sourcePath;
    }

    @Override
    public Integer call() throws Exception {
        try {
            // 调用命令行命令编译Java代码
            Process process = Runtime.getRuntime().exec("javac -d " + outputPath + " " + sourcePath);
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
