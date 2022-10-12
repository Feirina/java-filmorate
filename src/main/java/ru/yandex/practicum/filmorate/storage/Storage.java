package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class Storage<T> {
    public T get(Long id) {
        return getMap().get(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(getMap().values());
    }

    public void delete(Long id) {
        getMap().remove(id);
    }

    public T update(T object) {
        log.info("Данные {} обновлены", object);

        return object;
    }

    public T create(T object) {
        log.info("Данные {} сохранены", object);

        return object;
    }

    public abstract Map<Long, T> getMap();

}
