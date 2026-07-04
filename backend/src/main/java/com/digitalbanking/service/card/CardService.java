package com.digitalbanking.service.card;

import com.digitalbanking.dto.card.*;
import com.digitalbanking.entity.card.Card;
import com.digitalbanking.entity.account.Account;
import com.digitalbanking.exception.BadRequestException;
import com.digitalbanking.exception.ResourceNotFoundException;
import com.digitalbanking.mapper.CardMapper;
import com.digitalbanking.repository.CardRepository;
import com.digitalbanking.repository.AccountRepository;
import com.digitalbanking.util.ReferenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final CardMapper cardMapper;

    @Transactional
    public CardResponse issueCard(CardRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Card card = Card.builder()
                .cardNumber(generateCardNumber())
                .account(account)
                .cardType(Card.CardType.valueOf(request.getCardType()))
                .cardNetwork(Card.CardNetwork.valueOf(request.getCardNetwork()))
                .status(Card.CardStatus.PENDING_ACTIVATION)
                .cardHolderName(account.getCustomer().getUser().getFullName())
                .expiryMonth(String.valueOf(LocalDate.now().plusYears(3).getMonthValue()))
                .expiryYear(String.valueOf(LocalDate.now().plusYears(3).getYear()))
                .isVirtual(request.getIsVirtual() != null && request.getIsVirtual())
                .cvvHash("encrypted")
                .pinHash("not_set")
                .issuedDate(LocalDate.now())
                .build();

        card = cardRepository.save(card);
        log.info("Card issued: {} for account: {}", card.getCardNumber(), request.getAccountNumber());
        return cardMapper.cardToResponse(card);
    }

    @Transactional
    public CardResponse blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(Card.CardStatus.BLOCKED);
        card.setBlockedDate(LocalDate.now());
        card = cardRepository.save(card);
        return cardMapper.cardToResponse(card);
    }

    @Transactional
    public CardResponse unblockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(Card.CardStatus.ACTIVE);
        card.setBlockedDate(null);
        card = cardRepository.save(card);
        return cardMapper.cardToResponse(card);
    }

    @Transactional
    public void updateCardControls(Long cardId, Boolean online, Boolean international, Boolean tapToPay, Boolean atm, Boolean pos) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        if (online != null) card.setOnlineEnabled(online);
        if (international != null) card.setInternationalEnabled(international);
        if (tapToPay != null) card.setTapToPayEnabled(tapToPay);
        if (atm != null) card.setAtmEnabled(atm);
        if (pos != null) card.setPosEnabled(pos);
        cardRepository.save(card);
    }

    public List<CardResponse> getMyCards() {
        return cardRepository.findAll().stream()
                .map(cardMapper::cardToResponse)
                .toList();
    }

    private String generateCardNumber() {
        return ReferenceGenerator.generateCardNumber();
    }
}
