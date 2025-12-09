package org.application.tsiktsemestraljob.demo.Controller;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Get Resources by grouo",
            description = "This endpoint implement logic that help to get all resources that had a one group"
    )
    @GetMapping("/group/{groupId}")
    public List<ResourcesResponseDTO> getResourcesByGroup(@PathVariable Long groupId) {
        return resourcesService.getResourcesByGroup(groupId)
                .stream()
                .map(ResourcesMapper::toDto)
                .toList();
    }

    @Operation(
            summary = "Find all resources",
            description = "This endpoint help user to find all resources created"
    )
   @GetMapping
    public List<ResourcesResponseDTO> findAll() {
       return resourcesService.findAll()
               .stream()
               .map(ResourcesMapper::toDto)
               .toList();
   }

    @Operation(
            summary = "Find resource by id",
            description = "This endpoint helps to find resource by id"
    )
   @GetMapping("/{id}")
    public ResourcesResponseDTO findById(@PathVariable Long id) {
       Resources resources = resourcesService.findById(id);
       return ResourcesMapper.toDto(resources);
   }

    @Operation(
            summary = "Create resource",
            description = "This POST endpoint help to create an resource "
    )
   @PostMapping("/{groupId}")
    public ResourcesResponseDTO create(@PathVariable Long groupId, @RequestBody ResourcesRequestDTO dto) {
       Resources resources = ResourcesMapper.toEntity(dto);
       return ResourcesMapper.toDto(resourcesService.create(groupId, resources));
   }

    @Operation(
            summary = "Delete Resource",
            description = "This delete endpoint helps to delete a resource by id, he take an id in parameter and " +
                    "returns service method who implements delete by id"
    )
   @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
       resourcesService.delete(id);
   }

    @Operation(
            summary = "Update Resource",
            description = "This endpoint implements an update resource logic, when we need to change a params of resource" +
                    "this endpoint call an service-layer method to update resource data"
    )
   @PutMapping("/{id}")
    public ResourcesResponseDTO update(@PathVariable Long id, @RequestBody ResourcesRequestDTO dto) {
       Resources resources = ResourcesMapper.toEntity(dto);
       return ResourcesMapper.toDto(resourcesService.update(id, resources));
   }
}
