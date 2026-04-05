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
            "/danh-gia/{maSach}/danhSachDanhGia" ,
            "/books/**" ,
            "/tai-khoan/check-username",
            "/tai-khoan/check-email",
            "/book/search/filter",
            "/book/book-new-carousel" ,
            "/image/book/{maSach}/{soLuong}",
            "/book/category/{categoryId}",
            "/books/{maSach}",
            "/books/page-size?page=${page}&size=8",
            "/books/category-page-size?maTheLoai=${maTheLoai}&page=${page}&size=8"
    };
    public static final String[] PUBLIC_POST_ENDPOINS = {
            "/tai-khoan/dang-ky",
            "/tai-khoan/dang-nhap",
            "/tai-khoan/refresh-token",
            "/tai-khoan/logout" ,
            "/chat/users/dm"
    };
    public static final String[] ADMIN_GET_ENDPOINS = {
            "/nguoi-dung" ,
            "/nguoi-dung/**",
            "/profile/info/user-other" ,
            "/book/all" ,
            "/book/book-deleted",
            "/vouchers/all",
            "/stats/statToday",
            "/stats/once-week/{soNgay}"
    } ;
    public static final String[] ADMIN_POST_ENDPOINS = {
            "/them-sach" ,

            "/book/add-new-book",
            "/vouchers/add-voucher"
    } ;
    public static final String[] ADMIN_PUT_ENDPOINS ={
            "/tai-khoan/disable",
            "/book/update" ,
            "/book/restore",
            "/vouchers/update-voucher/{maGiam}"
    };

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
            "/vouchers/user" ,
            "/profile/info" ,
            "/chat/list-users" ,
            "chat/users",
            "chat/users/dm/messages" ,

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
            "/vouchers/user/use-voucher/{maGiam}" ,
            "/danh-gia/{maSach}/{noiDungDanhGia}" ,
            "/api/test/dm"
    } ;
    public  static final String[] USER_PUT_ENDPINTS = {
            "/don-hang/{maDonHang}/huy-don",
            "/don-hang/{maDonHang}/dat-lai",
            "/don-hang/{id}/trang-thai",
            "/vouchers/update/{maGiam}",
            "/tai-khoan/change-password" ,
            "/profile/update"
    } ;
}
