package com.example.webbansach_backend.Repository.customer;

import java.util.List;

public interface StatCustomerRepository {
    int getQuantityOrderSuccess();
    int getQuantityOrderFail() ;
    List<Integer> getBookOrder() ;
    double getRevenue() ;
    int getIdBookBestSeller() ;
}
