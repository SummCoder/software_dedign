package org.example.oj.strategy.score.program;

import org.example.oj.constant.Constant;
import org.example.oj.entity.question.ProgrammingQuestion;
import org.example.oj.entity.sample.Sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        try {
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
            for (Sample sample : samples) {
                // 执行程序并将输出重定向到文件
                String[] inputArgs = sample.getInput().split(" ");
                List<String> commandList = new ArrayList<>();
                commandList.add("java");
                commandList.add("-cp");
                commandList.add(Constant.getAnswerPath() + System.getProperty("file.separator") + "output");
                commandList.add(classFilePath);
                commandList.addAll(Arrays.asList(inputArgs));

                ProcessBuilder processBuilder = new ProcessBuilder(commandList);
                processBuilder.redirectOutput((ProcessBuilder.Redirect.appendTo(file)));
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
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return 0;
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
        for (int i = 0; i < samples.size(); i++) {
            if (!samples.get(i).getOutput().equals(fileLines.get(i))) {
                return 0;
            }
        }
        return question.getPoints();
    }
}
