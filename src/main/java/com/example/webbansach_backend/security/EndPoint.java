package com.example.webbansach_backend.security;

public class EndPoint {
    public static final String FRONT_END_ENDPOINS = "http://localhost:3000" ;
    public static final String[] PUBLIC_GET_ENDPOINS = {
            "/sach" ,
           "/sach/**" ,
            "/hinh-anh" ,
            "/hinh-anh/**",
            "/nguoi-dung/search/existsByTenDangNhap",
            "/nguoi-dung/search/existsByEmail" ,
            "/tai-khoan/kich-hoat",
            "/sach/{maSach}/danhSachDanhGia",
            "/danh-gia/{maSach}/danhSachDanhGia"
    };
    public static final String[] PUBLIC_POST_ENDPOINS = {
            "/tai-khoan/dang-ky",
            "/tai-khoan/dang-nhap",
            "/tai-khoan/refresh-token",
            "/tai-khoan/logout"
    };
    public static final String[] ADMIN_GET_ENDPOINS = {
            "/nguoi-dung" ,
            "/nguoi-dung/**"
    } ;
    public static final String[] ADMIN_POST_ENDPOINS = {
            "/them-sach"
    } ;
    public  static final String[] USER_GET_ENDPINTS = {
            "/cart/view-cart",
            "/wish-love/danh-sach-yeu-thich",
            "/wish-love/check",
            "/dia-chi/dia-chi-giao-hang" ,
            "/thanh-toan/lay-hinh-thuc-thanh-toan",
            "/giao-hang/hinh-thuc-giao-hang" ,
            "/don-hang/trang-thai/**",
            "cart/sach-dat-tu-gio-hang",
            "/tai-khoan/lay-thong-tin",

    } ;
    public  static final String[] USER_DELETE_ENDPINTS = {
            "/cart/delete-book"
    } ;
    public  static final String[] USER_POST_ENDPINTS = {
            "/cart/add-to-cart",
            "/wish-love/add-to-wish-love",
            "/order/place-order-from-cart",
            "/order/place-order",
            "/dia-chi/them-dia-chi-giao-hang" ,

    } ;
    public  static final String[] USER_PUT_ENDPINTS = {
            "/don-hang/{maDonHang}/huy-don",
            "/don-hang/{maDonHang}/dat-lai"
    } ;
}
