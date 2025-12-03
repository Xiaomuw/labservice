package com.xiaomu.labservice.module.repair.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.module.repair.dto.RepairCreateDTO;
import com.xiaomu.labservice.module.repair.dto.RepairVO;

/**
 * 报修服务接口
 */
public interface RepairService {
    Page<RepairVO> getMyRepairs(Long page, Long size, String status);
    RepairVO getRepairById(Long id);
    RepairVO createRepair(RepairCreateDTO createDTO);
    RepairVO updateRepair(Long id, RepairCreateDTO updateDTO);
    void cancelRepair(Long id);
    void uploadImage(Long repairId, String imageUrl);
    Page<RepairVO> getAllRepairs(Long page, Long size, String status);
    void processRepair(Long id);
    void resolveRepair(Long id, String resolveNote);
    void closeRepair(Long id);
}

