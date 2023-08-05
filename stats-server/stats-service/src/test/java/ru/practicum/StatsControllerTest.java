package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.util.DateTime.encodeDate;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {
    private static final String PATH_HIT = "/hit";
    private static final String PATH_STATS = "/stats";
    private static final boolean UNIQUE_DEFAULT = false;
    private static final List<String> URIS_DEFAULT = Collections.emptyList();
    private static final LocalDateTime NOW = LocalDateTime.now();

    private EndpointHitDto.EndpointHitDtoBuilder hitDtoBuilder;
    private ViewStatsDto.ViewStatsDtoBuilder viewStatsDtoBuilder;

    private String start;
    private String end;


    @Autowired
    ObjectMapper mapper;

    @MockBean
    StatsService service;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        String app = "ewm-main-service";
        String uri = "/events/1";
        String ip = "121.0.0.1";
        hitDtoBuilder = EndpointHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(NOW);

        viewStatsDtoBuilder = ViewStatsDto.builder()
                .app(app)
                .uri(uri);

        start = encodeDate(NOW.minusHours(1));
        end = encodeDate(NOW);
    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mvc);
    }

    @Test
    void shouldAdd() throws Exception {
        //Regular Case
        EndpointHitDto hitDto = hitDtoBuilder.build();
        String json = mapper
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .writeValueAsString(hitDto);
        doNothing().when(service).addHit(hitDto);
        mvc.perform(post(PATH_HIT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetEmptyStat() throws Exception {
        //EmptyList
        when(service.getStats(start, end, URIS_DEFAULT, UNIQUE_DEFAULT)).thenReturn(Collections.emptyList());
        mvc.perform(get(PATH_STATS)
                        .param("start", start)
                        .param("end", end))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldGetStat() throws Exception {
        //SingleList
        ViewStatsDto viewStatsDto = viewStatsDtoBuilder.hits(1).build();
        when(service.getStats(start, end, URIS_DEFAULT, UNIQUE_DEFAULT)).thenReturn(List.of(viewStatsDto));
        mvc.perform(get(PATH_STATS)
                        .param("start", start)
                        .param("end", end))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].app", is(viewStatsDto.getApp()), String.class))
                .andExpect(jsonPath("$[0].uri", is(viewStatsDto.getUri()), String.class))
                .andExpect(jsonPath("$[0].hits", is(viewStatsDto.getHits()), Long.class));
    }
}