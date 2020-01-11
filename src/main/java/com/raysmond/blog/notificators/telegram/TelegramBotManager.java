package com.raysmond.blog.notificators.telegram;

import com.raysmond.blog.services.AppSetting;
import com.raysmond.blog.services.TelegramBotSettings;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by bvn13 on 21.12.2017.
 */
@Slf4j
@Component
public class TelegramBotManager {

    @Autowired
    private AppSetting appSetting;

    // https://core.telegram.org/bots/api#sendmessage

    public static class TelegramBot extends AbilityBot {

        private static Logger logger = LoggerFactory.getLogger(TelegramBot.class);

        private TelegramBotManager manager;
        private String name;
        private String token;
        private String masterName;
        private String masterChatId;


        @Override
        public int creatorId() {
            return Integer.parseInt(masterChatId);
        }

        public TelegramBot(TelegramBotManager manager, String name, String token, String masterName, String masterChatId) {
            super(token, name);
            this.manager = manager;
            this.name = name;
            this.token = token;
            this.masterName = masterName;
            this.masterChatId = masterChatId;
        }

        public TelegramBot(TelegramBotManager manager, String name, String token, String masterName, String masterChatId, DefaultBotOptions options) {
            super(token, name, options);
            this.manager = manager;
            this.name = name;
            this.token = token;
            this.masterName = masterName;
            this.masterChatId = masterChatId;
        }


        @Override
        public void onUpdateReceived(Update update) {
            // We check if the update has a message and the message has text
            if (update.hasMessage() && update.getMessage().hasText()) {
                logger.debug("CHAT ID: "+update.getMessage().getChatId().toString()+" / MESSAGE: "+update.getMessage().getText());

                Long chatId = update.getMessage().getChatId();

                if (update.getMessage().getChat().getUserName().equals(masterName)) {
                    if (update.getMessage().getText().startsWith("/master")) {
                        this.manager.appSetting.setTelegramMasterChatId(chatId.toString());
                        this._send("Master chat ID changed to: "+chatId.toString(), chatId);
                    } else {
                        this._send("Hello, master!", chatId);
                    }
                } else {
                    this._send("You are not allowed to touch me!", chatId);
                }
            }
        }


        @Override
        public String getBotUsername() {
            return this.name;
        }

        @Override
        public String getBotToken() {
            return this.token;
        }

        public void sendMessageToChannel(String channelName, String message) throws TelegramApiException {
            this.send(message, "@" + channelName);
        }

        public void sendMessageToMaster(String message) throws TelegramApiException {
            this.send(message, this.masterChatId);
        }

        public void send(String message, String chatName) throws TelegramApiException {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatName);
            sm.enableMarkdown(true);
            sm.enableWebPagePreview();
            sm.setText(message);
            this.execute(sm);
        }
        public void send(String message, Long chatId) throws TelegramApiException {
            SendMessage sm = new SendMessage();
            sm.setChatId(chatId);
            sm.enableMarkdown(true);
            sm.enableWebPagePreview();
            sm.setText(message);
            this.execute(sm);
        }

        private void _send(String message, String chatName) {
            try {
                this.send(message, chatName);
            } catch (TelegramApiException e) {
                log.error("Error", e);
            }
        }
        private void _send(String message, Long chatId) {
            try {
                this.send(message, chatId);
            } catch (TelegramApiException e) {
                log.error("Error", e);
            }
        }
    }

    private TelegramBotSettings settings;
    private TelegramBot bot;

    private Boolean isActive = false;

    @Autowired
    public TelegramBotManager(TelegramBotSettings settings) {
        this.settings = settings;
    }


    @PostConstruct
    public void startBot() {
        if (settings.isActive()) {

            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();

            if (settings.isProxySet()) {

                // Create the Authenticator that will return auth's parameters for proxy authentication
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(settings.getProxyUsername(), settings.getProxyPassword().toCharArray());
                    }
                });

                // Set up Http proxy
                DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

                botOptions.setProxyHost(settings.getProxyHost());
                botOptions.setProxyPort(settings.getProxyPort());
                // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
                botOptions.setProxyType(detectProxyType());

                this.bot = new TelegramBot(this,
                        settings.getBotName(),
                        settings.getBotToken(),
                        settings.getMasterName(),
                        appSetting.getTelegramMasterChatId(),
                        botOptions
                );
            } else {

                this.bot = new TelegramBot(this,
                        settings.getBotName(),
                        settings.getBotToken(),
                        settings.getMasterName(),
                        appSetting.getTelegramMasterChatId()
                );
            }

            try {
                botsApi.registerBot(this.bot);

                this.isActive = true;
            } catch (TelegramApiException e) {
                log.error("Error", e);
            }

            this.isActive = true;

            try {
                if (isActive && appSetting.getTelegramMasterChatId() != null && !appSetting.getTelegramMasterChatId().isEmpty()) {
                    sendMessageToMaster("i'm online");
                }
            } catch (TelegramApiException e) {
                log.error("Error", e);
            }
        }
    }

    private DefaultBotOptions.ProxyType detectProxyType() {
        try {
            return DefaultBotOptions.ProxyType.valueOf(settings.getProxyType());
        } catch (IllegalArgumentException e) {
            log.error("Unknown proxy type "+settings.getProxyType()+". Must be one of: "+ StringUtils.arrayToCommaDelimitedString(DefaultBotOptions.ProxyType.values()));
            return DefaultBotOptions.ProxyType.NO_PROXY;
        }
    }

    @PreDestroy
    public void stopBot() {
        if (isActive) {
//            try {
//                sendMessageToMaster("i'm shutting down");
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
        }
    }


    public void sendMessageToChannel(String message) throws TelegramApiException {
        if (isActive) {
            bot.sendMessageToChannel(settings.getBotChannel(), message);
        }
    }

    public void sendMessageToMaster(String message) throws TelegramApiException {
        if (isActive) {
            bot.sendMessageToMaster(message);
        }
    }

}
