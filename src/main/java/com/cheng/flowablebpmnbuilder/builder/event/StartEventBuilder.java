package com.cheng.flowablebpmnbuilder.builder.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @ClassName EventBuilder
 * @Description event构造类
 * @Author CTPlayer
 * @DATE 2020/8/12 4:23 下午
 * @Version 1.0
 **/
@Getter
@Builder
public class StartEventBuilder {
    @NonNull
    private String id;
    private String name;
    @Builder.Default
    private StartEventType startEventType = StartEventType.noneStartEvent;
    @Builder.Default
    private TimeStartEventType timeStartEventType = TimeStartEventType.timeDate;
    /**
     * 定时器启动事件事件表达式
     */
    private String timeExpression;

    public enum StartEventType {
        /**
         * 空启动事件
         */
        noneStartEvent,
        /**
         * 定时启动事件
         */
        timeStartEvent
    }

    public enum TimeStartEventType {
        /**
         * 周期执行
         */
        timeCycle,
        /**
         * 到时执行
         */
        timeDate
    }
}
