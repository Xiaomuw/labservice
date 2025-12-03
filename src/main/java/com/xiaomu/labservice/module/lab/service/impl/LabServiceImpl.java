package com.xiaomu.labservice.module.lab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaomu.labservice.common.exception.BusinessException;
import com.xiaomu.labservice.common.response.ResultCode;
import com.xiaomu.labservice.module.lab.dto.LabVO;
import com.xiaomu.labservice.module.lab.entity.Lab;
import com.xiaomu.labservice.module.lab.mapper.LabMapper;
import com.xiaomu.labservice.module.lab.service.LabService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 实验室服务实现
 */
@Service
@RequiredArgsConstructor
public class LabServiceImpl implements LabService {

    private final LabMapper labMapper;

    @Override
    public Page<LabVO> getLabList(Long page, Long size, String keyword) {
        Page<Lab> labPage = new Page<>(page, size);
        LambdaQueryWrapper<Lab> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Lab::getName, keyword)
                    .or().like(Lab::getLocation, keyword));
        }
        
        wrapper.orderByDesc(Lab::getCreatedAt);
        Page<Lab> result = labMapper.selectPage(labPage, wrapper);
        
        Page<LabVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<LabVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public LabVO getLabById(Long id) {
        Lab lab = labMapper.selectById(id);
        if (lab == null) {
            throw new BusinessException(ResultCode.LAB_NOT_FOUND);
        }
        return convertToVO(lab);
    }

    @Override
    @Transactional
    public LabVO createLab(LabVO labVO) {
        Lab lab = new Lab();
        BeanUtils.copyProperties(labVO, lab);
        labMapper.insert(lab);
        return convertToVO(lab);
    }

    @Override
    @Transactional
    public LabVO updateLab(Long id, LabVO labVO) {
        Lab lab = labMapper.selectById(id);
        if (lab == null) {
            throw new BusinessException(ResultCode.LAB_NOT_FOUND);
        }
        BeanUtils.copyProperties(labVO, lab);
        lab.setId(id);
        labMapper.updateById(lab);
        return convertToVO(lab);
    }

    @Override
    @Transactional
    public void deleteLab(Long id) {
        Lab lab = labMapper.selectById(id);
        if (lab == null) {
            throw new BusinessException(ResultCode.LAB_NOT_FOUND);
        }
        labMapper.deleteById(id);
    }

    private LabVO convertToVO(Lab lab) {
        LabVO vo = new LabVO();
        BeanUtils.copyProperties(lab, vo);
        return vo;
    }
}

