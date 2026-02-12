package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.GioHang;
import com.example.webbansach_backend.Entity.GioHangSach;
import com.example.webbansach_backend.Entity.NguoiDung;
import com.example.webbansach_backend.Entity.Sach;
import com.example.webbansach_backend.Repository.GioHangRepository;
import com.example.webbansach_backend.Repository.GioHangSachRepository;
import com.example.webbansach_backend.Repository.NguoiDungRepository;
import com.example.webbansach_backend.Repository.SachRepository;
import com.example.webbansach_backend.converter.ViewCartConverter;
import com.example.webbansach_backend.dto.AddToCartRequestDTO;
import com.example.webbansach_backend.dto.ViewCartDTO;
import com.example.webbansach_backend.service.CartService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Transactional
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ViewCartConverter viewCartConverter ;
    @Autowired
    private ModelMapper modelMapper ;
    @Autowired
    private GioHangRepository gioHangRepository ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private GioHangSachRepository gioHangSachRepository ;

    // lấy hoặc tạo giỏ hàng
    public GioHang getOrCreateCart(NguoiDung nguoiDung){
        return gioHangRepository.findByNguoiDung(nguoiDung)
                .orElseGet(()->{
                    GioHang gioHang = new GioHang() ;
                    gioHang.setNguoiDung(nguoiDung);
                    return gioHangRepository.save(gioHang) ;
                }) ;
    }

    // thêm sách vào giỏ hàng
    @Override
    public void addToCart(String tenDangNhap , AddToCartRequestDTO addToCartRequestDTO){
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow(()-> new RuntimeException("Người dùng không tồn tại")) ;
        GioHang gioHang = getOrCreateCart(nguoiDung) ;
        Sach sach = sachRepository.findByMaSach(addToCartRequestDTO.getMaSach()).orElseThrow(()->new RuntimeException("Sách không tồn tại")) ;
        // kiểm tra để tránh trường hợp thêm sản phẩm trùng vào giỏ hàng
        Optional<GioHangSach> exsist = gioHang.getGioHangSaches()
                .stream().filter(i->i.getSach().getMaSach()==addToCartRequestDTO.getMaSach()).findFirst() ;
        if(exsist.isPresent()){
            exsist.get().setSoLuong(
                    exsist.get().getSoLuong() + addToCartRequestDTO.getSoLuong()
            );
        }else {
            GioHangSach gioHangSach = new GioHangSach() ;

            gioHangSach.setSoLuong(addToCartRequestDTO.getSoLuong());
            gioHangSach.setSach(sach);
            gioHangSach.setGioHang(gioHang);
            gioHang.getGioHangSaches().add(gioHangSach) ;
        }
        gioHangRepository.save(gioHang) ;
    }
    // xem giỏ hàng
    @Override
    public List<ViewCartDTO> viewCart(String tenDangNhap){
        GioHang gioHang = gioHangRepository.findByNguoiDung_TenDangNhap(tenDangNhap).orElseGet(
                ()->{
                   GioHang gioHang1 = new GioHang() ;
                   NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
                   gioHang1.setNguoiDung(nguoiDung);
                   gioHangRepository.save(gioHang1) ;
                   return gioHang1 ;
                }
        ) ;
        // filter -> DTO
        List<GioHangSach> gioHangSaches = gioHang.getGioHangSaches() ;
        return gioHangSaches.stream().map(viewCartConverter::toViewCart).toList();
    }

    // cập nhật số lượng
    @Override
    public void updateQuantity(String tenDangNhap , int maSach , int soLuong ){
        GioHangSach gioHangSach = gioHangSachRepository.findByGioHang_NguoiDung_TenDangNhapAndSach_MaSach(tenDangNhap , maSach).orElseThrow() ;
        if(soLuong<=0 ){
            gioHangSachRepository.delete(gioHangSach) ;
        }else {
            gioHangSach.setSoLuong(soLuong);
        }

    }
    // xóa 1 quyển sahs khỏi giỏ hàng
    @Override
    public void deleteBook(List<Long> danhSachSanPhamChon){
        if(danhSachSanPhamChon == null || danhSachSanPhamChon.isEmpty()){
            return ;
        }
        gioHangSachRepository.deleteByMaGioHangSachIn(danhSachSanPhamChon);

    }
    // clear sạch sẽ giỏ hàng nhưng k xóa giỏ hàng (làm cho giỏ hàng rỗng)
    @Override
    public void clearBookFromCart(String tenDangNhap){
        GioHang gioHang = gioHangRepository.findByNguoiDung_TenDangNhap(tenDangNhap).orElseThrow(()->new RuntimeException("Khong thấy giỏ hàng")) ;
        gioHang.getGioHangSaches().clear();
    }
    @Override
    public GioHang checkGioHang(String tenDangNhap){
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        return getOrCreateCart(nguoiDung) ;
    }

    @Override
    public List<ViewCartDTO> getSachDatTuGio( List<Long> danhSachSanPhamChon){
        List<GioHangSach> gioHangSaches = gioHangSachRepository.findByMaGioHangSachIn(danhSachSanPhamChon) ;
        return  gioHangSaches.stream().map(viewCartConverter::toViewCart).toList() ;
    }
}
