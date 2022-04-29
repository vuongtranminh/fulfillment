package com.bluebelt.fulfillment.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils extends PageRequest {

    protected PageUtils(int page, int size, String sort, boolean desc) {
        super(page-1, size, desc ? Sort.by(sort).descending() : Sort.by(sort));
    }

    public static Pageable pageable(int page, int size, String sort, boolean desc) {
        return new PageUtils(page, size, sort, desc);
    }

}
