package org.example.reader.entity.question;

import lombok.Data;

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
}
