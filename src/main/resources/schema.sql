DROP TABLE REVIEWS_LIKES IF EXISTS CASCADE;
DROP TABLE REVIEWS IF EXISTS CASCADE;
DROP TABLE USER_LIKES_FILM IF EXISTS CASCADE;
DROP TABLE FILM_GENRE IF EXISTS CASCADE;
DROP TABLE GENRE IF EXISTS CASCADE;
DROP TABLE FILM_DIRECTORS IF EXISTS CASCADE;
DROP TABLE DIRECTOR IF EXISTS CASCADE;
DROP TABLE FRIEND_LIST IF EXISTS CASCADE;
DROP TABLE FILMORATE_USER IF EXISTS CASCADE;
DROP TABLE FILM IF EXISTS CASCADE;
DROP TABLE MPA IF EXISTS CASCADE;
DROP TABLE FEED IF EXISTS CASCADE;
DROP TYPE IF EXISTS event_type CASCADE;
DROP TYPE IF EXISTS operation CASCADE;

CREATE TABLE IF NOT EXISTS MPA
(
    MPA_ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    MPA_NAME CHARACTER VARYING(50),
    CONSTRAINT uc_mpa_name UNIQUE (MPA_NAME)
);

CREATE TABLE IF NOT EXISTS FILM
(
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NAME CHARACTER VARYING(100) not null,
    DESCRIPTION CHARACTER VARYING(200),
    DURATION INTEGER,
    RELEASE_DATE DATE,
    MPA_ID BIGINT not null references MPA(MPA_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS FILMORATE_USER
(
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    EMAIL CHARACTER VARYING(100),
    LOGIN CHARACTER VARYING(50) not null,
    NAME CHARACTER VARYING(50),
    BIRTHDAY DATE
);

CREATE TABLE IF NOT EXISTS FRIEND_LIST
(
    USER_ID BIGINT not null references FILMORATE_USER(ID) ON DELETE CASCADE,
    FRIEND_ID BIGINT not null references FILMORATE_USER(ID) ON DELETE CASCADE,
    STATUS_OF_FRIENDSHIP BOOLEAN,
    constraint FRIEND_LIST_PK primary key (USER_ID, FRIEND_ID)
);

CREATE TABLE IF NOT EXISTS GENRE
(
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NAME CHARACTER VARYING(50),
    CONSTRAINT uc_genre_name UNIQUE (NAME)
);

CREATE TABLE IF NOT EXISTS FILM_GENRE
(
    GENRE_ID BIGINT not null references GENRE(ID) ON DELETE CASCADE,
    FILM_ID BIGINT not null references FILM(ID) ON DELETE CASCADE,
    constraint FILM_GENRE_PK primary key (GENRE_ID, FILM_ID)
);

CREATE TABLE IF NOT EXISTS USER_LIKES_FILM
(
    USER_ID BIGINT not null references FILMORATE_USER ON DELETE CASCADE,
    FILM_ID BIGINT not null references FILM ON DELETE CASCADE,
    constraint USER_LIKES_FILM_PK primary key (USER_ID, FILM_ID)
);

CREATE TABLE IF NOT EXISTS DIRECTOR
(
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    NAME CHARACTER VARYING(100) not null
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTORS
(
    FILM_ID BIGINT not null references FILM(ID) ON DELETE CASCADE,
    DIRECTOR_ID BIGINT not null references DIRECTOR(ID) ON DELETE CASCADE,
    constraint FILM_DIRECTORS_PK PRIMARY KEY (FILM_ID, DIRECTOR_ID)
);

CREATE TYPE IF NOT EXISTS event_type AS ENUM ('LIKE', 'REVIEW', 'FRIEND');
CREATE TYPE IF NOT EXISTS operation AS ENUM ('ADD', 'UPDATE', 'REMOVE');
CREATE TABLE IF NOT EXISTS FEED
(
    EVENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY,
    TIMESTAMP BIGINT,
    USER_ID BIGINT REFERENCES FILMORATE_USER(ID) ON DELETE CASCADE,
    EVENT_TYPE event_type NOT NULL,
    OPERATION operation NOT NULL,
    ENTITY_ID BIGINT NOT NULL,
    CONSTRAINT FEED_PK PRIMARY KEY(EVENT_ID)
);

CREATE TABLE IF NOT EXISTS REVIEWS
(
    ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    CONTENT CHARACTER VARYING NOT NULL,
    IS_POSITIVE BOOLEAN NOT NULL,
    USER_ID BIGINT not null references FILMORATE_USER ON DELETE CASCADE,
    FILM_ID BIGINT not null references FILM ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEWS_LIKES
(
    REVIEW_ID BIGINT not null references REVIEWS(ID) ON DELETE CASCADE,
    USER_ID BIGINT not null references FILMORATE_USER(ID) ON DELETE CASCADE,
    LIKE_VALUE BIGINT,
    constraint REVIEWS_LIKES_PK primary key (REVIEW_ID, USER_ID)
);
