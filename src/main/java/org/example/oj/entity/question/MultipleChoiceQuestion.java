package org.example.oj.entity.question;

import lombok.Data;
import org.example.oj.entity.answer.Answer;
import org.example.oj.factory.util.ModeFactory;

import java.util.List;
import java.util.Map;

/**
 * @author SummCoder
 * @desc 多选题
 * @date 2024/3/27 17:20
 */

@Data
public class MultipleChoiceQuestion extends Question {
    private List<String> options;
    private List<Integer> answer;
    private String scoreMode;
    private Integer fixScore;
    private List<Integer> partialScore;

    @Override
    public Question init(Map<String, Object> map, Question question) {
        MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
        multipleChoiceQuestion.setOptions((List<String>) map.get("options"));
        multipleChoiceQuestion.setAnswer((List<Integer>) map.get("answers"));
        multipleChoiceQuestion.setScoreMode((String) map.get("scoreMode"));
        multipleChoiceQuestion.setFixScore((Integer) map.get("fixScore"));
        multipleChoiceQuestion.setPartialScore((List<Integer>) map.get("partialScore"));
        return multipleChoiceQuestion;
    }

    @Override
    public int testAnswer(Question question, Answer answer) {
        MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
        String scoreMode = multipleChoiceQuestion.getScoreMode();
        // 利用工厂调用不同策略
        ModeFactory modeFactory = new ModeFactory(scoreMode);
        return modeFactory.getScore(multipleChoiceQuestion, answer);
    }
}
