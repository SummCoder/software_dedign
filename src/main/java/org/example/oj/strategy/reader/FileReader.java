package org.example.oj.strategy.reader;

import org.example.oj.entity.exam.Exam;

/**
 * @author SummCoder
 * @desc 文件读取接口类
 * @date 2024/3/26 22:14
 */
public interface FileReader {
    Exam getExam(String examPath);
}
