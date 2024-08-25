# Как нам Spring Cloud Gateway жить помогает

![Build Workflow](https://github.com/Romanow/gateway-lecture/workflows/Build%20project/badge.svg?branch=master)
[![pre-commit](https://img.shields.io/badge/pre--commit-enabled-brightgreen?logo=pre-commit)](https://github.com/pre-commit/pre-commit)

## Аннотация

Наверняка вы сталкивались с ситуацией, когда на сервисах нужно реализовать какую-то однотипную техническую логику (
например, логирование пользовательских запросов, security и т.п.), каким-то образом модифицировать запрос и пробросить
его дальше или просто сделать routing из одной точки, а не забивать url всех сервисов на frontend'е?

Поговорим про паттерн API Gateway, и как пример реализации возьмем Spring Cloud Gateway. В режиме Live Coding с его
помощью реализуем типовые операции: routing, модификация запросов и ответов, а так же прикрутим к нему security.

## План

1. Разберемся что такое паттерн API Gateway:
    * какие проблемы он решает;
    * а для чего его использовать не надо.
2. В двух словах про Spring Cloud Gateway и WebFlux.
3. Посмотрим что Spring Cloud Gateway умеет и чем он нам будет полезен:
    1. Route Predicate как способ настроить гибкие правила проксирования (а еще как мы еще проверяем запрос на
       соответствие OpenAPI);
    2. Gateway Filter Factory как средство модификации запросов.
        * `StripPrefixGatewayFactory` и `PrefixPathGatewayFilterFactory` – модифицируем path;
        * `AddRequestHeaderGatewayFilterFactory` – добавляем заголовки;
        * `RequestRateLimiterGatewayFilterFactory` – как средство контролировать количество запросов;
        * `RetryGatewayFilterFactory` – реализуем retry запросов;
        * `ModifyResponseBodyGatewayFilterFactory` – модифицируем ответ. Разберемся как добраться до "тела" запроса и
          каике с этим связаны проблемы.
    3. Задаем таймауты запросов.
4. Подключаем Spring Cloud Security для защиты наших endpoints.

## Доклад

### Разберемся что такое паттерн API Gateway

Шлюз API находится между клиентами и службами, он выполняет функцию обратного прокси, передавая запросы от клиентов к
сервисам. Также он может выполнять такие специализированные задачи, как аутентификация, SSL-termination и Rate Limiting.

Функции Gateway API можно сгруппировать в соответствии со следующими задачами:

* routing – используется в качестве обратного прокси-сервера для перенаправления запросов на одну или несколько сервисов
  с помощью маршрутизации L7. Шлюз предоставляет одну конечную точку для клиентов и позволяет разделить клиенты и
  сервисы;
* повторное выполнение запросов (retry), Circuit Breaker, timeouts;
* безопасность – аутентификация и авторизация, black/white list и т.п. (это очень важный пункт, т.к. на API Gateway
  решается вопрос безопасности, а за ним находится доверенна зона (DMZ));
* логирование запросов и ответов, мониторинг;
* кэширование ответов, gzip;
* агрегация и модификация запросов / ответов.

API Gateway в первую очередь утильный элемент системы, который _не должен_ содержать в себе бизнес логики. Но это идет в
противоречие с последним пунктом: "агрегация и модификация запросов / ответов", – потому что для любой манипуляции
данными требуется знать из каких блоков они состоят и как их собирать. А значит на Gateway переносится часть бизнес
логики, что делает его связанным с самим сервисом. Этого стоит избегать, но если есть необходимость в таком функционале,
то использовать его только для "обогащения" ответа, а не изменения его структуры.

### В двух словах про Spring Cloud Gateway и WebFlux

### Посмотрим что Spring Cloud Gateway умеет

#### Route Predicate как способ настроить гибкие правила проксирования

На базе этих правил Spring Cloud Gateway выполняет routing.

[Writing Custom Route Predicate Factories](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#writing-custom-route-predicate-factories)

Основные правила:

###### Path (Ant-Style формат)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: path-route
          uri: http://dictionary:8080
          predicates:
            - Path=/dict/**
```

[The Path Route Predicate Factory](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#the-path-route-predicate-factory)

###### Header

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: header-route
          uri: http://dictionary:8080
          predicates:
            - Header=X-Target-Service, dict
```

[The Header Route Predicate Factory](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#the-header-route-predicate-factory)

###### Method

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: method-route
          uri: http://dictionary:8080
          predicates:
            - Method=GET,POST
```

[The Method Route Predicate Factory](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#the-method-route-predicate-factory)

###### Query Params

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: query-route
          uri: http://dictionary:8080
          predicates:
            - Query=service, dict
```

[The Query Route Predicate Factory](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#the-query-route-predicate-factory)

#### CORS

_Cross-Origin Resource Sharing (CORS)_ — механизм, использующий дополнительные HTTP-заголовки, чтобы дать возможность
агенту пользователя получать разрешения на доступ к выбранным ресурсам с сервера на источнике (домене), отличном от
того, что сайт использует в данный момент.

Источник идентифицируется следующей тройкой параметров: схема, полностью определенное имя хоста и порт.
Например, `http://example.com` и `https://example.com` имеют разные источники: первый использует схему `http`, а второй
`https`. Следовательно, 2 источника отличаются схемой и портом, тогда как хост один и тот же (`example.com`).

Таким образом, если хотя бы один из трех элементов у двух ресурсов отличается, то источник ресурсов также считается
разным. В кросс-доменный запрос браузер автоматически добавляет заголовок `Origin`, содержащий домен, с которого
осуществлён запрос. Сервер должен, со своей стороны, ответить специальными заголовками, разрешает ли он такой запрос к
себе. Если сервер разрешает кросс-доменный запрос с этого домена – он должен добавить к ответу заголовок
`Access-Control-Allow-Origin`, содержащий домен запроса или звёздочку `*`.

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]':
            allowedOrigins: "*"
            allowedMethods:
              - GET
              - POST
              - PATCH
              - PUT
              - DELETE
```

```shell
$ curl -X OPTIONS \
  -H 'Access-Control-Request-Method: GET' \
  -H "Origin: http://localhost" \
  http://localhost:8000/dict/v1/lego-sets/ -v

* Connected to localhost (127.0.0.1) port 8000 (#0)
* Server auth using Basic with user 'ronin'
> OPTIONS /dict/v1/lego-sets/ HTTP/1.1
> Host: localhost:8000
> User-Agent: curl/7.79.1
> Accept: */*
> Access-Control-Request-Method: GET
> Origin: http://localhost
>
* Mark bundle as not supporting multiuse
< HTTP/1.1 200 OK
< Vary: Origin
< Vary: Access-Control-Request-Method
< Vary: Access-Control-Request-Headers
< Access-Control-Allow-Origin: *
< Access-Control-Allow-Methods: GET,POST,PATCH,PUT,DELETE
< Cache-Control: no-cache, no-store, max-age=0, must-revalidate
< Pragma: no-cache
< Expires: 0
< X-Content-Type-Options: nosniff
< X-Frame-Options: DENY
< X-XSS-Protection: 1 ; mode=block
< Referrer-Policy: no-referrer
< content-length: 0
<
* Connection #0 to host localhost left intact
```

#### Gateway Filter Factory как средство модификации запросов

[Writing Custom GatewayFilter Factories](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#writing-custom-gatewayfilter-factories)

##### Модификация path (`StripPrefixGatewayFactory`, `PrefixPathGatewayFilterFactory`)

Target URL: `http://localhost:8080/api/v1/lego-sets`, Gateway URL: `http://localhost:8080/dict/v1/lego-sets`.

```java
public class WebConfiguration {

    @Bean
    public RouteLocator routers(RouteLocatorBuilder builder, RoutesProperties routes) {
        return builder
            .routes()
            .route("dictionary", pathSpec -> pathSpec
                .path("/dict/**")                   // http://localhost:8080/dict/**
                .filters(filterSpec -> filterSpec
                    .stripPrefix(1)             // /dict/v1/lego-sets -> /v1/lego-sets
                    .prefixPath("/api"))        // /v1/lego-sets -> /api/v1/lego-sets
                .uri("http://localhost:8080"))
            .build();
    }

}
```

##### Добавляем заголовок (`AddRequestHeaderGatewayFilterFactory`)

```java
public class WebConfiguration {

    @Bean
    public RouteLocator routers(RouteLocatorBuilder builder, RoutesProperties routes) {
        return builder
            .routes()
            .route("dictionary", pathSpec -> pathSpec
                .path("/dict/**")
                .filters(filterSpec -> filterSpec
                    .addRequestHeader("X-Gateway-Timestamp", ISO_DATE_TIME.format(now()))
                    .uri(routes.getDictionary()))
                .uri("http://localhost:8080"))
            .build();
    }

}
```

##### Контроль пропускной способности сервиса (`RequestRateLimiterGatewayFilterFactory`)

Rate Limiter – ограничение скорости обработки запросов, т.е. это искусственный барьер, который не дает клиенту выполнить
больше определенного количества больше операций в единицу времени. Rate Limiter защищает систему от перегрузки, т.е. на
целевой сервис попадет только такое количество запросов, которое не приведет к дефициту ресурсов (Resource Starvation).

Так же Rate Limiter может использоваться для предотвращения brute force атак и для контроля доступа к платным ресурсам.

Spring Cloud Gateway предоставляет фильтр `RequestRateLimiterGatewayFilterFactory` для реализации Rate Limiter, но
оставляет за пользователем выбор ключа (`KeyResolver`) и самого алгоритма (`AbstractRateLimiter`).

Для простоты реализации считать, что Gateway запускаем в один instance или нам не требуется распространять информацию о
состоянии buckets между нодами.

Для реализации возьмем алгоритм Token Bucket (Алгоритм маркерной корзины):

![Token Bucket](images/Token%20Bucket.png)

Bucket – емкость конечного размера, ассоциированная с пользователем (ip, location и т.п.), куда помещаются маркеры. Если
количество маркеров больше заданного объема, то запрос отбрасывается (429 Too Many Requests), иначе в bucket добавляется
token и запрос продолжает выполнение. Количество token в корзине возобновляется в течение времени.

[InMemoryRateLimiter](gateway/src/main/java/ru/romanow/gateway/utils/InMemoryRateLimiter.kt)

```java

@Configuration
public class WebConfiguration {

    @Bean
    public RouteLocator routers(RouteLocatorBuilder builder, RoutesProperties routes) {
        return builder
            .routes()
            .route("dictionary", pathSpec -> pathSpec
                .path("/dict/**")
                .filters(filterSpec -> filterSpec
                    .requestRateLimiter(rateLimiterConfig -> rateLimiterConfig
                        .setRateLimiter(rateLimiter())
                        .setKeyResolver(keyResolver())))
                .uri(routes.getDictionary()))
            .build();
    }

    @Bean
    public RateLimiter<InMemoryRateLimiter.Config> rateLimiter() {
        return new InMemoryRateLimiter(1, 2, ofSeconds(10));
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

}
```

[System Design Basics: Rate Limiter](https://medium.com/geekculture/system-design-basics-rate-limiter-351c09a57d14)

##### Повтор запросов (`RetryGatewayFilterFactory`)

Если запрос завершился неуспешно (5xx Series), то выполняем повторный запрос. Spring Cloud Gateway позволяет настраивать
политику повтора:

* retries – количество повторов;
* backoff – экспоненциальная задержка перед следующим повтором.

```java

@Configuration
public class WebConfiguration {

    @Bean
    public RouteLocator routers(RouteLocatorBuilder builder, RoutesProperties routes) {
        // @formatter:off
        return builder
            .routes()
            .route("dictionary", pathSpec -> pathSpec
                .path("/dict/**")
                .filters(filterSpec -> filterSpec
                    .retry(retryConfig -> retryConfig
                        .setRetries(3)                                 // 3 повтора
                        .setStatuses(HttpStatus.NOT_FOUND)             // включить повтор при 404 Not Found
                        .setSeries(HttpStatus.Series.SERVER_ERROR)     // выполнять повтор при 5xx ошибках
                        .setBackoff(ofSeconds(1),                      // firstBackoff – первый повтор через 1 секунду,
                            ofSeconds(10),                     // maxBackoff – не более 10 секунд
                            2,                                 // factor – коэффициент задержки: backoff = firstBackoff * (factor ^ n), т.е.
                            false)))                           // basedOnPreviousValue – если true, то backoff = prevBackoff * factor


                // если basedOnPreviousValue = true, то prevBackoff * factor
                .uri(routes.getDictionary()))
            .build();
        // @formatter:on
    }

}
```

##### Модификация тела ответа (`ModifyResponseBodyGatewayFilterFactory`)

#### Таймауты запросов

Если мы знаем, что 99% запросов (99 line) выполняется за 200ms, не имеет смысла ждать окончания выполнения операции
больше 400ms. Это называется Fail Fast – если операция выполняется слишком долго, то ее лучше прервать и повторить еще
раз.

Для задания таймаутов в Java HTTP клиентах можно задать `Connection Timeout` (установка соединения) и Read Timeout (
таймаут чтения из InputStream после установки
соединения) ([Class URLConnection :: setReadTimeout](https://docs.oracle.com/javase/8/docs/api/java/net/URLConnection.html#setReadTimeout-int-))

```java

@Configuration
public class WebConfiguration {

    @Bean
    @Autowired
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(500))
            .setReadTimeout(Duration.ofSeconds(500))
            .build();
    }

}
```

Если мы используем API Gateway, то мы можем задавать суммарный таймаут на всю операцию. В Spring Cloud Gateway можно
задать общий таймаут и отдельный таймаут на каждую операцию:

```java

@Configuration
public class WebConfiguration {

    @Bean
    public RouteLocator routers(RouteLocatorBuilder builder, RoutesProperties routes) {
        return builder
            .routes()
            .route("dictionary", spec -> spec
                .path("/dict/**")
                .metadata(RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR, 2000)
                .uri(routes.getDictionary()))
            .build();
    }

}
```

### Подключаем Spring Cloud Security для защиты наших endpoints

Одна из основных задач API Gateway – авторизация, реализуем ее с помощью `Spring Security`. Т.к. Spring Cloud Gateway
построен на WebFlux, для настройки правил security требуется создать `SecurityWebFilterChain` (отличии от WebMVC, где мы
наследовались от `WebSecurityConfigurerAdapter` и задавали конфигурацию `HttpSecurity`).

```java

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(spec -> spec
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()     // для preflight request CORS
                .pathMatchers(HttpMethod.GET, "/manage/**").permitAll()  // для actuator
                .pathMatchers("/dict/**").authenticated()                // все остальное с Basic Auth
            )
            .httpBasic();

        return http.build();
    }

}
```

### Дополнительные возможности

Можно посмотреть настройки проксирования и примененные правила:

```shell
$ curl http://localhost:8000/manage/gateway/routes | jq
```

По конкретному route:

```shell
$ curl http://localhost:8000/manage/gateway/routes/dictionary | jq
```

## Запуск примера

```shell
$ docker compose up -d
$ ./gradlew clean build
$ ./gradlew dictionary:bootRun --args='--spring.profiles.active=local'

```

## Ссылки

1. [Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html)
2. [Bucket4j Rate Limiter](https://github.com/bucket4j/bucket4j)
