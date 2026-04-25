package com.example.webbansach_backend.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;

@Getter
@Setter
@Document(indexName = "sach")
@Setting(settingPath = "elastic-settings.json" )
public class SachDocument {
    @Id // dùng cho updqte , cập nhật ... như trong DB
    private int maSach ;

    @Field(type = FieldType.Search_As_You_Type , analyzer = "vietnamese_without_accents")
    private String tenSach ;
    @Field(type = FieldType.Text , analyzer = "vietnamese_without_accents")
    private String tenTacGia ;
    @Field(type = FieldType.Keyword)
    private String isbn  ;
    @Field(type = FieldType.Text , analyzer = "vietnamese_without_accents")
    private String moTa ;

    @Field(type = FieldType.Double)
    private double giaNiemYet ;

    @Field(type = FieldType.Double)
    private double giaBan ;

    @Field(type = FieldType.Double)
    private double trungBinhXepHang ;

    @Field(type = FieldType.Boolean) // T ,F ở inverted index
    private boolean isActive ;

    // tạm thời bỏ qua hình ảnh , vì làm chưa chuẩn phần đấy lắm
    @Field(type = FieldType.Keyword)
    private List<String> theLoai ;

}
// các dạng số khi chọn type thì nó lưu vào BKD block K - Dimensional tree)
