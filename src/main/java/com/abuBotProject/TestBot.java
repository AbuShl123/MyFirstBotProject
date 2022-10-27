package com.abuBotProject;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestBot extends TelegramLongPollingBot {
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
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
                        execute(SendMessage.builder()
                                .text("Please choose Original and Target currencies:")
                                .replyMarkup(InlineKeyboardMarkup.builder()
                                        .keyboard(buttons).build())
                                .chatId(message.getChatId().toString())
                                .build());
                        break;
                }
            }
        }
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
