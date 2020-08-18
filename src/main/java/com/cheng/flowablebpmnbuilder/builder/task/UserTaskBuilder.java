package com.cheng.flowablebpmnbuilder.builder.task;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * @ClassName UserTaskBuilder
 * @Description TODO
 * @Author CTPlayer
 * @DATE 2020/8/13 5:14 下午
 * @Version 1.0
 **/
@Getter
@Builder
public class UserTaskBuilder {
    @NonNull
    private String id;
    private String name;
    private String document;
    /**
     * 办理人
     */
    private String assignee;
    /**
     * 候选用户
     */
    private String candidateUsers;
    /**
     * 候选组
     */
    private String candidateGroups;
}
