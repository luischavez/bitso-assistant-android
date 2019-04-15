package com.luischavezb.bitso.assistant.android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.FundingDestination;
import com.geometrycloud.bitso.assistant.library.Ticker;
import com.luischavezb.bitso.assistant.android.db.DbContract;
import com.luischavezb.bitso.assistant.android.db.DbHandler;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.luischavezb.bitso.assistant.android.dialog.PinDialog;
import com.luischavezb.bitso.assistant.android.service.TickerService;
import com.luischavezb.bitso.assistant.android.task.Task;
import com.luischavezb.bitso.assistant.android.task.TaskManager;
import com.luischavezb.bitso.assistant.android.task.api.CancelOrderTask;
import com.luischavezb.bitso.assistant.android.task.api.CreateAccountTask;
import com.luischavezb.bitso.assistant.android.task.api.PlaceOrderTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestAccountStatusTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestBalancesTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestFundingDestinationTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestFundingsTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestHistoriesTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestMovementsTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestOrdersTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestTickersTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestTradesTask;
import com.luischavezb.bitso.assistant.android.task.api.RequestWithdrawalsTask;
import com.luischavezb.bitso.assistant.android.task.api.ValidateApiTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadAccountStatusTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadBalancesTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadFundingDestinationTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadFundingsTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadHistoriesTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadMovementsTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadOrdersTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadTickersTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadTradesTask;
import com.luischavezb.bitso.assistant.android.task.db.LoadWithdrawalsTask;
import com.luischavezb.bitso.assistant.android.task.download.DownloadGravatarTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luischavez on 25/02/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements Task.OnTaskResult,
        NavigationView.OnNavigationItemSelectedListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,
        PinDialog.OnPinDialogResult {

    private TickerService mTickerService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TickerService.LocalBinder binder = (TickerService.LocalBinder) service;
            mTickerService = binder.getService();

            onTickerServiceChanged(mTickerService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTickerService = null;

            onTickerServiceChanged(mTickerService);
        }
    };

    private static String BOOK = "BOOK";

    private final TaskManager mTaskManager = new TaskManager();

    private Bitso.Book mBook = Bitso.Book.BTC_MXN;
    private List<Ticker> mTickers = null;
    private Ticker mTicker;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;

    private Switch mServiceSwitch;

    private ImageView mAvatarImageView;
    private TextView mUserTextView;
    private TextView mEmailTextView;

    private LinearLayout mTickerLinearLayout;
    private ImageView mTickerLeftImageView;
    private ImageView mTickerRightImageView;
    private TextView mTickerTitleTextView;
    private TextView mTickerValueTextView;
    private TextView mTickerDateTextView;

    private Button mTickerInfoButton;
    private LinearLayout mTickerContentLayout;
    private ImageView mHighImageView;
    private TextView mHighTextView;
    private ImageView mLowImageView;
    private TextView mLowTextView;
    private ImageView mAskImageView;
    private TextView mAskTextView;
    private ImageView mBidImageView;
    private TextView mBidTextView;
    private ImageView mVolumeImageView;
    private TextView mVolumeTextView;
    private TextView mAccountIdTextView;
    private TextView mAccountTextView;

    public TaskManager getTaskManager() {
        return mTaskManager;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public NavigationView getNavigationView() {
        return mNavigationView;
    }

    protected void placeOrder(OrderType orderType, BigDecimal amount, BigDecimal price, boolean minor,
                              boolean enableDialog, int... targets) {
        mTaskManager.execute(new PlaceOrderTask(orderType, mBook, amount, price, minor, enableDialog, targets));
    }

    protected void cancelOrder(String oid, boolean enableDialog, int... targets) {
        mTaskManager.execute(new CancelOrderTask(oid, enableDialog, targets));
    }

    protected void loadAccountStatus(boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadAccountStatusTask(enableDialog, targets));
    }

    protected void requestAccountStatus(boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestAccountStatusTask(enableDialog, targets));
    }

    protected void downloadAvatar(String email, boolean enableDialog) {
        mTaskManager.execute(new DownloadGravatarTask(email, enableDialog));
    }

    protected void loadTickers(boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadTickersTask(enableDialog, targets), false);
    }

    protected void requestTickers(boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestTickersTask(enableDialog, targets));
    }

    protected void loadBalances(boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadBalancesTask(enableDialog, targets));
    }

    protected void requestBalances(boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestBalancesTask(enableDialog, targets));
    }

    protected void loadFundings(boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadFundingsTask(enableDialog, targets));
    }

    protected void requestFundings(String[] fids, boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestFundingsTask(fids, enableDialog, targets));
    }

    protected void loadHistories(int range, boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadHistoriesTask(mBook, range, enableDialog, targets));
    }

    protected void requestHistories(String lastHistoryDate, int range, boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestHistoriesTask(mBook, lastHistoryDate, range, enableDialog, targets));
    }

    protected void loadOrders(boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadOrdersTask(mBook, enableDialog, targets));
    }

    protected void requestOrders(String[] oids, boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestOrdersTask(mBook, oids, enableDialog, targets));
    }

    protected void loadTrades(boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadTradesTask(enableDialog, targets));
    }

    protected void requestTrades(String marker, boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestTradesTask(marker, enableDialog, targets));
    }

    protected void loadWithdrawals(boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadWithdrawalsTask(enableDialog, targets));
    }

    protected void requestWithdrawals(String[] wids, boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestWithdrawalsTask(wids, enableDialog, targets));
    }

    protected void validateApi(String key, String secret, String nip, boolean enableDialog, int... targets) {
        mTaskManager.execute(new ValidateApiTask(key, secret, nip, enableDialog, targets));
    }

    protected void createAccount(String key, String secret, String nip, boolean newAccount, boolean enableDialog, int... targets) {
        mTaskManager.execute(new CreateAccountTask(key, secret, nip, newAccount, enableDialog, targets));
    }

    protected void loadMovements(boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadMovementsTask(enableDialog, targets));
    }

    protected void requestMovements(String[] fids, String tradeMarket, String[] wids, boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestMovementsTask(fids, tradeMarket, wids, enableDialog, targets));
    }

    protected void loadFundingDestination(String currency, boolean enableDialog, int... targets) {
        mTaskManager.execute(new LoadFundingDestinationTask(currency, enableDialog, targets));
    }

    protected void requestFundingDestination(String currency, boolean enableDialog, int... targets) {
        mTaskManager.execute(new RequestFundingDestinationTask(currency, enableDialog, targets));
    }

    protected void onTickerServiceChanged(TickerService tickerService) {
        mServiceSwitch.setOnCheckedChangeListener(null);
        mServiceSwitch.setChecked(null != tickerService && tickerService.useProfiles());
        mServiceSwitch.setOnCheckedChangeListener(this);
    }

    protected TickerService getTickerService() {
        return mTickerService;
    }

    public Ticker tickerOf(Bitso.Book book) {
        if (null == mTickers) return null;

        for (Ticker ticker : mTickers) {
            if (mBook.equals(ticker.getBook())) {
                return ticker;
            }
        }

        return null;
    }

    @MainThread
    public void updateFundingDestination(FundingDestination fundingDestination) {
        if ("mxn".equals(fundingDestination.getCurrency().toLowerCase())) return;

        mAccountIdTextView.setText(fundingDestination.getId());
        mAccountTextView.setText(fundingDestination.getAccount());
    }

    @MainThread
    public void updateTicker(Ticker ticker) {
        if (null == ticker) return;

        Bitso.Book book = ticker.getBook();

        if (null != mTicker) {
            if (!ticker.getBook().equals(mTicker.getBook())) {
                mHighImageView.setVisibility(View.GONE);
                mLowImageView.setVisibility(View.GONE);
                mAskImageView.setVisibility(View.GONE);
                mBidImageView.setVisibility(View.GONE);
                mVolumeImageView.setVisibility(View.GONE);
            } else {
                mHighImageView.setVisibility(View.VISIBLE);
                mLowImageView.setVisibility(View.VISIBLE);
                mAskImageView.setVisibility(View.VISIBLE);
                mBidImageView.setVisibility(View.VISIBLE);
                mVolumeImageView.setVisibility(View.VISIBLE);
            }

            if (0 < ticker.getHigh().compareTo(mTicker.getHigh())) {
                mHighImageView.setImageResource(R.drawable.ic_up_arrow);
            } else if (0 > ticker.getHigh().compareTo(mTicker.getHigh())) {
                mHighImageView.setImageResource(R.drawable.ic_down_arrow);
            } else {
                mHighImageView.setVisibility(View.GONE);
            }

            if (0 < ticker.getLow().compareTo(mTicker.getLow())) {
                mLowImageView.setImageResource(R.drawable.ic_up_arrow);
            } else if (0 > ticker.getLow().compareTo(mTicker.getLow())) {
                mLowImageView.setImageResource(R.drawable.ic_down_arrow);
            } else {
                mLowImageView.setVisibility(View.GONE);
            }

            if (0 < ticker.getAsk().compareTo(mTicker.getAsk())) {
                mAskImageView.setImageResource(R.drawable.ic_up_arrow);
            } else if (0 > ticker.getAsk().compareTo(mTicker.getAsk())) {
                mAskImageView.setImageResource(R.drawable.ic_down_arrow);
            } else {
                mAskImageView.setVisibility(View.GONE);
            }

            if (0 < ticker.getBid().compareTo(mTicker.getBid())) {
                mBidImageView.setImageResource(R.drawable.ic_up_arrow);
            } else if (0 > ticker.getBid().compareTo(mTicker.getBid())) {
                mBidImageView.setImageResource(R.drawable.ic_down_arrow);
            } else {
                mBidImageView.setVisibility(View.GONE);
            }

            if (0 < ticker.getVolume().compareTo(mTicker.getVolume())) {
                mVolumeImageView.setImageResource(R.drawable.ic_up_arrow);
            } else if (0 > ticker.getVolume().compareTo(mTicker.getVolume())) {
                mVolumeImageView.setImageResource(R.drawable.ic_down_arrow);
            } else {
                mVolumeImageView.setVisibility(View.GONE);
            }
        }

        mHighTextView.setText(Utilities.currencyFormat(ticker.getHigh(), book.minorCoin()));
        mLowTextView.setText(Utilities.currencyFormat(ticker.getLow(), book.minorCoin()));
        mAskTextView.setText(Utilities.currencyFormat(ticker.getAsk(), book.minorCoin()));
        mBidTextView.setText(Utilities.currencyFormat(ticker.getBid(), book.minorCoin()));
        mVolumeTextView.setText(Utilities.currencyFormat(ticker.getVolume(), book.majorCoin(), true));

        if (null == mTicker || !ticker.getBook().equals(mTicker.getBook())) {
            loadFundingDestination(ticker.getBook().majorCoin(), false);
        }

        mTicker = ticker;
    }

    @MainThread
    public void displayTicker(Ticker ticker) {
        String currency = ticker.getBook().minorCoin();

        String titleString = ticker.getBook().name();
        String valueString =
                Utilities.currencyFormat(ticker.getLast(), currency) + " " + currency;
        String dateString = Utilities.formatDateTime(ticker.getCreatedAt());

        mTickerTitleTextView.setText(titleString);
        mTickerValueTextView.setText(valueString);
        mTickerDateTextView.setText(dateString);

        updateTicker(ticker);
    }

    public List<Ticker> getTickers() {
        return mTickers;
    }

    public int tickerIndex() {
        Bitso.Book[] books = Bitso.Book.values();

        for (int i = 0; i < books.length; i++) {
            if (mBook.equals(books[i])) {
                return i;
            }
        }

        return 0;
    }

    public Bitso.Book book() {
        return mBook;
    }

    public Bitso.Book getPreferedBook() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String bookName = preferences.getString(getString(R.string.configuration_book), "BTC_MXN");

        return Bitso.Book.valueOf(bookName);
    }

    @MainThread
    protected void onBookChanged(Bitso.Book book) {

    }

    @MainThread
    public void swipeTo(Bitso.Book book) {
        mBook = book;

        onBookChanged(mBook);

        Ticker ticker = tickerOf(mBook);

        if (null != mTicker) {
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.01f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            ));

            mTickerLinearLayout.startAnimation(animationSet);

            displayTicker(mTicker);

            mTicker = ticker;
        }
    }

    @MainThread
    public void swipeLeftTicker() {
        int tickerIndex = tickerIndex();
        int tickerCount = Bitso.Book.values().length;

        int newTickerIndex = 0 == tickerIndex ? tickerCount - 1 : tickerIndex - 1;

        mBook = Bitso.Book.values()[newTickerIndex];

        onBookChanged(mBook);

        Ticker ticker = tickerOf(mBook);

        if (null != ticker) {
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.01f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            ));

            mTickerLinearLayout.startAnimation(animationSet);

            displayTicker(ticker);

            mTicker = ticker;
        }
    }

    @MainThread
    public void swipeRightTicker() {
        int tickerIndex = tickerIndex();
        int tickerCount = Bitso.Book.values().length;

        int newTickerIndex = tickerIndex == tickerCount - 1 ? 0 : tickerIndex + 1;

        mBook = Bitso.Book.values()[newTickerIndex];

        onBookChanged(mBook);

        Ticker ticker = tickerOf(mBook);

        if (null != ticker) {
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.01f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            ));

            mTickerLinearLayout.startAnimation(animationSet);

            displayTicker(ticker);

            mTicker = ticker;
        }
    }

    @MainThread
    protected void updateNavigationViewMenu(boolean valid) {
        if (valid) {
            mNavigationView.getMenu().findItem(R.id.buy_menu_item).setEnabled(true);
            mNavigationView.getMenu().findItem(R.id.sell_menu_item).setEnabled(true);
            mNavigationView.getMenu().findItem(R.id.service_menu_item).setEnabled(true);

            mServiceSwitch.setEnabled(true);
        } else {
            mNavigationView.getMenu().findItem(R.id.buy_menu_item).setEnabled(false);
            mNavigationView.getMenu().findItem(R.id.sell_menu_item).setEnabled(false);
            mNavigationView.getMenu().findItem(R.id.service_menu_item).setEnabled(false);

            mServiceSwitch.setEnabled(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @CallSuper
    public void onConfigurationChange(DbHandler.ConfigurationChangeEvent event) {
        if (DbContract.ConfigurationEntry.COLUMN_NAME_API_VALID.equals(event.getField())) {
            updateNavigationViewMenu(Boolean.valueOf(event.getValue().toString()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTickers(TickerService.TickerEvent event) {
        mTickers = event.tickers();

        Ticker ticker = tickerOf(book());

        if (null != ticker) {
            displayTicker(ticker);

            mTicker = ticker;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == DbHelper.getInstance().readAccountStatus()) {
            startActivity(IntroActivity.intent(this));
            finish();
            return;
        }

        if (null != savedInstanceState) {
            mBook = (Bitso.Book) savedInstanceState.getSerializable(BOOK);
        } else {
            mBook = getPreferedBook();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mToolbar = findViewById(R.id.toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);

        mServiceSwitch = mNavigationView.getMenu().findItem(R.id.service_menu_item)
                .getActionView().findViewById(R.id.service_switch);

        mAvatarImageView = mNavigationView.getHeaderView(0)
                .findViewById(R.id.avatar_image_view);
        mUserTextView = mNavigationView.getHeaderView(0)
                .findViewById(R.id.user_text_view);
        mEmailTextView = mNavigationView.getHeaderView(0)
                .findViewById(R.id.email_text_view);

        mTickerLinearLayout = findViewById(R.id.ticker_linear_layout);
        mTickerLeftImageView = findViewById(R.id.ticker_left_image_view);
        mTickerRightImageView = findViewById(R.id.ticker_right_image_view);
        mTickerTitleTextView = findViewById(R.id.ticker_title_text_view);
        mTickerValueTextView = findViewById(R.id.ticker_value_text_view);
        mTickerDateTextView = findViewById(R.id.ticker_date_text_view);

        mTickerInfoButton = findViewById(R.id.ticker_info_button);
        mTickerContentLayout = findViewById(R.id.ticker_content_layout);
        mHighImageView = findViewById(R.id.high_image_view);
        mHighTextView = findViewById(R.id.high_text_view);
        mLowImageView = findViewById(R.id.low_image_view);
        mLowTextView = findViewById(R.id.low_text_view);
        mAskImageView = findViewById(R.id.ask_image_view);
        mAskTextView = findViewById(R.id.ask_text_view);
        mBidImageView = findViewById(R.id.bid_image_view);
        mBidTextView = findViewById(R.id.bid_text_view);
        mVolumeImageView = findViewById(R.id.volume_image_view);
        mVolumeTextView = findViewById(R.id.volume_text_view);
        mAccountIdTextView = findViewById(R.id.account_id_text_view);
        mAccountTextView = findViewById(R.id.account_text_view);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        mTickerLeftImageView.setOnClickListener(this);
        mTickerRightImageView.setOnClickListener(this);

        mTickerLinearLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                swipeLeftTicker();
            }

            @Override
            public void onSwipeRight() {
                swipeRightTicker();
            }
        });

        mTickerInfoButton.setOnClickListener(this);

        mAccountTextView.setOnClickListener(this);

        mTaskManager.attach(this);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(this);

        mActionBarDrawerToggle.syncState();

        updateNavigationViewMenu(true);

        bindService(new Intent(this, TickerService.class), mServiceConnection, BIND_AUTO_CREATE);

        mServiceSwitch.setOnCheckedChangeListener(this);

        loadTickers(false);
        loadAccountStatus(false);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);

        mTickerLeftImageView.setOnClickListener(null);
        mTickerRightImageView.setOnClickListener(null);

        mTickerLinearLayout.setOnTouchListener(null);

        mTickerInfoButton.setOnClickListener(null);

        mAccountTextView.setOnClickListener(null);

        mTaskManager.detach();

        mDrawerLayout.removeDrawerListener(mActionBarDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(null);

        mServiceSwitch.setOnCheckedChangeListener(null);

        unbindService(mServiceConnection);

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BOOK, mBook);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTaskResult(Task task, Object result, boolean success, boolean cancelled) {
        Log.d(BaseActivity.class.getName(),
                String.format("Task: %s success: %b cancelled: %b", task.getTag(), success, cancelled));

        String tag = task.getTag();

        switch (tag) {
            case LoadAccountStatusTask.TAG:
            case RequestAccountStatusTask.TAG:
                if (success) {
                    AccountStatus accountStatus = (AccountStatus) result;

                    mUserTextView.setText(accountStatus.getFirstName() + " " + accountStatus.getLastName());
                    mEmailTextView.setText(accountStatus.getEmailStored());

                    downloadAvatar(accountStatus.getEmailStored(), false);
                } else if (!RequestAccountStatusTask.TAG.equals(tag)) {
                    requestAccountStatus(true);
                }
                break;
            case DownloadGravatarTask.TAG:
                if (success) {
                    Bitmap bitmap = (Bitmap) result;

                    mAvatarImageView.setImageBitmap(bitmap);
                }
                break;
            case LoadTickersTask.TAG:
            case RequestTickersTask.TAG:
                if (success) {
                    List<Ticker> tickers = (List<Ticker>) result;

                    onTickers(new TickerService.TickerEvent(tickers));
                } else if (!RequestTickersTask.TAG.equals(tag)) {
                    requestTickers(false);
                }
                break;
            case LoadFundingDestinationTask.TAG:
            case RequestFundingDestinationTask.TAG:
                if (success) {
                    FundingDestination fundingDestination = (FundingDestination) result;

                    updateFundingDestination(fundingDestination);
                } else if (!RequestFundingDestinationTask.TAG.equals(tag)) {
                    requestFundingDestination(book().majorCoin(), true);
                }
                break;
        }

        mTaskManager.remove(tag);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(Gravity.LEFT);

        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mServiceSwitch == buttonView) {
            Bundle changeServiceArgs = new Bundle();
            changeServiceArgs.putBoolean("start", isChecked);

            PinDialog.getInstance(
                    getString(R.string.confirm_change_service_notice),
                    PinDialog.CHANGE_SERVICE, changeServiceArgs)
                    .show(getFragmentManager(), "pin_dialog");
        }
    }

    @Override
    public void onClick(View v) {
        if (mTickerLeftImageView == v) {
            swipeLeftTicker();
        } else if (mTickerRightImageView == v) {
            swipeRightTicker();
        } else if (mTickerInfoButton == v) {
            if (View.VISIBLE == mTickerContentLayout.getVisibility()) {
                mTickerContentLayout.setVisibility(View.GONE);
                mTickerInfoButton.setText(getString(R.string.more_info));
            } else {
                mTickerContentLayout.setVisibility(View.VISIBLE);
                mTickerInfoButton.setText(getString(R.string.less_info));
            }
        } else if (mAccountTextView == v) {
            String id = mAccountIdTextView.getText().toString();
            String account = mAccountTextView.getText().toString();

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(id, account);
            clipboard.setPrimaryClip(clip);

            FancyToast.makeText(this,
                    getString(R.string.copy_to_clipboard, id), Toast.LENGTH_SHORT, FancyToast.SUCCESS, false)
                    .show();
        }
    }

    @Override
    public void onPinDialogResult(String tag, Bundle args, boolean confirmed) {
        boolean start = args.getBoolean("start");

        if (PinDialog.CHANGE_SERVICE.equals(tag)) {
            if (confirmed && null != mTickerService) {
                mTickerService.useProfiles(start);
            } else {
                mServiceSwitch.setOnCheckedChangeListener(null);
                mServiceSwitch.setChecked(!start);
                mServiceSwitch.setOnCheckedChangeListener(BaseActivity.this);
            }
        }
    }
}
