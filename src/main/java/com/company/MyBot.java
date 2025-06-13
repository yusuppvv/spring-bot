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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBot extends TelegramLongPollingBot {

    private final Map<Long, List<String>> userCarts = new HashMap<>();
    private final Map<String, Double> menu = new HashMap<>() {{
        put("Пепперони", 65000.0);
        put("Маргарита", 55000.0);
        put("Гавайская", 70000.0);
        put("4 сыра", 80000.0);
    }};

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            switch (text) {
                case "/start" -> sendStartMessage(chatId);
                case "Меню" -> sendMenu(chatId);
                case "Корзина" -> showCart(chatId);
                case "Очистить корзину" -> clearCart(chatId);
                case "Оформить заказ" -> processOrder(chatId);
                default -> {
                    if (menu.containsKey(text)) {
                        addToCart(chatId, text);
                    }
                }
            }
        }
    }

    @SneakyThrows
    private void sendStartMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Добро пожаловать в PizzaBot!\nВыберите действие:");
        message.setReplyMarkup(getMainKeyboard());
        execute(message);
    }

    @SneakyThrows
    private void sendMenu(Long chatId) {
        StringBuilder menuText = new StringBuilder("Наше меню:\n\n");
        for (Map.Entry<String, Double> entry : menu.entrySet()) {
            menuText.append(entry.getKey()).append(" - ").append(entry.getValue()).append(" сум\n");
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(menuText.toString());
        message.setReplyMarkup(getPizzaKeyboard());
        execute(message);
    }

    @SneakyThrows
    private void addToCart(Long chatId, String pizza) {
        userCarts.putIfAbsent(chatId, new ArrayList<>());
        userCarts.get(chatId).add(pizza);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(pizza + " добавлена в корзину!");
        execute(message);
    }

    @SneakyThrows
    private void showCart(Long chatId) {
        List<String> cart = userCarts.getOrDefault(chatId, new ArrayList<>());
        if (cart.isEmpty()) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Ваша корзина пуста");
            execute(message);
            return;
        }

        double total = cart.stream()
                .mapToDouble(pizza -> menu.get(pizza))
                .sum();

        StringBuilder cartText = new StringBuilder("Ваша корзина:\n\n");
        for (String pizza : cart) {
            cartText.append(pizza).append(" - ").append(menu.get(pizza)).append(" сум\n");
        }
        cartText.append("\nИтого: ").append(total).append(" сум");

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(cartText.toString());
        execute(message);
    }

    @SneakyThrows
    private void clearCart(Long chatId) {
        userCarts.remove(chatId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Корзина очищена");
        execute(message);
    }

    @SneakyThrows
    private void processOrder(Long chatId) {
        List<String> cart = userCarts.get(chatId);
        if (cart == null || cart.isEmpty()) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Ваша корзина пуста!");
            execute(message);
            return;
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Спасибо за заказ! Ожидайте звонка оператора.");
        execute(message);
        clearCart(chatId);
    }

    private ReplyKeyboardMarkup getMainKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Меню"));
        row1.add(new KeyboardButton("Корзина"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Очистить корзину"));
        row2.add(new KeyboardButton("Оформить заказ"));

        rows.add(row1);
        rows.add(row2);
        keyboard.setKeyboard(rows);

        return keyboard;
    }

    private ReplyKeyboardMarkup getPizzaKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        int count = 0;
        for (String pizza : menu.keySet()) {
            if (count % 2 == 0) {
                row = new KeyboardRow();
                rows.add(row);
            }
            row.add(new KeyboardButton(pizza));
            count++;
        }

        keyboard.setKeyboard(rows);
        return keyboard;
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