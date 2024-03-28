package org.example.oj.factory.util;

import org.example.oj.factory.question.MultipleChoiceQuestionFactory;
import org.example.oj.factory.question.ProgrammingQuestionFactory;
import org.example.oj.factory.question.QuestionFactory;
import org.example.oj.factory.question.SingleChoiceQuestionFactory;

/**
 * @author SummCoder
 * @desc 简单工厂，用于判断题目类型创建不同的具体工厂，工厂创建工厂
 * @date 2024/3/27 19:47
 */
public class TypeFactory {
    public QuestionFactory getQuestionFactory(int type) {
        switch (type) {
            case 1:
                return new SingleChoiceQuestionFactory();
            case 2:
                return new MultipleChoiceQuestionFactory();
            case 3:
                return new ProgrammingQuestionFactory();
            default:
                return null;
        }
    }
}
