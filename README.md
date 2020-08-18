### 简介
使用java快速构建基于 flowable bpmn2.0 规范的流程定义xml，以及通过约定格式实现json快速转化为符合flowable bpmn2.0规范的xml文件

示例：
```java
BpmnXmlBuilder bpmnXmlBuilder = new BpmnXmlBuilder();
bpmnXmlBuilder.addDefinitions()
        .addProcess(ProcessBuilder.builder().id("holidayRequest").build())
        .addStartEvent(StartEventBuilder.builder().id("startEvent").build())
        .addNoneSequenceFlow(NoneSequenceFlowBuilder.builder().sourceRef("startEvent").targetRef("approveTask").build())
        .addUserTask(UserTaskBuilder.builder().id("approveTask").name("Approve or reject request").assignee("managers").build())
        .addNoneSequenceFlow(NoneSequenceFlowBuilder.builder().sourceRef("approveTask").targetRef("decision").build())
        .addExclusiveGateway(ExclusiveGateway.builder().id("decision").build())
        .addConditionalSequenceFlow(ConditionalSequenceFlowBuilder.builder().sourceRef("decision").targetRef("externalSystemCall").conditionExpression("<![CDATA[${approved}]]>").build())
        .addJavaServiceTask(JavaServiceTaskBuilder.builder().id("externalSystemCall").name("Enter holidays in external system").fullyQualifiedClassname("com.cheng.commoncompenents.flowable.CallExternalSystemDelegate").build())
        .addNoneSequenceFlow(NoneSequenceFlowBuilder.builder().sourceRef("externalSystemCall").targetRef("holidayApprovedTask").build())
        .addUserTask(UserTaskBuilder.builder().id("holidayApprovedTask").name("Holiday approved").assignee("${employee}").build())
        .addNoneSequenceFlow(NoneSequenceFlowBuilder.builder().sourceRef("holidayApprovedTask").targetRef("approveEnd").build())
        .addEndEvent(EndEventBuilder.builder().id("approveEnd").build())
        .addConditionalSequenceFlow(ConditionalSequenceFlowBuilder.builder().sourceRef("decision").targetRef("sendRejectionMail").conditionExpression("<![CDATA[${!approved}]]>").build())
        .addJavaServiceTask(JavaServiceTaskBuilder.builder().id("sendRejectionMail").name("Send out rejection email").fullyQualifiedClassname("com.cheng.commoncompenents.flowable.SendEmailDelegate").build())
        .addNoneSequenceFlow(NoneSequenceFlowBuilder.builder().sourceRef("sendRejectionMail").targetRef("rejectEnd").build())
        .addEndEvent(EndEventBuilder.builder().id("rejectEnd").build());
// 生成xml文件
BpmnXmlUtils.generateBpmnXmlFile(path, "test.xml", bpmnXmlBuilder.getDocument());
```

生成的xml：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions targetNamespace="http://flowable.org/bpmn20" xmlns:flowable="http://flowable.org/bpmn">
    <process id="holidayRequest">
        <startEvent id="startEvent"/>
        <sequenceFlow sourceRef="startEvent" targetRef="approveTask"/>
        <userTask id="approveTask" name="Approve or reject request" flowable:assignee="managers"/>
        <sequenceFlow sourceRef="approveTask" targetRef="decision"/>
        <exclusiveGatewayBuilder id="decision"/>
        <sequenceFlow sourceRef="decision" targetRef="externalSystemCall">
            <conditionExpression>&lt;![CDATA[${approved}]]&gt;</conditionExpression>
        </sequenceFlow>
        <serviceTask id="externalSystemCall" name="Enter holidays in external system"
                     flowable:class="com.cheng.commoncompenents.flowable.CallExternalSystemDelegate"/>
        <sequenceFlow sourceRef="externalSystemCall" targetRef="holidayApprovedTask"/>
        <userTask id="holidayApprovedTask" name="Holiday approved" flowable:assignee="${employee}"/>
        <sequenceFlow sourceRef="holidayApprovedTask" targetRef="approveEnd"/>
        <endEvent id="approveEnd"/>
        <sequenceFlow sourceRef="decision" targetRef="sendRejectionMail">
            <conditionExpression>&lt;![CDATA[${!approved}]]&gt;</conditionExpression>
        </sequenceFlow>
        <serviceTask id="sendRejectionMail" name="Send out rejection email"
                     flowable:class="com.cheng.commoncompenents.flowable.SendEmailDelegate"/>
        <sequenceFlow sourceRef="sendRejectionMail" targetRef="rejectEnd"/>
        <endEvent id="rejectEnd"/>
    </process>
