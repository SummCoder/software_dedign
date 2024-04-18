package org.example.oj.strategy.score.multiple;

import org.example.oj.entity.answer.Answer;
import org.example.oj.entity.question.MultipleChoiceQuestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SummCoder
 * @desc nothing给分模式
 * @date 2024/3/28 12:33
 */
public class NothingScore implements Score{
    @Override
    public Integer getScore(MultipleChoiceQuestion question, Answer answer) {
        // 创建数字到字母的映射关系
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "A");
        map.put(1, "B");
        map.put(2, "C");
        map.put(3, "D");
        List<Integer> answerList = question.getAnswer();
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer ans : answerList) {
            stringBuilder.append(map.get(ans));
        }
        if (stringBuilder.toString().equals(answer.getAnswer())) {
            return question.getPoints();
        }
        return 0;
    }
}
