package com.example.webbansach_backend.service.impl;

import com.example.webbansach_backend.Entity.*;
import com.example.webbansach_backend.Enum.TrangThaiGiaoHang;
import com.example.webbansach_backend.Repository.*;
import com.example.webbansach_backend.dto.*;
import com.example.webbansach_backend.exception.OutOfStockException;
import com.example.webbansach_backend.service.CartService;
import com.example.webbansach_backend.service.OrderService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private GioHangSachRepository gioHangSachRepository ;
    @Autowired
    private CartService cartService ;
    @Autowired
    private DonHangRepository donHangRepository ;
    @Autowired
    private SachRepository sachRepository ;
    @Autowired
    private NguoiDungRepository nguoiDungRepository ;
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository ;
    @Autowired
    private HinhThucGiaoHangRepository hinhThucGiaoHangRepository ;
    @Autowired
    private DiaChiGiaoHangRepository diaChiGiaoHangRepository ;
    @Autowired
    private ModelMapper modelMapper ;

    // đầu tiên phải check xem ngươiuf dùng có đơn hàng hay chưa . Nếu chưa có thì tạo mơi
    // đây là trường hợp đặt tất cả sản pphaamr trong giỏ hàng .
    @Override
    public void placeOrderFromCart(String tenDangNhap , DatHangFromCartRequestDTO datHangFromCartRequestDTO){
        GioHang gioHang = cartService.checkGioHang(tenDangNhap) ;
        if(gioHang.getGioHangSaches().isEmpty()) {
            throw new RuntimeException("Giỏ hàng chống!");
        }
        if(datHangFromCartRequestDTO.getDanhSachSanPhamDatHang().isEmpty()){
            throw new RuntimeException("sản phẩm chọn hàng khồn hợp lệ") ;
        }
        // thiêts lập cho đơn hàng
        HinhThucGiaoHang hinhThucGiaoHang = hinhThucGiaoHangRepository.findById(datHangFromCartRequestDTO.getMaHinhThucGiaoHang()).orElseThrow(()->new RuntimeException("Không thấy HÌnh thức giao hàng")) ;
        HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepository.findById(datHangFromCartRequestDTO.getMaHinhThucThanhToan()).orElseThrow(()->new RuntimeException("Không thấy HÌnh thức thamh toán")) ;
        DiaChiGiaoHang diaChiGiaoHang = diaChiGiaoHangRepository.findById(datHangFromCartRequestDTO.getMaDiaChiGiaoHang()).orElseThrow(()->new RuntimeException("Không thấy địa chỉ giao hàng"));
        String diaChi = diaChiGiaoHang.getSoNha() +","+ diaChiGiaoHang.getPhuongOrXa() +","+ diaChiGiaoHang.getQuanOrHuyen() +","+ diaChiGiaoHang.getTinhOrCity() ;
        DonHang donHang = new DonHang() ;
        donHang.setNguoiDung(gioHang.getNguoiDung());
        donHang.setNgayTao(LocalDateTime.now());
        donHang.setTrangThai(TrangThaiGiaoHang.CHO_XAC_NHAN);
        if(hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Giao hàng nhanh")
                || hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Giao hàng tiết kiệm")
                || hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Hỏa tốc") ){
            donHang.setChiPhiGiaoHang(hinhThucGiaoHang.getChiPhiGiaoHang());
        }
        donHang.setHinhThucGiaoHang(hinhThucGiaoHang);
        donHang.setHinhThucThanhToan(hinhThucThanhToan);
        donHang.setDiaChiNhanHang(diaChi);
        // thiết lập cho cho tết đơn hàng

        List<ChiTietDonHang> chiTietDonHangs = new ArrayList<>() ;
        System.out.println("FE gửi : " + datHangFromCartRequestDTO.getDanhSachSanPhamDatHang());
        System.out.println("BE có : " );
        for(GioHangSach gioHangSach: gioHang.getGioHangSaches()){
            System.out.print(gioHangSach.getMaGioHangSach() + " ");

            if(datHangFromCartRequestDTO.getDanhSachSanPhamDatHang().contains(gioHangSach.getMaGioHangSach())){
                Sach sach = sachRepository.findByIdForUpdate(gioHangSach.getSach().getMaSach()).orElseThrow(()-> new RuntimeException("không tìm thấy sách")) ;
                if(gioHangSach.getSoLuong() > sach.getSoLuong()){
                    throw new OutOfStockException("sốl lượng sách tồn kho không đủ")  ;
                }
                sach.setSoLuong(sach.getSoLuong() - gioHangSach.getSoLuong());
                ChiTietDonHang chiTietDonHang = new ChiTietDonHang() ;
                chiTietDonHang.setDonHang(donHang);
                chiTietDonHang.setSach(sach);
                chiTietDonHang.setSoLuong(gioHangSach.getSoLuong());
                chiTietDonHang.setGiaBan(sach.getGiaBan()) ;
                chiTietDonHang.setTongGia(gioHangSach.getSoLuong()*sach.getGiaBan());
                chiTietDonHangs.add(chiTietDonHang) ;
            }

        }
        if(chiTietDonHangs.isEmpty()){
            throw new RuntimeException("Chọn sản phẩm đề đăth hàng") ;
        }
        donHang.setDanhSachChiTietDonHang(chiTietDonHangs);
        donHangRepository.save(donHang) ;

        // xử lý xóa sản phẩm khỏi giỏ hàng : chỗ này không xóa đơn lẻ vì nó đang đc quản lý bởi cha nên dù có xóa cx sẽ bị flush lại
        Set<Long> ids = datHangFromCartRequestDTO.getDanhSachSanPhamDatHang() ; // lấy ra những cái cần xóa
        // sau đó xóa theo điều kiên của collection.removeIf() ;
        gioHang.getGioHangSaches().removeIf(gioHangSach -> ids.contains(gioHangSach.getMaGioHangSach())) ;

    }

    @Override
    public void placeOder(String tenDangNhap, DatHangRequestDTO datHangRequestDTO) {
        DonHang donHang = new DonHang() ;
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElseThrow() ;
        Sach sach = sachRepository.findByMaSach(datHangRequestDTO.getMaSach()).orElseThrow(()->new RuntimeException("Sách không tồn tại")) ;
        HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepository.findById(datHangRequestDTO.getMaHinhThucThanhToan()).orElseThrow(()->new RuntimeException("Không thấy HÌnh thức thamh toán")) ;
        HinhThucGiaoHang hinhThucGiaoHang = hinhThucGiaoHangRepository.findById(datHangRequestDTO.getMaHinhThucGiaoHang()).orElseThrow(()->new RuntimeException("Không thấy HÌnh thức giao hàng")) ;
        DiaChiGiaoHang diaChiGiaoHang = diaChiGiaoHangRepository.findById(datHangRequestDTO.getMaDiaChiGiaoHang()).orElseThrow(()->new RuntimeException("Không thấy địa chỉ giao hàng"));

        String diaChi = diaChiGiaoHang.getSoNha() +","+ diaChiGiaoHang.getPhuongOrXa() +","+ diaChiGiaoHang.getQuanOrHuyen() +","+ diaChiGiaoHang.getTinhOrCity() ;
        donHang.setNguoiDung(nguoiDung);
        donHang.setNgayTao(LocalDateTime.now());
        // chỗ này nên điều kiện nếu nó là hình thức giao hnagf nhanh  hay chậm ... thì mk sẽ cso cái phí giao hàng khác nhau
        if(hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Giao hàng nhanh")
                || hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Giao hàng tiết kiệm")
                || hinhThucGiaoHang.getTenHinhThucGiaoHang().equals("Hỏa tốc") ){
            donHang.setChiPhiGiaoHang(hinhThucGiaoHang.getChiPhiGiaoHang());
        }
        donHang.setTrangThai(TrangThaiGiaoHang.CHO_XAC_NHAN); // set trạng thaias đơn hàng , luacs đầu luôn là đang chờ xác nhận từ người bán
        donHang.setHinhThucThanhToan(hinhThucThanhToan);
        donHang.setHinhThucGiaoHang(hinhThucGiaoHang);
        donHang.setDiaChiNhanHang(diaChi);
        if(datHangRequestDTO.getSoLuong() > sach.getSoLuong()){
            throw  new RuntimeException("Kho không đủ số lượng") ;
        }else {
            sach.setSoLuong(sach.getSoLuong() - datHangRequestDTO.getSoLuong());
            ChiTietDonHang chiTietDonHang = new ChiTietDonHang() ;
            chiTietDonHang.setDonHang(donHang);
            chiTietDonHang.setSach(sach);
            chiTietDonHang.setSoLuong(datHangRequestDTO.getSoLuong());
            chiTietDonHang.setGiaBan(sach.getGiaBan()) ;
            chiTietDonHang.setTongGia(sach.getGiaBan() * datHangRequestDTO.getSoLuong());
            donHang.getDanhSachChiTietDonHang().add(chiTietDonHang) ;
        }
        donHangRepository.save(donHang) ;
    }

    @Override
    public void capNhatTrangThaiDonHang(int maDonHang, TrangThaiGiaoHang trangThai){
        DonHang donHang = donHangRepository.findById(maDonHang).orElseThrow() ;
        donHang.setTrangThai(trangThai);
    }
    @Override
    public List<DonHangTrangThaiResponeDTO> getDonHangTheoTrangThai(String tenDangNhap , TrangThaiGiaoHang trangThaiGiaoHang) {
        List<DonHang> donHangs = donHangRepository.findByNguoiDung_TenDangNhapAndTrangThai( tenDangNhap, trangThaiGiaoHang) ;

        List<DonHangTrangThaiResponeDTO> result = new ArrayList<>() ;
        for(DonHang donHang : donHangs){
            DonHangTrangThaiResponeDTO donHangTrangThaiResponeDTO = modelMapper.map(donHang , DonHangTrangThaiResponeDTO.class) ;
            List<ChiTietDonHang> chiTietDonHangs = donHang.getDanhSachChiTietDonHang() ;
            List<SachTrongDonDTO> sachTrongDonDTOS = new ArrayList<>() ;
            for(ChiTietDonHang chiTietDonHang : chiTietDonHangs){
                SachTrongDonDTO sachTrongDonDTO = modelMapper.map(chiTietDonHang , SachTrongDonDTO.class) ;
                sachTrongDonDTO.setMaSach(chiTietDonHang.getSach().getMaSach());
                sachTrongDonDTO.setTenSach(chiTietDonHang.getSach().getTenSach());
                sachTrongDonDTOS.add(sachTrongDonDTO) ;

            }
            donHangTrangThaiResponeDTO.setSachTrongDonDTOS(sachTrongDonDTOS);
            result.add(donHangTrangThaiResponeDTO) ;

        }
        return result ;
    }
    @Override
    public void thaoTacDonHang(String tenDangNhap , int maDonHang){
        DonHang donHang = donHangRepository.findByNguoiDung_TenDangNhapAndMaDonHang(tenDangNhap , maDonHang).orElseThrow(()->new RuntimeException("Don hàng không tồn tại")) ;
        if(donHang.getTrangThai().equals(TrangThaiGiaoHang.DA_HUY)){


            for(ChiTietDonHang chiTietDonHang : donHang.getDanhSachChiTietDonHang()){
                Sach sach = sachRepository.findByMaSach(chiTietDonHang.getSach().getMaSach()).orElseThrow() ;
                if(sach.getSoLuong() >= chiTietDonHang.getSoLuong()){
                    sach.setSoLuong(sach.getSoLuong() - chiTietDonHang.getSoLuong());
                }else {
                    throw new RuntimeException("Số lượng sachs khòng đủ để đặt hàng") ;
                }
            }
            donHang.setTrangThai(TrangThaiGiaoHang.CHO_XAC_NHAN);
        }else if(donHang.getTrangThai().equals(TrangThaiGiaoHang.CHO_XAC_NHAN)){
            List<ChiTietDonHang> chiTietDonHangs = donHang.getDanhSachChiTietDonHang() ;
            for(ChiTietDonHang chiTietDonHang : chiTietDonHangs){
                Sach sach = sachRepository.findByMaSach(chiTietDonHang.getSach().getMaSach()).orElseThrow() ;
                sach.setSoLuong(sach.getSoLuong() + chiTietDonHang.getSoLuong());
            }
            donHang.setTrangThai(TrangThaiGiaoHang.DA_HUY);
        }else {
            throw new RuntimeException("khong thể thao tác với trạng thái này , thao tác không hợp lệ") ;
        }

    }


}
