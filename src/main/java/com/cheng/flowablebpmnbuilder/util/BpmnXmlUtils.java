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

/**
 * @ClassName BpmnXmlUtils
 * @Description 工具类
 * @Author CTPlayer
 * @DATE 2020/8/12 3:42 下午
 * @Version 1.0
 **/
@Component
public class BpmnXmlUtils {
    private final String path = "";


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
            if (jsonMap.containsKey(BuilderType.process.name())) {
                String jsonStr = objectMapper.writeValueAsString(jsonMap.get(BuilderType.process.name()));
                bpmnXmlBuilder.addProcess(objectMapper.readValue(jsonStr, ProcessBuilder.class));
            } else {
                throw new ProcessNotDefinedException();
            }
            jsonMap.forEach((k, v) -> {
                try {
                    String jsonStr = objectMapper.writeValueAsString(v);
                    if (BuilderType.startEvent.name().equals(k)) {
                        bpmnXmlBuilder.addStartEvent(objectMapper.readValue(jsonStr, StartEventBuilder.class));
                    } else if (BuilderType.endEvent.name().equals(k)) {
                        bpmnXmlBuilder.addEndEvent(objectMapper.readValue(jsonStr, EndEventBuilder.class));
                    } else if (BuilderType.exclusiveGateway.name().equals(k)) {
                        bpmnXmlBuilder.addExclusiveGateway(objectMapper.readValue(jsonStr, ExclusiveGatewayBuilder.class));
                    } else if (BuilderType.conditionalSequenceFlow.name().equals(k)) {
                        bpmnXmlBuilder.addConditionalSequenceFlow(objectMapper.readValue(jsonStr, ConditionalSequenceFlowBuilder.class));
                    } else if (BuilderType.noneSequenceFlow.name().equals(k)) {
                        bpmnXmlBuilder.addNoneSequenceFlow(objectMapper.readValue(jsonStr, NoneSequenceFlowBuilder.class));
                    } else if (BuilderType.javaServiceTask.name().equals(k)) {
                        bpmnXmlBuilder.addJavaServiceTask(objectMapper.readValue(jsonStr, JavaServiceTaskBuilder.class));
                    } else if (BuilderType.userTask.name().equals(k)) {
                        bpmnXmlBuilder.addUserTask(objectMapper.readValue(jsonStr, UserTaskBuilder.class));
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
