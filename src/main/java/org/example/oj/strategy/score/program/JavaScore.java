package org.example.oj.strategy.score.program;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import org.example.oj.composite.AbstractElement;
import org.example.oj.composite.ClassElement;
import org.example.oj.composite.MethodElement;
import org.example.oj.entity.question.ProgrammingQuestion;
import org.example.oj.entity.sample.Sample;
import org.example.oj.strategy.score.program.impl.CompileTaskImpl;
import org.example.oj.strategy.score.program.impl.ExecuteTaskImpl;
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
    ThreadPool threadPool = new ThreadPool();

    @Override
    public Integer compileCode(String outputPath, String sourcePath) {
        Future<Integer> future = threadPool.submit(new CompileTaskImpl(outputPath, sourcePath));
        try {
            return future.get();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Integer executeCode(String classFilePath, ProgrammingQuestion question, String outputFilePath) {
        List<Sample> samples = question.getSamples();
        for (int i = 0; i < samples.size(); i++) {
            File file = new File(outputFilePath + System.getProperty("file.separator") + i + ".txt");
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
        }

        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < samples.size(); i++) {
            Future<Integer> future = threadPool.submit(new ExecuteTaskImpl(classFilePath, samples.get(i), outputFilePath + System.getProperty("file.separator") + i + ".txt", question.getTimeLimit()));
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
        for (int i = 0; i < samples.size(); i++) {
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFilePath + System.getProperty("file.separator") + i + ".txt"))) {
                String line;
                if ((line = reader.readLine()) != null) {
                    if (!samples.get(i).getOutput().equals(line)) {
                        return 0;
                    }
                }
            } catch (IOException e) {
                // 处理文件读取异常
                e.printStackTrace();
            }
        }
        return question.getPoints();
    }

    @Override
    public Integer calculateCyclomaticComplexity(String code) {
        AbstractElement classElement = new ClassElement();
        CompilationUnit cu = StaticJavaParser.parse(code);
        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            MethodElement methodElement = new MethodElement(method.toString());
            classElement.add(methodElement);
        }
        return classElement.calculateCyclomaticComplexity();
    }

}
