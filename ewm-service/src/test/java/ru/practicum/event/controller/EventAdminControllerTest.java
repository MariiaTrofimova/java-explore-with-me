package ru.practicum.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.service.EventService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EventAdminController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventAdminControllerTest {

    private static final String PATH = "/admin/events";

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private EventService service;

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mvc);
    }

    @Test
    void shouldGetEmptyList() throws Exception {
        //Null params
        when(service.getAllByCriteriaByAdmin(anyList(), anyList(), anyList(), any(), any(),
                anyFloat(), anyFloat(), anyInt(), anyInt(), anyInt())).thenReturn(Collections.emptyList());
        mvc.perform(get(PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldGetSingleList() throws Exception {
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(1L)
                .title("title")
                .build();
        String json = mapper.writeValueAsString(List.of(eventFullDto));
        //Null params | default from size
        when(service.getAllByCriteriaByAdmin(null, null, null, null, null,
                null, null, null, 0, 10)).thenReturn(List.of(eventFullDto));
        mvc.perform(get(PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(eventFullDto.getTitle())))
                .andExpect(content().json(json));
    }

    @Test
    void shouldFailGetAll() throws Exception {
        mvc.perform(get(PATH)
                        .param("lat", "-1"))
                .andDo(print())
                .andExpect(status().isConflict());

        mvc.perform(get(PATH)
                        .param("lon", "-1"))
                .andDo(print())
                .andExpect(status().isConflict());

        mvc.perform(get(PATH)
                        .param("radius", "-1"))
                .andDo(print())
                .andExpect(status().isConflict());

        mvc.perform(get(PATH)
                        .param("from", "-1"))
                .andDo(print())
                .andExpect(status().isConflict());

        mvc.perform(get(PATH)
                        .param("size", "-1"))
                .andDo(print())
                .andExpect(status().isConflict());

        mvc.perform(get(PATH)
                        .param("size", "0"))
                .andDo(print())
                .andExpect(status().isConflict());

        mvc.perform(get(PATH)
                        .param("states", "asdf"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}