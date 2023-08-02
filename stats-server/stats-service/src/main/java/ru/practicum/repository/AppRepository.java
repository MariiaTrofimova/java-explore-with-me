package ru.practicum.repository;

import ru.practicum.model.App;

import java.util.List;
import java.util.Optional;

public interface AppRepository {
    Optional<App> findByAppAndUri(String app, String uri);

    long add(App app);

    List<App> getAppsByUris(String[] uris);

    List<App> getAppsByIds(List<Long> ids);
}
