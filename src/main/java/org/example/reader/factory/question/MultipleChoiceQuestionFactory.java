package org.example.reader.factory.question;

import org.example.reader.entity.question.MultipleChoiceQuestion;
import org.example.reader.entity.question.Question;

import java.util.Map;

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
