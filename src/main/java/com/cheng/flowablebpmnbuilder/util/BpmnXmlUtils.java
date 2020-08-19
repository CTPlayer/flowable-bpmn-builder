package com.cheng.flowablebpmnbuilder.util;

import com.cheng.flowablebpmnbuilder.builder.BpmnXmlBuilder;
import com.cheng.flowablebpmnbuilder.builder.event.EndEventBuilder;
import com.cheng.flowablebpmnbuilder.builder.event.StartEventBuilder;
import com.cheng.flowablebpmnbuilder.builder.exception.ProcessNotDefinedException;
import com.cheng.flowablebpmnbuilder.builder.gateway.ExclusiveGatewayBuilder;
import com.cheng.flowablebpmnbuilder.builder.process.ProcessBuilder;
import com.cheng.flowablebpmnbuilder.builder.sequenceflow.ConditionalSequenceFlowBuilder;
import com.cheng.flowablebpmnbuilder.builder.sequenceflow.NoneSequenceFlowBuilder;
import com.cheng.flowablebpmnbuilder.builder.task.JavaServiceTaskBuilder;
import com.cheng.flowablebpmnbuilder.builder.task.UserTaskBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @ClassName BpmnXmlUtils
 * @Description 工具类
 * @Author CTPlayer
 * @DATE 2020/8/12 3:42 下午
 * @Version 1.0
 **/
@Component
public class BpmnXmlUtils {
    // ToDo:把path暴露为可配置的属性
///    private final String path = "";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<String, BiConsumer<String, BpmnXmlBuilder>> builderMap = new HashMap<String, BiConsumer<String, BpmnXmlBuilder>>(){{
        put(BuilderType.startEvent.name(), (jsonStr, bpmnXmlBuilder) -> {
            try {
                bpmnXmlBuilder.addStartEvent(objectMapper.readValue(jsonStr, StartEventBuilder.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        put(BuilderType.endEvent.name(), (jsonStr, bpmnXmlBuilder) -> {
            try {
                bpmnXmlBuilder.addEndEvent(objectMapper.readValue(jsonStr, EndEventBuilder.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        put(BuilderType.exclusiveGateway.name(), (jsonStr, bpmnXmlBuilder) -> {
            try {
                bpmnXmlBuilder.addExclusiveGateway(objectMapper.readValue(jsonStr, ExclusiveGatewayBuilder.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        put(BuilderType.conditionalSequenceFlow.name(), (jsonStr, bpmnXmlBuilder) -> {
            try {
                bpmnXmlBuilder.addConditionalSequenceFlow(objectMapper.readValue(jsonStr, ConditionalSequenceFlowBuilder.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        put(BuilderType.noneSequenceFlow.name(), (jsonStr, bpmnXmlBuilder) -> {
            try {
                bpmnXmlBuilder.addNoneSequenceFlow(objectMapper.readValue(jsonStr, NoneSequenceFlowBuilder.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        put(BuilderType.javaServiceTask.name(), (jsonStr, bpmnXmlBuilder) -> {
            try {
                bpmnXmlBuilder.addJavaServiceTask(objectMapper.readValue(jsonStr, JavaServiceTaskBuilder.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        put(BuilderType.userTask.name(), (jsonStr, bpmnXmlBuilder) -> {
            try {
                bpmnXmlBuilder.addUserTask(objectMapper.readValue(jsonStr, UserTaskBuilder.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }};

    /**
     * 生成BpmnXml文件
     * @param path
     * @param document
     * @param fileName
     */
    public void generateBpmnXmlFile(String path, String fileName, Document document) {
        XMLWriter output = null;
        try {
            output = new XMLWriter(new FileWriter(new File(path + File.separator + fileName)));
            output.write(document);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据Document生成BpmnModel
     * @param document
     * @return
     * @throws IOException
     * @throws XMLStreamException
     */
    public BpmnModel generateBpmnModel(Document document) {
        InputStream inputStream = new ByteArrayInputStream(document.asXML().getBytes(StandardCharsets.UTF_8));

        BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(inputStream);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        assert reader != null;
        return bpmnXmlConverter.convertToBpmnModel(reader);
    }

    /**
     * 校验生成的document是否符合bpmn标准
     * @param document
     * @return 错误信息
     */
    public List<ValidationError> validateDocument(Document document) {
        // 创建模型校验器工厂
        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        // 创建默认模型校验器
        ProcessValidator processValidator = processValidatorFactory.createDefaultProcessValidator();
        return processValidator.validate(generateBpmnModel(document));
    }


    /**
     * 通过约定json构建Document
     * @param json
     * @return
     */
    @SuppressWarnings("unchecked")
    public Document convertJson2Document(String json) {
        BpmnXmlBuilder bpmnXmlBuilder = new BpmnXmlBuilder();
        bpmnXmlBuilder.addDefinitions();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap;
        try {
            jsonMap = objectMapper.readValue(json, Map.class);
            // 解析json构造Document
            // 需要先解析构造process节点，其他节点都是process的子节点
            if (jsonMap.containsKey(BuilderType.process.name())) {
                String jsonStr = objectMapper.writeValueAsString(jsonMap.get(BuilderType.process.name()));
                bpmnXmlBuilder.addProcess(objectMapper.readValue(jsonStr, ProcessBuilder.class));
            } else {
                throw new ProcessNotDefinedException();
            }
            jsonMap.forEach((k, v) -> {
                try {
                    if (builderMap.containsKey(k)) {
                        String jsonStr = objectMapper.writeValueAsString(v);
                        builderMap.get(k).accept(jsonStr, bpmnXmlBuilder);
                    }
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return bpmnXmlBuilder.getDocument();
    }

    private enum BuilderType {
        /**
         * process节点
         */
        process,
        /**
         * 开始事件节点
         */
        startEvent,
        /**
         * 结束事件节点
         */
        endEvent,
        /**
         * 排他网关节点
         */
        exclusiveGateway,
        /**
         * 条件顺序流节点
         */
        conditionalSequenceFlow,
        /**
         * 空顺序流节点
         */
        noneSequenceFlow,
        /**
         * java服务任务节点
         */
        javaServiceTask,
        /**
         * 用户任务节点
         */
        userTask
    }
}
