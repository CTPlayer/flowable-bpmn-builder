package com.cheng.flowablebpmnbuilder.builder;

import com.cheng.flowablebpmnbuilder.builder.event.EndEventBuilder;
import com.cheng.flowablebpmnbuilder.builder.event.StartEventBuilder;
import com.cheng.flowablebpmnbuilder.builder.gateway.ExclusiveGatewayBuilder;
import com.cheng.flowablebpmnbuilder.builder.process.ProcessBuilder;
import com.cheng.flowablebpmnbuilder.builder.sequenceflow.ConditionalSequenceFlowBuilder;
import com.cheng.flowablebpmnbuilder.builder.sequenceflow.NoneSequenceFlowBuilder;
import com.cheng.flowablebpmnbuilder.builder.task.JavaServiceTaskBuilder;
import com.cheng.flowablebpmnbuilder.builder.task.UserTaskBuilder;
import lombok.Getter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Optional;

/**
 * @ClassName BpmnXmlFactory
 * @Description Bpmn XML 模型构建类
 * @Author CTPlayer
 * @DATE 2020/8/12 3:18 下午
 * @Version 1.0
 **/
public class BpmnXmlBuilder {

    @Getter
    private Document document;

    private Element definitions;

    private Element process;

    public BpmnXmlBuilder() {
        this.document = DocumentHelper.createDocument();
    }

    /**
     * 增加definitions节点
     * @return
     */
    public BpmnXmlBuilder addDefinitions() {
        this.definitions = this.document.addElement("definitions");
        definitions.addAttribute("targetNamespace", "http://flowable.org/bpmn20")
                .addAttribute("xmlns:flowable", "http://flowable.org/bpmn")
                .addAttribute("xmlns", "http://www.omg.org/spec/BPMN/20100524/MODEL");
        return this;
    }


    /**
     * 增加process节点
     * @param processBuilder
     * @return
     */
    public BpmnXmlBuilder addProcess(ProcessBuilder processBuilder) {
        this.process = this.definitions.addElement("process")
                .addAttribute("id", processBuilder.getId());
        Optional.ofNullable(processBuilder.getName()).ifPresent(name -> this.process.addAttribute("name", name));
        return this;
    }

    /**
     * 增加startEvent节点
     * @param eventBuilder
     * @return
     */
    public BpmnXmlBuilder addStartEvent(StartEventBuilder startEventBuilder) {
        Element event = this.process.addElement("startEvent").addAttribute("id", startEventBuilder.getId());
        Optional.ofNullable(startEventBuilder.getName()).ifPresent(name -> {
            event.addAttribute("name", startEventBuilder.getName());
        });

        switch (startEventBuilder.getStartEventType()) {
            // 定时器启动事件
            case timeStartEvent: {
                Element timerEventDefinition = event.addElement("timerEventDefinition");
                Optional.ofNullable(startEventBuilder.getTimeStartEventType()).ifPresent(timeStartEventType -> {
                    Element timeStartEventTypeElement = timerEventDefinition.addElement(timeStartEventType.name());
                    Optional.ofNullable(startEventBuilder.getTimeExpression()).ifPresent(timeExpression -> timeStartEventTypeElement.setText(timeExpression));
                });
            }
        }
        return this;
    }

    /**
     * 增加endEvent节点
     * @param endEventBuilder
     * @return
     */
    public BpmnXmlBuilder addEndEvent(EndEventBuilder endEventBuilder) {
        this.process.addElement("endEvent").addAttribute("id", endEventBuilder.getId())
                .addAttribute("name", endEventBuilder.getName());
        return this;
    }

    /**
     * 增加空顺序流节点
     * @param noneSequenceFlowBuilder
     * @return
     */
    public BpmnXmlBuilder addNoneSequenceFlow(NoneSequenceFlowBuilder noneSequenceFlowBuilder) {
        Element sequenceFlow = this.process.addElement("sequenceFlow").addAttribute("id", noneSequenceFlowBuilder.getId())
                .addAttribute("sourceRef", noneSequenceFlowBuilder.getSourceRef())
                .addAttribute("targetRef", noneSequenceFlowBuilder.getTargetRef());
        return this;
    }

    /**
     * 增加条件顺序流节点
     * @param noneSequenceFlowBuilder
     * @return
     */
    public BpmnXmlBuilder addConditionalSequenceFlow(ConditionalSequenceFlowBuilder conditionalSequenceFlowBuilder) {
        Element sequenceFlow = this.process.addElement("sequenceFlow").addAttribute("id", conditionalSequenceFlowBuilder.getId())
                .addAttribute("sourceRef", conditionalSequenceFlowBuilder.getSourceRef())
                .addAttribute("targetRef", conditionalSequenceFlowBuilder.getTargetRef());
        Optional.ofNullable(conditionalSequenceFlowBuilder.getConditionExpression()).ifPresent(conditionExpression -> sequenceFlow.addElement("conditionExpression").setText(conditionExpression));
        return this;
    }

