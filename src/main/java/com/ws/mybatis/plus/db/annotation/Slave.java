package com.ws.mybatis.plus.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2019年12月31日 18:02
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Slave {

}