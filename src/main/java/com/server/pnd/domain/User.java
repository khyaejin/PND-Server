package com.server.pnd.domain;

import com.server.pnd.util.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER")
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "github_id")
    private String githubId;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "email")
    private String email;

}
