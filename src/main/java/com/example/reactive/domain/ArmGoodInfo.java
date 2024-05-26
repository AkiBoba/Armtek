package com.example.reactive.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("armtek_goods")
public class ArmGoodInfo {
    @Id
    private Long id;
    @Column("code")
    private String code;
    @Column("article")
    private String article;
    @Column("name")
    private String name;
    @Column("brand")
    private String brand;
    @Column("width") //ширина
    private String width;
    @Column("height") //высота
    private String height;
    @Column("length") //длина
    private String length;
    @Column("weight") //вес
    private String weight;
    @Column("descriptions") //описание
    private String description;
    @Column("other") //Другое
    private String other;
    @Column("url") //url
    private String url;

    @Override
    public String toString() {
        return "ArmGoodInfo{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", article='" + article + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", length='" + length + '\'' +
                ", weight='" + weight + '\'' +
                ", description='" + description + '\'' +
                ", other='" + other + '\'' +
                '}';
    }
}
