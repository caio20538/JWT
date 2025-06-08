package com.caio.springsecurity.controller.dto;

import java.util.List;

public record FeedDTO (List<FeedItemDTO> feedItens,
                       int page,
                       int pageSize,
                       int totalPages,
                       Long totalElements){
}
