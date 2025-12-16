package com.playersync.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
/*
 * TODO:
 * - Remove explicit getters and setters since Lombok is used
 * -
 * -
 * -
 * -
 */
@Entity
@Table(name = "tags")
@Getter @Setter
public class Tag {
    @Id
    private Long id;
    private String name;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}