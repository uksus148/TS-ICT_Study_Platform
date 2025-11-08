package org.application.tsiktsemestraljob.demo.Controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.Resources;
import org.application.tsiktsemestraljob.demo.Service.ResourcesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourcesController {
    private final ResourcesService resourcesService;

   @GetMapping
    public List<Resources> findAll() {
       return resourcesService.findAll();
   }

   @GetMapping("/{id}")
    public Resources findById(@PathVariable Long id) {
       return resourcesService.findById(id);
   }

   @PostMapping("/{userId}/{groupId}")
    public Resources create(@PathVariable Long userId, @PathVariable Long groupId, @RequestBody Resources resources) {
       return resourcesService.create(userId, groupId, resources);
   }

   @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
       resourcesService.delete(id);
   }

   @PutMapping("/{id}")
    public Resources update(@PathVariable Long id, @RequestBody Resources resources) {
       return resourcesService.update(id, resources);
   }
}
