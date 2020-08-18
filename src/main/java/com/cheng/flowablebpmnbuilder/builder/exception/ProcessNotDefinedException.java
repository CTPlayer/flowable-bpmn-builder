package com.cheng.flowablebpmnbuilder.builder.exception;

/**
 * @ClassName ProcessNotDefinedException
 * @Description TODO
 * @Author CTPlayer
 * @DATE 2020/8/18 4:59 下午
 * @Version 1.0
 **/
public class ProcessNotDefinedException extends RuntimeException {
    public ProcessNotDefinedException() {
        super("缺少process的构建信息");
    }
}
