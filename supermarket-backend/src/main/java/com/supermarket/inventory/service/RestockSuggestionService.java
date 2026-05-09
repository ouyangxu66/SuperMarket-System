package com.supermarket.inventory.service;

import com.supermarket.inventory.vo.RestockSuggestionVO;

import java.util.List;

/**
 * 智能补货建议服务
 */
public interface RestockSuggestionService {

    /**
     * 获取智能补货建议列表
     * @param restockDays 建议覆盖的天数（默认7天）
     * @return 补货建议列表
     */
    List<RestockSuggestionVO> getRestockSuggestions(Integer restockDays);
}
