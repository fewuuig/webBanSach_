package com.example.webbansach_backend.mapper;

import com.example.webbansach_backend.Entity.MaGiamGia;
import com.example.webbansach_backend.dto.MaGiamGiaCuaUserResponeDTO;
import com.example.webbansach_backend.dto.voucher.UpdateVoucherDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring"  , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MaGiamGiaUserMapper {
    @Mapping(target = "maGiamNguoiDung" , ignore = true)
    @Mapping(target = "daDung", ignore = true)
    MaGiamGiaCuaUserResponeDTO toDto (MaGiamGia maGiamGia) ;

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVoucherFromDTO(UpdateVoucherDTO updateVoucherDTO, @MappingTarget MaGiamGia maGiamGia) ;
}
