package com.example.reactive.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("alliance_goods3")
public class GoodInfo {
    @Id
    private Long id;
    @Column("code")
    private String code;
    @Column("article")
    private String article;
    @Column("name")
    private String name;
    @Column("description")
    private String description;
    @Column("width")
    private String width;
    @Column("height")
    private String height;
    @Column("length")
    private String length;
    @Column("weight")
    private String weight;

    public GoodInfo(String article, String name, String description) {
        this.article = article;
        this.name = name;
        this.description = description;
    }

    public GoodInfo(String code, String article, String name, String description, String width, String height, String length, String weight) {
        this.code = code;
        this.article = article;
        this.name = name;
        this.description = description;
        this.width = width;
        this.height = height;
        this.length = length;
        this.weight = weight;
    }
}
