package org.example.oj.entity.question;

import lombok.Data;
import org.example.oj.entity.answer.Answer;

import java.util.Map;

/**
 * @author SummCoder
 * @desc 问题实体类
 * @date 2024/3/27 16:36
 */

@Data
public abstract class Question {
    private Integer id;
    private Integer type;
    private String question;
    private Integer points;
    public abstract Question init(Map<String, Object> map, Question question);

    public abstract int testAnswer(Question question, Answer answer);
}
