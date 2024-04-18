package org.example.oj.constant;

import lombok.Data;

/**
 * @author SummCoder
 * @desc 存放一些常量
 * @date 2024/4/17 23:41
 */
@Data
public class Constant {
    private static String examsPath;
    private static String answersPath;
    private static String outputPath;

    public static void setExamsPath(String exam) {
        examsPath = exam;
    }

    public static void setAnswerPath(String answer) {
        answersPath = answer;
    }

    public static void setOutputPath(String output) {
        outputPath = output;
    }

    public static String getExamsPath() {
        return examsPath;
    }

    public static String getAnswerPath() {
        return answersPath;
    }

    public static String getOutputPath() {
        return outputPath;
    }
}
