# Spring Cloud 微服务系列四： OAuth 2.0



## 1.  OAuth2 协议

>  官网：https://oauth.net/2/ 



## 2.  OAuth2 简介

> 1.  OAuth 2.0是用于授权的行业标准协议。OAuth 2.0为简化客户端开发提供了特定的授权流，包括Web应用、桌面应用、移动端应用等。 
> 2.  OAuth 2.0协议为用户资源的授权提供了一个安全的、开放而又简易的标准。与以往的授权方式不同之处是 OAuth 2.0的授权不会使第三方触及到用户的帐号信息（如用户名与密码），即第三方无需使用用户的用户名与密码就可以申请获得该用户资源的授权，因此 oAuth 是安全的。 



## 3.  OAuth2 相关名词解释

> 1. Resource owner（资源拥有者）：拥有该资源的最终用户，他有访问资源的账号密码；
> 2. Resource server（资源服务器）：拥有受保护资源的服务器，如果请求包含正确的访问令牌，可以访问资源；
> 3. Client（客户端）：访问资源的客户端，会使用访问令牌去获取资源服务器的资源，可以是浏览器、移动设备或者服务器；
> 4. Authorization server（授权服务器）：用于授权用户的服务器，如果客户端授权通过，发放访问资源服务器的令牌。



## 4.  OAuth2 四种授权模式

> 1. Authorization Code（授权码模式）：正宗的OAuth2的授权模式，客户端先将用户导向授权服务器，登录后获取授权码，然后进行授权，最后根据授权码获取访问令牌；
> 2. Implicit（简化模式）：和授权码模式相比，取消了获取授权码的过程，直接获取访问令牌；
> 3. Resource Owner Password Credentials（密码模式）：客户端直接向用户获取用户名和密码，之后向授权服务器获取访问令牌；
> 4. Client Credentials（客户端模式）：客户端直接通过客户端授权（比如client_id和client_secret）从授权服务器获取访问令牌。



## 5.  OAuth2协议流程

```txt
     + -------- + + --------------- +
     | |-（A）-授权请求-> | 资源|
     | | | 客户|
     | | <-（B）-授权授予--- | |
     | | + --------------- +
     | |
     | | + --------------- +
     | |-（C）-授权通过-> | 授权|
     | 客户| | 服务器|
     | | <-（D）-----访问令牌------- | |
     | | + --------------- +
     | |
     | | + --------------- +
     | |-（E）-----访问令牌------> | 资源|
     | | | 服务器|
     | | <-（F）---受保护的资源--- | |
     + -------- + + --------------- +

                     图1：抽象协议流程
                     
图1所示的抽象OAuth 2.0流程描述了
   四个角色之间的交互，包括以下步骤：
   （A）客户端请求资源所有者的授权。的授权请求可以直接向资源所有者提出（如图所示），或者最好间接通过授权
        服务器作为中介。
   （B）客户收到授权授权，即代表资源所有者授权的凭证，使用此定义的四种赠款类型之一表示
        规范或使用扩展授权类型。的授权授予类型取决于客户端请求授权以及授权服务器。
   （C）客户端通过向客户端进行身份验证来请求访问令牌授权服务器并显示授权授权。
   （D）授权服务器对客户端进行身份验证并验证授权授予，如果有效，则颁发访问令牌。
   （E）客户端从资源请求受保护的资源服务器并通过提供访问令牌进行身份验证。
   （F）资源服务器验证访问令牌，如果有效，服务请求。
```



## 6.  OAuth2  API

```java
/oauth/authorize：验证
/oauth/token：获取token
/oauth/confirm_access：用户授权
/oauth/error：认证失败
/oauth/check_token：资源服务器用来校验token
/oauth/token_key：如果jwt模式则可以用此来从认证服务器获取公钥
```



## 7.  创建并配置认证服务器  【基于内存】

###  *1、引入需要的 maven 包* 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.zhangwei</groupId>
    <artifactId>authorization-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>authorization-server</name>
    <description>Demo project for Spring Boot</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.5.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.SR1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-oauth2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```



###  *2、配置好 application.yml* 

```yml
spring:
  application:
    name: authorization-server
  main:
    allow-bean-definition-overriding: true

server:
  port: 8766

management:
  endpoints:
    web:
      exposure:
        include: "*"
```



###  3、java文件配置

```java
/**
 * @author zhangwei
 **/
