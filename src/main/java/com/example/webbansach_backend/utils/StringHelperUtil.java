package com.example.webbansach_backend.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringHelperUtil {
    public static String removeAccents(String str) {
        if (str == null) {
            return null;
        }

        // 1. Chuẩn hóa chuỗi về dạng NFD (Tách chữ cái cơ bản và dấu riêng ra)
        // Ví dụ: "ế" sẽ tách thành "e" + "^" + "'"
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);

        // 2. Dùng regex để tìm và thay thế tất cả các "dấu" (Combining Diacritical Marks) bằng chuỗi rỗng
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(nfdNormalizedString).replaceAll("");

        // 3. Bắt buộc phải xử lý thủ công chữ 'Đ' và 'đ'
        // (Vì trong Unicode, 'đ' là một ký tự riêng biệt chứ không phải 'd' thêm dấu ngoặc ngang)
        return result.replace("Đ", "D").replace("đ", "d");
    }
}
