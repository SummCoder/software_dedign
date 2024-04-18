package org.example.oj.strategy.score.multiple;

import org.example.oj.entity.answer.Answer;
import org.example.oj.entity.question.MultipleChoiceQuestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SummCoder
 * @desc partial给分策略
 * @date 2024/3/28 12:33
 */
public class PartialScore implements Score{
    @Override
    public Integer getScore(MultipleChoiceQuestion question, Answer answer) {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "A");
        map.put(1, "B");
        map.put(2, "C");
        map.put(3, "D");
        List<Integer> answerList = question.getAnswer();
        List<Integer> partialScore = question.getPartialScore();
        String answerString = answer.getAnswer();
        int score = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer ans : answerList) {
            stringBuilder.append(map.get(ans));
        }
        String exceptedString = stringBuilder.toString();
        for (int i = 0; i < answerString.length(); i++) {
            if (!exceptedString.contains(answerString.subSequence(i, i+1))){
                return 0;
            }
        }
        for (int i = 0; i < exceptedString.length(); i++) {
            if (answerString.contains(exceptedString.subSequence(i, i+1))) {
                score += partialScore.get(i);
            }
        }
        return score;
    }
}
