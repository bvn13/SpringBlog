package com.raysmond.blog.notificators.telegram;

import com.raysmond.blog.services.AppSetting;
import com.raysmond.blog.services.TelegramBotSettings;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Created by bvn13 on 21.12.2017.
 */
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
                e.printStackTrace();
            }
        }
        private void _send(String message, Long chatId) {
            try {
                this.send(message, chatId);
            } catch (TelegramApiException e) {
                e.printStackTrace();
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

                // Set up Http proxy
                DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(settings.getProxyHost(), settings.getProxyPort()),
                        new UsernamePasswordCredentials(settings.getProxyUsername(), settings.getProxyPassword()));

                HttpHost httpHost = new HttpHost(settings.getProxyHost(), settings.getProxyPort());

                RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(true).build();
                botOptions.setRequestConfig(requestConfig);
                botOptions.setCredentialsProvider(credsProvider);
                botOptions.setHttpProxy(httpHost);

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
                e.printStackTrace();
            }

            this.isActive = true;

            try {
                if (isActive && appSetting.getTelegramMasterChatId() != null && !appSetting.getTelegramMasterChatId().isEmpty()) {
                    sendMessageToMaster("i'm online");
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
