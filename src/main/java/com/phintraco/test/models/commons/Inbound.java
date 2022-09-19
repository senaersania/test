package com.phintraco.test.models.commons;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "inbound")
@Data
public class Inbound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Size(max = 100)
    @Column(name = "channel")
    private String channel;
    @Size(max = 256)
    @Column(name = "content")
    private String content;
    @Size(max = 100)
    @Column(name = "type")
    private String type;
}
