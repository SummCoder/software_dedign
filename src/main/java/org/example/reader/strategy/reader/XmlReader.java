package org.example.reader.strategy.reader;

import org.example.reader.entity.exam.Exam;
import org.example.reader.entity.question.Question;
import org.example.reader.entity.sample.Sample;
import org.example.reader.factory.question.QuestionFactory;
import org.example.reader.factory.util.TypeFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SummCoder
 * @desc XML读取文件策略类
 * @date 2024/3/26 22:16
 */
public class XmlReader implements FileReader {

    /**
     * 变化的事物：问题的类型
     */

    @Override
    public Exam getExam(String examPath) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(examPath));
            Element root = doc.getRootElement();
            List<Question> questions = new ArrayList<>();
            Element questionsElement = root.getChild("questions");
            List<Element> questionElements = questionsElement.getChildren("question");
            for (Element questionElement : questionElements) {
                int type = Integer.parseInt(questionElement.getChildText("type"));
                Question question;
                QuestionFactory questionFactory;
                TypeFactory typeFactory = new TypeFactory();
                // 简单工厂获取具体工厂类
                questionFactory = typeFactory.getQuestionFactory(type);
                assert questionFactory != null;
                Map<String, Object> map = new HashMap<>();
                if (questionElement.getChild("options") != null) {
                    Element options = questionElement.getChild("options");
                    List<Element> optionsElement = options.getChildren("option");
                    List<String> optionsList = new ArrayList<>();
                    for (Element optionElement : optionsElement) {
                        String option = optionElement.getText();
                        optionsList.add(option);
                    }
                    map.put("options", optionsList);
                }
                if (questionElement.getChild("answer") != null) {
                    map.put("answer", Integer.valueOf(questionElement.getChildText("answer")));
                }
                if (questionElement.getChild("answers") != null) {
                    Element options = questionElement.getChild("answers");
                    List<Element> childElement = options.getChildren("answer");
                    List<Integer> optionsList = new ArrayList<>();
                    for (Element child : childElement) {
                        Integer option = Integer.valueOf(child.getText());
                        optionsList.add(option);
                    }
                    map.put("answers", optionsList);
                }
                if (questionElement.getChild("scoreMode") != null) {
                    map.put("scoreMode", questionElement.getChildText("scoreMode"));
                }
                if (questionElement.getChild("fixScore") != null) {
                    map.put("fixScore", Integer.valueOf(questionElement.getChildText("fixScore")));
                }
                if (questionElement.getChild("partialScores") != null) {
                    Element options = questionElement.getChild("partialScores");
                    List<Element> childElement = options.getChildren("partialScore");
                    List<String> optionsList = new ArrayList<>();
                    for (Element child : childElement) {
                        String option = child.getText();
                        optionsList.add(option);
                    }
                    map.put("partialScore", optionsList);
                }
                if (questionElement.getChild("timeLimit") != null) {
                    map.put("timeLimit", Integer.valueOf(questionElement.getChildText("timeLimit")));
                }
                if (questionElement.getChild("samples") != null) {
                    Element options = questionElement.getChild("samples");
                    List<Element> childElement = options.getChildren("sample");
                    List<Sample> optionsList = new ArrayList<>();
                    for (Element child : childElement) {
                        String input = child.getChildText("input");
                        String output = child.getChildText("output");
                        optionsList.add(new Sample(input, output));
                    }
                    map.put("samples", optionsList);
                }
                question = questionFactory.getQuestion();
                // 如何赋值特有属性？
                question.setId(Integer.valueOf(questionElement.getChildText("id")));
                question.setType(Integer.valueOf(questionElement.getChildText("type")));
                question.setQuestion(questionElement.getChildText("question"));
                question.setPoints(Integer.valueOf(questionElement.getChildText("points")));
                question = question.init(map, question);
                questions.add(question);
//                System.out.println(question);
            }
            Integer id = Integer.parseInt(root.getChildText("id"));
            String title = root.getChildText("title");
            Long startTime = Long.parseLong(root.getChildText("startTime"));
            Long endTime = Long.parseLong(root.getChildText("endTime"));
            return new Exam(id, title, startTime, endTime, questions);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
