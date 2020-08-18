package com.cheng.flowablebpmnbuilder.builder.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @ClassName EndEventBuilder
 * @Description TODO
 * @Author CTPlayer
 * @DATE 2020/8/13 4:14 下午
 * @Version 1.0
 **/
@Getter
@Builder
public class EndEventBuilder {
    @NonNull
    private String id;
    private String name;
}
