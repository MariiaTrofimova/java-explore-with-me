package ru.practicum.util;

import ru.practicum.location.model.SearchArea;

public class LocationSearch {

    public static SearchArea makeSearchArea(Float lat, Float lon, Integer radius) {
        SearchArea searchArea = null;
        if (lat != null && lon != null && radius != null) {
            searchArea = SearchArea.builder()
                    .lat(lat)
                    .lon(lon)
                    .radius(radius)
                    .build();
        } else if (lat != null || lon != null || radius != null) {
            throw new IllegalArgumentException("Область поиска должна быть задана тремя параметрами: lat, lon, radius");
        }
        return searchArea;
    }
}