package org.example;

import org.example.reader.Reader;
import org.example.reader.entity.exam.Exam;

import java.io.File;

/**
 * @author SummCoder
 * @date 2024/03/22 23:51
 */

public class Main {
    public static void main(String[] args) {
        String casePath = args[0];
        // 题目文件夹路径
        String examsPath = casePath + System.getProperty("file.separator") + "exams";
        // 答案文件夹路径
        String answersPath = casePath + System.getProperty("file.separator") + "answers";
        // 输出文件路径
        String output = args[1];
        // TODO:在下面调用你实现的功能
        // 遍历读取所有题目文件夹中的文件
        File examFolder = new File(examsPath);
        File[] files = examFolder.listFiles();
        assert files != null;
        // 变化的量：文件的类型
        for (File file : files) {
            // 获取文件拓展名，例如json、xml
            String fileName = file.getName();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            // 声明一个文件读取者
            Reader reader = new Reader(fileExtension);
            String examPath = file.getAbsolutePath();
            // 获得试卷
            Exam exam = reader.getExam(examPath);
//            System.out.println(exam);
            // 进行评分

        }
    }
}