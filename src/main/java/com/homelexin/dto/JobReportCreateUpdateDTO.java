package com.homelexin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobReportCreateUpdateDTO {

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Work performed is required")
    private String workPerformed;

    private String materials;

    private String observations;

    private String recommendations;
}
