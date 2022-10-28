package com.abuBotProject.service;

import com.abuBotProject.entity.Currency;
import com.abuBotProject.service.impl.HashMapCurrencyModeService;

public interface CurrencyModeService {
    static CurrencyModeService getInstance() {
        return new HashMapCurrencyModeService();
    }

    Currency getOriginalCurrency(long chatId);

    Currency getTargetCurrency(long chatId);

    void setOriginalCurrency(long chatId, Currency currency);

    void setTargetCurrency(long chatId, Currency currency);
}
