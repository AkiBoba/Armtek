package com.example.reactive.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("armtek_cat_link_error")
public class CatLinksErrors {
    @Id
    private Long id;
    private String link;

    public CatLinksErrors(String link) {
        this.link = link;
    }
}
