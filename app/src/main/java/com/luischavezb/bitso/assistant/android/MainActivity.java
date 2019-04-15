package com.luischavezb.bitso.assistant.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.geometrycloud.bitso.assistant.library.Balance;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.FundingDestination;
import com.geometrycloud.bitso.assistant.library.History;
import com.geometrycloud.bitso.assistant.library.Order;
import com.geometrycloud.bitso.assistant.library.Profile;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.luischavezb.bitso.assistant.android.alarm.Alarm;
import com.luischavezb.bitso.assistant.android.billing.IabHelper;
import com.luischavezb.bitso.assistant.android.billing.IabResult;
import com.luischavezb.bitso.assistant.android.billing.Inventory;
import com.luischavezb.bitso.assistant.android.billing.Purchase;
import com.luischavezb.bitso.assistant.android.db.DbContract;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.luischavezb.bitso.assistant.android.dialog.PinDialog;
import com.luischavezb.bitso.assistant.android.dialog.PremiumDialog;
import com.luischavezb.bitso.assistant.android.service.TickerService;
import com.luischavezb.bitso.assistant.android.service.WebSocketService;
import com.luischavezb.bitso.assistant.android.task.MovementsResult;
import com.luischavezb.bitso.assistant.android.task.Task;
import com.luischavezb.bitso.assistant.android.task.TaskManager;
import com.luischavezb.bitso.assistant.android.task.api.CancelOrderTask;
import com.luischavezb.bitso.assistant.android.task.api.CreateAccountTask;
import com.luischavezb.bitso.assistant.android.task.api.PlaceOrderTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestAccountStatusTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestBalancesTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestHistoriesTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestMovementsTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestOrdersTask;
import com.luischavezb.bitso.assistant.android.task.api.ValidateApiTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadAccountStatusTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadBalancesTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadHistoriesTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadMovementsTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadOrdersTask;
import com.luischavezb.bitso.assistant.android.task.download.DownloadGravatarTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by luischavez on 25/02/18.
 */

