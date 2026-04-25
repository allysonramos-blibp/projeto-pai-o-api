    package com.devlapa.o_pai_o.controllers;
    
    import com.devlapa.o_pai_o.domain.usuarios.Usuarios;
    import com.devlapa.o_pai_o.domain.vendas.Vendas;
    import com.devlapa.o_pai_o.domain.vendas.VendasRequestDTO;
    import com.devlapa.o_pai_o.domain.vendas.VendasResponseDTO;
    import com.devlapa.o_pai_o.mapper.VendasMapper;
    import com.devlapa.o_pai_o.repositories.UsuarioRepository;
    import com.devlapa.o_pai_o.service.VendasService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;

    import java.math.BigDecimal;
    import java.util.List;
    import java.util.Map;

    import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

    @RestController
    @RequestMapping("/api/vendas")
    public class VendasController {
    
        @Autowired
        VendasService vendasService;
        @Autowired
        UsuarioRepository userRepository;
    
        @PostMapping
        @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
        public ResponseEntity<VendasResponseDTO> postVendas(@RequestBody @Validated VendasRequestDTO body, @AuthenticationPrincipal Usuarios usuarios){
            VendasResponseDTO newVenda = this.vendasService.createVenda(body,usuarios);
            return ResponseEntity.ok(newVenda);
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
        public ResponseEntity<List<VendasResponseDTO>> listVendas(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "100") int size) {
            List<VendasResponseDTO> allVendas = this.vendasService.getVendas(page, size);
            return ResponseEntity.ok(allVendas);
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
        public ResponseEntity<VendasResponseDTO> getIdVenda(@PathVariable Long id){
            return ResponseEntity.ok(vendasService.getVendaById(id));
        }

        @PatchMapping("/{id}/finalizar")
        @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
        public ResponseEntity<VendasResponseDTO> finalizarVendas(@PathVariable Long id, @RequestBody VendasRequestDTO body){
            try{
                return ResponseEntity.ok(vendasService.finalizarVenda(id,body));
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    
        @PatchMapping("/{id}/cancelar")
        @PreAuthorize("hasAnyRole('ADMIN','GERENTE','USUARIO')")
        public ResponseEntity<String> cancelarVendas(@PathVariable Long id){
            try{
                vendasService.cancelarVendas(id);
                return ResponseEntity.ok("Cancelamento realizado com sucesso!");
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }


        @GetMapping("/total-hoje")
        @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
        public ResponseEntity<BigDecimal> getTotalHoje() {
            return ResponseEntity.ok(vendasService.getTotalHoje());
        }

        @PatchMapping("/{id}/pagamento")
        @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'USUARIO')")
        public  ResponseEntity<VendasResponseDTO> mudarPagamento(
                @PathVariable Long id,
                @RequestBody Map<String, Long> payload) {

            Long novaFormaId = payload.get("formaPagamentosId");

            Vendas vendasAtualizada = vendasService.atualizarFormaPagamento(id, novaFormaId);

            return ResponseEntity.ok(VendasMapper.toDTO(vendasAtualizada));
        }
    }
