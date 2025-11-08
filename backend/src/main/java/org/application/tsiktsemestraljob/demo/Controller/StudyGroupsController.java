package org.application.tsiktsemestraljob.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Service.StudyGroupsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studyGroups")
@RequiredArgsConstructor
public class StudyGroupsController {
    private final StudyGroupsService studyGroupsService;

    @GetMapping
    public List<StudyGroups> getAll() {
        return studyGroupsService.getStudyGroups();
    }

    @PostMapping("/{id}")
    public StudyGroups create(@PathVariable Long id, @RequestBody StudyGroups studyGroups) {
        return studyGroupsService.create(id, studyGroups);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studyGroupsService.deleteStudyGroups(id);
    }
    @PutMapping("/{id}")
    public StudyGroups update(@PathVariable Long id, @RequestBody StudyGroups studyGroups) {
        return studyGroupsService.updateStudyGroups(id, studyGroups);
    }
}
