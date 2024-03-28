package org.example.reader.factory.question;

import org.example.reader.entity.question.ProgrammingQuestion;
import org.example.reader.entity.question.Question;

import java.util.Map;

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
