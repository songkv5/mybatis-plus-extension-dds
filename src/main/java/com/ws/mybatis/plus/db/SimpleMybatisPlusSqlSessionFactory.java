package com.ws.mybatis.plus.db;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.ws.mybatis.plus.interceptors.MybatisSqlExecuteInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2020年01月04日 1:08
 */
public final class SimpleMybatisPlusSqlSessionFactory extends MybatisSqlSessionFactoryBean {
    private Logger logger = LoggerFactory.getLogger(SimpleMybatisPlusSqlSessionFactory.class);

    private SimpleMybatisPlusSqlSessionFactory() {
        super();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder{
        /**
         * 数据源
         */
        private DataSource dataSource;
        /**
         * 是否打印sql
         */
        private boolean printSql = false;
        /**
         * 插件
         */
        private List<Interceptor> interceptors;
        /**
         * mapper.xml文件路径
         */
        private Resource[] mapperLocations;

        private Builder() {
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptor == null) {
                return this;
            }
            if (this.interceptors == null) {
                this.interceptors = new ArrayList<>();
            }
            this.interceptors.add(interceptor);
            return this;
        }
        public Builder printSql(boolean printSql) {
            this.printSql = printSql;
            return this;
        }
        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public Builder mapperLocations(Resource[] resources) {
            if (resources == null) {
                return this;
            }

            this.mapperLocations = resources;
            return this;
        }
        public SimpleMybatisPlusSqlSessionFactory build() {
            SimpleMybatisPlusSqlSessionFactory result = new SimpleMybatisPlusSqlSessionFactory();
            /** 主从分离*/
            MybatisSqlExecuteInterceptor mybatisSqlExecuteInterceptor = new MybatisSqlExecuteInterceptor();
            mybatisSqlExecuteInterceptor.setPrintSql(printSql);
            /*//重新封装分页
            PaginationInterceptor page = new PaginationInterceptor();
            page.setDialectType("mysql");*/

            if (this.interceptors != null && this.interceptors.size() > 0) {
                interceptors.add(mybatisSqlExecuteInterceptor);
            }
            result.setDataSource(this.dataSource);
            result.setPlugins(interceptors.toArray(new Interceptor[interceptors.size()]));
            if (this.mapperLocations != null && this.mapperLocations.length > 0) {
                result.setMapperLocations(this.mapperLocations);
            }
            return result;
        }
    }
}