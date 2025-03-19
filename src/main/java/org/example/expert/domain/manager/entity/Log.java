package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String record;
    private LocalDateTime createdAt;

    public Log(String record, LocalDateTime createdAt) {
        this.record = record;
        this.createdAt = createdAt;
    }
}
