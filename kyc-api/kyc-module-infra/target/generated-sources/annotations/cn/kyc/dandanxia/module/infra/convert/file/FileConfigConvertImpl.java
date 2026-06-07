package cn.kyc.dandanxia.module.infra.convert.file;

import cn.kyc.dandanxia.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import cn.kyc.dandanxia.module.infra.dal.dataobject.file.FileConfigDO;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-06T04:26:10+0800",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.14 (Oracle Corporation)"
)
public class FileConfigConvertImpl implements FileConfigConvert {

    @Override
    public FileConfigDO convert(FileConfigSaveReqVO bean) {
        if ( bean == null ) {
            return null;
        }

        FileConfigDO.FileConfigDOBuilder fileConfigDO = FileConfigDO.builder();

        fileConfigDO.id( bean.getId() );
        fileConfigDO.name( bean.getName() );
        fileConfigDO.storage( bean.getStorage() );
        fileConfigDO.remark( bean.getRemark() );

        return fileConfigDO.build();
    }
}
