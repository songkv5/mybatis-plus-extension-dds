package com.ws.mybatis.plus.db;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.ws.mybatis.plus.plugin.MybatisSqlExecutePlugin;
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
        private List<Interceptor> plugins;
        /**
         * mapper.xml文件路径
         */
        private Resource[] mapperLocations;

        private Builder() {
        }

        public Builder addPlugin(Interceptor interceptor) {
            if (interceptor == null) {
                return this;
            }
            if (this.plugins == null) {
                this.plugins = new ArrayList<>();
            }
            this.plugins.add(interceptor);
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
            MybatisSqlExecutePlugin mybatisSqlExecuteInterceptor = new MybatisSqlExecutePlugin();
            mybatisSqlExecuteInterceptor.setPrintSql(printSql);
            /*//重新封装分页
            PaginationInterceptor page = new PaginationInterceptor();
            page.setDialectType("mysql");*/

            if (this.plugins != null && this.plugins.size() > 0) {
                plugins.add(mybatisSqlExecuteInterceptor);
            }
            result.setDataSource(this.dataSource);
            result.setPlugins(plugins.toArray(new Interceptor[plugins.size()]));
            if (this.mapperLocations != null && this.mapperLocations.length > 0) {
                result.setMapperLocations(this.mapperLocations);
            }
            return result;
        }
    }
}