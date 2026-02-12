package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.*;
import com.example.webbansach_backend.Enum.DoiTuongApDungMa;
import com.example.webbansach_backend.Enum.TrangThaiMaGiamGia;
import com.example.webbansach_backend.Repository.*;
import com.example.webbansach_backend.dto.*;
import com.example.webbansach_backend.exception.NotFoundException;
import com.example.webbansach_backend.exception.OutOfStockException;
import com.example.webbansach_backend.exception.VoucherStateException;
import com.example.webbansach_backend.mapper.MaGiamGiaMapper;
import com.example.webbansach_backend.service.MaGiamGiaService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MaGiamGiaServiceImpl implements MaGiamGiaService {
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;

    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private ModelMapper modelMapper ;
    @Autowired
    private MaGiamGiaNguoiDungRepository maGiamGiaNguoiDungRepository ;
    //
    @Autowired
    private MaGiamGiaRepository maGiamGiaRepository ;
    @Autowired
    private MaGiamGiaMapper maGiamGiaMapper ;
    @Autowired
    private MaGiamGiaSachRepository maGiamGiaSachRepository ;

    // chỗ này chú ý rằng nó sẽ có mã như 1 chuỗi ký tự . VD : "1,2,3" . khi lấy phải trùng y hệt "1,2,3" thì ms lấy đc .
    // "1" hay "2,3" cx k bao giừo lấy đc . vì nó có dạng chuỗi để so khớp cho nhanh nhất có thể
    @Cacheable(
            value = "maGiamGiaSach" ,
            key = "#danhSachMaSach.stream().sorted().collect(T(java.util.stream.Collectors).joining(','))"
    )
    @Override
    public List<MaGiamGiaResponeDTO> getMaGiamGiaCuaSach(List<Integer> danhSachMaSach) {
        List<Sach> saches = sachRepository.findByMaSachInFetch(danhSachMaSach) ;
        if(saches.isEmpty()) throw new NotFoundException("Sách không tồn tại") ;
        List<MaGiamGiaResponeDTO> maGiamGiaResponeDTOS = new ArrayList<>() ;
        // láy mã giảm giá theo sách
        for(Sach sach : saches){

            Set<MaGiamGiaSach> maGiamGiaSaches = sach.getMaGiamGiaSaches() ;
            MaGiamGiaResponeDTO maGiamGiaResponeDTO = new MaGiamGiaResponeDTO() ;
            maGiamGiaResponeDTO.setMaSach(sach.getMaSach());
            List<MaGiamGiaCuaSachRespone> maGiamGiaCuaSachRespones = new ArrayList<>() ;
            for(MaGiamGiaSach maGiamGiaSach : maGiamGiaSaches){
                if(maGiamGiaSach.getMaGiamGia().getSoLuong() > maGiamGiaSach.getMaGiamGia().getSoMaDaDung()
                   && maGiamGiaSach.getMaGiamGia().getTrangThaiMaGiamGia() == TrangThaiMaGiamGia.DANG_HOAT_DONG){
                    MaGiamGiaCuaSachRespone maGiamGiaCuaSachRespone =maGiamGiaMapper.toDTO(maGiamGiaSach.getMaGiamGia()) ;
                    maGiamGiaCuaSachRespones.add(maGiamGiaCuaSachRespone) ;
                }
            }
            maGiamGiaResponeDTO.setMaGiamGiaCuaSachRespones(maGiamGiaCuaSachRespones);
            maGiamGiaResponeDTOS.add(maGiamGiaResponeDTO) ;
        }


       return maGiamGiaResponeDTOS ;

    }

    //
    @Cacheable(
            value = "maGiamGiaUser" ,
            key = "#tenDangNhap"
    )
    @Override
    public MaGiamGiaUserResponeDTO getMaGiamGiaCuaNguoiDung(String tenDangNhap) {

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        Set<MaGiamGiaNguoiDung> maGiamGiaNguoiDungs = nguoiDung.getMaGiamGiaNguoiDungs() ;
        List<MaGiamGiaCuaUserResponeDTO> maGiamGiaCuaUserResponeDTOS = new ArrayList<>() ;
        MaGiamGiaUserResponeDTO maGiamGiaUserResponeDTO = new MaGiamGiaUserResponeDTO() ;
        for(MaGiamGiaNguoiDung maGiamGiaNguoiDung : maGiamGiaNguoiDungs){
            // filter ở đây đẻe lấy lên những mã còn khả dụng
           if(maGiamGiaNguoiDung.getDaDung() < maGiamGiaNguoiDung.getLuotDungToiDa()
                   && maGiamGiaNguoiDung.getMaGiamGia().getTrangThaiMaGiamGia()== TrangThaiMaGiamGia.DANG_HOAT_DONG){
               MaGiamGiaCuaUserResponeDTO maGiamGiaCuaUserResponeDTO = modelMapper.map(maGiamGiaNguoiDung.getMaGiamGia() , MaGiamGiaCuaUserResponeDTO.class) ;
               maGiamGiaCuaUserResponeDTO.setMaGiamNguoiDung(maGiamGiaNguoiDung.getMaGiamNguoiDung());
               maGiamGiaCuaUserResponeDTO.setDaDung(maGiamGiaNguoiDung.getDaDung());
               maGiamGiaCuaUserResponeDTO.setLuotDungToiDa(maGiamGiaNguoiDung.getLuotDungToiDa());

               maGiamGiaCuaUserResponeDTOS.add(maGiamGiaCuaUserResponeDTO) ;
           }
        }
        maGiamGiaUserResponeDTO.setMaNguoiDung(nguoiDung.getMaNguoiDung());
        maGiamGiaUserResponeDTO.setMaGiamGiaCuaUserResponeDTOS(maGiamGiaCuaUserResponeDTOS);

        return maGiamGiaUserResponeDTO ;

    }
    //xóa mã giảm gía khỏi cache khi nó đc đc dùng
    @CacheEvict(
            value = "maGiamGiaUser" ,
            key = "#tenDangNhap"
    )
    public void dungMaGiamGiaUser(String tenDangNhap , int maGiamNguoiDung){
        MaGiamGiaNguoiDung maGiamGiaNguoiDung = maGiamGiaNguoiDungRepository.findByMaGiamNguoiDungAndNguoiDung_TenDangNhap(maGiamNguoiDung , tenDangNhap).orElseThrow() ;
        if(maGiamGiaNguoiDung.getDaDung() == maGiamGiaNguoiDung.getLuotDungToiDa()) {
            throw new OutOfStockException("Bạn đã dùng hết lượt giảm");
        }
        // nêu k lock tại đây thì sẽ sảy ra vấn đề lỗi và bug nguy g=hiểm về mã vượt quá số lượng tối đa
        // dẫn đến 1 số tình huống hết mã nhưng vẫn áp mã giảm giá được => lõi nguy hiểm

        MaGiamGia maGiamGia = maGiamGiaRepository.findByMaGiamForUpdate(maGiamGiaNguoiDung.getMaGiamGia().getMaGiam()).orElseThrow() ;

        if(maGiamGia.getTrangThaiMaGiamGia()== TrangThaiMaGiamGia.KHOA
                || maGiamGia.getTrangThaiMaGiamGia()== TrangThaiMaGiamGia.HET_HAN  ){
            throw new VoucherStateException("mã giảm giá đã đóng") ;
        }
        if(maGiamGia.getSoMaDaDung() >= maGiamGia.getSoLuong()){
            throw new OutOfStockException("mã đã dùng hết") ;
        }

        // nếu k rơi vào 3 trường hợp trên thì tăng số lượng mã đã dùng lên
        maGiamGiaNguoiDung.setDaDung(maGiamGiaNguoiDung.getDaDung() + 1);
        maGiamGia.setSoMaDaDung(maGiamGia.getSoMaDaDung() + 1);
        if(maGiamGia.getSoMaDaDung() >= maGiamGia.getSoLuong()){
            maGiamGia.setTrangThaiMaGiamGia(TrangThaiMaGiamGia.KHOA);
        }

    }
   @Caching(
        evict = {
                @CacheEvict(value = "maGiamGiaSach" , allEntries = true , condition = "#maGiamGiaRequestDTO.doiTuongApDungMa == T(com.example.webbansach_backend.Enum.DoiTuongApDungMa).SACH") ,
                @CacheEvict(value = "maGiamGiaUser" , allEntries = true , condition = "#maGiamGiaRequestDTO.doiTuongApDungMa == T(com.example.webbansach_backend.Enum.DoiTuongApDungMa).NGUOI_DUNG")
        }
   )
   @Override
    public void themMaGiamGia(MaGiamGiaRequestDTO maGiamGiaRequestDTO){
        MaGiamGia maGiamGia = maGiamGiaMapper.toEntity(maGiamGiaRequestDTO) ;


        if(maGiamGiaRequestDTO.getDoiTuongApDungMa()== DoiTuongApDungMa.SACH){
            if(maGiamGiaRequestDTO.getDanhSachMaSach() != null && !maGiamGiaRequestDTO.getDanhSachMaSach().isEmpty() ){
                maGiamGiaRepository.save(maGiamGia) ;
                List<Sach> saches = sachRepository.findByMaSachIn(maGiamGiaRequestDTO.getDanhSachMaSach()) ;
                if(saches.isEmpty() || saches.size()!= maGiamGiaRequestDTO.getDanhSachMaSach().size()) throw new NotFoundException("Sách không tồn tại / thiếu sách") ;
                List<MaGiamGiaSach> maGiamGiaSaches = new ArrayList<>() ;
                for(Sach sach : saches ){
                    MaGiamGiaSach maGiamGiaSach = new MaGiamGiaSach() ;
                    maGiamGiaSach.setSach(sach);
                    maGiamGiaSach.setMaGiamGia(maGiamGia);
                    maGiamGiaSaches.add(maGiamGiaSach) ;
                }
                maGiamGiaSachRepository.saveAll(maGiamGiaSaches) ;
            }else throw new RuntimeException("Sách chưa được chọn đối với mã giảm giá dành cho sách") ;
        }else if(maGiamGiaRequestDTO.getDoiTuongApDungMa()== DoiTuongApDungMa.THE_LOAI){
            if(maGiamGiaRequestDTO.getMaTheLoai() != null){
                maGiamGiaRepository.save(maGiamGia) ;
            }else throw new RuntimeException("ma thể loại bị null") ;

        }else if(maGiamGiaRequestDTO.getDoiTuongApDungMa() == DoiTuongApDungMa.NGUOI_DUNG){
            maGiamGiaRepository.save(maGiamGia) ;
        }else if(maGiamGiaRequestDTO.getDoiTuongApDungMa() == DoiTuongApDungMa.TOAN_HE_THONG){
            maGiamGiaRepository.save(maGiamGia) ;
        }

    }

    //xóa cache toàn bộ
    @CacheEvict(
            value = "maGiamGiaSach" ,
            allEntries = true
    )
    @Override
    public void capNhatMaGiamGiaSach(int maGiam , UpdateMaGiamGiaDTO updateMaGiamGiaDTO){
        MaGiamGia maGiamGia =  maGiamGiaRepository.findByMaGiam(maGiam).orElseThrow(()-> new NotFoundException("mã giảm giá không tồn tại")) ;

        if(updateMaGiamGiaDTO.getSoLuong() !=null){
            if(updateMaGiamGiaDTO.getSoLuong() < maGiamGia.getSoMaDaDung()){
                throw new RuntimeException("KHông hợp lệ do đã có số lượng mã dùng lớn hơn so với sô lượng cập nhật") ;
            }
            maGiamGia.setSoLuong(updateMaGiamGiaDTO.getSoLuong());
        }
        if(updateMaGiamGiaDTO.getNgayHetHan()!=null){
            if(updateMaGiamGiaDTO.getNgayHetHan().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Không hợp lệ khi mã vẫn đang còn hạn") ;
            }
            maGiamGia.setNgayHetHan(updateMaGiamGiaDTO.getNgayHetHan());
        }
        if(updateMaGiamGiaDTO.getDonGiaTu()!=null){
            maGiamGia.setDonGiaTu(updateMaGiamGiaDTO.getDonGiaTu());
        }
        if(updateMaGiamGiaDTO.getGiamToiDa()!=null){
            maGiamGia.setGiamToiDa(updateMaGiamGiaDTO.getGiamToiDa());
        }
    }

    @Scheduled(cron ="0 * * * * ?" )
    public void updateVoucherStatusAuto(){
        System.out.println("Vừa mới chạy lịch trình hạn vé");
        List<MaGiamGia> maHetHans = maGiamGiaRepository.findByTrangThaiHoatDongAndNgayHetHan(TrangThaiMaGiamGia.DANG_HOAT_DONG , LocalDateTime.now()) ;

        for(MaGiamGia maGiamGia : maHetHans){
            maGiamGia.setTrangThaiMaGiamGia(TrangThaiMaGiamGia.HET_HAN);
        }

    }
}

// caching : jeets hợp nhièu anotation cache : cacheevict , cachable ....
// cacheput : chạy method rồi cập nhật cache
// unless : điều kiện sau khi chạy method : unless ="# " ,
// condition : điều kiện trước , tùy vào từng @ để sử dùng phù phù hợp
