package org.example.oj.factory.util;

import org.example.oj.entity.answer.Answer;
import org.example.oj.entity.question.MultipleChoiceQuestion;
import org.example.oj.strategy.score.multiple.FixScore;
import org.example.oj.strategy.score.multiple.NothingScore;
import org.example.oj.strategy.score.multiple.PartialScore;
import org.example.oj.strategy.score.multiple.Score;

import java.util.Objects;

/**
 * @author SummCoder
 * @desc 评分策略简单工厂
 * @date 2024/3/28 12:27
 */
public class ModeFactory {
    private Score score;
    public ModeFactory(String mode) {
        if (Objects.equals(mode, "fix")) {
            this.score = new FixScore();
        }else if (Objects.equals(mode, "nothing")) {
            this.score = new NothingScore();
        } else if (Objects.equals(mode, "partial")) {
            this.score = new PartialScore();
        }
    }

    public Integer getScore(MultipleChoiceQuestion question, Answer answer) {
        return score.getScore(question, answer);
    }
}
