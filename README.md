# Как нам Spring Cloud Gateway жить помогает

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
        * `AddRequestHeaderGatewayFilter` – добавляем заголовки;
        * `RequestRateLimiterGatewayFilter` – как средство контролировать количество запросов;
        * `StripPrefixGatewayfactory` и `RewritePathGatewayFilter` – модифицируем path;
        * `RetryGatewayFilter` – реализуем retry запросов;
        * `ModifyRequestBodyGatewayFilter`, `ModifyResponseBodyGatewayFilter` – модифицируем запросы и ответы.
          Разберемся как добраться до "тела" запроса и каике с этим связаны проблемы.
    3. Задаем таймауты запросов.
4. Подключаем Spring Cloud Security для защиты наших endpoints.