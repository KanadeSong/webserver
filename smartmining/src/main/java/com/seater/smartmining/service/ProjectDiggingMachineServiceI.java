package com.seater.smartmining.service;

import com.seater.smartmining.entity.ProjectDiggingMachine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProjectDiggingMachineServiceI {
     ProjectDiggingMachine get(Long id) throws IOException;
     ProjectDiggingMachine save(ProjectDiggingMachine log) throws IOException;
     void delete(Long id);
     void delete(List<Long> ids);
     Page<ProjectDiggingMachine> query();
     Page<ProjectDiggingMachine> query(Specification<ProjectDiggingMachine> spec);
     Page<ProjectDiggingMachine> query(Pageable pageable);
     Page<ProjectDiggingMachine> query(Specification<ProjectDiggingMachine> spec, Pageable pageable);

    List<ProjectDiggingMachine> queryWx(Specification<ProjectDiggingMachine> spec);
     List<ProjectDiggingMachine> getAll();
     List<ProjectDiggingMachine> getByProjectIdOrderById(Long projectId);

    List<ProjectDiggingMachine> getByProjectIdAndIsVaild(Long projectId, Boolean isVaild);
     ProjectDiggingMachine getByProjectIdAndCode(Long projectId, String code);
     ProjectDiggingMachine getByProjectIdAndUid(Long projectId, String uid);
     ProjectDiggingMachine getAllByUid(String uid);
     Map getAllCountByProjectId(Long projectId);
     void setICCardByDiggingMachineId(Long diggingMachineId, String icCardNumber, Boolean icCardStatus);
     List<ProjectDiggingMachine> getAllByProjectIdAndIsVaildAndSelected(Long projectId, Boolean selected);
     void batchSave(List<ProjectDiggingMachine> projectDiggingMachineList);
    List<String> getAllByProjectIdAndIsVaild(Long projectId, Boolean isVaild);
    void updateSeleted(boolean selected, List<String> machineCodeList);
}
