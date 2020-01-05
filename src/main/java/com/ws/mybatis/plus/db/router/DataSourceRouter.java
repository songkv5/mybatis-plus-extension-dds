package com.ws.mybatis.plus.db.router;

import com.ws.mybatis.plus.db.DsRouteContextHolder;
import com.ws.mybatis.plus.enums.DBType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author willis<songkai01>
 * @chapter 数据源切换切面
 * @section
 * @since 2019年11月07日 14:27
 */
@Aspect
public class DataSourceRouter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceRouter.class);

    /**
     * 强制全部走从库
     */
    @Pointcut("@annotation(com.ws.mybatis.plus.db.annotation.Slave)")
    public void readPointCut(){}

    /**
     * 强制全部走主库
     */
    @Pointcut("@annotation(com.ws.mybatis.plus.db.annotation.Master)")
    public void writePointcut() {}

    /**
     * 默认切面，读走从库，写走主库
     */
    @Pointcut("!@annotation(com.ws.mybatis.plus.db.annotation.Master) && !@annotation(com.ws.mybatis.plus.db.annotation.Slave)")
    public void defaultPointcut() {}

    /**
     * 强制使用从库切面
     */
    @Before("readPointCut()")
    public void read(JoinPoint pjp){
        DataSourceRouteContext dsRouteCtx = DsRouteContextHolder.get();
        DBType crtDbType = dsRouteCtx.getCrtDbType();
        Boolean force = dsRouteCtx.getForce();
        // 如果当前使用的就是从库，不必切换
        String method = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName() + "()";

        if (DBType.MASTER == crtDbType) {
            if (force) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("数据源强制使用主库，无法切换到从库，方法={}", method);
                }
            } else {// 切换到从库
//                DataSourceRouteContext.routeKey(db)
                DsRouteContextHolder.slave(true);
            }
        } else {
            // 当前线程已经使用了从库，不必重复切换
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("当前线程已经使用了从库，不必重复切换");
            }
        }
    }

    /**
     * 读完后
     */
    @After("readPointCut()")
    public void afterRead() {
        DsRouteContextHolder.clearDsRouteCxt();
    }

    /**
     * 切换到主库模式
     * @param pjp
     */
    @Before("writePointcut()")
    public void write(JoinPoint pjp){
        DataSourceRouteContext dsRouteCtx = DsRouteContextHolder.get();
        String method = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName() + "()";
        DBType type = dsRouteCtx.getCrtDbType();
        Boolean force = dsRouteCtx.getForce();
        if (DBType.MASTER == type) {
            // 当前使用的已经是主库，不必切换
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("当前已经是主库，不必切换,method:{}", method);
            }
            return;
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("数据源切换到主库，触发方法:{}", method);
            }
            // 切换数据源
            DsRouteContextHolder.master(true);
        }
    }
    @After("writePointcut()")
    public void afterWrite() {
        DsRouteContextHolder.clearDsRouteCxt();
    }
}