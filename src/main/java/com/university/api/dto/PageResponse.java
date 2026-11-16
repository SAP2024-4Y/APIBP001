package com.university.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data;
    @JsonProperty("next_cursor")
    private String nextCursor;
    @JsonProperty("has_more")
    private boolean hasMore;
    private int count;
}
