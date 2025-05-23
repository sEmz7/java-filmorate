# java-filmorate
Template repository for Filmorate project.
## Схема DB
![db](https://github.com/user-attachments/assets/10e8b04e-de18-45c0-aeed-9c8f8d8b88e8)


### 1. users
- Таблица с пользователями

### 2. friends
- Таблица с id пользователей, их друзей и id статусов дружбы 

### 3. status
- Таблица с id статусов и их названиями (Неподтвержденная/Подтвержденная)

### 4. likes
- Таблица с лайками пользователей на фильмы

### 5. films
- Таблица с фильмами

### 6. film_genres
- Таблица с id фильмов и id их жанров

### 7. genres
- Таблица с id жанров и их названиями

### 8. rating
- Таблица c id рейтингов и их названиями



## Примеры запросов SQL для основных операций

### 1. Получить информацию о всех пользователей

```sql
SELECT *
FROM users
```

### 2. Получить всю информацию о пользователе по id

```sql
SELECT * 
FROM users
WHERE user_id = {userId}`
```

### 3. Посмотреть имена друзей пользователя

```sql
SELECT u.name
FROM friends f
JOIN users u ON f.friend_id = u.user_id
WHERE f.user_id = {userId};
```

### 4. Посмотреть информацию о всех фильмах

```sql
SELECT *
FROM films
```

### 5. Посмотреть информацию о фильме по id

```sql
SELECT *
FROM films
WHERE film_id = {filmId}
```

### 6. Посмотреть имена пользователей, кто поставил лайк на фильм

```sql
SELECT u.name
FROM likes as l
JOIN users as u ON l.user_id = u.user_id
WHERE l.film_id = {filmId}
```

### 7. Получить список фильмов, которые лайкнул пользователь с ID = `{userId}`

```sql
SELECT f.name
FROM likes as l
JOIN films as f ON f.film_id = l.film_id
WHERE l.user_id = {userId}
```

### 8. Получить количество лайков у каждого фильма

```sql
SELECT
  f.name,
  COUNT(l.user_id) as likes_count
FROM films as f
JOIN likes as l ON l.film_id = f.film_id
GROUP BY f.film_id, f.name
```
