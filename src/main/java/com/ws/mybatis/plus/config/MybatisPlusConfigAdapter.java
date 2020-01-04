package com.ws.mybatis.plus.config;

import com.ws.mybatis.plus.aop.DataSourceAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author willis<songkai01>
 * @chapter mybatis 适配器
 * @section
 * @since 2020年01月02日 20:33
 */
public class MybatisPlusConfigAdapter implements ImportBeanDefinitionRegistrar {
    private Logger logger = LoggerFactory.getLogger(MybatisPlusConfigAdapter.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceAspect.class);
        AbstractBeanDefinition rawBeanDefinition = builder.getRawBeanDefinition();
        registry.registerBeanDefinition("ddsAspect", rawBeanDefinition);
    }

}
