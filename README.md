# java-filmorate

## Для чего предназначен данный проект.

**java-filmorate** это социальная сеть по оценке фильмов с возможностью добавления пользователей в друзья, вывода рекомендаций фильмов к просмотру, поиска фильмов по названию/режиссеру. Она поможет вам найти фильм для просмотра на вечер на основе рекомендаций, с учетом схожих оценок, которые вы с вашими друзьями поставили другим фильмам.

## Функциональности проекта
### Функциональности пути /films
**GET /films** получение списка всех фильмов

**GET /films/{id}** получение фильма по id

**GET /films/popular?count={limit}&genreId={genreId}&year={year}** получение списка самых популярных фильмов, в параметре запроса передается число фильмов, которые необходимо вывести (по умолчанию = 10), так же возможно отфильтровать по жанру и за указанный год (по умолчанию поиск осуществляется без данных фильтров)

**GET /films/director/{directorId}** получение фильма по id режиссера

**GET /films/common?userId={userId}&friendId={friendId}** получение списка фильмов общих с другом с сортировкой по их популярности

**GET /fimls/search?query={query}&by={by}** получение списка фильмов, остсортированного по популярности, по текстовому запросу и режиссеру/названию. `query` — текст для поиска, `by` — может принимать значения `director` (поиск по режиссёру), `title` (поиск по названию), либо оба значения через запятую при поиске одновременно и по режиссеру и по названию.

**POST /films** создание и внесение в БД нового фильма

**PUT /films** обновление данных фильма

**PUT /films/{id}/like/{userId}** добавление лайка фильму по id пользователем с userId

**DELETE /films/{id}** удаление фильма по id

**DELETE /films/{id}/like/{userId}** удаление лайка фильму по id пользователем с userId

### Функциональности пути /directors
**GET /directors** получение списка всех режиссеров

**GET /directors/{id}** получение режиссера по id

**POST /directors** создание и добавление режиссера в БД

**PUT /directors** обновление данных режиссера

**DELETE /directors/{id}** удаление режиссера

### Функциональности пути /genres
**GET /genres** получение списка всех жанров фильмов

**GET /genres/{id}** получение жанра фильма по id

### Функциональности пути /mpa
**GET /mpa** получение списка всех рейтингов фильмов

**GET /mpa/{id}** получение рейтинга фильма по id

### Функциональности пути /reviews
**GET /reviews?filmId={filmId}&count={count}** получение всех отзывов по фильму с filmId, если фильм не указан то все. Если кол-во не указано то 10.

**GET /reviews/{id}** получение отзыва по id

**POST /reviews** создание нового отзыва

**PUT /reviews** редактирование отзыва

**PUT /reviews/{id}/like/{userId}** добавление лайка отзыву по id пользователем с userId

**PUT /reviews/{id}/dislike/{userId}** добавление дизлайка отзыву по id пользователем с userId

**DELETE /reviews/{id}** удаление отзыва по id

**DELETE /reviews/{id}/like/{userId}** удаление лайка отзыву по id пользователем с userId

**DELETE /reviews/{id}/dislike/{userId}** удаление лайка отзыву по id пользователем с userId

### Функциональности пути /users
**GET /users** получение списка всех пользователей

**GET /users/{id}** получение пользователя по id

**GET /users/{id}/friends** получение списка друзей пользователя с id

**GET /users/{id}/friends/common/{otherId}** получение списка общих друзей пользователя с id и пользователя с otherId

**GET /users/{id}/feed** получение ленты событий пользователя по id

**GET /users/{id}/recommendations** получение рекомендаций фильмов для пользователя с id

**POST /users** создание пользователя

**PUT /users** обновленние данных пользователя

**PUT /users/{id}/friends/{friendId}** добавление пользователя с friendId в друзья пользователя с id

**DELETE /users/{id}** удаление пользователя по id

**DELETE /users/{id}/friends/{friendId}** удаление пользователя с friendId из друзей пользователя с id

## Схема базы данных
![Filmorate Data Base diagram](https://github.com/Feirina/java-filmorate/blob/main/Filmorate%20DB%20diagram.jpg)

## Над проектом работали 
* [Feirina](https://github.com/Feirina) - положила начало проекту, тк на этапе группового проекта была выбрана тимлидом, то мой репозиторий взяли за основу, на которой продолжили разрабатывать и внедрять новые функциональности при работе на групповом проекте. На групповом этапе разрабатывала функциональности "Рекомендации" и "Общие фильмы".
* [artemklinkov](https://github.com/artemklinkov) - на групповом этапе разрабатывал функциональность "Лента событий".
* [STravinJ](https://github.com/STravinJ) - на групповом этапе отвечал разрабатывал функциональности "Добавление режиссёров в фильмы" и "Вывод самых популярных фильмов по жанру и годам".
* [TatyanaBezverkhova](https://github.com/TatyanaBezverkhova) - на групповом этапе разрабатывала функциональность "Поиск".
* [TrapeznikovDanila](https://github.com/TrapeznikovDanila) - на групповом этапе разрабатывал функциональность "Отзывы".

