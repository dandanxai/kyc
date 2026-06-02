package cn.kyc.dandanxia.module.member.controller.app.auth;

import cn.kyc.dandanxia.module.member.controller.app.auth.vo.*;
import cn.kyc.dandanxia.module.member.dal.dataobject.enterpriseinfo.EnterpriseInfoDO;
import cn.kyc.dandanxia.module.member.service.auth.MemberAuthService;
import cn.kyc.dandanxia.module.member.service.enterpriseinfo.EnterpriseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.annotation.security.PermitAll;
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


@Slf4j
@Tag(name = "用户 APP - 企业信息")
@RestController
@RequestMapping("/enterprise/auth")
@Validated
public class AppEnterpriseInfoController {



    @Resource
    private MemberAuthService authService;

    @Resource
    private EnterpriseInfoService enterpriseInfoService;

    @PostMapping("/create")
    @PermitAll
    @Operation(summary = "创建企业信息")
    public CommonResult<Long> createEnterpriseInfo(@Valid @RequestBody AppEnterpriseInfoSaveReqVO createReqVO) {
        log.error("注册" , createReqVO.toString());
        return null;
//        return success(enterpriseInfoService.createEnterpriseInfo(createReqVO));
    }

    @PostMapping("/login")
    @Operation(summary = "使用手机 + 密码登录")
    @PermitAll
    public CommonResult<AppAuthLoginRespVO> login(@RequestBody @Valid AppAuthLoginReqVO reqVO) {
        return success(authService.login(reqVO));
    }

//    @PostMapping("/register")
//    @Operation(summary = "注册会员(接收注册对象)")
//    @PermitAll
//    public CommonResult<AppAuthLoginRespVO> register(@RequestBody @Valid AppAuthRegisterReqVO reqVO) {
//        return success(authService.register(reqVO));
//    }



    @PutMapping("/update")
    @Operation(summary = "更新企业信息")
    public CommonResult<Boolean> updateEnterpriseInfo(@Valid @RequestBody AppEnterpriseInfoSaveReqVO updateReqVO) {
        enterpriseInfoService.updateEnterpriseInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除企业信息")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteEnterpriseInfo(@RequestParam("id") Long id) {
        enterpriseInfoService.deleteEnterpriseInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除企业信息")
    public CommonResult<Boolean> deleteEnterpriseInfoList(@RequestParam("ids") List<Long> ids) {
        enterpriseInfoService.deleteEnterpriseInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得企业信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppEnterpriseInfoRespVO> getEnterpriseInfo(@RequestParam("id") Long id) {
        EnterpriseInfoDO enterpriseInfo = enterpriseInfoService.getEnterpriseInfo(id);
        return success(BeanUtils.toBean(enterpriseInfo, AppEnterpriseInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得企业信息分页")
    public CommonResult<PageResult<AppEnterpriseInfoRespVO>> getEnterpriseInfoPage(@Valid AppEnterpriseInfoPageReqVO pageReqVO) {
        PageResult<EnterpriseInfoDO> pageResult = enterpriseInfoService.getEnterpriseInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppEnterpriseInfoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出企业信息 Excel")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEnterpriseInfoExcel(@Valid AppEnterpriseInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EnterpriseInfoDO> list = enterpriseInfoService.getEnterpriseInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "企业信息.xls", "数据", AppEnterpriseInfoRespVO.class,
                        BeanUtils.toBean(list, AppEnterpriseInfoRespVO.class));
    }

}
