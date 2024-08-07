package com.server.pnd.diagram.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagramRequestDto {
    private Long repoId; // repo ID
}
