package com.luischavezb.bitso.assistant.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.BitsoWebSocketClient;
import com.geometrycloud.bitso.assistant.library.WebSocketOrder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by luischavez on 20/02/18.
 */

public class WebSocketService extends IntentService {

    public static class WebSocketOrdersEvent {

        private final Bitso.Book mBook;

        private final List<WebSocketOrder> mAsks;
        private final List<WebSocketOrder> mBids;

        public WebSocketOrdersEvent(Bitso.Book book, List<WebSocketOrder> asks, List<WebSocketOrder> bids) {
            mBook = book;
            mAsks = asks;
            mBids = bids;
        }

        public Bitso.Book book() {
            return mBook;
        }

        public List<WebSocketOrder> asks() {
            return mAsks;
        }

        public List<WebSocketOrder> bids() {
            return mBids;
        }
    }

    public static class WebSocketAddedOrderEvent {

        private final Bitso.Book mBook;
        private final WebSocketOrder mOrder;

        public WebSocketAddedOrderEvent(Bitso.Book book, WebSocketOrder order) {
            mBook = book;
            mOrder = order;
        }

        public Bitso.Book book() {
            return mBook;
        }

        public WebSocketOrder order() {
            return mOrder;
        }
    }

    public static class WebSocketRemovedOrderEvent {

        private final Bitso.Book mBook;
        private final WebSocketOrder mOrder;

        public WebSocketRemovedOrderEvent(Bitso.Book book, WebSocketOrder order) {
            mBook = book;
            mOrder = order;
        }

        public Bitso.Book book() {
            return mBook;
        }

        public WebSocketOrder order() {
            return mOrder;
        }
    }

    public static final String EXTRA_BOOK = "com.luischavezb.bitso.assistant.android.intent.BOOk";

    public static BitsoWebSocketClient sWebSocketClient;

    public WebSocketService() {
        super(WebSocketService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bitso.Book book = (Bitso.Book) intent.getSerializableExtra(EXTRA_BOOK);

        if (null != sWebSocketClient) {
            sWebSocketClient.disconnect();
            sWebSocketClient = null;
        }

        if (null != book) {
            sWebSocketClient = new WebSocketClient(book);
            sWebSocketClient.connect(true);
        }
    }

    private class WebSocketClient extends BitsoWebSocketClient {

        public WebSocketClient(Bitso.Book book) {
            super(book);
        }

        @Override
        protected void onOrders(Bitso.Book book, List<WebSocketOrder> asks, List<WebSocketOrder> bids) {
            EventBus.getDefault().post(new WebSocketOrdersEvent(book, asks, bids));
        }

        @Override
        protected void onOrder(Bitso.Book book, WebSocketOrder order) {
            EventBus.getDefault().post(new WebSocketAddedOrderEvent(book, order));
        }

        @Override
        protected void onRemove(Bitso.Book book, WebSocketOrder order) {
            EventBus.getDefault().post(new WebSocketRemovedOrderEvent(book, order));
        }
    }
}
