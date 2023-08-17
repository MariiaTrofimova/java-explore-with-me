package ru.practicum.location.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.location.enums.LocationType.OUTDOOR;

@JsonTest
class LocationFullDtoTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JacksonTester<LocationFullDto> json;

    private LocationFullDto locationFullDto;

    @BeforeEach
    public void setup() {
        locationFullDto = LocationFullDto.builder()
                .id(1L)
                .lat(0f)
                .lon(0f)
                .radius(1)
                .type(OUTDOOR.toString())
                .name("park")
                .build();
    }

    @Test
    void testLocationSerialization() throws Exception {
        JsonContent<LocationFullDto> result = json.write(locationFullDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(locationFullDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.type").isEqualTo(locationFullDto.getType());
    }


    @Test
    void testLocationDeserialization() throws Exception {
        String jsonContent = mapper.writeValueAsString(locationFullDto);
        LocationFullDto result = this.json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo(locationFullDto.getName());
    }
}