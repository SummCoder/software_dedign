package org.example.oj.factory.question;

import org.example.oj.entity.question.Question;
import org.example.oj.entity.question.SingleChoiceQuestion;

/**
 * @author SummCoder
 * @desc 单选题工厂
 * @date 2024/3/27 17:26
 */
public class SingleChoiceQuestionFactory extends QuestionFactory{

    @Override
    public Question getQuestion() {
        return new SingleChoiceQuestion();
    }
}
