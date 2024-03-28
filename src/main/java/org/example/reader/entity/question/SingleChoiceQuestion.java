package org.example.reader.entity.question;

import lombok.Data;

import java.util.List;
import java.util.Map;

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
}
