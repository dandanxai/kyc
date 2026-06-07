package cn.kyc.dandanxia.module.infra.controller.app.file;

import cn.hutool.core.io.IoUtil;
import cn.kyc.dandanxia.framework.common.pojo.CommonResult;
import cn.kyc.dandanxia.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import cn.kyc.dandanxia.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import cn.kyc.dandanxia.module.infra.controller.app.file.vo.AppFileUploadReqVO;
import cn.kyc.dandanxia.module.infra.service.file.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.kyc.dandanxia.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class AppFileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    @Parameter(name = "file", description = "文件附件", required = true,
            schema = @Schema(type = "string", format = "binary"))
    @PermitAll
    public CommonResult<String> uploadFile(@Valid AppFileUploadReqVO uploadReqVO) throws Exception {
//        MultipartFile file = uploadReqVO.getFile();
//        byte[] content = IoUtil.readBytes(file.getInputStream());
//        return success(fileService.createFile(content, file.getOriginalFilename(),
//                uploadReqVO.getDirectory(), file.getContentType()));
        MultipartFile file = uploadReqVO.getFile();
        byte[] content = IoUtil.readBytes(file.getInputStream());

        // 1. 获取原始文件名和后缀 (例如: 直聘简历-未命名.pdf)
        String originalFilename = file.getOriginalFilename();
        String suffix = cn.hutool.core.io.FileUtil.extName(originalFilename);

        // 2. 🚀【核心指纹清洗】：利用时间戳 + 唯一分布式UUID，组合成在阿里云上独一无二的存储Path
        // 比如洗完后在阿里云叫: 1717614000_a1b2c3d4e5f6.pdf
        String uniquePath = cn.hutool.core.date.DateUtil.current() + "_"
                + cn.hutool.core.util.IdUtil.fastSimpleUUID() + "." + suffix;

        // 3. 稳稳砸向阿里云 OSS，此时 path 绝不冲突，但内部表的 name 字段依旧会保留用户的 originalFilename
        return success(fileService.createFile(content, uniquePath,
                uploadReqVO.getDirectory(), file.getContentType()));
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址（上传）", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @Parameters({
            @Parameter(name = "name", description = "文件名称", required = true),
            @Parameter(name = "directory", description = "文件目录")
    })
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(
            @RequestParam("name") String name,
            @RequestParam(value = "directory", required = false) String directory) {
        return success(fileService.presignPutUrl(name, directory));
    }

    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @PermitAll
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        return success(fileService.createFile(createReqVO));
    }

}
