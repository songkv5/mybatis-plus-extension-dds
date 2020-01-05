package com.ws.mybatis.plus.config;

import com.ws.mybatis.plus.exception.DdsException;
import com.ws.mybatis.plus.exception.ExceptionCode;
import org.mybatis.spring.annotation.MapperScannerRegistrar;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author willis<songkai01>
 * @chapter mybatis 适配器
 * @section
 * @since 2020年01月02日 20:33
 */
public class SimpleMybatisPlusConfigAdapter implements ImportBeanDefinitionRegistrar {
    private Logger logger = LoggerFactory.getLogger(SimpleMybatisPlusConfigAdapter.class);
    private String DS_ROUTER_CLASS = "com.ws.mybatis.plus.db.router.DataSourceRouter";
    private String ANNOTATION_CLASS_NAME = "com.ws.mybatis.plus.config.annotation.SimpleMybatisPlusConfig";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        try {
            Class<?> routerClass = Class.forName(DS_ROUTER_CLASS);
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(routerClass);
            AbstractBeanDefinition rawBeanDefinition = builder.getRawBeanDefinition();
            registry.registerBeanDefinition("ddsAspect", rawBeanDefinition);

            // 获取注解属性
            AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                    .fromMap(importingClassMetadata.getAnnotationAttributes(ANNOTATION_CLASS_NAME));
            if (mapperScanAttrs != null) {
                registerMapperScannerConfigurer(mapperScanAttrs, registry, generateBaseBeanName(importingClassMetadata, 0));
            }
        } catch (Exception e) {
            logger.error("mybatis-plus 配置失败,", e);
            throw new DdsException(ExceptionCode.DDS_CONFIG_FAILED, e.getLocalizedMessage());
        }
    }
    private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
        return importingClassMetadata.getClassName() + "#" + MapperScannerRegistrar.class.getSimpleName() + "#" + index;
    }


    /**
     * Reference
     * {@link org.mybatis.spring.annotation.MapperScannerRegistrar#registerBeanDefinitions}
     * @param annoAttrs
     * @param registry
     * @param beanName
     */
    void registerMapperScannerConfigurer(AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry, String beanName) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        builder.addPropertyValue("processPropertyPlaceHolders", true);


        List<String> basePackages = new ArrayList<>();
        /*basePackages.addAll(
                Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));*/

        basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText)
                .collect(Collectors.toList()));
        builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

    }

}
