package ru.practicum.repository;

import ru.practicum.model.App;

import java.util.List;

public interface AppRepository {
    List<App> findByAppAndUri(String app, String uri);

    long add(App app);

    List<App> getAppsByUris(List<String> uris);

    List<App> getAppsByIds(List<Long> ids);
}
