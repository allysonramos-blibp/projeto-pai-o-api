package com.devlapa.o_pai_o.service;

import com.devlapa.o_pai_o.domain.categorias.CategoriaRequestDTO;
import com.devlapa.o_pai_o.domain.categorias.CategoriaResponseDTO;
import com.devlapa.o_pai_o.domain.categorias.Categorias;
import com.devlapa.o_pai_o.domain.produtos.Produtos;
import com.devlapa.o_pai_o.repositories.CategoriasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.xml.crypto.dsig.Reference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class CategoriaServices {
    @Autowired
    CategoriasRepository categorias;

    @Autowired
    GeradordeIdServices geradordeIdServices;

    public Categorias createCategoria(CategoriaRequestDTO data){
        if(categorias.existsByNome(data.nome())){
            throw new RuntimeException("Categoria já existente");
        }
        Categorias newCategoria = new Categorias();
        newCategoria.setNome(data.nome());
        newCategoria.setDescricao(data.descricao());
        return categorias.save(newCategoria);

    }

    public List<CategoriaResponseDTO> getCategorias(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Categorias> categoriasPage = this.categorias.findAll(pageable);
        return categoriasPage.map(event -> new CategoriaResponseDTO((long)Math.toIntExact(event.getId()),
                event.getNome(), event.getDescricao(),event.getData_criacao()))
                .stream().toList();
    }

    public Categorias updateCategoriaFinds(Long id, Map<String, Object>fields) {
        Categorias findCategoria = categorias.findById(id)
                .orElseThrow(() -> new RuntimeException("Categorias não encontrada"));
            fields.forEach((key,value) -> {
                Field field = ReflectionUtils.findField(Categorias.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field,findCategoria,value);
            });
            return categorias.save(findCategoria);
    }

    public void deleteCategoria(Long id) {
        Categorias categoria = categorias.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
        Produtos produtos = new Produtos();

        if(categoria == produtos.getCategoria()){
            throw new RuntimeException("Existem produtos com essa categoria");
        }
        categorias.delete(categoria);
    }
}
