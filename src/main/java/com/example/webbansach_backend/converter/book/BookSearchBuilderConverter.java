package com.example.webbansach_backend.converter.book;

import com.example.webbansach_backend.builder.BookSearchBuiler;
import com.example.webbansach_backend.utils.MapUtil;

import java.util.Map;

public class  BookSearchBuilderConverter {
    public static BookSearchBuiler toBookSearchBuiler(Map<String , Object> params){
        return new BookSearchBuiler.Builder()
                                .setten_tac_gia(MapUtil.getObject(params , "ten_tac_gia" , String.class))
                                .setma_the_loai(MapUtil.getObject(params , "ma_the_loai" , Integer.class))
                                .setPriceFrom(MapUtil.getObject(params , "priceFrom" , Double.class))
                                .setPriceTo(MapUtil.getObject(params , "priceTo" , Double.class))
                                .build();
    }
}
// ở đây có thể ăn lôi npe -> k search đc / phải try . tuy nhiên cần sủ lý cách khác