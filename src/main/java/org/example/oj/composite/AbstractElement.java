package org.example.oj.composite;

/**
 * @author SummCoder
 * @desc 类和方法计算圈复杂度的抽象父类
 * @date 2024/5/12 0:45
 */
public abstract class AbstractElement {
    public abstract Integer calculateCyclomaticComplexity();
    public abstract void add(AbstractElement element);
    public abstract void remove(AbstractElement element);
    public abstract AbstractElement getChild(int i);
}
