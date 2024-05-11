package org.example.oj.entity.question;

import lombok.Data;
import org.example.oj.entity.answer.Answer;
import org.example.oj.entity.sample.Sample;
import org.example.oj.factory.util.CodeFactory;

import java.util.List;
import java.util.Map;

/**
 * @author SummCoder
 * @desc 编程题
 * @date 2024/3/27 17:21
 */

@Data
public class ProgrammingQuestion extends Question{
    private List<Sample> samples;
    private Integer timeLimit;

    @Override
    public Question init(Map<String, Object> map, Question question) {
        ProgrammingQuestion programmingQuestion = (ProgrammingQuestion) question;
        programmingQuestion.setSamples((List<Sample>) map.get("samples"));
        programmingQuestion.setTimeLimit((Integer) map.get("timeLimit"));
        return programmingQuestion;
    }

    @Override
    public int testAnswer(Question question, Answer answer) {
        CodeFactory codeFactory = new CodeFactory("java");
        return codeFactory.getScore((ProgrammingQuestion) question, answer);
    }

    public int calculateCyclomaticComplexity(Answer answer) {
        CodeFactory codeFactory = new CodeFactory("java");
        return codeFactory.getComplexity(answer);
    }
}
