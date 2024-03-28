package org.example.oj.factory.question;

import org.example.oj.entity.question.ProgrammingQuestion;
import org.example.oj.entity.question.Question;

/**
 * @author SummCoder
 * @desc 编程题工厂
 * @date 2024/3/27 17:26
 */
public class ProgrammingQuestionFactory extends QuestionFactory {
    @Override
    public Question getQuestion() {
        return new ProgrammingQuestion();
    }
}