public class MainActivity extends BaseActivity implements WalletFragment.WalletEvents,
        AccountFragment.AccountEvents,
        TradeFragment.TradeEvents,
        ChartFragment.ChartEvents,
        LiveTradesFragment.LiveTradesEvents,
        ServiceFragment.ServiceEvents,
        ProfileFragment.ProfileEvents,
        AlarmsFragment.AlarmsEvents,
        PremiumFragment.PremiumEvents,
        ConfigurationFragment.ConfigurationEvents,
        PremiumDialog.OnPremiumDialogResult {

    private final String SKU_PREMIUM = "sku_bitso_assistant_premium_2";
    private final int REQUEST_PREMIUM_UPGRADE = 1000;

    private static final long TIME_BETWEEN_ADS = TimeUnit.MINUTES.toMillis(1);
    private static final int MAX_PROFILES_NOT_PREMIUM = 1;

    private IabHelper mIabHelper;
    private boolean mIabEnabled = false;

    private long mlastShowingAdTimemillis;

    private boolean mFirstLaunch = true;
    private boolean mShowReload = true;

    private InterstitialAd mInterstitialAd;

    private WalletFragment mWalletFragment;
    private AccountFragment mAccountFragment;
    private CredentialsFragment mCredentialsFragment;
    private TradeFragment.BuyFragment mBuyFragment;
    private TradeFragment.SellFragment mSellFragment;
    private LiveTradesFragment mLiveTradesFragment;
    private ChartFragment mChartFragment;
    private ServiceFragment mServiceFragment;
    private ProfileFragment mProfileFragment;
    private AlarmsFragment mAlarmsFragment;
    private AlarmFragment mAlarmFragment;
    private PremiumFragment mPremiumFragment;
    private ConfigurationFragment mConfigurationFragment;

    private void setFragment(Fragment fragment) {
        if (CredentialsFragment.class.isAssignableFrom(fragment.getClass())
                || ProfileFragment.class.isAssignableFrom(fragment.getClass())
                || AlarmFragment.class.isAssignableFrom(fragment.getClass())) {
            mShowReload = false;
        } else {
            mShowReload = true;
        }

        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame_layout, fragment, fragment.getClass().getName())
                //.addToBackStack(null)
                .commit();

        invalidateOptionsMenu();

        if (!AssistantApplication.sPremium
                && !PremiumFragment.class.isAssignableFrom(fragment.getClass())) {
            long currentTimeMillis = System.currentTimeMillis();

            if (TIME_BETWEEN_ADS <= currentTimeMillis - mlastShowingAdTimemillis) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    mlastShowingAdTimemillis = System.currentTimeMillis();
                }
            }
        }
    }

    public void showWallet() {
        setFragment(mWalletFragment);
    }

    public void showAccount() {
        setFragment(mAccountFragment);
    }

    public void showCredentials() {
        setFragment(mCredentialsFragment);
    }

    public void showBuy() {
        setFragment(mBuyFragment);
    }

    public void showSell() {
        setFragment(mSellFragment);
    }

    public void showLiveTrades() {
        setFragment(mLiveTradesFragment);
    }

    public void showChart() {
        setFragment(mChartFragment);
    }

    public void showService() {
        setFragment(mServiceFragment);
    }

    public void showProfile(Long id) {
        mProfileFragment.editProfile(id);
        setFragment(mProfileFragment);
    }

    public void showAlarms() {
        setFragment(mAlarmsFragment);
    }

    public void showAlarm(Long id) {
        mAlarmFragment.editAlarm(id);
        setFragment(mAlarmFragment);
    }

    public void showPremium() {
        setFragment(mPremiumFragment);
    }

    public void showConfiguration() {
        setFragment(mConfigurationFragment);
    }

    @MainThread
    protected void onIabSetupFinished(boolean success) {
        mIabEnabled = success;

        if (!success) return;

        try {
            mIabHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    if (!result.isSuccess()) return;

                    boolean purchase = inventory.hasPurchase(SKU_PREMIUM);

                    DbHelper.getInstance()
                            .storeConfigurationField(
                                    DbContract.ConfigurationEntry.COLUMN_NAME_PREMIUM,
                                    purchase);

                    premiumChanged(purchase);
                }
            });
        } catch (IabHelper.IabAsyncInProgressException ex) {
            // IGNORE
        }
    }

    @MainThread
    protected void premiumChanged(boolean premium) {
        AssistantApplication.sPremium = premium;
    }

    @MainThread
    protected void showPremiumDialog(@StringRes int noticeId, Object... formats) {
        PremiumDialog.getInstance(getString(noticeId, formats), "premium")
                .show(getFragmentManager(), "premium_dialog");
    }

    protected void disposeIab() {
        if (null != mIabHelper) {
            try {
                mIabHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException ex) {
                // IGNORE
            }

            mIabHelper = null;
            mIabEnabled = false;
        }
    }

    protected void setupIab() {
        disposeIab();

        mIabHelper = new IabHelper(this, getString(R.string.iab_public_key_base64));
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(final IabResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.onIabSetupFinished(result.isSuccess());
                    }
                });
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, getString(R.string.ad_id));

        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));

        if (null != savedInstanceState) {
            mWalletFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), WalletFragment.class);
            mAccountFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), AccountFragment.class);
            mCredentialsFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), CredentialsFragment.class);
            mBuyFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), TradeFragment.BuyFragment.class);
            mSellFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), TradeFragment.SellFragment.class);
            mLiveTradesFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), LiveTradesFragment.class);
            mChartFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), ChartFragment.class);
            mServiceFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), ServiceFragment.class);
            mProfileFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), ProfileFragment.class);
            mAlarmsFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), AlarmsFragment.class);
            mAlarmFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), AlarmFragment.class);
            mPremiumFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), PremiumFragment.class);
            mConfigurationFragment = TaskManager.getFragmentOrCreate(getFragmentManager(), ConfigurationFragment.class);

            if (mCredentialsFragment.isAdded()
                    || mProfileFragment.isAdded()
                    || mAlarmFragment.isAdded()) {
                mShowReload = false;
            } else {
                mShowReload = true;
            }
        } else {
            mWalletFragment = new WalletFragment();
            mAccountFragment = new AccountFragment();
            mCredentialsFragment = new CredentialsFragment();
            mBuyFragment = new TradeFragment.BuyFragment();
            mSellFragment = new TradeFragment.SellFragment();
            mLiveTradesFragment = new LiveTradesFragment();
            mChartFragment = new ChartFragment();
            mServiceFragment = new ServiceFragment();
            mProfileFragment = new ProfileFragment();
            mAlarmsFragment = new AlarmsFragment();
            mAlarmFragment = new AlarmFragment();
            mPremiumFragment = new PremiumFragment();
            mConfigurationFragment = new ConfigurationFragment();

            showWallet();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupIab();

        if (!AssistantApplication.sPremium) {
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });

            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    protected void onPause() {
        disposeIab();

        mInterstitialAd.setAdListener(null);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);

        if (mShowReload) {
            menu.findItem(R.id.reload_menu_item).setVisible(true);
            menu.findItem(R.id.ok_menu_item).setVisible(false);
        } else {
            menu.findItem(R.id.reload_menu_item).setVisible(false);
            menu.findItem(R.id.ok_menu_item).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok_menu_item:
                if (mCredentialsFragment.isAdded()) {
                    String key = mCredentialsFragment.getKey();
                    String secret = mCredentialsFragment.getSecret();
                    String nip = mCredentialsFragment.getNip();

                    Bundle saveCredentialsArgs = new Bundle();
                    saveCredentialsArgs.putString("key", key);
                    saveCredentialsArgs.putString("secret", secret);
                    saveCredentialsArgs.putString("nip", nip);

                    PinDialog.getInstance(
                            getString(R.string.confirm_save_credentials_notice),
                            PinDialog.SAVE_CREDENTIALS, saveCredentialsArgs)
                            .show(getFragmentManager(), "pin_dialog");
                }

                if (mProfileFragment.isAdded()) {
                    if (mProfileFragment.validate()) {
                        Profile profile = mProfileFragment.getProfile();

                        if (null != profile) {
                            AndroidAssistant.getInstance().addProfile(profile);
                        }

                        mProfileFragment.clear();

                        showService();
                    }
                }

                if (mAlarmFragment.isAdded()) {
                    if (mAlarmFragment.validate()) {
                        Alarm alarm = mAlarmFragment.getAlarm();
                        DbHelper.getInstance().storeAlarm(alarm);

                        mAlarmFragment.clear();

                        showAlarms();
                    }
                }
                break;
            case R.id.reload_menu_item:
                if (mWalletFragment.isAdded()) {
                    requestBalances(true);
                    requestMovements(null, null, null, true);
                }

                if (mAccountFragment.isAdded()) {
                    requestAccountStatus(true);
                }

                if (mBuyFragment.isAdded() || mSellFragment.isAdded()) {
                    requestOrders(null, true);
                }

                if (mChartFragment.isAdded()) {
                    requestHistories(
                            DbHelper.getInstance().lastHistoryDate(book()), mChartFragment.range(),
                            true);
                }

                if (mLiveTradesFragment.isAdded()) {
                    mLiveTradesFragment.clear();
                    onLiveTradesRequestStartService();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_menu_item:
                showWallet();
                break;
            case R.id.account_menu_item:
                showAccount();
                break;
            case R.id.buy_menu_item:
                showBuy();
                break;
            case R.id.sell_menu_item:
                showSell();
                break;
            case R.id.trade_menu_item:
                showLiveTrades();
                break;
            case R.id.chart_menu_item:
                showChart();
                break;
            case R.id.service_menu_item:
                showService();
                break;
            case R.id.alarms_menu_item:
                showAlarms();
                break;
            case R.id.premium_menu_item:
                showPremium();
                break;
            case R.id.configuration_menu_item:
                showConfiguration();
                break;
        }

        return super.onNavigationItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mWalletFragment.isAdded()) {
            super.onBackPressed();
        } else {
            showWallet();
        }
    }

    @Override
    public void onTickers(TickerService.TickerEvent event) {
        super.onTickers(event);

        if (mWalletFragment.isAdded()) {
            mWalletFragment.updateTotalBalance(event.tickers());
        }
    }

    @Override
    protected void onBookChanged(Bitso.Book book) {
        super.onBookChanged(book);

        if (mBuyFragment.isAdded()) {
            mBuyFragment.setCurrency(book.majorCoin(), book.minorCoin());
            loadOrders(true);
        }

        if (mSellFragment.isAdded()) {
            mSellFragment.setCurrency(book.majorCoin(), book.minorCoin());
            loadOrders(true);
        }

        if (mChartFragment.isAdded()) {
            loadHistories(mChartFragment.range(), true);
        }

        if (mLiveTradesFragment.isAdded()) {
            mLiveTradesFragment.clear();
            onLiveTradesRequestStartService();
        }
    }

    @Override
    public void updateFundingDestination(FundingDestination fundingDestination) {
        super.updateFundingDestination(fundingDestination);

        if ("mxn".equals(fundingDestination.getCurrency().toLowerCase())) {
            if (mAccountFragment.isAdded()) {
                mAccountFragment.updateFundingDestination(fundingDestination);
            }
        }
    }

    @Override
    public void onTaskResult(Task task, Object result, boolean success, boolean cancelled) {
        super.onTaskResult(task, result, success, cancelled);

        String tag = task.getTag();

        switch (tag) {
            case LoadMovementsTask.TAG:
            case RequestMovementsTask.TAG:
                if (success) {
                    MovementsResult movementsResult = (MovementsResult) result;

                    if (mWalletFragment.isAdded()) {
                        mWalletFragment.setMovements(movementsResult.getTrades(), movementsResult.getFundings(), movementsResult.getWithdrawals());
                    }

                    if (mFirstLaunch && !RequestMovementsTask.TAG.equals(tag)
                            && null == movementsResult.getFundings()
                            && null == movementsResult.getTrades()
                            && null == movementsResult.getWithdrawals()) {
                        requestMovements(null, null, null, true);
                    }

                    mFirstLaunch = false;
                }
                break;
            case LoadAccountStatusTask.TAG:
            case RequestAccountStatusTask.TAG:
                if (success) {
                    AccountStatus accountStatus = (AccountStatus) result;

                    if (mAccountFragment.isAdded()) {
                        mAccountFragment.updateAccount(accountStatus);
                    }
                }
                break;
            case DownloadGravatarTask.TAG:
                if (success) {
                    Bitmap bitmap = (Bitmap) result;

                    if (mAccountFragment.isAdded()) {
                        mAccountFragment.setAvatar(bitmap);
                    }
                }
                break;
            case LoadBalancesTask.TAG:
            case RequestBalancesTask.TAG:
                if (success) {
                    List<Balance> balances = (List<Balance>) result;

                    if (mWalletFragment.isAdded()) {
                        mWalletFragment.updateBalanceChart(balances, getTickers());
                    }
                } else if (!RequestBalancesTask.TAG.equals(tag)) {
                    requestBalances(true);
                }
                break;
            case LoadOrdersTask.TAG:
            case RequestOrdersTask.TAG:
                if (success) {
                    List<Order> orders = (List<Order>) result;

                    if (mBuyFragment.isAdded()) {
                        mBuyFragment.setOrders(orders);
                    }

                    if (mSellFragment.isAdded()) {
                        mSellFragment.setOrders(orders);
                    }
                } else if (!RequestOrdersTask.TAG.equals(tag)) {
                    if (mBuyFragment.isAdded()) {
                        mBuyFragment.setOrders(new ArrayList<Order>());
                    }

                    if (mSellFragment.isAdded()) {
                        mSellFragment.setOrders(new ArrayList<Order>());
                    }

                    requestOrders(null, true);
                }
                break;
            case PlaceOrderTask.TAG:
                if (success) {
                    loadOrders(true);
                }
                break;
            case CancelOrderTask.TAG:
                if (success) {
                    String oid = result.toString();

                    if (mBuyFragment.isAdded()) {
                        mBuyFragment.removeOrder(oid);
                    }

                    if (mSellFragment.isAdded()) {
                        mSellFragment.removeOrder(oid);
                    }
                }
                break;
            case LoadHistoriesTask.TAG:
            case RequestHistoriesTask.TAG:
                if (success) {
                    List<History> histories = (List<History>) result;

                    if (mChartFragment.isAdded()) {
                        mChartFragment.updateCharts(histories);
                    }
                } else if (!RequestHistoriesTask.TAG.equals(tag)) {
                    requestHistories(
                            DbHelper.getInstance().lastHistoryDate(book()), mChartFragment.range(), true);
                }
                break;
            case ValidateApiTask.TAG:
                if (success) {
                    ValidateApiTask.ValidationResult validationResult = (ValidateApiTask.ValidationResult) result;

                    String key = validationResult.getKey();
                    String secret = validationResult.getSecret();
                    String nip = validationResult.getNip();

                    if (validationResult.isValid()) {
                        FancyToast.makeText(this,
                                getString(R.string.valid_api_key_secret),
                                FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false)
                                .show();

                        if (validationResult.isNewAccount()) {
                            Bundle newAccountArgs = new Bundle();
                            newAccountArgs.putString("key", key);
                            newAccountArgs.putString("secret", secret);
                            newAccountArgs.putString("nip", nip);

                            PinDialog.getInstance(
                                    getString(R.string.new_account_notice),
                                    PinDialog.NEW_ACCOUNT, newAccountArgs)
                                    .show(getFragmentManager(), "pin_dialog");
                        } else {
                            createAccount(key, secret, nip, false, true);
                        }
                    } else {
                        FancyToast.makeText(this,
                                getString(R.string.invalid_api_key_secret),
                                FancyToast.LENGTH_SHORT, FancyToast.ERROR, false)
                                .show();
                    }
                }
                break;
            case CreateAccountTask.TAG:
                if (success && (Boolean) result) {
                    showAccount();
                }
                break;
        }
    }

    @Override
    public void onWalletInitialized() {
        loadBalances(false, R.id.reload_menu_item);
        loadTickers(false, R.id.reload_menu_item);
        loadMovements(false, R.id.reload_menu_item);
    }

    @Override
    public void onAccountInitialized() {
        loadAccountStatus(false, R.id.reload_menu_item);
        loadFundingDestination("mxn", false, R.id.reload_menu_item);
    }

    @Override
    public void onAccountCredentialsEdit() {
        PinDialog.getInstance(
                getString(R.string.confirm_edit_credentials_notice),
                PinDialog.EDIT_CREDENTIALS)
                .show(getFragmentManager(), "pin_dialog");
    }

    @Override
    public void onTradeInitialized() {
        if (mBuyFragment.isAdded()) {
            mBuyFragment.setCurrency(book().majorCoin(), book().minorCoin());
        }

        if (mSellFragment.isAdded()) {
            mSellFragment.setCurrency(book().majorCoin(), book().minorCoin());
        }

        loadOrders(false, R.id.reload_menu_item);
    }

    @Override
    public void onTradeCurrencyChanged(String currency) {

    }

    @Override
    public void onTradeEqualsButtonClick() {
        if (mBuyFragment.isAdded()) {
            mBuyFragment.setCurrentPrice(tickerOf(book()).getLast());
        }

        if (mSellFragment.isAdded()) {
            mSellFragment.setCurrentPrice(tickerOf(book()).getLast());
        }
    }

    @Override
    public void onTradePlaceBuyOrderButtonClick(final BigDecimal amount, final BigDecimal price, final boolean minor) {
        Bundle placeOrderArgs = new Bundle();
        placeOrderArgs.putSerializable("type", OrderType.BUY);
        placeOrderArgs.putSerializable("amount", amount);
        placeOrderArgs.putSerializable("price", price);
        placeOrderArgs.putBoolean("minor", minor);

        PinDialog.getInstance(
                getString(R.string.confirm_place_order_notice),
                PinDialog.PLACE_ORDER, placeOrderArgs)
                .show(getFragmentManager(), "pin_dialog");
    }

    @Override
    public void onTradePlaceSellOrderButtonClick(final BigDecimal amount, final BigDecimal price, final boolean minor) {
        Bundle placeOrderArgs = new Bundle();
        placeOrderArgs.putSerializable("type", OrderType.SELL);
        placeOrderArgs.putSerializable("amount", amount);
        placeOrderArgs.putSerializable("price", price);
        placeOrderArgs.putBoolean("minor", minor);

        PinDialog.getInstance(
                getString(R.string.confirm_place_order_notice),
                PinDialog.PLACE_ORDER, placeOrderArgs)
                .show(getFragmentManager(), "pin_dialog");
    }

    @Override
    public void onTradeRequestDeleteOrder(final Order order) {
        Bundle deleteOrderArgs = new Bundle();
        deleteOrderArgs.putString("order_id", order.getOid());

        PinDialog.getInstance(
                getString(R.string.confirm_delete_order_notice),
                PinDialog.CANCEL_ORDER, deleteOrderArgs)
                .show(getFragmentManager(), "pin_dialog");
    }

    @Override
    public void onChartInitialized() {
        loadHistories(mChartFragment.range(), true);
    }

    @Override
    public void onChartRangeChanged(int range) {
        loadHistories(range, true);
    }

    @Override
    public void onLiveTradesInitialized() {

    }

    @Override
    public void onLiveTradesRequestStartService() {
        Intent intent = new Intent(this, WebSocketService.class);
        intent.putExtra(WebSocketService.EXTRA_BOOK, book());

        startService(intent);
    }

    @Override
    public void onLiveTradesRequestStopService() {
        Intent intent = new Intent(this, WebSocketService.class);

        startService(intent);
    }

    @Override
    public void onServiceRequestNewProfile() {
        if (!AssistantApplication.sPremium) {
            int profileCount = AndroidAssistant.getInstance().getData().getProfiles().size();

            if (MAX_PROFILES_NOT_PREMIUM <= profileCount) {
                showPremiumDialog(R.string.not_premium_profile_notice);
                return;
            }
        }

        PinDialog.getInstance(
                getString(R.string.confirm_new_profile_notice),
                PinDialog.NEW_PROFILE)
                .show(getFragmentManager(), "pin_dialog");
    }

    @Override
    public void onServiceRequestEditProfile(Profile profile) {
        Bundle deleteProfileArgs = new Bundle();
        deleteProfileArgs.putSerializable("profile", profile);

        PinDialog.getInstance(
                getString(R.string.confirm_edit_profile_notice),
                PinDialog.EDIT_PROFILE, deleteProfileArgs)
                .show(getFragmentManager(), "pin_dialog");
    }

    @Override
    public void onServiceRequestDeleteProfile(Profile profile) {
        Bundle deleteProfileArgs = new Bundle();
        deleteProfileArgs.putSerializable("profile", profile);

        PinDialog.getInstance(
                getString(R.string.confirm_delete_profile_notice),
                PinDialog.DELETE_PROFILE, deleteProfileArgs)
                .show(getFragmentManager(), "pin_dialog");
    }

    @Override
    public void onProfilePremiumError(@StringRes int noticeId, Object... formats) {
        showPremiumDialog(noticeId, formats);
    }

    @Override
    public void onAlarmsRequestNewAlarm() {
        showAlarm(null);
    }

    @Override
    public void onAlarmsRequestEditProfile(Alarm alarm) {
        showAlarm(alarm.getId());
    }

    @Override
    public void onAlarmsRequestDeleteProfile(Alarm alarm) {
        DbHelper.getInstance().deleteAlarm(alarm);

        if (mAlarmsFragment.isAdded()) {
            mAlarmsFragment.reloadAlarms();
        }
    }

    @Override
    public void onPremiumRequest() {
        if (!mIabEnabled) {
            FancyToast.makeText(AssistantApplication.getContext(),
                    getString(R.string.premium_upgrade_error),
                    Toast.LENGTH_SHORT, FancyToast.ERROR, false)
                    .show();
            return;
        }

        try {
            mIabHelper.launchPurchaseFlow(this, SKU_PREMIUM, REQUEST_PREMIUM_UPGRADE, new IabHelper.OnIabPurchaseFinishedListener() {
                @Override
                public void onIabPurchaseFinished(final IabResult result, final Purchase info) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            AssistantApplication.sPremium = result.isSuccess();

                            if (mPremiumFragment.isAdded()) {
                                mPremiumFragment.updateStatus(AssistantApplication.sPremium);
                            }

                            if (AssistantApplication.sPremium) {
                                FancyToast.makeText(AssistantApplication.getContext(),
                                        getString(R.string.premium_upgrade_success),
                                        Toast.LENGTH_SHORT, FancyToast.SUCCESS, false)
                                        .show();
                            } else {
                                FancyToast.makeText(AssistantApplication.getContext(),
                                        getString(R.string.premium_upgrade_error),
                                        Toast.LENGTH_SHORT, FancyToast.ERROR, false)
                                        .show();
                            }
                        }
                    });
                }
            });
        } catch (IabHelper.IabAsyncInProgressException ex) {
            FancyToast.makeText(AssistantApplication.getContext(),
                    getString(R.string.premium_upgrade_error),
                    Toast.LENGTH_SHORT, FancyToast.ERROR, false)
                    .show();
        }
    }

    @Override
    public void onConfigurationUpdated(String key) {
        if (getString(R.string.configuration_update_interval).equals(key)) {
            TickerService tickerService = getTickerService();

            if (null != tickerService) {
                tickerService.startTicker();
            }
        }

        if (getString(R.string.configuration_tickers).equals(key)) {
            TickerService tickerService = getTickerService();

            if (null != tickerService) {
                tickerService.updateTickers();
            }
        }
    }

    @Override
    public void onPinDialogResult(String tag, Bundle args, boolean confirmed) {
        super.onPinDialogResult(tag, args, confirmed);

        if (!confirmed) return;

        switch (tag) {
            case PinDialog.EDIT_CREDENTIALS:
                showCredentials();
                break;
            case PinDialog.SAVE_CREDENTIALS:
            case PinDialog.NEW_ACCOUNT:
                String key = args.getString("key");
                String secret = args.getString("secret");
                String nip = args.getString("nip");

                if (PinDialog.SAVE_CREDENTIALS.equals(tag)) {
                    validateApi(key, secret, nip, true);
                } else {
                    createAccount(key, secret, nip, true, true);
                }

                break;
            case PinDialog.NEW_PROFILE:
            case PinDialog.EDIT_PROFILE:
            case PinDialog.DELETE_PROFILE:
                Profile profile = (Profile) args.getSerializable("profile");

                if (PinDialog.DELETE_PROFILE.equals(tag)) {
                    AndroidAssistant.getInstance().removeProfile(profile);

                    if (mServiceFragment.isAdded()) {
                        mServiceFragment.reloadProfiles();
                    } else {
                        showService();
                    }
                } else {
                    showProfile(null != profile ? profile.getId() : null);
                }

                break;
            case PinDialog.PLACE_ORDER:
                OrderType type = (OrderType) args.getSerializable("type");
                BigDecimal amount = (BigDecimal) args.getSerializable("amount");
                BigDecimal price = (BigDecimal) args.getSerializable("price");
                boolean minor = args.getBoolean("minor");

                placeOrder(type, amount, price, minor, true);
                break;
            case PinDialog.CANCEL_ORDER:
                String oid = args.getString("order_id");

                cancelOrder(oid, true);
                break;

        }
    }

    @Override
    public void onPremiumDialogResult(String tag, boolean confirmed) {
        if (confirmed) {
            showPremium();
        }
    }

    public static Intent intent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return intent;
    }
}
