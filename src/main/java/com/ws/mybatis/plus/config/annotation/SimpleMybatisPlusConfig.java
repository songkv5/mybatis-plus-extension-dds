package com.ws.mybatis.plus.config.annotation;

import com.ws.mybatis.plus.config.SimpleMybatisPlusConfigAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2020年01月03日 23:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(SimpleMybatisPlusConfigAdapter.class)
@Configuration
public @interface SimpleMybatisPlusConfig {
    /**
     * mapper class的包扫描路径
     * @return
     */
    String[] basePackages() default {};
}