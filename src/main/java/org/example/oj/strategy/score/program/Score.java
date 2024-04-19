package org.example.oj.strategy.score.program;

import org.example.oj.entity.question.ProgrammingQuestion;

/**
 * @author SummCoder
 * @desc 编程题的评分接口
 * @date 2024/4/17 23:13
 */
public interface Score {
    Integer compileCode(String outputPath, String sourcePath);
    Integer executeCode(String classFilePath, ProgrammingQuestion question, String outputFilePath);
    Integer score(ProgrammingQuestion programmingQuestion, String outputFilePath);
}
