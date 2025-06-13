package com.company;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {

    private static List<String> products = new ArrayList<>();
    private static List<String> pictures =  new ArrayList<>();
    private static final Long ADMIN_ID = 1206667836L;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (chatId.equals(ADMIN_ID)) {
            startAdmin(ADMIN_ID);
            switch (text) {
                case "Create product" -> {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(ADMIN_ID);
                    sendMessage.setText("Menu tanlang:");
                    sendMessage.setReplyMarkup(button());
                    execute(sendMessage);
                }
                case "Read product" -> {
                    readProduct(update , ADMIN_ID);
                }
                case "Update product" -> {
                    updateProduct(update , ADMIN_ID);
                }
                case "Delete product" -> {
                    deleteProduct(update , ADMIN_ID);
                }
                case "Product nomi" -> {
                    save(update , ADMIN_ID);
                }
                case "Product rasmi" -> {
                    picture(update , ADMIN_ID);
                }
            }
        }

    }

    @SneakyThrows
    private void readProduct(Update update, Long adminId) {
        if (products.isEmpty()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(adminId);
            sendMessage.setText("Afsuski productlar ro'yxati bo'sh.");
        }
        else {
            for (int i = 0; i < products.size(); i++) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(adminId);
                sendMessage.setText(products.get(i));
                execute(sendMessage);
            }
            for (int i = 0; i < pictures.size(); i++) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(adminId);
                sendPhoto.setPhoto(new InputFile(new File(pictures.get(i))));
                execute(sendPhoto);
            }
        }
    }

    @SneakyThrows
    private void updateProduct(Update update, Long adminId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(adminId);
        sendMessage.setText("Productni id sini yuboring");
        execute(sendMessage);
        if (!update.getMessage().getText().equals("Productni id sini yuboring")) {
            String id = update.getMessage().getText();
            for (String product : products) {
                if (id.equals(product)) {
                    SendMessage message = new SendMessage();
                    message.setChatId(adminId);
                    message.setText("Menu tanlang:");
                    message.setReplyMarkup(button());
                    execute(message);
                } else {
                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(adminId);
                    sendMessage1.setText(id + " ushbu id dagi product topilmadi.");
                }
            }
        }
    }

    @SneakyThrows
    private void deleteProduct(Update update, Long adminId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(adminId);
        sendMessage.setText("Productni id sini yuboring");
        execute(sendMessage);
        if (!update.getMessage().getText().equals("Productni id sini yuboring")) {
            String id = update.getMessage().getText();
            for (String product : products) {
                if (id.equals(product)) {
                    products.remove(product);
                } else {
                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(adminId);
                    sendMessage1.setText(id + " ushbu id dagi product topilmadi.");
                }
            }
        } else {
            deleteProduct(update , adminId);
        }
    }

    @SneakyThrows
    private void picture(Update update , Long adminId) {
        if (update.getMessage().hasPhoto()) {
            String fileId = update.getMessage().getPhoto().get(0).getFileId();
            pictures.add(fileId);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(adminId);
            sendMessage.setText("Iltimos rasm yuboring");
            execute(sendMessage);
        }

    }

    private ReplyKeyboardMarkup button() {
        KeyboardButton button1 = new KeyboardButton();
        KeyboardButton button2 = new KeyboardButton();
        button1.setText("Product nomi");
        button2.setText("Product rasmi");
        KeyboardRow row1 = new KeyboardRow();
        row1.add(button1);
        row1.add(button2);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row1);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    @SneakyThrows
    private void save(Update update , Long adminId) {
        if (!update.getMessage().getText().equals("Create product")) {
            String product = update.getMessage().getText();
            products.add(product);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(adminId);
            sendMessage.setText("Iltimos product nomini yuboring!");
            execute(sendMessage);
        }
    }

    @SneakyThrows
    private void startAdmin(Long adminId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(adminId);
        sendMessage.setText("Xush kelibsiz admin!");
        sendMessage.setReplyMarkup(admin());
        execute(sendMessage);
    }

    private ReplyKeyboardMarkup admin() {
        KeyboardButton createButton = new KeyboardButton();
        KeyboardButton readButton = new KeyboardButton();
        KeyboardButton updateButton = new KeyboardButton();
        KeyboardButton deleteButton = new KeyboardButton();
        createButton.setText("Create product");
        readButton.setText("Read product");
        updateButton.setText("Update product");
        deleteButton.setText("Delete product");
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(createButton);
        row1.add(readButton);
        row2.add(updateButton);
        row2.add(deleteButton);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return "@azizbek_yusuppvv_bot";
    }

    @Override
    public String getBotToken() {
        return "7731085874:AAHp9hsyp6L_k4cBibpsLQVWbsTzrCf6n-M";
    }
}