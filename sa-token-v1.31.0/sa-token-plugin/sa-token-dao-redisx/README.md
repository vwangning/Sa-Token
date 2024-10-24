
### 使用示例

#### 1.配置

```yaml
sa-token-dao: #名字可以随意取
  redis:
    server: "localhost:6379"
    password: 123456
    db: 1
```

#### 2.代码

**注入风格**

```java
@Configuration
public class Config {
    @Bean
    public SaTokenDao saTokenDaoInit(@Inject("${sa-token-dao.redis}") SaTokenDaoOfRedis saTokenDao) {
        return saTokenDao;
    }
}
```

**手动风格**

```java
SaTokenDaoOfRedis saTokenDao = new SaTokenDaoOfRedis(props);
SaManager.setSaTokenDao(saTokenDao);
```