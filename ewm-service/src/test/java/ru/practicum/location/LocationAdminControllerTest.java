package ru.practicum.location;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.service.LocationService;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.location.enums.LocationType.OUTDOOR;

@WebMvcTest(controllers = LocationAdminController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LocationAdminControllerTest {
    private static final String PATH = "/admin/locations";

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private LocationService service;

    @Autowired
    private MockMvc mvc;

    private LocationFullDto locationFullDto;
    private NewLocationDto newLocationDto;
    private LocationFullDto.LocationFullDtoBuilder builder;
    private NewLocationDto.NewLocationDtoBuilder newBuilder;


    @BeforeEach
    void setup() {
        builder = LocationFullDto.builder()
                .id(1L)
                .lat(0f)
                .lon(0f)
                .radius(1)
                .type(OUTDOOR.toString())
                .name("park");

        newBuilder = NewLocationDto.builder()
                .lat(0f)
                .lon(0f)
                .radius(1)
                .type(OUTDOOR.toString())
                .name("park");
    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mvc);
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
                        .param("type", "asdf"))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldGetAll() throws Exception {
        //All null parameters | EmptyList Result
        when(service.getAllByLocationCriteria(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        mvc.perform(get(PATH))
                .andDo(print())
                .andExpect(status().isOk());

        //All null parameters | SingleList Result
        locationFullDto = builder.build();
        when(service.getAllByLocationCriteria(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(locationFullDto));
        String json = mapper.writeValueAsString(List.of(locationFullDto));
        mvc.perform(get(PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));

        //Regular case | SingleList Result
        mvc.perform(get(PATH)
                        .param("lat", "0")
                        .param("lon", "0")
                        .param("radius", "1")
                        .param("from", "0")
                        .param("size", "1000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    @Test
    void shouldFailAdd() throws Exception {
        //Fail validation lat
        newLocationDto = newBuilder
                .lat(-1f)
                .build();
        String json = mapper.writeValueAsString(newLocationDto);
        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());

        //Fail validation lon
        newLocationDto = newBuilder
                .lat(0f)
                .lon(-1f)
                .build();
        json = mapper.writeValueAsString(newLocationDto);
        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());

        //Fail validation radius
        newLocationDto = newBuilder
                .lon(0f)
                .radius(-1)
                .build();
        json = mapper.writeValueAsString(newLocationDto);
        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());

        //Fail validation name
        newLocationDto = newBuilder
                .radius(1)
                .name("")
                .build();
        json = mapper.writeValueAsString(newLocationDto);
        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAdd() throws Exception {
        //Regular Case
        newLocationDto = newBuilder.build();
        locationFullDto = builder.build();
        String json = mapper.writeValueAsString(newLocationDto);
        String jsonAdded = mapper.writeValueAsString(locationFullDto);
        when(service.add(any(NewLocationDto.class))).thenReturn(locationFullDto);
        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonAdded));
    }

    @Test
    void shouldFailPatch() throws Exception {
        //Fail Negative
        long id = 1L;
        locationFullDto = builder
                .lat(-1f)
                .build();
        String json = mapper.writeValueAsString(locationFullDto);
        mvc.perform(patch(PATH + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPatchWithNullParameters() throws Exception {
        //Regular Case
        long id = 1L;
        locationFullDto = builder.build();
        LocationFullDto locationFullDtoToUpdate = builder
                .lat(null)
                .lon(null)
                .radius(null)
                .build();
        String json = mapper.writeValueAsString(locationFullDtoToUpdate);
        when(service.patch(anyLong(), any(LocationFullDto.class))).thenReturn(locationFullDto);
        mvc.perform(patch(PATH + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void shouldPatch() throws Exception {
        //Regular Case
        long id = 1L;
        locationFullDto = builder.build();
        String json = mapper.writeValueAsString(locationFullDto);
        when(service.patch(anyLong(), any(LocationFullDto.class))).thenReturn(locationFullDto);
        mvc.perform(patch(PATH + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldDelete() throws Exception {
        doNothing().when(service).delete(anyLong());
        mvc.perform(delete(PATH + "/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFailDelete() throws Exception {
        long id = 1L;
        String message = String.format("Локация с id %d не найдена", id);
        doThrow(new NotFoundException(message)).when(service).delete(anyLong());
        mvc.perform(delete(PATH + "/" + id))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(message), String.class));
    }
}