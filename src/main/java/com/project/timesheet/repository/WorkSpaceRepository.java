package com.project.timesheet.repository;

import com.project.timesheet.entity.WorkSpaceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkSpaceRepository extends MongoRepository<WorkSpaceEntity, String> {

    Optional<WorkSpaceEntity> findById(String workSpaceId);

    List<WorkSpaceEntity> findAllByEmail(String email);
}
