package com.cheng.flowablebpmnbuilder;

import com.cheng.flowablebpmnbuilder.util.BpmnXmlUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName BpmnBuilderConfiguration
 * @Description 自定义starter配置
 * @Author CTPlayer
 * @DATE 2020/8/19 2:39 下午
 * @Version 1.0
 **/
@Configuration
@ConditionalOnClass({BpmnXmlUtils.class})
public class BpmnBuilderConfiguration {

    @Bean
    @ConditionalOnMissingBean(BpmnXmlUtils.class)
    public BpmnXmlUtils bpmnXmlUtils() {
        return new BpmnXmlUtils();
    }
}
