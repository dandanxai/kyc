package cn.kyc.dandanxia.module.member.controller.app.position;

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

import cn.kyc.dandanxia.module.member.controller.app.position.vo.*;
import cn.kyc.dandanxia.module.member.dal.dataobject.position.PositionDO;
import cn.kyc.dandanxia.module.member.service.position.PositionService;

@Tag(name = "用户 APP - 企业岗位招聘档案")
@RestController
@RequestMapping("/member/position")
@Validated
public class AppPositionController {

    @Resource
    private PositionService positionService;

    @PostMapping("/create")
    @Operation(summary = "创建企业岗位招聘档案")
    public CommonResult<Long> createPosition(@Valid @RequestBody AppPositionSaveReqVO createReqVO) {
        return success(positionService.createPosition(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新企业岗位招聘档案")
    public CommonResult<Boolean> updatePosition(@Valid @RequestBody AppPositionSaveReqVO updateReqVO) {
        positionService.updatePosition(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除企业岗位招聘档案")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deletePosition(@RequestParam("id") Long id) {
        positionService.deletePosition(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除企业岗位招聘档案")
    public CommonResult<Boolean> deletePositionList(@RequestParam("ids") List<Long> ids) {
        positionService.deletePositionListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得企业岗位招聘档案")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppPositionRespVO> getPosition(@RequestParam("id") Long id) {
        PositionDO position = positionService.getPosition(id);
        return success(BeanUtils.toBean(position, AppPositionRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得企业岗位招聘档案分页")
    public CommonResult<PageResult<AppPositionRespVO>> getPositionPage(@Valid AppPositionPageReqVO pageReqVO) {
        PageResult<PositionDO> pageResult = positionService.getPositionPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppPositionRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出企业岗位招聘档案 Excel")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPositionExcel(@Valid AppPositionPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PositionDO> list = positionService.getPositionPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "企业岗位招聘档案.xls", "数据", AppPositionRespVO.class,
                        BeanUtils.toBean(list, AppPositionRespVO.class));
    }

}