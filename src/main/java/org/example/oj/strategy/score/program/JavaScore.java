package org.example.oj.strategy.score.program;

import org.example.oj.entity.question.ProgrammingQuestion;
import org.example.oj.entity.sample.Sample;
import org.example.oj.thread.ThreadPool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author SummCoder
 * @desc Java代码的处理
 * @date 2024/4/17 23:18
 */
public class JavaScore implements Score {
    @Override
    public Integer compileCode(String outputPath, String sourcePath) {
        try {
            // 调用命令行命令编译Java代码
            Process process = Runtime.getRuntime().exec("javac -d " + outputPath + " " + sourcePath);
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public Integer executeCode(String classFilePath, ProgrammingQuestion question, String outputFilePath) {
        List<Sample> samples = question.getSamples();
        File file = new File(outputFilePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("Parent directories created successfully.");
            } else {
                System.out.println("Failed to create parent directories.");
            }
        }
        if (file.exists()) {
            file.delete();
            try {
                file.createNewFile();
                System.out.println("File created successfully.");
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file: " + e.getMessage());
            }
        }
        ThreadPool threadPool = new ThreadPool();
        List<Future<Integer>> futures = new ArrayList<>();

        for (Sample sample : samples) {
            Future<Integer> future = threadPool.submit(new ExecuteTaskImpl(classFilePath, sample, outputFilePath));
            futures.add(future);
        }

        for (Future<Integer> integerFuture : futures) {
            try {
                Integer result = integerFuture.get();
                if (result == 0) {
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    @Override
    public Integer score(ProgrammingQuestion question, String outputFilePath) {
        List<Sample> samples = question.getSamples();
        List<String> fileLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(outputFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileLines.add(line);
            }
        } catch (IOException e) {
            // 处理文件读取异常
            e.printStackTrace();
        }
        int cnt = 0;
        for (Sample sample : samples) {
            for (String fileLine : fileLines) {
                if (sample.getOutput().equals(fileLine)) {
                    cnt++;
                }
            }
        }
        if (cnt == samples.size()) {
            return question.getPoints();
        }
        return 0;
    }
}
