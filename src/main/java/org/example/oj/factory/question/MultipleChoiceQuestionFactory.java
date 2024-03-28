package org.example.oj.factory.question;

import org.example.oj.entity.question.MultipleChoiceQuestion;
import org.example.oj.entity.question.Question;

/**
 * @author SummCoder
 * @desc 多选题工厂
 * @date 2024/3/27 17:26
 */
public class MultipleChoiceQuestionFactory extends QuestionFactory {
    @Override
    public Question getQuestion() {
        return new MultipleChoiceQuestion();
    }
}
