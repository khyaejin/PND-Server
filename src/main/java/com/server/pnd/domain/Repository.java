package com.server.pnd.domain;

import com.server.pnd.util.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REPOSITORY")
public class Repository extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotEmpty @Column(name = "id")
    private Long id;

    @NotEmpty @Column(name = "user_id")
    private Long userId;

    @NotEmpty @Column(name = "name")
    private String name; // 레포지토리 이름

    @NotEmpty @Column(name = "url")
    private String url; // 레포지토리 URL

    @Column(name = "description")
    private String description; // 레포지토리 설명

}
