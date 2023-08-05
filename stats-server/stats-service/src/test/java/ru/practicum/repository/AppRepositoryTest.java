package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.App;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class AppRepositoryTest {

    private final AppRepository appRepo;

    private App app;
    private App.AppBuilder appBuilder;

    @BeforeEach
    void setup() {
        String app = "ewm-main-service";
        String uri = "/events/1";

        appBuilder = App.builder()
                .name(app)
                .uri(uri);
    }

    @Test
    void shouldAdd() {
        //Regular Case
        app = appBuilder.build();
        Long appId = appRepo.add(app);
        assertNotNull(appId);

        List<App> apps = appRepo.findByAppAndUri(app.getName(), app.getUri());
        assertThat(apps)
                .isNotNull()
                .hasSize(1);
        assertEquals(apps.get(0).getId(), appId);
    }

    @Test
    void shouldFindByAppAndUri() {
        //EmptyList
        app = appBuilder.build();
        List<App> apps = appRepo.findByAppAndUri(app.getName(), app.getUri());
        assertThat(apps)
                .isNotNull()
                .hasSize(0);

        //RegularCase
        app = appBuilder.build();
        long appId = appRepo.add(app);

        appBuilder.uri("/events/2").build();

        apps = appRepo.findByAppAndUri(app.getName(), app.getUri());
        assertThat(apps)
                .isNotNull()
                .hasSize(1);
        assertEquals(apps.get(0).getId(), appId);
    }

    @Test
    void getAppsByUris() {
        //EmptyList
        List<App> apps = appRepo.getAppsByUris(Collections.emptyList());
        assertThat(apps)
                .isNotNull()
                .hasSize(0);

        //SingleList
        app = appBuilder.build();
        long appId = appRepo.add(app);
        apps = appRepo.getAppsByUris(List.of(app.getUri()));
        assertThat(apps)
                .isNotNull()
                .hasSize(1);
        assertEquals(apps.get(0).getId(), appId);

        //RegularCase
        App app1 = appBuilder.uri("/events/2").build();
        long appId1 = appRepo.add(app1);

        apps = appRepo.getAppsByUris(List.of(app.getUri(), app1.getUri()));
        assertThat(apps)
                .isNotNull()
                .hasSize(2);
        assertEquals(apps.get(0).getId(), appId);
        assertEquals(apps.get(1).getId(), appId1);
    }

    @Test
    void getAppsByIds() {
        //EmptyList
        List<App> apps = appRepo.getAppsByIds(Collections.emptyList());
        assertThat(apps)
                .isNotNull()
                .hasSize(0);

        //SingleList
        app = appBuilder.build();
        long appId = appRepo.add(app);
        apps = appRepo.getAppsByIds(List.of(appId));
        assertThat(apps)
                .isNotNull()
                .hasSize(1);
        assertEquals(apps.get(0).getId(), appId);

        //Regular Case
        App app1 = appBuilder.uri("/events/2").build();
        long appId1 = appRepo.add(app1);

        apps = appRepo.getAppsByIds(List.of(appId, appId1));
        assertThat(apps)
                .isNotNull()
                .hasSize(2);
        assertEquals(apps.get(0).getId(), appId);

    }
}