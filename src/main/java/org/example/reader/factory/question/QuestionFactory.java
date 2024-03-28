package org.example.reader.factory.question;

import org.example.reader.entity.question.Question;

import java.util.Map;

/**
 * @author SummCoder
 * @desc 问题抽象工厂类
 * @date 2024/3/27 17:23
 */
public abstract class QuestionFactory {
    public abstract Question getQuestion();
}
