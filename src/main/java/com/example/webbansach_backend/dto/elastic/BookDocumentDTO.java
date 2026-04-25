package com.example.webbansach_backend.dto.elastic;

import com.example.webbansach_backend.document.SachDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class BookDocumentDTO {
    List<SachDocument> sachDocuments ;
    Map<String ,Object> stats ;
}
