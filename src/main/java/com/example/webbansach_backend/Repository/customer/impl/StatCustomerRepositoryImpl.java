package com.example.webbansach_backend.Repository.customer.impl;

import com.example.webbansach_backend.Repository.customer.StatCustomerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatCustomerRepositoryImpl implements StatCustomerRepository {
    //  ,
    @PersistenceContext
    private EntityManager entityManager ;
    // get số đơn đặt hanghf thành công
    @Override
    public int getQuantityOrderSuccess(){
        StringBuilder query = new StringBuilder("SELECT count(*) soLuong  FROM don_hang dh WHERE date(dh.ngay_tao)=CURDATE() and dh.trang_thai='DA_XAC_NHAN' ");
        Number result =  (Number) entityManager.createNativeQuery(query.toString() ,Integer.class ).getSingleResult();
        return result.intValue() ;
    }
    @Override
    public int getQuantityOrderFail(){
        StringBuilder query = new StringBuilder("SELECT count(*) soLuong  FROM don_hang dh WHERE date(dh.ngay_tao)=CURDATE() and dh.trang_thai='DA_HUY' ");
        Number result =(Number) entityManager.createNativeQuery(query.toString() ,Integer.class ).getSingleResult() ;
        return result.intValue();
    }
    // lấy lên sách được bán trong ngày

    @Override
    public List<Integer> getBookOrder(){
        StringBuilder query = new StringBuilder("SELECT DISTINCT ctdh.ma_sach  FROM don_hang dh " +
                "JOIN chi_tiet_don_hang ctdh ON ctdh.ma_don_hang=dh.ma_don_hang " +
                "WHERE date(dh.ngay_tao)=CURDATE() and dh.trang_thai='DA_XAC_NHAN' ");
        return entityManager.createNativeQuery(query.toString() ,Integer.class ).getResultList() ;
    }
    @Override
    public double getRevenue(){
        StringBuilder query = new StringBuilder("SELECT SUM(dh.tong_gia) tongGia FROM don_hang dh " +
                "WHERE date(dh.ngay_tao)=CURDATE() and dh.trang_thai='DA_XAC_NHAN'") ;
        Number result =(Number) entityManager.createNativeQuery(query.toString() , Double.class).getSingleResult() ;
        return result.doubleValue() ;
    }
    @Override
    public int getIdBookBestSeller(){
        StringBuilder query = new StringBuilder("SELECT count(ctdh.ma_sach) FROM don_hang dh " +
                "JOIN chi_tiet_don_hang ctdh  ON ctdh.ma_don_hang=dh.ma_don_hang " +
                "WHERE date(dh.ngay_tao)=CURDATE() and dh.trang_thai='DA_XAC_NHAN' ");
        Number result = (Number) entityManager.createNativeQuery(query.toString() ,Integer.class ).getSingleResult() ;
        return result.intValue();
    }

}
