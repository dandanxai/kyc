package cn.kyc.dandanxia.module.member.controller.app.resume;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.kyc.dandanxia.framework.common.pojo.PageParam;
import cn.kyc.dandanxia.framework.common.pojo.PageResult;
import cn.kyc.dandanxia.framework.common.pojo.CommonResult;
import cn.kyc.dandanxia.framework.common.util.object.BeanUtils;
import static cn.kyc.dandanxia.framework.common.pojo.CommonResult.success;

import cn.kyc.dandanxia.framework.excel.core.util.ExcelUtils;

import cn.kyc.dandanxia.framework.apilog.core.annotation.ApiAccessLog;
import static cn.kyc.dandanxia.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.kyc.dandanxia.module.member.controller.app.resume.vo.*;
import cn.kyc.dandanxia.module.member.dal.dataobject.resume.ResumeDO;
import cn.kyc.dandanxia.module.member.service.resume.ResumeService;

@Tag(name = "用户 APP - 用户简历档案")
@RestController
@RequestMapping("/member/resume")
@Validated
public class AppResumeController {

    @Resource
    private ResumeService resumeService;

    @PostMapping("/create")
    @Operation(summary = "创建用户简历档案")
    public CommonResult<Long> createResume(@Valid @RequestBody AppResumeSaveReqVO createReqVO) {
        return success(resumeService.createResume(createReqVO));
    }

    @PutMapping("/update-active")
    @Operation(summary = "更新用户简历档案")
    public CommonResult<Boolean> updateResumeActive(@Valid @RequestBody AppResumeUpdateActiveReqVO updateActiveReqVO) {
        resumeService.updateResumeActive(updateActiveReqVO);
        return success(true);
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户简历档案")
    public CommonResult<Boolean> updateResume(@Valid @RequestBody AppResumeSaveReqVO updateReqVO) {
        resumeService.updateResume(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户简历档案")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteResume(@RequestParam("id") Long id) {
        resumeService.deleteResume(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除用户简历档案")
    public CommonResult<Boolean> deleteResumeList(@RequestParam("ids") List<Long> ids) {
        resumeService.deleteResumeListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户简历档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppResumeRespVO> getResume(@RequestParam("id") Long id) {
        ResumeDO resume = resumeService.getResume(id);
        return success(BeanUtils.toBean(resume, AppResumeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户简历档案分页")
    public CommonResult<PageResult<AppResumeRespVO>> getResumePage(@Valid AppResumePageReqVO pageReqVO) {
        PageResult<ResumeDO> pageResult = resumeService.getResumePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppResumeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户简历档案 Excel")
    @ApiAccessLog(operateType = EXPORT)
    public void exportResumeExcel(@Valid AppResumePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ResumeDO> list = resumeService.getResumePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户简历档案.xls", "数据", AppResumeRespVO.class,
                        BeanUtils.toBean(list, AppResumeRespVO.class));
    }

}
