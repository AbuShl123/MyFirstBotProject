package com.abuBotProject;

import com.abuBotProject.entity.Currency;
import com.abuBotProject.service.CurrencyModeService;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

public class TestBot extends TelegramLongPollingBot {
    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()){
            handleCallBackQuery(update.getCallbackQuery());
        }
        else if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    @SneakyThrows
    private void handleCallBackQuery(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();

        String[] param = callbackQuery.getData().split(":");

        String action = param[0];
        Currency newCurrency = Currency.valueOf(param[1]);

        switch (action) {
            case "ORIGINAL":
                currencyModeService.setOriginalCurrency(message.getChatId(), newCurrency);
                break;
            case "TARGET":
                currencyModeService.setTargetCurrency(message.getChatId(), newCurrency);
                break;
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());

        for (Currency currency : Currency.values()) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(originalCurrency, currency))
                                    .callbackData("ORIGINAL:" + currency)
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text(getCurrencyButton(targetCurrency, currency))
                                    .callbackData("TARGET:" + currency)
                                    .build()
                    )
            );
        }

        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        //handle command
        if (message.hasText() & message.hasEntities()) {
            Optional<MessageEntity> commandEntity = message.getEntities().
                    stream().filter(e -> e.getType().equals("bot_command")).findFirst();
            if (commandEntity.isPresent()) {
                System.out.println(commandEntity);
                String command = message.getText().
                        substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/set_currency":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
                        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());

                        for (Currency currency : Currency.values()) {
                            buttons.add(
                                    Arrays.asList(
                                            InlineKeyboardButton.builder()
                                                    .text(getCurrencyButton(originalCurrency, currency))
                                                    .callbackData("ORIGINAL:" + currency)
                                                    .build(),
                                            InlineKeyboardButton.builder()
                                                    .text(getCurrencyButton(targetCurrency, currency))
                                                    .callbackData("TARGET:" + currency)
                                                    .build()
                                    )
                            );
                        }

                        execute(SendMessage.builder()
                                .text("Please choose Original and Target currencies:")
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .chatId(message.getChatId().toString())
                                .build());
                        break;
                }
            }
        }
    }

    private String getCurrencyButton(Currency saved, Currency current) {
        return saved == current ? current + " ✅" : current.name();
    }

    @Override
    public String getBotUsername() {
        return "@LrlRealTestBot";
    }

    @Override
    public String getBotToken() {
        return "5451830303:AAGlNCSxaQIXmTfpNbh0M47JU4sSyC21PYI";
    }

    @SneakyThrows
    public static void main(String[] args) {
        TestBot bot = new TestBot();
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
    }
}
