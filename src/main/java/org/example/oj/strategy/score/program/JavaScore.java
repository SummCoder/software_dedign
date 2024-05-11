package org.example.oj.strategy.score.program;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
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
//        List<String> fileLines = new ArrayList<>();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(outputFilePath))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                fileLines.add(line);
//            }
//        } catch (IOException e) {
//            // 处理文件读取异常
//            e.printStackTrace();
//        }
//        int cnt = 0;
//        for (Sample sample : samples) {
//            for (String fileLine : fileLines) {
//                if (sample.getOutput().equals(fileLine)) {
//                    cnt++;
//                }
//            }
//        }
//        if (cnt == samples.size()) {
//            return question.getPoints();
//        }
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
        CompilationUnit cu = StaticJavaParser.parse(code);
        int classComplexity = 0;

        for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
            int methodComplexity = calculateMethodComplexity(method);
            classComplexity += methodComplexity;
        }

        return classComplexity;
    }

    private int calculateMethodComplexity(MethodDeclaration method) {
        int complexity = 1;

        complexity += method.findAll(IfStmt.class).size() + method.findAll(WhileStmt.class).size() + method.findAll(DoStmt.class).size() + method.findAll(ForStmt.class).size() + method.findAll(ConditionalExpr.class).size();

        for (BinaryExpr binaryExpr : method.findAll(BinaryExpr.class)) {
            BinaryExpr.Operator operator = binaryExpr.asBinaryExpr().getOperator();
            if (operator == BinaryExpr.Operator.AND || operator == BinaryExpr.Operator.OR) {
                complexity++; // 布尔运算符作为判定节点，增加 1
            }
        }


//        for (IfStmt ignored : method.findAll(IfStmt.class)) {
//            complexity++; // 每个 if 语句增加 1
//        }
//
//        for (WhileStmt ignored : method.findAll(WhileStmt.class)) {
//            complexity++; // 每个 while 循环增加 1
//        }
//
//        for (DoStmt ignored : method.findAll(DoStmt.class)) {
//            complexity++; // 每个 do-while 循环增加 1
//        }
//
//        for (ForStmt ignored : method.findAll(ForStmt.class)) {
//            complexity++; // 每个 for 循环增加 1
//        }
//
//        for (ConditionalExpr ignored : method.findAll(ConditionalExpr.class)) {
//            complexity++;
//        }
//
//        for (BinaryExpr binaryExpr : method.findAll(BinaryExpr.class)) {
//            BinaryExpr.Operator operator = binaryExpr.asBinaryExpr().getOperator();
//            if (operator == BinaryExpr.Operator.AND || operator == BinaryExpr.Operator.OR) {
//                complexity++; // 布尔运算符作为判定节点，增加 1
//            }
//        }

        return complexity;
    }

//    // 计算给定语句中的判定节点数量
//    private int countDecisionNodes(Statement stmt) {
//        int decisionNodes = 0;
//
//        if (stmt instanceof IfStmt) {
//            decisionNodes++;
//            IfStmt ifStmt = (IfStmt) stmt;
//            decisionNodes += countDecisionNodes(ifStmt.getThenStmt());
//            if (ifStmt.getElseStmt().isPresent()) {
//                decisionNodes += countDecisionNodes(ifStmt.getElseStmt().get());
//            }
//        } else if (stmt instanceof WhileStmt || stmt instanceof DoStmt || stmt instanceof ForStmt) {
//            decisionNodes++;
//        } else if (stmt instanceof ExpressionStmt) {
//            ExpressionStmt exprStmt = (ExpressionStmt) stmt;
//            if (hasDecisionNode(exprStmt.getExpression())) {
//                decisionNodes++;
//            }
//        }
//
//        return decisionNodes;
//    }
//
//    // 判断给定表达式是否包含判定节点
//    private boolean hasDecisionNode(Expression expr) {
//        return expr.isBinaryExpr() || expr.isConditionalExpr();
//    }


}