@EnableAuthorizationServer
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final static String SECRET = "secret";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("user")
                .resourceIds("zhangwei")
                .secret(new BCryptPasswordEncoder().encode("123456"))
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .scopes("all")
                .autoApprove(false)
                .redirectUris("http://localhost:8086/login")
                .and()

                .withClient("admin")
                .secret(new BCryptPasswordEncoder().encode("123456"))
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .scopes("all")
                .autoApprove(false)
                .redirectUris("http://www.baidu.com");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
                endpoints
                .tokenStore(tokenStore())
                .userDetailsService(userDetailsService)
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager)
                .allowedTokenEndpointRequestMethods(HttpMethod.POST, HttpMethod.GET);
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .passwordEncoder(passwordEncoder)
                .tokenKeyAccess("isAuthenticated()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SECRET);
        return converter;
    }
}
```

```java
/**
 * @author zhangwei
 **/
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .requestMatchers().antMatchers("/oauth/**", "/login/**", "/logout/**")
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").authenticated()
                .and()
                .formLogin().permitAll();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

```

```java
@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new User(s, passwordEncoder.encode("123456"),
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }
}
```



### 4、测试获取code,  获取token, 刷新token, 检查token, 获取token_key

```java
1. 浏览器访问验证, GET请求  
http://localhost:8766/oauth/authorize?response_type=code&client_id=admin&redirect_uri=http://www.baidu.com&scope=all
```

```java
2. 输入任意用户名，密码123456
```

```java
3. 选择认证
```

```java
4. 浏览器自动重定向获取授权码code
https://www.baidu.com/?code=ZfzU7h
```

```java
5. 通过 postman访问获取token, POST请求
http://localhost:8766/oauth/token
请求体表单方式添加:
grant_type:authorization_code
code:jd4CzL
redirect_uri:http://www.baidu.com
scope:all
client_id:admin
client_secret:123456
    
返回如下:
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTI1OTE3MTUsInVzZXJfbmFtZSI6ImFhYSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiJjOWFmMzljYy0yNGQ0LTQxOWEtOGEzYy0zZTljZTRiYzQ5YzEiLCJjbGllbnRfaWQiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdfQ.dKR-P-cnag-gdMjXNFyWlqTShfKQ8rRdIVWPOnKem3k",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhYWEiLCJzY29wZSI6WyJhbGwiXSwiYXRpIjoiYzlhZjM5Y2MtMjRkNC00MTlhLThhM2MtM2U5Y2U0YmM0OWMxIiwiZXhwIjoxNTk1MTQwNTE1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMDhiMzM3NmUtMzUyYy00MTgxLWE0N2ItY2Y5MTJkYTk5NGM3IiwiY2xpZW50X2lkIjoiYWRtaW4ifQ.zJi60zQ5kktqzU3DB-TzgkDBdepYZu107LCYhusY_s4",
    "expires_in": 43199,
    "scope": "all",
    "jti": "c9af39cc-24d4-419a-8a3c-3e9ce4bc49c1"
}
```



```java
6. 检查token, GET请求
http://localhost:8766/oauth/check_token?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsienciXSwidXNlcl9uYW1lIjoiYWFhIiwic2NvcGUiOlsiYWxsIl0sImV4cCI6MTU5MjU0OTA0NywiYXV0aG9yaXRpZXMiOlsiVVNFUiJdLCJqdGkiOiIzZThhZWZhZi1iZjc5LTQyZDktYTllYS01MDExYzJkN2MwYWEiLCJjbGllbnRfaWQiOiJhZG1pbiJ9.IKJgOPXCRPgT-RWgrYQNWugyTvklkCzAIEsycx3DpSQ
请求头添加: 
Basic Auth: admin 123456

返回如下:
{
    "aud": [
        "zw"
    ],
    "user_name": "aaa",
    "scope": [
        "all"
    ],
    "active": true,
    "exp": 1592549047,
    "authorities": [
        "USER"
    ],
    "jti": "3e8aefaf-bf79-42d9-a9ea-5011c2d7c0aa",
    "client_id": "admin"
}
```



```java
7. 刷新token, POST请求
http://localhost:8766/oauth/token
请求头添加: 
Basic Auth: admin 123456
请求体表单方式添加:
grant_type:refresh_token
refresh_token:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhYWEiLCJzY29wZSI6WyJhbGwiXSwiYXRpIjoiYzlhZjM5Y2MtMjRkNC00MTlhLThhM2MtM2U5Y2U0YmM0OWMxIiwiZXhwIjoxNTk1MTQwNTE1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMDhiMzM3NmUtMzUyYy00MTgxLWE0N2ItY2Y5MTJkYTk5NGM3IiwiY2xpZW50X2lkIjoiYWRtaW4ifQ.zJi60zQ5kktqzU3DB-TzgkDBdepYZu107LCYhusY_s4
    
