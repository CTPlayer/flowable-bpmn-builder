package com.cheng.flowablebpmnbuilder.builder.sequenceflow;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @ClassName SequenceFlowBuilder
 * @Description TODO
 * @Author CTPlayer
 * @DATE 2020/8/13 4:19 下午
 * @Version 1.0
 **/
@Getter
@Builder
public class NoneSequenceFlowBuilder {
    private String id;
    @NonNull
    private String sourceRef;
    @NonNull
    private String targetRef;
}
