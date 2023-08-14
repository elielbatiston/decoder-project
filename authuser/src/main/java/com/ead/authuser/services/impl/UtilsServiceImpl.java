package com.ead.authuser.services.impl;

import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.services.UtilsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {

    @Override
    public String createUrl(final UUID userId, final Pageable pageable) {
        return "/courses?userId=" + userId + "&page=" + pageable.getPageNumber() + "&size="
                + pageable.getPageSize() + "&sort=" + pageable.getSort().toString().replaceAll(": ", ",");
    }
}
