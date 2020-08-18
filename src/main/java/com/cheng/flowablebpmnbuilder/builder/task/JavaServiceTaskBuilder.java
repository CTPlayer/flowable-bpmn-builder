package com.cheng.flowablebpmnbuilder.builder.task;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

/**
 * @ClassName JavaServiceTask
 * @Description TODO
 * @Author CTPlayer
 * @DATE 2020/8/14 11:00 上午
 * @Version 1.0
 **/
@Getter
@Builder
public class JavaServiceTaskBuilder {
    @NonNull
    private String id;
    private String name;
    @NonNull
    private String fullyQualifiedClassname;
    /**
     * delegateExpressionBean是一个实现了JavaDelegate接口的bean，定义在Spring容器中
     */
    private String delegateExpression;
    /**
     * 指定服务的表达式
     */
    private String expression;
    /**
     * 为逻辑实现类设置固定属性值
     */
    private List<Map<String, String>> fieldMapList;
    /**
     * 为逻辑实现类设置动态属性值
     */
    private List<Map<String, String>> expressionFieldMapList;

    private String resultVariable;
}
