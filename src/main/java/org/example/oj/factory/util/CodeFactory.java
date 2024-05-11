package org.example.oj.factory.util;

import org.example.oj.constant.Constant;
import org.example.oj.entity.answer.Answer;
import org.example.oj.entity.question.ProgrammingQuestion;
import org.example.oj.strategy.score.program.JavaScore;
import org.example.oj.strategy.score.program.Score;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author SummCoder
 * @desc 编程题简单工厂
 * @date 2024/4/17 23:56
 */
public class CodeFactory {
    private Score score;
    public CodeFactory(String type) {
        if (Objects.equals(type, "java")) {
            this.score = new JavaScore();
        }
    }

    public Integer getScore(ProgrammingQuestion question, Answer answer) {
        String sourcePath = Constant.getAnswerPath() + System.getProperty("file.separator") + answer.getAnswer();
        String outputPath = Constant.getAnswerPath() + System.getProperty("file.separator") + "output";
        String fileName = answer.getAnswer().replace("code-answers/", "").replace(".java", "");
        String resultPath = Constant.getAnswerPath() + System.getProperty("file.separator") + "result" + System.getProperty("file.separator") + fileName;
        int compileResult = score.compileCode(outputPath, sourcePath);
        if (compileResult == 0) {
            int executeResult = score.executeCode(fileName, question, resultPath);
            if (executeResult == 1) {
                return score.score(question, resultPath);
            }
        }

        return 0;
    }

    public int getComplexity(Answer answer) {
        String sourcePath = Constant.getAnswerPath() + System.getProperty("file.separator") + answer.getAnswer();
        String outputPath = Constant.getAnswerPath() + System.getProperty("file.separator") + "output";
        int compileResult = score.compileCode(outputPath, sourcePath);
        if (compileResult == 0) {
            try {
                String code = new String(Files.readAllBytes(Paths.get(sourcePath)));
                return score.calculateCyclomaticComplexity(code);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return -1;
    }
}
