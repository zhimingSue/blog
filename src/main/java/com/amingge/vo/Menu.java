package com.amingge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String url;
}
