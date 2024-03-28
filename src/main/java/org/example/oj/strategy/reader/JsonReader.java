package org.example.oj.strategy.reader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.oj.entity.exam.Exam;
import org.example.oj.entity.question.Question;
import org.example.oj.factory.question.*;
import org.example.oj.factory.util.TypeFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SummCoder
 * @desc Json文件读取策略类
 * @date 2024/3/26 22:17
 */
public class JsonReader implements FileReader {

    /**
     * 变化的事物：问题的类型
     */
    @Override
    public Exam getExam(String examPath) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(examPath));
            // 解析题目
            JsonNode questionsNode = jsonNode.get("questions");
            List<Question> questions = new ArrayList<>();
            for (JsonNode questionNode : questionsNode) {
                int type = questionNode.get("type").asInt();
                Question question;
                QuestionFactory questionFactory;
                TypeFactory typeFactory = new TypeFactory();
                // 简单工厂获取具体工厂类
                questionFactory = typeFactory.getQuestionFactory(type);
                assert questionFactory != null;
                question = questionFactory.getQuestion();
                questions.add(objectMapper.treeToValue(questionNode, question.getClass()));
            }
            Integer id = jsonNode.get("id").asInt();
            String title = jsonNode.get("title").asText();
            Long startTime = jsonNode.get("startTime").asLong();
            Long endTime = jsonNode.get("endTime").asLong();
            return new Exam(id, title, startTime, endTime, questions);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
