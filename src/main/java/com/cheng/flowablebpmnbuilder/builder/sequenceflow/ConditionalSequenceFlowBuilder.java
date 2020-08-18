package com.cheng.flowablebpmnbuilder.builder.sequenceflow;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @ClassName ConditionalSequenceFlow
 * @Description TODO
 * @Author CTPlayer
 * @DATE 2020/8/14 3:45 下午
 * @Version 1.0
 **/
@Getter
@Builder
public class ConditionalSequenceFlowBuilder {
    private String id;
    @NonNull
    private String sourceRef;
    @NonNull
    private String targetRef;
    /**
     * 条件顺序流条件表达式
     */
    @NonNull
    private String conditionExpression;
}
