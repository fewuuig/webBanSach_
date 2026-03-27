package com.example.webbansach_backend.Repository.customer.impl;
import com.example.webbansach_backend.Repository.customer.SachCustomRepository;
import com.example.webbansach_backend.builder.BookSearchBuiler;
import com.example.webbansach_backend.dto.book.BookResponeDTO;
import com.example.webbansach_backend.utils.NumberUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.List;

@Repository
public class SachRepositoryCustomImpl implements SachCustomRepository {

    private void queryNormal(BookSearchBuiler bookSearchBuiler , StringBuilder where){
        try {
            Field[] fields = BookSearchBuiler.class.getDeclaredFields() ;
            for(Field item : fields) {
                item.setAccessible(true);
                String fieldName = item.getName() ;
                if(!fieldName.startsWith("price") && !fieldName.equals("ma_the_loai")) {
                    Object value = item.get(bookSearchBuiler) ;
                    if(value != null ) {
                        where.append(" AND s."+fieldName +" Like '%"+value+"%'") ;
                    }
                }
            }

        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    private void joinTable( BookSearchBuiler bookSearchBuiler,StringBuilder query ,StringBuilder countQuery){
        if(bookSearchBuiler.getma_the_loai()!=null){
            countQuery.append(" JOIN sach_theloai as stl ON stl.ma_sach = s.ma_sach ");
            query.append(" JOIN sach_theloai as stl ON stl.ma_sach = s.ma_sach ") ;
        }
    }

    private void querySpecial(BookSearchBuiler bookSearchBuiler , StringBuilder where){
        if(bookSearchBuiler.getma_the_loai() != null ){
            where.append(" And stl.ma_the_loai = " +bookSearchBuiler.getma_the_loai()) ;
        }
        if(bookSearchBuiler.getPriceFrom() != null){
            where.append(" And s.gia_ban >= "+bookSearchBuiler.getPriceFrom()) ;
        }
        if(bookSearchBuiler.getPriceTo() != null){
            where.append(" And s.gia_ban <= "+bookSearchBuiler.getPriceTo()) ;
        }
    }

    // lấy lên và phân trang cho nó luôn
    @PersistenceContext
    private EntityManager entityManager ;
    @Override
    public Page<BookResponeDTO> findBookFilter(BookSearchBuiler bookSearchBuiler , Pageable pageable){
        StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM sach s");
        StringBuilder query = new StringBuilder("SELECT " +
                "s.ma_sach,s.ten_sach,s.ten_tac_gia,s.mo_ta,s.gia_niem_yet,s.gia_ban,s.so_luong,s.trung_binh_xep_hang " +
                "FROM sach s ");
        StringBuilder where = new StringBuilder(" WHERE 1=1 ") ;
        joinTable(bookSearchBuiler , query ,countQuery);
        queryNormal(bookSearchBuiler,where);
        querySpecial(bookSearchBuiler , where);
        query = query.append(where) ;

        // query data lên
        Query data = entityManager.createNativeQuery(query.toString() , BookResponeDTO.class) ;
        data.setFirstResult((int)pageable.getOffset()) ;
        data.setMaxResults(pageable.getPageSize()) ;
        List<BookResponeDTO> bookResponeDTOS = data.getResultList() ;

        countQuery.append(where);

        Query countQ = entityManager.createNativeQuery(countQuery.toString());
        Long total = ((Number) countQ.getSingleResult()).longValue();
        return new PageImpl<>(bookResponeDTOS , pageable , total);
    }

}