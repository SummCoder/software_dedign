# software_design

软件系统设计三次迭代

# iter1

软件设计的核心目的：软件的可维护和可复用。

设计原则：
- 单一职责原则
- 开闭原则
- 里氏替换原则
- 依赖倒转原则
- 接口隔离原则
- 合成复用原则
- 迪米特法则

现有已学的设计模式：
- 策略模式
- 简单工厂
- 工厂方法
- 抽象工厂
- 建造者模式
- 原型模式


我目前迭代一的设计就基于此进行。

首先明晰一下迭代一的功能需求：题目读取和评分，我就依据流程来进行设计了。

整体设计类图如下：
![image.png](https://s2.loli.net/2024/05/11/OLrSsZvxADoQ5Jp.png)

## Part1

### 文件读取

首先是试题文件的读取。

第一个变化的量为需要支持XML与JSON文件读取一次测试的所有题目信息。我在这里也没有做过多考虑，不同的读取方式，那就简单工厂+策略模式，其中简单工厂的参数为文件的类型（后缀名），由此得到了下面的一段代码：

```java
public class ReaderFactory {
    // 读取方法策略类
    private FileReader fileReader;

    public ReaderFactory(String type) {
        // 简单工厂 + 策略模式选择构建Reader策略
        if (type.equals("json")) {
            this.fileReader = new JsonReader();
        } else if (type.equals("xml")) {
            this.fileReader = new XmlReader();
        }
    }

    public Exam getExam(String examPath) {
        return this.fileReader.getExam(examPath);
    }
}
```

由于目前工厂类负责创建的对象较少，简单工厂即可满足我的需求和日后可能的拓展需求。同时结合策略模式，依赖于抽象接口而非具体实现，很大程度上减少了代码的耦合。日后若要新增不同的文件，直接implements接口并在上述简单工厂增加 if 即可。

### 题目读取

紧接着来到了第二个会变化的点，三种类型的题目，该如何抽离出变化的量呢？我是想到了利用工厂方法去做到这一点。

```java
public abstract class QuestionFactory {
    public abstract Question getQuestion();
}
```

之所以不直接采用简单工厂，一来是我进行题目分析读取的地方为不同的具体策略类中，如果使用简单工厂，增加一种题型，如果我存在5种不同的文件类型，则需要修改至少5处代码实现，耦合度过高。

这里我采用了一种简单工厂+工厂方法的设计方法。
```text
int type = Integer.parseInt(questionElement.getChildText("type"));
Question question;
QuestionFactory questionFactory;
TypeFactory typeFactory = new TypeFactory();
// 简单工厂获取具体工厂类
questionFactory = typeFactory.getQuestionFactory(type);
```

根据题目的type属性，用一个简单工厂去实例化具体的工厂，代码如下：
```java
public class TypeFactory {
    public QuestionFactory getQuestionFactory(int type) {
        switch (type) {
            case 1:
                return new SingleChoiceQuestionFactory();
            case 2:
                return new MultipleChoiceQuestionFactory();
            case 3:
                return new ProgrammingQuestionFactory();
            default:
                return null;
        }
    }
}
```

新增题目类型时，由于类型全部交由简单工厂，因此只需修改简单工厂新增具体工厂即可，各个原先策略类中的代码完全不需要修改，高度的可扩展。

一个问题在这里就困扰我了，根据工厂方法是由具体的QuestionFactory创建出具体的Question类型了，如何赋值呢？我们仅仅是new了一个对象出来啊，尽管这个对象目前已经是具体子类了。传参数给工厂去创建？可是这样我们不是还得判断现在具体是什么类型，由此我们需要读取什么参数？如果不这样，那么就需要将所有参数一股脑全部交给工厂，这行吗？

其实json这里是没有问题的，我可以直接通过`questions.add(objectMapper.treeToValue(questionNode, question.getClass()));`，让他帮我自动赋值转化为具体的对象。可是xml我似乎并没有发现如此简单的方法，又或者今后新增不同的文件类型，我们都这样做吗？

我是觉得不妥的，因此我采取了Map的方式，在`Question`抽象类中增加了`init(Map<String, Object> map, Question question)`方法。

在每一个具体实现类中，都像下面这样：
```java
public class xxx extends Question {
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
```

调用了子类的`init()`方法则必然存在这些特有的Key，今后新增文件类型时只需要遵循这一点，传递map，则这些部分代码基本不需要修改，同时确保只拿到了需要的数据。

由此，我们就结束了读取的相关工作。

## Part2

### 评分

这里我首先关注了题目类型的变化，在每一个`Question`类中新增方法`testAnswer`进行不同题型的对应评分。

以多选题为例：
```java
public class MultipleChoiceQuestion extends Question {
    @Override
    public int testAnswer(Question question, Answer answer) {
        MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
        String scoreMode = multipleChoiceQuestion.getScoreMode();
        // 利用工厂调用不同策略
        ModeFactory modeFactory = new ModeFactory(scoreMode);
        return modeFactory.getScore(multipleChoiceQuestion, answer);
    }
}
```

此时注意到多选题的需要中对于给分策略也是变化的，将变化抽离出来。跟先前类似，采取简单工厂+策略模式，借助一个简单的工厂`ModeFactory`

```java
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
```

由此工厂决定具体`score`策略，进而计算分数。


# iter2

## 整体设计

设计类图：
![image.png](https://s2.loli.net/2024/05/11/WYXfsg7wr3aIU89.png)

考虑到不同语言的评测要求，设计了相关的接口以及目前需要的Java语言的实现类：

Score接口设计如下：

```java
public interface Score {
    Integer compileCode(String outputPath, String sourcePath);
    Integer executeCode(String classFilePath, ProgrammingQuestion question, String outputFilePath);
    Integer score(ProgrammingQuestion programmingQuestion, String outputFilePath);
}
```
三个模块分别负责预处理、执行和编程题评分。采取单一职责的设计思想，将功能解耦。

由于多处需要使用文件路径，通过参数传递的方式方法与方法之间耦合度极高，故抽离出一个类存放常量：
```java
public class Constant {
    private static String examsPath;
    private static String answersPath;
    private static String outputPath;

    public static void setExamsPath(String exam) {
        examsPath = exam;
    }

    public static void setAnswerPath(String answer) {
        answersPath = answer;
    }

    public static void setOutputPath(String output) {
        outputPath = output;
    }

    public static String getExamsPath() {
        return examsPath;
    }

    public static String getAnswerPath() {
        return answersPath;
    }

    public static String getOutputPath() {
        return outputPath;
    }
}
```

## 代码的预处理

还是使用一个简单工厂来生产具体的实现类：

```text
        CodeFactory codeFactory = new CodeFactory("java");
        return codeFactory.getScore((ProgrammingQuestion) question, answer);
```

Java预处理相关代码：
```text
    public Integer compileCode(String outputPath, String sourcePath) {
        Future<Integer> future = threadPool.submit(new CompileTaskImpl(outputPath, sourcePath));
        try {
            return future.get();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
```

通过将下面的任务放入线程池进行具体的任务执行。将具体任务的实现类与实体类抽离开来，易于进行不同任务的替换装配。

```java
public class CompileTaskImpl implements Callable<Integer> {

    private final String outputPath;
    private final String sourcePath;

    public CompileTaskImpl(String outputPath, String sourcePath) {
        this.outputPath = outputPath;
        this.sourcePath = sourcePath;
    }

    @Override
    public Integer call() throws Exception {
        try {
            // 调用命令行命令编译Java代码
            Process process = Runtime.getRuntime().exec("javac -d " + outputPath + " " + sourcePath);
            return process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
```

由于后面需要支持任意中任务的线程池，故将其设计继承自`Callable<Integer>`


## 代码的执行

```text
        List<Future<Integer>> futures = new ArrayList<>();

        for (Sample sample : samples) {
            Future<Integer> future = threadPool.submit(new ExecuteTaskImpl(classFilePath, sample, outputFilePath));
            futures.add(future);
        }
```

具体实际线程实现：
```text
public Integer call() {
        try {
            // 执行程序并将输出重定向到文件
            String[] inputArgs = sample.getInput().split(" ");
            List<String> commandList = new ArrayList<>();
            commandList.add("java");
            commandList.add("-cp");
            commandList.add(Constant.getAnswerPath() + System.getProperty("file.separator") + "output");
            commandList.add(classFilePath);
            commandList.addAll(Arrays.asList(inputArgs));

            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            processBuilder.redirectOutput((ProcessBuilder.Redirect.appendTo(new File(outputFilePath))));
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                // 运行出错，返回0分
                // 获取错误输出流
                InputStream errorStream = process.getErrorStream();
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
                String errorLine;
                StringBuilder errorOutput = new StringBuilder();
                // 读取错误输出
                while ((errorLine = errorReader.readLine()) != null) {
                    errorOutput.append(errorLine).append("\n");
                }
                // 输出错误信息
                System.out.println("命令行运行报错信息：" + errorOutput);
                return 0;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }
```

将结果重定向到对应文件。

## 并发需求
 

简单线程池的实现，为了支持任务拥有返回值，使用Callable，一个简单线程池如下：

```java
public class ThreadPool {

    private static final int poolSize = 5;
    private final Worker[] workers;
    private final BlockingQueue<FutureTask<Integer>> taskQueue;

    public ThreadPool() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.workers = new Worker[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    public Future<Integer> submit(Callable<Integer> task) {
        FutureTask<Integer> futureTask = new FutureTask<>(task);
        try {
            taskQueue.put(futureTask);
        } catch (InterruptedException e) {
            // 处理任务添加异常
            e.printStackTrace();
        }
        return futureTask;
    }

    private class Worker extends Thread {
        public void run() {
            while (true) {
                try {
                    FutureTask<Integer> futureTask = taskQueue.take();
                    futureTask.run();
                } catch (InterruptedException e) {
                    // 处理任务执行异常
                    e.printStackTrace();
                }
            }
        }
    }
}
```

由于编程题只有一道，故而我本人虽然觉得预处理编译时多线程意义不大，但执行代码时，采用多线程同时评测多个测试用例确实效率得到提升，整体评测时间由10s左右减少到6s左右。

# iter3

## 功能需求

迭代三需要我们对迭代二的编程题评测进一步扩展，要求实现一种代码性能评测限制指标——时间复杂度和一种代码风格评测指标——圈复杂度。

### 时间限制

在代码执行阶段对运行时间进行计算并对于超出时间限制的作答结果判0分。

### 圈复杂度

采用节点判定法，按照基本判定规则以相对统一的方式分别实现单个函数和整个类的圈复杂度实现。

## 设计类图

![image.png](https://s2.loli.net/2024/05/11/6OwnSghjoPKuJGp.png)

三次迭代后，整体的设计类图如上。

从这张图不难看出以下几点：
- 几乎所有的具体实现类的创建，我们都交由工厂去进行，符合工厂类的职责。
- 客户端调用尽量持有抽象类而非具体实现类
- 最小接口以及单一职责，每一个类尽量只有一个功能对外提供。

因此，该OJ系统是高内聚。低耦合的，我们可以说拓展新功能以及修改功能也会变得十分方便，且影响较小。

## 迭代三功能设计

### 时间限制

```java
// 等待执行线程
boolean completedWithinTime = process.waitFor(timeLimit, java.util.concurrent.TimeUnit.MILLISECONDS);

if (!completedWithinTime) {
    // 超出时间限制，返回0分
    process.destroy(); // 终止进程
    // 输出超时信息
    System.out.println("TimeOut!");
    return 0;
}
```

超时则终止运行进程并使得线程返回。

### 圈复杂度

考虑到不同编程语言圈复杂度的计算是会有所区别的，我们复用先前的JavaScore，为Score接口加上calculateCyclomaticComplexity方法，在具体实现类中针对不同编程语言进行具体实现。将相关的变化内聚起来。

```java
public Integer calculateCyclomaticComplexity(String code) {
    CompilationUnit cu = StaticJavaParser.parse(code);
    int classComplexity = 0;

    for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
        int methodComplexity = calculateMethodComplexity(method);
        classComplexity += methodComplexity;
    }

    return classComplexity;
}

private int calculateMethodComplexity(MethodDeclaration method) {
    int complexity = 1;

    for (IfStmt ignored : method.findAll(IfStmt.class)) {
        complexity++; // 每个 if 语句增加 1
    }

    for (WhileStmt ignored : method.findAll(WhileStmt.class)) {
        complexity++; // 每个 while 循环增加 1
    }

    for (DoStmt ignored : method.findAll(DoStmt.class)) {
        complexity++; // 每个 do-while 循环增加 1
    }

    for (ForStmt ignored : method.findAll(ForStmt.class)) {
        complexity++; // 每个 for 循环增加 1
    }

    for (ConditionalExpr ignored : method.findAll(ConditionalExpr.class)) {
        complexity++;
    }

    for (BinaryExpr binaryExpr : method.findAll(BinaryExpr.class)) {
        BinaryExpr.Operator operator = binaryExpr.asBinaryExpr().getOperator();
        if (operator == BinaryExpr.Operator.AND || operator == BinaryExpr.Operator.OR) {
            complexity++; // 布尔运算符作为判定节点，增加 1
        }
    }

    return complexity;
}
```

由于直接采用节点判定法，我们直接采用上述一段简单的代码，查找所有符合的节点即可。

当然，简化一下如下写就行：
```java
complexity += method.findAll(IfStmt.class).size() + method.findAll(WhileStmt.class).size() + method.findAll(DoStmt.class).size() + method.findAll(ForStmt.class).size() + method.findAll(ConditionalExpr.class).size();

for (BinaryExpr binaryExpr : method.findAll(BinaryExpr.class)) {
    BinaryExpr.Operator operator = binaryExpr.asBinaryExpr().getOperator();
    if (operator == BinaryExpr.Operator.AND || operator == BinaryExpr.Operator.OR) {
        complexity++; // 布尔运算符作为判定节点，增加 1
    }
}
```

~~为了以相对统一的方式实现单个函数和整个类的圈复杂度实现，我们规定他们的输入都为String，return都为计算得到的圈复杂度。~~

#### 设计调整

在笔者依据设计文档设计完成后重新审视该设计时，发觉这里其实与课堂所讲树形结构是极其相似的，类中有函数，类中也可以有其他的类，当然这里测试极其简单，只有单一的类。

如此，应该可以使用组合模型对于上述的实现进行优化？加之需求要求相对一致地对待单个函数和整个类，不就更加符合组合模式的适用情形？

进行设计的修改

抽象元素：
```java
public abstract class AbstractElement {
    public abstract Integer calculateCyclomaticComplexity();
    public abstract void add(AbstractElement element);
    public abstract void remove(AbstractElement element);
    public abstract AbstractElement getChild(int i);
}
```

类元素：
```java
public class ClassElement extends AbstractElement{

    private ArrayList<AbstractElement> children = new ArrayList<>();

    @Override
    public Integer calculateCyclomaticComplexity() {
        int complexity = 0;
        for (AbstractElement obj : children) {
            complexity += obj.calculateCyclomaticComplexity();
        }
        return complexity;
    }

    @Override
    public void add(AbstractElement element) {
        children.add(element);
    }

    @Override
    public void remove(AbstractElement element) {
        children.remove(element);
    }

    @Override
    public AbstractElement getChild(int i) {
        return children.get(i);
    }
}
```

方法元素：
```java
public class MethodElement extends AbstractElement{

    private String code;

    public MethodElement(String code) {
        this.code = code;
    }

    @Override
    public Integer calculateCyclomaticComplexity() {
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(code);
        int complexity = 1;
        complexity += method.findAll(IfStmt.class).size() + method.findAll(WhileStmt.class).size() + method.findAll(DoStmt.class).size() + method.findAll(ForStmt.class).size() + method.findAll(ConditionalExpr.class).size();
        for (BinaryExpr binaryExpr : method.findAll(BinaryExpr.class)) {
            BinaryExpr.Operator operator = binaryExpr.getOperator();
            if (operator == BinaryExpr.Operator.AND || operator == BinaryExpr.Operator.OR) {
                complexity++; // Boolean operators as decision points, add 1
            }
        }
        return complexity;
    }

    @Override
    public void add(AbstractElement element) {

    }

    @Override
    public void remove(AbstractElement element) {

    }

    @Override
    public AbstractElement getChild(int i) {
        return null;
    }
}
```

客户端调用：
```java
public Integer calculateCyclomaticComplexity(String code) {
    AbstractElement classElement = new ClassElement();
    CompilationUnit cu = StaticJavaParser.parse(code);
    for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
        MethodElement methodElement = new MethodElement(method.toString());
        classElement.add(methodElement);
    }
    return classElement.calculateCyclomaticComplexity();
}
```

采用上面的设计，进行递归调用即可。当然这里只有一个类，不必如此。

更改后的设计类图：
![image.png](https://s2.loli.net/2024/05/12/KEHfyszLx1Bd8A2.png)

## 三次迭代的变化

![image.png](https://s2.loli.net/2024/05/11/OLrSsZvxADoQ5Jp.png)

![image.png](https://s2.loli.net/2024/05/11/WYXfsg7wr3aIU89.png)

![image.png](https://s2.loli.net/2024/05/12/KEHfyszLx1Bd8A2.png)

可以看出，随着不断进行迭代，系统的复杂度以及组件不断增加，采用好的设计原则可以使得我们不改变原有函数以及类的情况下，进行系统的扩展和维护。

![image.png](https://s2.loli.net/2024/05/11/hliJUORIMz8THXy.png)

工厂类和策略类的分别存放，良好的项目组织结构。

## 关于前两次迭代的反思

前一次需要实现线程池用以加速，当时的想法就是将一道题目的所有输出全部重定向到一个文件之中去。当时就被一个问题困扰许久，也就是多线程重定向先后的问题。

由于上次的测试用例较为宽容，这个问题虽然纠结许久，但是最后采用了一个十分不合理的方式通过了测试，即遍历重定向后的文件内容，只要有与答案文件相同的输出且数量等于测试用例数量即判定为通过测试。

但这次测试用例中出现一道题目测试样例中有相同的答案，此方法便行不通了。

最后思考出来的解决方案：将每个测试用例输出重定向到不同文件，如此也不需要考虑写入先后的问题，读取相对应文件内容即可。

反思：很多在某些阶段看起来能解决问题（就比如这里通过测试）的设计，在很多时候，往往是灾难性的，会给未来的维护以及出现的未曾想过的情况造成许许多多的困扰。


