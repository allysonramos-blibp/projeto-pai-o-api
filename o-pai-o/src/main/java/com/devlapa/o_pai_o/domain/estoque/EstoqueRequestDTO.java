package com.devlapa.o_pai_o.domain.estoque;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EstoqueRequestDTO(
        Long produtoId,
        @NotNull @Positive Integer quantidade,
        @NotNull @Positive Integer minimo
) {}