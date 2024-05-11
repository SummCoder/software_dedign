package org.example.oj.composite;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

/**
 * @author SummCoder
 * @desc 单个函数圈复杂度计算
 * @date 2024/5/12 0:47
 */
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
