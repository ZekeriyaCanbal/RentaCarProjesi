package com.lecture.carrental.domain;

import com.lecture.carrental.domain.enumeration.UserRole;
import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UserRole name;

    @Override
    public String toString() {
        return "" + name + '}';
    }
}
