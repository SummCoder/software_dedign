package org.example.oj.composite;

import java.util.ArrayList;

/**
 * @author SummCoder
 * @desc 类复杂度的计算
 * @date 2024/5/12 0:47
 */
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
