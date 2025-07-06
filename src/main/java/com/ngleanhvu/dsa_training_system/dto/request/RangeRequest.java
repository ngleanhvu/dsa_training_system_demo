package com.ngleanhvu.dsa_training_system.dto.request;

import lombok.Data;

@Data
public class RangeRequest<T> {
    private T from;
    private T to;
}
