package com.luischavezb.bitso.assistant.android;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geometrycloud.bitso.assistant.library.WebSocketOrder;
import com.luischavezb.bitso.assistant.android.adapter.LiveTradeAdapter;
import com.luischavezb.bitso.assistant.android.service.WebSocketService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by luischavez on 16/03/18.
 */

public class LiveTradesFragment extends Fragment {

    public interface LiveTradesEvents {

        void onLiveTradesInitialized();

        void onLiveTradesRequestStartService();

        void onLiveTradesRequestStopService();
    }

    private LiveTradesEvents mLiveTradesEvents;

    private RecyclerView mBuyTradeRecyclerView;
    private RecyclerView.LayoutManager mBuyTradeLayoutManager;
    private LiveTradeAdapter mBuyTradeAdapter;

    private RecyclerView mSellTradeRecyclerView;
    private RecyclerView.LayoutManager mSellTradeLayoutManager;
    private LiveTradeAdapter mSellTradeAdapter;

    @MainThread
    public void clear() {
        mBuyTradeAdapter.clear();
        mSellTradeAdapter.clear();
    }

    @MainThread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setOrders(WebSocketService.WebSocketOrdersEvent event) {
        mBuyTradeAdapter.setOrders(event.bids());
        mSellTradeAdapter.setOrders(event.asks());
    }

    @MainThread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addOrder(WebSocketService.WebSocketAddedOrderEvent event) {
        WebSocketOrder order = event.order();

        if ("0".equals(order.getType())) {
            mBuyTradeAdapter.addOrder(order);
        } else {
            mSellTradeAdapter.addOrder(order);
        }
    }

    @MainThread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void removeOrder(WebSocketService.WebSocketRemovedOrderEvent event) {
        WebSocketOrder order = event.order();

        if ("0".equals(order.getType())) {
            mBuyTradeAdapter.removeOrder(order);
        } else {
            mSellTradeAdapter.removeOrder(order);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_trades, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBuyTradeRecyclerView = view.findViewById(R.id.buy_trade_recycler_view);
        mSellTradeRecyclerView = view.findViewById(R.id.sell_trade_recycler_view);

        mBuyTradeLayoutManager = new LinearLayoutManager(getActivity());
        mBuyTradeAdapter = new LiveTradeAdapter(OrderType.BUY, new ArrayList<WebSocketOrder>());

        mBuyTradeRecyclerView.setHasFixedSize(false);
        mBuyTradeRecyclerView.setNestedScrollingEnabled(false);

        mBuyTradeRecyclerView.setLayoutManager(mBuyTradeLayoutManager);
        mBuyTradeRecyclerView.setAdapter(mBuyTradeAdapter);

        mSellTradeLayoutManager = new LinearLayoutManager(getActivity());
        mSellTradeAdapter = new LiveTradeAdapter(OrderType.SELL, new ArrayList<WebSocketOrder>());

        mSellTradeRecyclerView.setHasFixedSize(false);
        mSellTradeRecyclerView.setNestedScrollingEnabled(false);

        mSellTradeRecyclerView.setLayoutManager(mSellTradeLayoutManager);
        mSellTradeRecyclerView.setAdapter(mSellTradeAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != mLiveTradesEvents) {
            mLiveTradesEvents.onLiveTradesInitialized();
        }

        EventBus.getDefault().register(this);

        if (null != mLiveTradesEvents) {
            mLiveTradesEvents.onLiveTradesRequestStartService();
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);

        if (null != mLiveTradesEvents) {
            mLiveTradesEvents.onLiveTradesRequestStopService();
        }

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof LiveTradesEvents) {
            mLiveTradesEvents = (LiveTradesEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mLiveTradesEvents = null;

        super.onDetach();
    }
}
