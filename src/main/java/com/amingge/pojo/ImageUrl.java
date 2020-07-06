package com.amingge.pojo;

import javax.persistence.*;

@Entity
public class ImageUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String imgUrl;
}
