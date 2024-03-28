package org.example.oj.factory.question;

import org.example.oj.entity.question.Question;

/**
 * @author SummCoder
 * @desc 问题抽象工厂类
 * @date 2024/3/27 17:23
 */
public abstract class QuestionFactory {
    public abstract Question getQuestion();
}