    /**
     * 增加网关节点
     * @param exclusiveGatewayBuilder
     * @return
     */
    public BpmnXmlBuilder addExclusiveGateway(ExclusiveGatewayBuilder exclusiveGatewayBuilder) {
        Element gateway = this.process.addElement("exclusiveGateway").addAttribute("id", exclusiveGatewayBuilder.getId());
        Optional.ofNullable(exclusiveGatewayBuilder.getName()).ifPresent(name -> gateway.addAttribute("name", name));
        // ToDo:思考需不需要限制每种GatewayBuilder所能持有的SequenceFlowBuilder类型？
//        Optional.ofNullable(gatewayBuilder.getSequenceFlowBuilderList()).ifPresent(sequenceFlowBuilderList -> {
//            sequenceFlowBuilderList.forEach(sequenceFlowBuilder -> addSequenceFlow(sequenceFlowBuilder));
//        });
        return this;
    }

    /**
     * 增加用户任务节点
     * @param userTaskBuilder
     * @return
     */
    public BpmnXmlBuilder addUserTask(UserTaskBuilder userTaskBuilder) {
        Element userTask = this.process.addElement("userTask").addAttribute("id", userTaskBuilder.getId());
        Optional.ofNullable(userTaskBuilder.getName()).ifPresent(name -> userTask.addAttribute("name", name));
        Optional.ofNullable(userTaskBuilder.getDocument()).ifPresent(document -> {
            userTask.addElement("documentation").setText(document);
        });
        Optional.ofNullable(userTaskBuilder.getAssignee()).ifPresent(assignee -> userTask.addAttribute("flowable:assignee", assignee));
        Optional.ofNullable(userTaskBuilder.getCandidateUsers()).ifPresent(candidateUsers -> userTask.addAttribute("flowable:candidateUsers", candidateUsers));
        Optional.ofNullable(userTaskBuilder.getCandidateGroups()).ifPresent(candidateGroups -> userTask.addAttribute("flowable:candidateGroups", candidateGroups));
        return this;
    }

    /**
     * 增加java服务任务节点
     * @param javaServiceTaskBuilder
     * @return
     */
    public BpmnXmlBuilder addJavaServiceTask(JavaServiceTaskBuilder javaServiceTaskBuilder) {
        Element javaServiceTask = this.process.addElement("serviceTask").addAttribute("id", javaServiceTaskBuilder.getId());
        Optional.ofNullable(javaServiceTaskBuilder.getName()).ifPresent(name -> javaServiceTask.addAttribute("name", name));
        // 同时声明多种调用Java逻辑时，按以下优先级使用其中一种
        if (javaServiceTaskBuilder.getFullyQualifiedClassname() != null) {
            javaServiceTask.addAttribute("flowable:class", javaServiceTaskBuilder.getFullyQualifiedClassname());
        } else if (javaServiceTaskBuilder.getFullyQualifiedClassname() != null) {
            javaServiceTask.addAttribute("flowable:delegateExpression", javaServiceTaskBuilder.getDelegateExpression());
        } else if (javaServiceTaskBuilder.getExpression() != null) {
            javaServiceTask.addAttribute("flowable:expression", javaServiceTaskBuilder.getExpression());
        }
        // 设置java服务任务逻辑实现类属性值
        if (javaServiceTaskBuilder.getFieldMapList() != null || javaServiceTaskBuilder.getExpressionFieldMapList() != null) {
            Element extensionElements = javaServiceTask.addElement("extensionElements");
            Optional.ofNullable(javaServiceTaskBuilder.getFieldMapList()).ifPresent(fieldMapList -> {
                fieldMapList.forEach(fieldMap -> {
                    fieldMap.forEach((k, v) -> extensionElements.addElement("flowable:field").addAttribute("name", k).addAttribute("stringValue", v));
                });
            });
            Optional.ofNullable(javaServiceTaskBuilder.getExpressionFieldMapList()).ifPresent(expressionFieldMapList -> {
                expressionFieldMapList.forEach(expressionFieldMap -> {
                    expressionFieldMap.forEach((k, v) -> extensionElements.addElement("flowable:field").addAttribute("name", k).addAttribute("expression", v));
                });
            });
        }
        // 将结果值设置为流程变量
        Optional.ofNullable(javaServiceTaskBuilder.getResultVariable()).ifPresent(resultVariable -> javaServiceTask.addAttribute("flowable:resultVariable", resultVariable));
        return this;
    }
}


























