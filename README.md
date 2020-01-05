# mybatis-plus 动态数据源读写分离组件库

## 使用方法
1. 下载源码，自行编译，打包，在工程项目引入jar。如
```xml
<dependency>
    <groupId>com.ws.mybatis.plus</groupId>
    <artifactId>mybatis-plus-extension-dds</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
2. 配置。使用@SimpleMybatisPlusConfig 注解，注册SimpleMybatisPlusSqlSessionFactory的Bean，如下
```java
@SimpleMybatisPlusConfig(basePackages = "mapper class的扫描路径")
public class Config {
@Bean
public SimpleMybatisPlusSqlSessionFactory mybatisSqlSessionFactoryBean() {
	DynamicDataSource.Builder ddsBuilder = DynamicDataSource.newBuilder();
	// 动态数据源配置
    DynamicDataSource dds = ddsBuilder
                            .master(master)   // 主库
                            .addSlave(slave1) // 从库1
                            .addSlave(slave2) // 从库2
                            .addSlave(slave3) // 从库3
                            .build();

    SimpleMybatisPlusSqlSessionFactory.Builder builder = SimpleMybatisPlusSqlSessionFactory.newBuilder();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    SimpleMybatisPlusSqlSessionFactory sqlSessionFactory = 
    builder.dataSource(dds) //配置数据源
            .printSql(true) // 是否打印SQL，对性能可能有影响，生产环境建议设置为false
            .addPlugin(组件1) // 加一个插件，如分页插件
            .addPlugin(组件2) // 加一个插件
            // mapper.xml文件位置
            .mapperLocations(resolver.getResources("classpath:mapper/*.xml"))
            .build();
    return sqlSessionFactory;
}
```
**配置说明**：使用SimpleMybatisPlusConfig注解的basePackages属性和直接只用@MapperScan的basePackage属性效果一样

3. 使用，像平时编写业务代码一样
```java
@Service
public class TestService {
	//自己生成的数据库mapper类
    @Autowired
    private TestMapper mapper;
    // @Slave注解会强制使用从库，如果当前有写操作，会提示错误
    @Slave
    public Object test1() {
    	// 数据库查询
        Test test = mapper.selectById(1L);
        Test r = new Test();
        r.setId(1L);
        // 数据库插入
        mapper.insert(r);
        return test;
    }
    public void test2() {
        test1();
    }
}
```

## 说明

1. 使用@Slave 或 @Master注解进行显式数据源选择
2. 不加任何注解，系统会根据当前操作，自动选择为读操作走从库，写操作走主库
3. 涉及多个从库，系统会自动分配到其中一个从库中去