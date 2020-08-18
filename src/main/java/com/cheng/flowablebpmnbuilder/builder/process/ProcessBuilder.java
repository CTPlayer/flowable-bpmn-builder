package com.cheng.flowablebpmnbuilder.builder.process;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @ClassName ProcessBuilder
 * @Description process构造类
 * @Author CTPlayer
 * @DATE 2020/8/13 2:39 下午
 * @Version 1.0
 **/
@Getter
@Builder
public class ProcessBuilder {
    @NonNull
    private String id;
    private String name;
}
