package org.example.oj.strategy.score.program;

import org.example.oj.constant.Constant;
import org.example.oj.entity.sample.Sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author SummCoder
 * @desc
 * @date 2024/4/19 13:48
 */

public class ExecuteTaskImpl implements Callable<Integer> {
    private final String classFilePath;
    private final Sample sample;
    private final String outputFilePath;

    public ExecuteTaskImpl(String classFilePath, Sample sample, String outputFilePath) {
        this.classFilePath = classFilePath;
        this.sample = sample;
        this.outputFilePath = outputFilePath;
    }

    @Override
    public Integer call() {
        try {
            // 执行程序并将输出重定向到文件
            String[] inputArgs = sample.getInput().split(" ");
            List<String> commandList = new ArrayList<>();
            commandList.add("java");
            commandList.add("-cp");
            commandList.add(Constant.getAnswerPath() + System.getProperty("file.separator") + "output");
            commandList.add(classFilePath);
            commandList.addAll(Arrays.asList(inputArgs));

            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            processBuilder.redirectOutput((ProcessBuilder.Redirect.appendTo(new File(outputFilePath))));
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // 运行出错，返回0分
                // 获取错误输出流
                InputStream errorStream = process.getErrorStream();
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                String errorLine;
                StringBuilder errorOutput = new StringBuilder();
                // 读取错误输出
                while ((errorLine = errorReader.readLine()) != null) {
                    errorOutput.append(errorLine).append("\n");
                }
                // 输出错误信息
                System.out.println("命令行运行报错信息：" + errorOutput);
                return 0;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
}