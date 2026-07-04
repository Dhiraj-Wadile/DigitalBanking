package com.digitalbanking.mapper;

import com.digitalbanking.entity.card.Card;
import com.digitalbanking.dto.card.CardResponse;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardResponse cardToResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setCardNumber(maskCardNumber(card.getCardNumber()));
        response.setCardType(card.getCardType().name());
        response.setCardNetwork(card.getCardNetwork().name());
        response.setStatus(card.getStatus().name());
        response.setCardHolderName(card.getCardHolderName());
        response.setExpiryMonth(card.getExpiryMonth());
        response.setExpiryYear(card.getExpiryYear());
        response.setInternationalEnabled(card.getInternationalEnabled());
        response.setOnlineEnabled(card.getOnlineEnabled());
        response.setTapToPayEnabled(card.getTapToPayEnabled());
        response.setIsVirtual(card.getIsVirtual());
        if (card.getAccount() != null) {
            response.setAccountNumber(card.getAccount().getAccountNumber());
        }
        return response;
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }
}
