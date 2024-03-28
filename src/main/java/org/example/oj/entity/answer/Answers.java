package org.example.oj.entity.answer;

import lombok.Data;

import java.util.List;

/**
 * @author SummCoder
 * @desc 作答试卷
 * @date 2024/3/28 10:39
 */

@Data
public class Answers {
    private Integer examId;
    private Integer stuId;
    private Long submitTime;
    private List<Answer> answers;
}
