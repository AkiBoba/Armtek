package com.example.reactive.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@Table("armtek_good_link_error")
public class GoodsLinksError {
    @Id
    private Long id;
    private String link;

    public GoodsLinksError(String link) {
        this.link = link;
    }
}
