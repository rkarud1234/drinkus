package com.ssafy.drinkus.room.request;

import com.ssafy.drinkus.category.response.CategoryListResponse;
import com.ssafy.drinkus.category.response.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchRequest {
    // 검색어 (방 제목)
    private String searchKeyword;

    // 또래 체크
    private Boolean sameAge;

    // 오래된순, 최신순
    private Integer sortOrder;

    //관심사
    private Long categoryId;
}
