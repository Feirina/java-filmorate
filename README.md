# java-filmorate
Template repository for Filmorate project.
![Filmorate Data Base diagram](https://github.com/Feirina/java-filmorate/blob/main/Filmorate%20DB%20diagram.jpg)

*Получение списка всех фильмов*
```sql
SELECT *
FROM film;
```

*Получение фильма по id*
```sql
SELECT *
FROM film
WHERE film_id = id;
```

*Получение списка n самых популярных фильмов*
```sql
SELECT *
FROM film
WHERE film_id IN (SELECT f.film_id,
                  COUNT(uf.user_id) AS count_of_like
                  FROM film AS f
                  LEFT JOIN user_likes_film AS uf ON f.film_id = uf.film_id
                  GROUP BY f.film_id
                  ORDER BY count_of_like DESC
                  LIMIT n);
```

*Получение списка всех пользователей*
```sql
SELECT *
FROM user;
```

*Получение пользователя по id*
```sql
SELECT *
FROM user
WHERE user_id = id;
```

*Получение списка друзей пользователя по id*
```sql
SELECT *
FROM user
WHERE user_id IN (SELECT friend_id
                  FROM list_of_friends 
                  WHERE user_id = id);
```

*Получение списка общих друзей пользователя по id и otherId*
```sql
SELECT *
FROM user
WHERE user_id IN (SELECT friend_id
                  FROM list_of_friend
                  WHERE user_id = id
                  AND friend_id IN (SELECT friend_id
                                    FROM list_of_friends 
                                    WHERE user_id = otherId));
```