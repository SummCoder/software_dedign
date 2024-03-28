package org.example.reader.factory.question;

import org.example.reader.entity.question.Question;
import org.example.reader.entity.question.SingleChoiceQuestion;

import java.util.Map;

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
