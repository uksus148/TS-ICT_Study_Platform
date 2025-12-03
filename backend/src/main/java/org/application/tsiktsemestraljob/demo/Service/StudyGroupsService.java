package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.application.tsiktsemestraljob.demo.Entities.StudyGroups;
import org.application.tsiktsemestraljob.demo.Entities.User;
import org.application.tsiktsemestraljob.demo.Repository.StudyGroupsRepository;
import org.application.tsiktsemestraljob.demo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyGroupsService {
    private final StudyGroupsRepository studyGroupsRepository;
    private final UserRepository userRepository;

    public StudyGroups create(Long userId ,StudyGroups group) {
        User creator = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));
        group.setCreatedBy(creator);
        return studyGroupsRepository.save(group);
    }

    public List<StudyGroups> getStudyGroups() {
        return studyGroupsRepository.findAll();
    }

    public void deleteStudyGroups(Long id) {
        studyGroupsRepository.deleteById(id);
    }

    public StudyGroups updateStudyGroups(Long id, StudyGroups newStudyGroups) {
        StudyGroups studyGroups = studyGroupsRepository.findById(id).orElse(null);
        if (studyGroups == null) {throw new IllegalArgumentException("Study groups not found");}
        if(newStudyGroups.getName() != null) {studyGroups.setName(newStudyGroups.getName());}
        if(newStudyGroups.getCreatedBy() != null) {studyGroups.setCreatedBy(newStudyGroups.getCreatedBy());}
        if(newStudyGroups.getDescription() != null) {studyGroups.setDescription(newStudyGroups.getDescription());}
        return studyGroupsRepository.save(studyGroups);
    }

    public StudyGroups getStudyGroupById(Long id) {
        return studyGroupsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study group not found"));
    }
}