</definitions>
```

### 使用说明
#### 目前支持的bpmn2.0结构
* 启动事件
```java
StartEventBuilder.builder().id("startEvent").build();
```
* 结束事件
```java
EndEventBuilder.builder().id("approveEnd").build();
```
* 顺序流
```java
NoneSequenceFlowBuilder.builder().sourceRef("startEvent").targetRef("approveTask").build();
```
* 条件顺序流
```java
ConditionalSequenceFlowBuilder.builder().sourceRef("decision").targetRef("externalSystemCall").conditionExpression("<![CDATA[${approved}]]>").build()
```
* 排他网关
```java
ExclusiveGateway.builder().id("decision").build();
```
* 用户任务
```java
UserTaskBuilder.builder().id("approveTask").name("Approve or reject request").assignee("managers").build();
```

* Java服务任务
```java
JavaServiceTaskBuilder.builder().id("sendRejectionMail").name("Send out rejection email").fullyQualifiedClassname("com.cheng.commoncompenents.flowable.SendEmailDelegate").build()
```

#### json属性说明
* process(process为流程定义的根节点，必传)
|属性|含义|类型|是否必传|
| ------------ | ------------ |
|id||String|是|
|name||String||

* startEvent(开始事件)
|属性|含义|类型|是否必传|
| ------------ | ------------ |
|id||String|是|
|name||String||
|startEventType||枚举||

* endEvent(结束事件)
|属性|含义|类型|是否必传|
| ------------ | ------------ |
|id||String|是|
|name||String||

* exclusiveGateway(排他网关)
|属性|含义|类型|是否必传|
| ------------ | ------------ |
|id||String|是|
|name||String||

* conditionalSequenceFlow(条件顺序流)
|属性|含义|类型|是否必传|
| ------------ | ------------ |
|id||String|是|
|sourceRef||String|是|
|targetRef||String|是|
|conditionExpression||String|是|

* noneSequenceFlow(空顺序流)
|属性|含义|类型|是否必传|
| ------------ | ------------ |
|id||String|是|
|sourceRef||String|是|
|targetRef||String|是|

* javaServiceTask(java服务任务)
|属性|含义|类型|是否必传|
| ------------ | ------------ |
|id||String|是|
|name||String||
|fullyQualifiedClassname||String||
|delegateExpression|一个实现了JavaDelegate接口的bean，定义在Spring容器中|String||
|expression|指定服务的表达式|String||
|fieldMapList|为逻辑实现类设置固定属性值|List<Map<String, String>>||
|expressionFieldMapList|为逻辑实现类设置动态属性值|List<Map<String, String>>||
|resultVariable|将任务结果值设置为流程变量|String||

* userTask(用户任务节点)
|属性|含义|类型|是否必传|
| ------------ | ------------ |
|id||String|是|
|name||String||
|document||String||
|assignee|办理人|String||
|candidateUsers|候选用户|String||
|candidateGroups|候选组|String||

示例：
```java
// 通过json字符串生成Document对象和xml文件
Document document = bpmnXmlUtils.convertJson2Document("{\"process\":{\"id\":\"processId\",\"name\":\"processName\"},\"startEvent\":{\"id\":\"startEventId\",\"name\":\"startEventName\",\"startEventType\":\"noneStartEvent\"}}\n");
bpmnXmlUtils.generateBpmnXmlFile(path, "test5.xml", document);
```