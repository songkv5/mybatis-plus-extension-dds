package com.ws.mybatis.plus.interceptors;

import com.ws.mybatis.plus.db.DataSourceRouteContext;
import com.ws.mybatis.plus.db.DsRouteContextHolder;
import com.ws.mybatis.plus.enums.DBType;
import com.ws.mybatis.plus.exception.DdsException;
import com.ws.mybatis.plus.exception.ExceptionCode;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author willis<songkai01>
 * @chapter
 * @section
 * @since 2020年01月02日 11:11
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
        @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class })
})
public class MybatisSqlExecuteInterceptor implements Interceptor {
    private boolean printSql = false;
    private static Logger logger = LoggerFactory.getLogger(MybatisSqlExecuteInterceptor.class);
    private DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType commandType = mappedStatement.getSqlCommandType();
        DataSourceRouteContext dsRouteCtx = DsRouteContextHolder.get();
        Boolean force = dsRouteCtx.getForce();
        // 当前所选数据源
        DBType crtDbType = dsRouteCtx.getCrtDbType();
        if (force) {
            // 如果显式要求强制使用某个数据源
            if (commandType == SqlCommandType.SELECT) {
                // 读权限，不需要切换
            } else {
                if (crtDbType == DBType.SLAVE) {
                    // 从库不支持写操作
                    throw new DdsException(ExceptionCode.CRT_DATA_SOURCE_NO_PERMIT, "当前数据源不支持操作:" + commandType.name());
                }
            }
        } else {
            if (commandType == SqlCommandType.SELECT) {
                // 查询
                if (crtDbType == DBType.MASTER) {
                    // 如果当前是主库，切换到从库。如果已经是从库，不需要切换，减小切换数据源开销
                    DsRouteContextHolder.slave();
                }
            } else {
                // 如果更新，则设置设置为主库
                if (crtDbType == DBType.SLAVE) {
                    // 如果当前是从库，则切换到主库
                    DsRouteContextHolder.master();
                }
            }
        }

        long startTime = System.currentTimeMillis();
        Object result = invocation.proceed();
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        String sqlId = mappedStatement.getId();
        if (printSql) {
            Object parameter = null;
            if (invocation.getArgs().length > 1) {
                parameter = invocation.getArgs()[1];
            }

            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            String sql = getSql(configuration, boundSql, sqlId, executeTime);
            logger.info(sql);
        } else {
            logger.info("{}耗时[{}ms]", sqlId, executeTime);
        }
        return result;
    }
    public String getSql(Configuration configuration, BoundSql boundSql, String sqlId, long time) {
        String sql = showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
        str.append(sqlId);
        str.append(":");
        str.append(sql);
        str.append(":");
        str.append(time);
        str.append("ms");
        return str.toString();
    }

    public String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }
    private String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    public void setPrintSql(boolean printSql) {
        this.printSql = printSql;
    }
}