返回如下:
{
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTI1OTE3NjMsInVzZXJfbmFtZSI6ImFhYSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiJlMGI4M2Y2Mi1mODc1LTRhMGQtYjIzZi0yYzRkYzA3ZWNiMjEiLCJjbGllbnRfaWQiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdfQ.Di5zDBrazp-VPI0XPCHIavoj9DbF-ohOACkeKHdQM8o",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhYWEiLCJzY29wZSI6WyJhbGwiXSwiYXRpIjoiZTBiODNmNjItZjg3NS00YTBkLWIyM2YtMmM0ZGMwN2VjYjIxIiwiZXhwIjoxNTk1MTQwNTE1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMDhiMzM3NmUtMzUyYy00MTgxLWE0N2ItY2Y5MTJkYTk5NGM3IiwiY2xpZW50X2lkIjoiYWRtaW4ifQ.9vEIQ-KsbJ1qTFkBbVt53e068uL8cpj_SJPAItj3nBM",
    "expires_in": 43199,
    "scope": "all",
    "jti": "e0b83f62-f875-4a0d-b23f-2c4dc07ecb21"
}
    
```



```java
8. 检查token_key, GET请求
http://localhost:8766/oauth/token_key
请求头添加: 
Basic Auth: admin 123456
请求体表单方式添加:
token:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1OTI1OTE3MTUsInVzZXJfbmFtZSI6ImFhYSIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiJjOWFmMzljYy0yNGQ0LTQxOWEtOGEzYy0zZTljZTRiYzQ5YzEiLCJjbGllbnRfaWQiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdfQ.dKR-P-cnag-gdMjXNFyWlqTShfKQ8rRdIVWPOnKem3k

 返回如下:
{
    "alg": "HMACSHA256",
    "value": "secret"
}
```



## 8. 创建并配置资源服务器 

	### 1、 引入需要的 maven 包

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.15.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.zhangwei</groupId>
    <artifactId>springcloud-oauth2-resources</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>springcloud-oauth2-resources</name>
    <description>Demo project for Spring Boot</description>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.SR1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-oauth2</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```



### 2、 配置application.yml

```yaml
spring:
  application:
    name: resource-server

server:
  port: 8767
```



### 3、java文件配置

```java
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String SIGNING_KEY = "secret";
    private static final String RESOURCE_ID = "zhangwei";

    @Autowired
    private TokenStore tokenStore;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        resources
                .resourceId(RESOURCE_ID)
                .tokenStore(tokenStore)
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }

    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices services = new RemoteTokenServices();
        services.setCheckTokenEndpointUrl("http://localhost:8766/oauth/check_token");
        //  oauth2 server 端添加的客户端配置，即当前客户端
        services.setClientId("admin");
        services.setClientSecret("123456");
        return services;
    }
}
```



```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
                .anyRequest()
                .permitAll();
    }
}
```



### 4、测试访问资源服务器

```java
GET请求: http://localhost:8767/user
返回如下:
{
    "error": "unauthorized",
    "error_description": "Full authentication is required to access this resource"
}
```

返回结果，我们可以发现，提示资源服务器访问需要认证，所以我们需要在请求头添加token

Authorization: Bearer token

```java
GET请求: http://localhost:8767/user
添加请求头:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiemhhbmd3ZWkiXSwidXNlcl9uYW1lIjoiemhhbndlaSIsInNjb3BlIjpbImFsbCJdLCJleHAiOjE1OTI3MTI5MzIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJqdGkiOiJiMmRiMmE4OC1jZjY1LTQyNjAtYjkzOC1kNTJkOGFlNzk0MDQiLCJjbGllbnRfaWQiOiJhZG1pbiJ9.7QHBCIUBUsIv6QRUXVWt1JOGIFS-k72lzTZoQVuskUk
    
返回如下:
{
    "authorities": [
        {
            "authority": "ROLE_USER"
        }
    ],
    "details": null,
    "authenticated": true,
    "userAuthentication": {
        "authorities": [
            {
                "authority": "ROLE_USER"
            }
        ],
        "details": null,
        "authenticated": true,
        "principal": "zhanwei",
        "credentials": "N/A",
        "name": "zhanwei"
    },
    "credentials": "",
    "clientOnly": false,
    "oauth2Request": {
        "clientId": "admin",
        "scope": [
            "all"
        ],
        "requestParameters": {
            "client_id": "admin"
        },
        "resourceIds": [
            "zhangwei"
        ],
        "authorities": [],
        "approved": true,
        "refresh": false,
        "redirectUri": null,
        "responseTypes": [],
        "extensions": {},
        "grantType": null,
        "refreshTokenRequest": null
    },
    "principal": "zhanwei",
    "name": "zhanwei"
}
```


## 授权服务器，资源服务器，授权资源二合一服务器，基于jdbc方式见代码，sql见/resource/目录下 

## 持续更新中 ..... 
