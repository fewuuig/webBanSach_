package com.example.webbansach_backend.builder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchBuiler {
    private Double priceFrom ;
    private Double priceTo ;
    private Integer ma_the_loai ;
    private String ten_tac_gia ;
    private BookSearchBuiler(Builder builder){
        this.priceFrom = builder.priceFrom ;
        this.priceTo = builder.priceTo ;
        this.ma_the_loai = builder.ma_the_loai;
        this.ten_tac_gia = builder.ten_tac_gia;
    }

    public Double getPriceFrom() {
        return priceFrom;
    }

    public Double getPriceTo() {
        return priceTo;
    }

    public Integer getma_the_loai() {
        return ma_the_loai;
    }

    public String getten_tac_gia() {
        return ten_tac_gia;
    }
    public static class Builder{
        private Double priceFrom ;
        private Double priceTo ;
        private Integer ma_the_loai ;
        private String ten_tac_gia ;

        public Builder setPriceFrom(Double priceFrom) {
            this.priceFrom = priceFrom;
            return this;
        }

        public Builder setPriceTo(Double priceTo) {
            this.priceTo = priceTo;
            return this;
        }

        public Builder setma_the_loai(Integer ma_the_loai) {
            this.ma_the_loai = ma_the_loai;
            return this;
        }

        public Builder setten_tac_gia(String ten_tac_gia) {
            this.ten_tac_gia = ten_tac_gia;
            return this;
        }
        public BookSearchBuiler build(){
            return new BookSearchBuiler(this) ;
        }

    }

}
