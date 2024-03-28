package org.example.oj.entity.question;

import lombok.Data;
import org.example.oj.entity.answer.Answer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author SummCoder
 * @desc 单选题
 * @date 2024/3/27 17:20
 */

@Data
public class SingleChoiceQuestion extends Question{
    private List<String> options;
    private Integer answer;

    @Override
    public Question init(Map<String, Object> map, Question question) {
        SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion) question;
        singleChoiceQuestion.setOptions((List<String>) map.get("options"));
        singleChoiceQuestion.setAnswer((Integer) map.get("answer"));
        return singleChoiceQuestion;
    }

    @Override
    public int testAnswer(Question question, Answer answer) {
        SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion) question;
        String answerString = answer.getAnswer();
        int answerInteger = 0;
        switch (answerString) {
            case "A":
                break;
            case "B":
                answerInteger = 1;
                break;
            case "C":
                answerInteger = 2;
                break;
            case "D":
                answerInteger = 3;
                break;
        }
        if (Objects.equals(singleChoiceQuestion.getAnswer(), answerInteger)) {
            return singleChoiceQuestion.getPoints();
        }
        return 0;
    }
}
