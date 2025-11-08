package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupsService {
    private final StudyGroupsRepository studyGroupsRepository;
    private final UserService userService;

    public StudyGroups create(Long id ,StudyGroups group) {
        User creator = userService.getUserById(id);
        group.setCreatedBy( creator );
        group.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return studyGroupsRepository.save(group);
    }

    public List<StudyGroups> getStudyGroups() {
        return studyGroupsRepository.findAll();
    }

    public void deleteStudyGroups(Long id) {
        studyGroupsRepository.deleteById(id);
    }

    public StudyGroups updateStudyGroups(Long id, StudyGroups newStudyGroups) {
        studyGroupsRepository.deleteById(id);
        return studyGroupsRepository.save(newStudyGroups);
    }
}
