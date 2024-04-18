package org.example.oj.strategy.score.multiple;

import org.example.oj.entity.answer.Answer;
import org.example.oj.entity.question.MultipleChoiceQuestion;

/**
 * @author SummCoder
 * @desc 给分策略类接口
 * @date 2024/3/28 12:32
 */
public interface Score {
    Integer getScore(MultipleChoiceQuestion question, Answer answer);
}
