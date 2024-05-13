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
@Table("armtek_good_link")
public class ArmatekGoodLink implements Comparable<ArmatekGoodLink> {

    @Id
    private Long id;
    private String link;
    @Column("parent_link")
    private String parentLink;

    public ArmatekGoodLink(String link, String parentLink) {
        this.link = link;
        this.parentLink = parentLink;
    }

    @Override
    public int compareTo(ArmatekGoodLink otherArmLink) {
        return this.link.compareTo(otherArmLink.getLink());
    }
}
