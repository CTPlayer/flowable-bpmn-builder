package com.cheng.flowablebpmnbuilder.builder.gateway;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @ClassName GatewayBuilder
 * @Description TODO
 * @Author CTPlayer
 * @DATE 2020/8/13 4:39 下午
 * @Version 1.0
 **/
@Getter
@Builder
public class ExclusiveGatewayBuilder {
    @NonNull
    private String id;
    private String name;
//    @NonNull
//    private List<SequenceFlowBuilder> sequenceFlowBuilderList;
}
