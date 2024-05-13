package com.example.reactive.domain;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GoodInfoDTO {
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
}
