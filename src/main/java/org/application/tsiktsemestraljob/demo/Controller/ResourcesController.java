package org.application.tsiktsemestraljob.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.DTO.ResourcesDTO.ResourcesMapper;
import org.application.tsiktsemestraljob.demo.DTO.ResourcesDTO.ResourcesRequestDTO;
import org.application.tsiktsemestraljob.demo.DTO.ResourcesDTO.ResourcesResponseDTO;
import org.application.tsiktsemestraljob.demo.Entities.Resources;
import org.application.tsiktsemestraljob.demo.Service.ResourcesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourcesController {
    private final ResourcesService resourcesService;

    @GetMapping("/group/{groupId}")
    public List<ResourcesResponseDTO> getResourcesByGroup(@PathVariable Long groupId) {
        return resourcesService.getResourcesByGroup(groupId)
                .stream()
                .map(ResourcesMapper::toDto)
                .toList();
    }

   @GetMapping
    public List<ResourcesResponseDTO> findAll() {
       return resourcesService.findAll()
               .stream()
               .map(ResourcesMapper::toDto)
               .toList();
   }

   @GetMapping("/{id}")
    public ResourcesResponseDTO findById(@PathVariable Long id) {
       Resources resources = resourcesService.findById(id);
       return ResourcesMapper.toDto(resources);
   }

   @PostMapping("/{groupId}")
    public ResourcesResponseDTO create(@PathVariable Long groupId, @RequestBody ResourcesRequestDTO dto) {
       Resources resources = ResourcesMapper.toEntity(dto);
       return ResourcesMapper.toDto(resourcesService.create(groupId, resources));
   }

   @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
       resourcesService.delete(id);
   }

   @PutMapping("/{id}")
    public ResourcesResponseDTO update(@PathVariable Long id, @RequestBody ResourcesRequestDTO dto) {
       Resources resources = ResourcesMapper.toEntity(dto);
       return ResourcesMapper.toDto(resourcesService.update(id, resources));
   }
}
