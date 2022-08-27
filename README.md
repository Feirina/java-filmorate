# java-filmorate
Template repository for Filmorate project.
![Filmorate Data Base diagram](https://github.com/Feirina/java-filmorate/blob/main/Filmorate%20DB%20diagram.jpg)

*Get all films*
```sql
SELECT *
FROM film;
```

*Get film by id*
```sql
SELECT *
FROM film
WHERE film_id = id;
```

*Get most popular n films*
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

*Get all users*
```sql
SELECT *
FROM user;
```

*Get user by id*
```sql
SELECT *
FROM user
WHERE user_id = id;
```

*Get user`s list of friends by id*
```sql
SELECT *
FROM user
WHERE user_id IN (SELECT friend_id
                  FROM list_of_friends 
                  WHERE user_id = id);
```

*Get list of mutual friends of user and other user*
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