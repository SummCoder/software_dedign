package org.example.oj.entity.exam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.oj.entity.question.Question;

import java.util.List;

/**
 * @author SummCoder
 * @desc Exam实体类
 * @date 2024/3/27 15:13
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exam {
    private Integer id;
    private String title;
    private Long startTime;
    private Long endTime;
    private List<Question> questions;
}
