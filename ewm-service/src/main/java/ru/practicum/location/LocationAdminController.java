package ru.practicum.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.location.dto.LocationFullDto;
import ru.practicum.location.dto.NewLocationDto;
import ru.practicum.location.enums.LocationType;
import ru.practicum.location.service.LocationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.error.util.ErrorMessages.FROM_ERROR_MESSAGE;
import static ru.practicum.error.util.ErrorMessages.SIZE_ERROR_MESSAGE;

@RestController
@RequestMapping("/admin/locations")
@Slf4j
@Validated
@RequiredArgsConstructor
public class LocationAdminController {
    private final LocationService service;

    @GetMapping
    public List<LocationFullDto> getAll(@PositiveOrZero(message = "Широта не может быть отрицательной")
                                        @RequestParam(required = false) Float lat,
                                        @PositiveOrZero(message = "Долгота не может быть отрицательной")
                                        @RequestParam(required = false) Float lon,
                                        @PositiveOrZero(message = "Радиус поиска должен быть положительным")
                                        @RequestParam(required = false) Integer radius,
                                        @RequestParam(required = false, name = "type") String type,
                                        @PositiveOrZero(message = FROM_ERROR_MESSAGE)
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @Positive(message = SIZE_ERROR_MESSAGE)
                                        @RequestParam(defaultValue = "10") Integer size) {
        if (type != null) {
            LocationType.from(type).orElseThrow(() ->
                    new IllegalArgumentException("Unknown state: " + type));
        }
        return service.getAllByLocationCriteria(lat, lon, radius, type, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationFullDto add(@Valid @RequestBody NewLocationDto newLocationDto) {
        return service.add(newLocationDto);
    }

    @PatchMapping("/{locId}")
    public LocationFullDto patch(@PathVariable long locId,
                                 @Valid @RequestBody LocationFullDto locationFullDto) {
        return service.patch(locId, locationFullDto);
    }

    @DeleteMapping("/{locId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long locId) {
        service.delete(locId);
    }
}