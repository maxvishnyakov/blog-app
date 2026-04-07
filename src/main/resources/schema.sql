CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    image BYTEA,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    tags VARCHAR(1000) DEFAULT '[]',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

INSERT INTO posts (title, content, tags, likes_count, comments_count) VALUES
(
    'Введение в Spring Boot',
    'Spring Boot — это фреймворк для создания микросервисов и веб-приложений на Java. Он упрощает настройку и развертывание приложений. В этой статье мы рассмотрим основные концепции и создадим первое приложение.',
    '["java", "spring", "tutorial"]',
    15,
    3
),
(
    'Работа с JDBC в Spring',
    'Spring JDBC предоставляет удобные абстракции для работы с базами данных. JdbcTemplate упрощает выполнение SQL-запросов и обработку результатов.',
    '["java", "spring", "database"]',
    8,
    1
),
(
    'REST API с нуля',
    'REST — это архитектурный стиль для создания веб-сервисов. В этом посте разберём основные принципы: ресурсы, HTTP методы, статус коды и форматы данных.',
    '["api", "rest", "web"]',
    23,
    5
),
(
    'Короткий пост без тегов',
    'Пост для проверки отображения.',
    '[]',
    2,
    0
),
(
    'Ещё один полезный пост про Spring',
    'Spring — это отличный выбор для разработки на Java благодаря мощной экосистеме, удобству и высокой востребованности в индустрии.',
    '["tips", "programming"]',
    7,
    2
);

INSERT INTO comments (post_id, content) VALUES
(1, 'Отличная статья! Очень помогла разобраться.'),
(1, 'А где можно найти примеры кода?'),
(1, 'Спасибо, очень понятно объяснили!'),
(2, 'JdbcTemplate действительно удобная штука.'),
(3, 'Лучшее объяснение REST API, что я видел!'),
(3, 'А как обрабатывать авторизацию?'),
(3, 'Спасибо за материал! Жду продолжения.'),
(3, 'Очень полезно, добавил в закладки.'),
(3, 'А можно пример с HATEOAS?'),
(5, 'Хороший пост!'),
(5, 'Полезно, спасибо!');