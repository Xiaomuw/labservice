package com.xiaomu.labservice.module.lab.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.module.lab.dto.LabVO;

/**
 * 实验室服务接口
 */
public interface LabService {
    Page<LabVO> getLabList(Long page, Long size, String keyword);
    LabVO getLabById(Long id);
    LabVO createLab(LabVO labVO);
    LabVO updateLab(Long id, LabVO labVO);
    void deleteLab(Long id);
}

