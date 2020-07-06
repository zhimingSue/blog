package com.amingge.pojo;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Data
public class Catalog {

    @Id //主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增长策略
    private Long id;

    @NotEmpty(message = "名称不能为空")
    @Size(min = 2,max = 30)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String name;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Catalog(User user,String name) {
        this.name = name;
        this.user = user;
    }

    public Catalog() {
    }
}
