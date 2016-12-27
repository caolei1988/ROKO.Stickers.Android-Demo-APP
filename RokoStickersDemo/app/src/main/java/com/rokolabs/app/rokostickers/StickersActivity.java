package com.rokolabs.app.rokostickers;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.rokolabs.app.common.cache.HttpApiHelper;
import com.rokolabs.app.common.cache.HttpApiHelper.CallbackType;
import com.rokolabs.app.common.cache.HttpApiHelper.StringResponseListener;
import com.rokolabs.app.common.image.ImageFetcher;
import com.rokolabs.app.common.image.SharedImageFetcher;
import com.rokolabs.app.common.util.AssetsUtils;
import com.rokolabs.app.common.util.ColorUtils;
import com.rokolabs.app.common.util.LayoutUtils;
import com.rokolabs.app.common.util.ListSplitUtils;
import com.rokolabs.app.common.util.Logger;
import com.rokolabs.app.common.util.MathUtils;
import com.rokolabs.app.common.util.SharedPreHelper;
import com.rokolabs.app.common.util.Util;
import com.rokolabs.app.rokostickers.analytics.Property;
import com.rokolabs.app.rokostickers.camera.ExifUtils;
import com.rokolabs.app.rokostickers.camera.VisualView;
import com.rokolabs.app.rokostickers.camera.WatermarkHelper;
import com.rokolabs.app.rokostickers.camera.WatermarkView;
import com.rokolabs.app.rokostickers.data.IconFile;
import com.rokolabs.app.rokostickers.data.NavigationBar;
import com.rokolabs.app.rokostickers.data.Sticker;
import com.rokolabs.app.rokostickers.data.StickersList;
import com.rokolabs.app.rokostickers.data.StickersListItem;
import com.rokolabs.app.rokostickers.data.StickersSettings;
import com.rokolabs.app.rokostickers.data.StickersSettingsItem;
import com.rokolabs.app.rokostickers.data.local.StickerPackInfo;
import com.rokolabs.app.rokostickers.utils.RequestBuilder;
import com.rokolabs.app.rokostickers.widget.HorizontalListView;
import com.rokolabs.app.rokostickers.widget.LocalPackAdapter;
import com.rokolabs.app.rokostickers.widget.LocalStickerAdapter;
import com.rokolabs.app.rokostickers.widget.PortalPackAdapter;
import com.rokolabs.app.rokostickers.widget.PortalStickerAdapter;
import com.rokolabs.app.rokostickers.widget.SimpleAnimationListener;
import com.rokolabs.app.rokostickers.widget.ViewPagerAdapter;
import com.rokolabs.sdk.RokoMobi;
import com.rokolabs.sdk.analytics.Event;
import com.rokolabs.sdk.analytics.RokoLogger;
import com.rokolabs.sdk.http.Response;
import com.rokolabs.sdk.share.RokoShare;
import com.rokolabs.sdk.stickers.RokoStickers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StickersActivity extends Activity implements OnItemClickListener
{
    public final static String         ACTION_PICK_PHOTO           = "com.rokolabs.app.rokostickers.PICK_PHOTO";
    public final static String         ACTION_TAKE_PHOTO           = "com.rokolabs.app.rokostickers.TAKE_PHOTO";

    public final static String         KEY_CUSTOMIZE_UI            = "customize_ui";
    public final static String         KEY_CUSTOMIZE_STICKERS      = "customize_stickers";
    public final static String         KEY_CUSTOMIZE_STICKERS_DATA = "customize_stickers_data";
    public final static String         CUSTOMIZE_UI_FILE           = "stickers_settings.json";

    private final static String        TAG                         = "StickersActivity";
    static final int                   REQUEST_IMAGE_CAPTURE       = 1;
    static final int                   TAKE_PHOTO                  = 2;
    static final int                   PICK_PHOTO                  = 3;
    private static String              tempCaptureImgPath          = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/temp_capture.jpg";
    private static String              tempMergeImgPath            = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/temp_merge.jpg";
    private static final int           MAX_AR_NUMBER               = 100;
    private int                        current                     = 0;
    private ExecutorService            mPool                       = Executors.newSingleThreadExecutor();
    private static Bitmap.Config       BMP_CONFIG                  = Bitmap.Config.ARGB_8888;
    private HorizontalListView         mPackListView;
    private PortalPackAdapter          mPortalPackAdapter;
    private List<PortalStickerAdapter> mPortalStickerAdapterList;
    private LocalPackAdapter           mLocalPackAdapter;
    private List<LocalStickerAdapter>  mLocalStickerAdapterList;
    private ImageFetcher               mFetcher;
    private ImageFetcher               mArrowFetcher;
    private ArrayList<View>            mStickerGridViews;
    private ViewPager                  mStickerViewPager;
    private LinearLayout               mIndicatorLayout;
    private ArrayList<ImageView>       mIndicatorViews;
    private ImageView                  _stillPicture, _stillPicture1;
    private RelativeLayout             mStickerChooseLayout;
    private boolean                    isStickerShow               = true;
    private LinearLayout               mStickerLayout;
    private RelativeLayout             mDragRemoveLayout;
    private FrameLayout                mARHodlerLayout, mWMHolderLayout;
    private GestureDetector            gestureDetector;
    private ImageView                  mCloseArrow;
    private Animation                  animToUp, animToDown;
    private RelativeLayout             acSharedLayout, acEditLayout;                                                                                                     //, acGetPhotoLayout;
    private Context                    mContext;
    private TextView                   mTitleTxt, mTitle2Txt, mBackTxt, mBack2Txt, mNextTxt, mDoneTxt;
    private ImageView                  mLeftImg, mRightImg, mLeft2Img;
    private List<StickersList>         stickersLists               = new ArrayList<StickersList>();
    //private ShareSettings              shareSettings;
    private List<Property>             mStickerPlaceds             = new ArrayList<Property>();
    private List<Property>             mStickerUseds               = new ArrayList<Property>();
    private Map<Integer, Integer>      mStickerPacksUseds          = new HashMap<Integer, Integer>();
    private int                        mCurrentSelectPack          = 0;
    private StickersSettings           mStickersSettings;
    private boolean                    isCustomizeUI               = false;
    private boolean                    isCustomizeStickers         = false;
    private List<StickerPackInfo>      mStickerPackInfos           = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!Util.loadMeta(this))
        {
            finish();
            return;
        }

        setContentView(R.layout.activity_stickers);
        mContext = this;
        getExtras();
        initView();
        RokoMobi.start(this, new RokoMobi.CallbackStart() {
            @Override
            public void start() {
                initData();
                RokoLogger.addEvents(new Event("_ROKO.Stickers.Entered"));
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        deleteTempFile();
        deleteAllWaterMark();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            mLastStep = mCurrentStep;
            mCurrentStep -= 1;
            if (mCurrentStep >= 1)
                handleOnStepChanged(mLastStep, mCurrentStep);
            else
                finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO)
        {
            if (resultCode == RESULT_OK)
            {
                Uri selectedImage = data.getData();
                _stillPicture.setImageBitmap(null);
                new ProcessPickedPhoto().executeOnExecutor(mPool, selectedImage);
                handleOnStepChanged(STEP_GET_PHOTO, STEP_ADD_STICKERS);
            } else
            {
                finish();//handleOnStepChanged(STEP_GET_PHOTO, STEP_GET_PHOTO);
            }
        } else if (requestCode == TAKE_PHOTO)
        {
            if (resultCode == RESULT_OK)
            {
                _stillPicture.setImageBitmap(null);
                new ProcessPickedPhoto().executeOnExecutor(mPool, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",tempImage));
                handleOnStepChanged(STEP_GET_PHOTO, STEP_ADD_STICKERS);
            } else
            {
                finish();//handleOnStepChanged(STEP_GET_PHOTO, STEP_GET_PHOTO);
            }
        }
    }

    public void onClick(View view)
    {
        int id = view.getId();
        if (id == R.id.hide_stcikers_choose)
        {
            handlerOnArrowButtonClick();
        } else if (id == R.id.next || id == R.id.next_right_icon)
        {
            new ProcessMergePhoto().executeOnExecutor(mPool, ((BitmapDrawable) _stillPicture.getDrawable()).getBitmap());
        } else if (id == R.id.done)
        {
            insertImageIntoMediaStore(new File(tempMergeImgPath));
            trackStickerAnalytics(R.id.done);
            finish();//handleOnStepChanged(STEP_UPLOAD, STEP_GET_PHOTO);
        } else if (id == R.id.share)
        {
            sharedImage();
            trackStickerAnalytics(R.id.share);
        } else if (id == R.id.take_photo)
        {
            tempImage = new File(tempCaptureImgPath);
            Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",tempImage));
            startActivityForResult(getImageByCamera, TAKE_PHOTO);
        } else if (id == R.id.choose_photo)
        {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, PICK_PHOTO);
        }
    }

    private void initView()
    {
        float dp = getResources().getDisplayMetrics().density;
        mFetcher = SharedImageFetcher.getNewFetcher(getBaseContext(), 4);
        mFetcher.setImageSize((int) (dp * 400));

        mArrowFetcher = SharedImageFetcher.getNewFetcher(getBaseContext(), 4);
        mArrowFetcher.setImageSize((int) (dp * 40));

        mStickerGridViews = new ArrayList<View>();
        mIndicatorViews = new ArrayList<ImageView>();
        mStickerViewPager = (ViewPager) findViewById(R.id.vp_contains);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.iv_image);
        _stillPicture = (ImageView) findViewById(R.id.gr_camera_still_picture);
        _stillPicture1 = (ImageView) findViewById(R.id.gr_camera_still_picture1);
        mPackListView = (HorizontalListView) findViewById(R.id.visual_gallery);

        mARHodlerLayout = (FrameLayout) findViewById(R.id.gr_camera_ar_holder);
        mWMHolderLayout = (FrameLayout) findViewById(R.id.gr_camera_wm_holder);
        mStickerLayout = (LinearLayout) findViewById(R.id.stickers_layout);
        mStickerChooseLayout = (RelativeLayout) findViewById(R.id.stcikers_choose);
        mDragRemoveLayout = (RelativeLayout) findViewById(R.id.drag_layout);
        gestureDetector = new GestureDetector(this, new GestureListener());

        animToUp = AnimationUtils.loadAnimation(this, R.anim.translate_up);
        animToDown = AnimationUtils.loadAnimation(this, R.anim.translate_down);
        animToUp.setFillAfter(true);// 动画停止后不返回初始位置
        animToDown.setFillAfter(true);

        // init activity layout
        //acGetPhotoLayout = (RelativeLayout) findViewById(R.id.photo_action_layout);
        acSharedLayout = (RelativeLayout) findViewById(R.id.share_activity_layout);
        acEditLayout = (RelativeLayout) findViewById(R.id.sticker_activity_layout);

        //acGetPhotoLayout.setVisibility(View.VISIBLE);
        acSharedLayout.setVisibility(View.GONE);
        acEditLayout.setVisibility(View.GONE);

        mTitleTxt = (TextView) findViewById(R.id.title_edit);
        mTitle2Txt = (TextView) findViewById(R.id.title_share);
        mBackTxt = (TextView) findViewById(R.id.back_edit);
        mBack2Txt = (TextView) findViewById(R.id.back_share);
        mDoneTxt = (TextView) findViewById(R.id.done);
        mNextTxt = (TextView) findViewById(R.id.next);

        mLeftImg = (ImageView) findViewById(R.id.left_icon);
        mRightImg = (ImageView) findViewById(R.id.next_right_icon);

        mLeftImg = (ImageView) findViewById(R.id.left_icon);
        mRightImg = (ImageView) findViewById(R.id.next_right_icon);

        mLeft2Img = (ImageView) findViewById(R.id.sleft_icon);
        mCloseArrow = (ImageView) findViewById(R.id.hide_stcikers_choose);

        if (isCustomizeStickers)
        {
            mLocalStickerAdapterList = new ArrayList<LocalStickerAdapter>();
            mLocalPackAdapter = new LocalPackAdapter(this);
            mPackListView.setAdapter(mLocalPackAdapter);
            mPackListView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3)
                {
                    handlerOnLocalPackSelect(position);
                }
            });
        } else
        {
            mPortalStickerAdapterList = new ArrayList<PortalStickerAdapter>();
            mPortalPackAdapter = new PortalPackAdapter(this, mArrowFetcher);
            mPackListView.setAdapter(mPortalPackAdapter);
            mPackListView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3)
                {
                    handlerOnPackSelect(position);
                }
            });
        }
    }

    private void initData()
    {
        if (isCustomizeUI)
        {
            String responseBody = AssetsUtils.getFromAssets(CUSTOMIZE_UI_FILE, mContext);
            StickersSettingsItem data = StickersSettingsItem.getStickersSettings(responseBody);
            updateView(data.data);
        } else
        {
            getStickersSettingsData();
        }

        if (isCustomizeStickers)
        {
            mLocalPackAdapter.mItems.clear();
            mLocalPackAdapter.mItems.addAll(mStickerPackInfos);
            mLocalPackAdapter.notifyDataSetChanged();
            initSelectPackViewLocal(mStickerPackInfos.get(0).stickerInfos);
        } else
        {
            getStickersData();
        }
    }

    @SuppressWarnings("unchecked")
    private void getExtras()
    {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            isCustomizeUI = bundle.getBoolean(KEY_CUSTOMIZE_UI);
            isCustomizeStickers = bundle.getBoolean(KEY_CUSTOMIZE_STICKERS);
            mStickerPackInfos = (List<StickerPackInfo>) getIntent().getSerializableExtra(KEY_CUSTOMIZE_STICKERS_DATA);
            Log.v(TAG, "isCustomizeUI = " + isCustomizeUI + " , isCustomizeStickers = " + isCustomizeStickers + " , mStickerPackInfos = " + mStickerPackInfos.size());
        }
        if (ACTION_PICK_PHOTO.equals(getIntent().getAction()))
        {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, PICK_PHOTO);
        } else if (ACTION_TAKE_PHOTO.equals(getIntent().getAction()))
        {
            tempImage = new File(tempCaptureImgPath);
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",tempImage));
            startActivityForResult(getImageByCamera, TAKE_PHOTO);

        }
    }

    public void onBack(View view)
    {
        if (mCurrentStep == 1)
        {
            finish();
            return;
        }
        mLastStep = mCurrentStep;
        mCurrentStep -= 1;
        handleOnStepChanged(mLastStep, mCurrentStep);
    }

    class ProcessPickedPhoto extends AsyncTask<Uri, Void, Bitmap>
    {
        boolean showFlipHint = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Uri... arg0)
        {
            try
            {
                Uri imgUri = arg0[0];
                Log.d(TAG, "Picked: " + imgUri.toString());
                InputStream imageStream = null;
                imageStream = getContentResolver().openInputStream(imgUri);

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                opt.inPreferredConfig = BMP_CONFIG;

                BitmapFactory.decodeStream(imageStream, null, opt);
                imageStream.close();

                if (opt.outWidth == -1 || opt.outHeight == -1)
                    return null;
                int maxRatio = MathUtils.calcRatio(opt.outWidth, opt.outHeight, mContext);

                opt.inJustDecodeBounds = false;
                opt.inSampleSize = maxRatio;

                Log.d(TAG, "Decode picked image: " + opt.outWidth + " x " + opt.outHeight + " | sampleSize: " + opt.inSampleSize);

                imageStream = getContentResolver().openInputStream(imgUri);
                Bitmap pictureBitmap = BitmapFactory.decodeStream(imageStream, null, opt);
                imageStream.close();

                if (pictureBitmap == null)
                    return null;

                int pictureWidth = pictureBitmap.getWidth();
                int pictureHeight = pictureBitmap.getHeight();

                Matrix mx = new Matrix();

                int orientation = ExifUtils.getExifOrientation(getApplicationContext(), imgUri);
                if (orientation == 90 || orientation == 270)
                {
                    pictureWidth = pictureBitmap.getHeight();
                    pictureHeight = pictureBitmap.getWidth();
                }
                mx.postTranslate(-pictureBitmap.getWidth() / 2, -pictureBitmap.getHeight() / 2);
                mx.postRotate(orientation);
                mx.postTranslate(pictureWidth / 2, pictureHeight / 2);

                Bitmap mergedBitmap = Bitmap.createBitmap(pictureWidth, pictureHeight, BMP_CONFIG);

                Canvas mergeCanvas = new Canvas(mergedBitmap);
                mergeCanvas.drawBitmap(pictureBitmap, mx, null);

                pictureBitmap.recycle();
                pictureBitmap = null;
                return mergedBitmap;
            } catch (Throwable e)
            {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result)
        {
            if (result != null)
            {
                _stillPicture.setImageBitmap(result);
            }
        }
    }

    class ProcessMergePhoto extends AsyncTask<Bitmap, Void, Bitmap>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            tempMergeImgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + System.currentTimeMillis() + "_temp_merge.jpg";
        }

        @Override
        protected void onPostExecute(Bitmap result)
        {
            super.onPostExecute(result);
            _stillPicture1.setImageBitmap(result);
            handleOnStepChanged(STEP_ADD_STICKERS, STEP_UPLOAD);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params)
        {
            Bitmap pictureBitmap = params[0];
            if (pictureBitmap == null)
                return null;

            int pictureWidth = pictureBitmap.getWidth();
            int pictureHeight = pictureBitmap.getHeight();
            Log.v(TAG, "pictureWidth = " + pictureWidth + " , pictureHeight = " + pictureHeight);

            Matrix mx = new Matrix();

            Bitmap mergedBitmap = Bitmap.createBitmap(pictureWidth, pictureHeight, BMP_CONFIG);
            Canvas mergeCanvas = new Canvas(mergedBitmap);

            mergeCanvas.drawBitmap(pictureBitmap, mx, null);
            for (VisualView vv : iViews)
            {
                float halfW = vv.getDrawable().getIntrinsicWidth() / 2.0f;
                float halfH = vv.getDrawable().getIntrinsicHeight() / 2.0f;

                float transX = vv.transX;
                float transY = vv.transY;

                Matrix mxImgTransform = new Matrix();
                mxImgTransform.postTranslate(-halfW, -halfH);
                if (vv.shouldFlip)
                {
                    mxImgTransform.postScale(-1, 1, 1, -1);
                }
                mxImgTransform.postRotate(vv.rotate);

                mxImgTransform.postScale(vv.scale, vv.scale);
                mxImgTransform.postTranslate(transX, transY);

                Matrix inverse = new Matrix();
                _stillPicture.getImageMatrix().invert(inverse);
                mxImgTransform.postConcat(inverse);
                if (vv.getDrawable() instanceof BitmapDrawable && ((BitmapDrawable) vv.getDrawable()).getBitmap() != null)
                    mergeCanvas.drawBitmap(((BitmapDrawable) vv.getDrawable()).getBitmap(), mxImgTransform, null);
            }

            for (WatermarkView wv : wViews)
            {
                if (wv.getDrawable() instanceof BitmapDrawable)
                {
                    BitmapDrawable watermarkDrawable = (BitmapDrawable) wv.getDrawable();
                    if (watermarkDrawable.getBitmap() != null)
                    {
                        Matrix ovlMx = wv.mWatermark.createPositioningMergeMatrix(watermarkDrawable.getIntrinsicWidth(), watermarkDrawable.getIntrinsicHeight(), pictureWidth, pictureHeight, 0, (float) pictureWidth / wv.getWidth());
                        mergeCanvas.drawBitmap(((BitmapDrawable) wv.getDrawable()).getBitmap(), ovlMx, null);
                    }
                }
            }

            mergeCanvas = null;
            saveImageToTemp(mergedBitmap);
            fbPreviewPic = mergedBitmap;
            return fbPreviewPic;
        }
    }

    protected void saveImageToTemp(Bitmap bmp)
    {
        Log.v(TAG, "saveImageToTemp");
        try
        {
            File tempImage = new File(tempMergeImgPath);
            if (tempImage.exists())
                tempImage.delete();
            OutputStream imageFileOS = new FileOutputStream(tempImage);
            bmp.compress(CompressFormat.JPEG, COMPRESSION_QUALITY, imageFileOS);
            imageFileOS.flush();
            imageFileOS.close();
        } catch (Exception e)
        {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void insertImageIntoMediaStore(File imgFile)
    {
        ContentValues newValues = new ContentValues(6);
        newValues.put(MediaStore.Images.Media.TITLE, imgFile.getName());
        newValues.put(MediaStore.Images.Media.DISPLAY_NAME, imgFile.getName());
        newValues.put(MediaStore.Images.Media.DATA, imgFile.getPath());
        newValues.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000);
        newValues.put(MediaStore.Images.Media.SIZE, imgFile.length());
        newValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
    }

    private Bitmap                          fbPreviewPic        = null;
    private Uri                             mImageUri;
    private File                            tempImage;
    private static final int                COMPRESSION_QUALITY = 100;

    List<VisualView>                        iViews              = new ArrayList<VisualView>();
    List<WatermarkView>                     wViews              = new ArrayList<WatermarkView>();
    List<WatermarkHelper>                   mHelpers            = new ArrayList<WatermarkHelper>();
    SparseArray<PointerTrack>               mPointMap           = new SparseArray<PointerTrack>();
    HashMap<VisualView, List<PointerTrack>> mViewTrackMap       = new HashMap<VisualView, List<PointerTrack>>();

    private static final int                STEP_GET_PHOTO      = 0;
    private static final int                STEP_ADD_STICKERS   = 1;
    private static final int                STEP_UPLOAD         = 2;
    private int                             mCurrentStep        = STEP_GET_PHOTO;
    private int                             mLastStep           = STEP_GET_PHOTO;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int index = event.getActionIndex();
        float x = event.getX(index);
        float y = event.getY(index);

        int id = event.getPointerId(index);
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                for (int i = iViews.size() - 1; i >= 0; i--)
                {
                    VisualView iView = iViews.get(i);
                    if (iView.isOnImage(x, y))
                    {
                        PointerTrack track = new PointerTrack();
                        track.imageView = iView;
                        track.pointerId = id;
                        track.lastX = x;
                        track.lastY = y;
                        mPointMap.put(id, track);
                        List<PointerTrack> ptl = mViewTrackMap.get(iView);
                        if (ptl == null)
                        {
                            ptl = new ArrayList<PointerTrack>();
                            mViewTrackMap.put(iView, ptl);
                        }
                        if (ptl.size() < 2)
                            ptl.add(track);
                        showDragRemoveZone();
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                for (Entry<VisualView, List<PointerTrack>> entry : mViewTrackMap.entrySet())
                {
                    List<PointerTrack> activePtl = new ArrayList<PointerTrack>();
                    for (PointerTrack pt : entry.getValue())
                    {
                        if (event.findPointerIndex(pt.pointerId) != -1)
                            activePtl.add(pt);
                    }
                    if (activePtl.size() == 1)
                    {
                        PointerTrack pTrack = activePtl.get(0);
                        int pid = event.findPointerIndex(pTrack.pointerId);
                        pTrack.move(event.getX(pid), event.getY(pid));
                    } else if (activePtl.size() > 1)
                    {
                        PointerTrack pTrack1 = activePtl.get(0);
                        PointerTrack pTrack2 = activePtl.get(1);
                        int idx1 = event.findPointerIndex(pTrack1.pointerId);
                        int idx2 = event.findPointerIndex(pTrack2.pointerId);
                        float x1 = event.getX(idx1);
                        float y1 = event.getY(idx1);
                        float x2 = event.getX(idx2);
                        float y2 = event.getY(idx2);

                        float space0 = MathUtils.calcSpace(pTrack1.lastX, pTrack1.lastY, pTrack2.lastX, pTrack2.lastY);
                        float space1 = MathUtils.calcSpace(x1, y1, x2, y2);

                        float r0 = MathUtils.calcAngle(pTrack1.lastX, pTrack1.lastY, pTrack2.lastX, pTrack2.lastY);
                        float r1 = MathUtils.calcAngle(x1, y1, x2, y2);

                        float dscale = 1f;
                        if (space0 != 0)
                            dscale = space1 / space0;
                        float dx = (x1 + x2 - pTrack1.lastX - pTrack2.lastX) / 2;
                        float dy = (y1 + y2 - pTrack1.lastY - pTrack2.lastY) / 2;

                        float dr = r1 - r0;

                        // Log.d(TAG, String.format("dx %f, dy %f, dscale %f, dr %f", dx, dy, dscale, dr));
                        pTrack1.imageView.move(dx, dy, dscale, dr);
                        pTrack1.lastX = x1;
                        pTrack1.lastY = y1;
                        pTrack2.lastX = x2;
                        pTrack2.lastY = y2;
                    }
                    updateDragRemoveZone(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                for (Entry<VisualView, List<PointerTrack>> entry : mViewTrackMap.entrySet())
                {
                    List<PointerTrack> activePtl = new ArrayList<PointerTrack>();
                    for (PointerTrack pt : entry.getValue())
                    {
                        if (event.findPointerIndex(pt.pointerId) != -1)
                            activePtl.add(pt);
                    }
                    if (activePtl.size() == 1)
                    {
                        PointerTrack pTrack = activePtl.get(0);
                        int pid = event.findPointerIndex(pTrack.pointerId);
                        removeStickerIfInDragZone(pid, (int) event.getX(pid), (int) event.getY(pid));
                    } else if (activePtl.size() > 1)
                    {
                        PointerTrack pTrack1 = activePtl.get(0);
                        PointerTrack pTrack2 = activePtl.get(1);
                        int idx1 = event.findPointerIndex(pTrack1.pointerId);
                        int idx2 = event.findPointerIndex(pTrack2.pointerId);
                        removeStickerIfInDragZone(idx1, (int) event.getX(idx1), (int) event.getY(idx1));
                        removeStickerIfInDragZone(idx2, (int) event.getX(idx2), (int) event.getY(idx2));
                    }
                }
                hideDragRemoveZone();
                break;
            case MotionEvent.ACTION_CANCEL:
                mPointMap.clear();
                mViewTrackMap.clear();
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void removeStickerIfInDragZone(int pid, int x, int y)
    {
        PointerTrack pt = mPointMap.get(pid);
        if (pt != null)
        {
            boolean inDragZone = isInDeleteZone(x, y);
            Log.v(TAG, "inDragZone ============ " + inDragZone);
            if (inDragZone)
            {
                iViews.remove(pt.imageView);
                mARHodlerLayout.removeView(pt.imageView);
                if (!isCustomizeStickers)
                {
                    mStickerUseds.remove(pt.imageView.getProperty());
                    removeWaterMark((Integer) pt.imageView.mProperty.get("positionInPack"));
                }
                if (iViews.size() == 0)
                    hideDragRemoveZone();
            }
            mPointMap.remove(pid);
            mViewTrackMap.get(pt.imageView).remove(pt);
        }
    }

    private void initSelectPackView(StickersList datas)
    {
        initGridView(datas);
        initIndicator();
        initStickerViewPager();
    }

    private void initGridView(StickersList item)
    {
        mStickerGridViews.clear();
        View nullView1 = new View(this);
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        mStickerGridViews.add(nullView1);

        mPortalStickerAdapterList.clear();
        List<Sticker> stickers = item.stickers;
        int pageCount = stickers.size() / 10 + ((stickers.size() % 10) == 0 ? 0 : 1);

        for (int i = 0; i < pageCount; i++)
        {
            PortalStickerAdapter adapter = new PortalStickerAdapter(this, ListSplitUtils.splitStickerList(stickers, i + 1), mFetcher);
            GridView view = createStickerGridView();
            view.setAdapter(adapter);
            mStickerGridViews.add(view);
            mPortalStickerAdapterList.add(adapter);
        }

        View nullView2 = new View(this);
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        mStickerGridViews.add(nullView2);
    }

    private void initSelectPackViewLocal(List<Integer> items)
    {
        initGridViewLocal(items);
        initIndicator();
        initStickerViewPager();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mStickerGridViews != null){
            int size = getGridSize();
            for(View gv : mStickerGridViews)
                if(gv instanceof GridView){
                    ((GridView)gv).setNumColumns(size);
                }
        }
        android.view.ViewGroup.LayoutParams lp = mStickerChooseLayout.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.face_layout_height);
        mStickerChooseLayout.setLayoutParams(lp);
    }

    private void initGridViewLocal(List<Integer> items)
    {
        mStickerGridViews.clear();
        View nullView1 = new View(this);
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        mStickerGridViews.add(nullView1);

        mLocalStickerAdapterList.clear();
        int pageCount = items.size() / 10 + ((items.size() % 10) == 0 ? 0 : 1);

        for (int i = 0; i < pageCount; i++)
        {
            LocalStickerAdapter adapter = new LocalStickerAdapter(this, ListSplitUtils.splitIntegerList(items, i + 1));
            GridView view = createStickerGridView();
            view.setAdapter(adapter);
            mStickerGridViews.add(view);
            mLocalStickerAdapterList.add(adapter);
        }

        View nullView2 = new View(this);
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        mStickerGridViews.add(nullView2);
    }

    private void initIndicator()
    {
        mIndicatorViews.clear();
        mIndicatorLayout.removeAllViews();
        ImageView imageView;
        for (int i = 0; i < mStickerGridViews.size(); i++)
        {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            mIndicatorLayout.addView(imageView, layoutParams);
            if (i == 0 || i == mStickerGridViews.size() - 1)
            {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1)
            {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            mIndicatorViews.add(imageView);
        }
    }

    private void initStickerViewPager()
    {
        current = 0;
        mStickerViewPager.setAdapter(new ViewPagerAdapter(mStickerGridViews));
        mStickerViewPager.setCurrentItem(1);
        mStickerViewPager.setOnPageChangeListener(new OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                current = position - 1;
                updateIndicator(position);
                if (position == mIndicatorViews.size() - 1 || position == 0)
                {
                    if (position == 0)
                    {
                        mStickerViewPager.setCurrentItem(position + 1);
                        mIndicatorViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else
                    {
                        mStickerViewPager.setCurrentItem(position - 1);
                        mIndicatorViews.get(position - 1).setBackgroundResource(R.drawable.d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
            }

            @Override
            public void onPageScrollStateChanged(int arg0)
            {
            }
        });
    }

    public void updateIndicator(int index)
    {
        for (int i = 1; i < mIndicatorViews.size(); i++)
        {
            if (index == i)
                mIndicatorViews.get(i).setBackgroundResource(R.drawable.d2);
            else
                mIndicatorViews.get(i).setBackgroundResource(R.drawable.d1);
        }
    }

    private void handlerOnArrowButtonClick()
    {
        if (isStickerShow)
        {
            hideFaceGallery();
            isStickerShow = false;
        } else
        {
            showFaceGallery();
            isStickerShow = true;
        }
    }

    public void handlerOnPackSelect(int position)
    {
        mPortalPackAdapter.setSelectChild(position);
        mPortalPackAdapter.notifyDataSetChanged();
        initSelectPackView(stickersLists.get(position));
        mCurrentSelectPack = position;
        if (!isStickerShow)
        {
            showFaceGallery();
            isStickerShow = true;
        }
    }

    public void handlerOnLocalPackSelect(int position)
    {
        mLocalPackAdapter.setSelectChild(position);
        mLocalPackAdapter.notifyDataSetChanged();
        initSelectPackViewLocal(mStickerPackInfos.get(position).stickerInfos);
        mCurrentSelectPack = position;
        if (!isStickerShow)
        {
            showFaceGallery();
            isStickerShow = true;
        }
    }

    private void handlerOnStickerSelect(int position)
    {
        Sticker sticker = (Sticker) mPortalStickerAdapterList.get(current).getItem(position);
        if (sticker != null && iViews.size() < MAX_AR_NUMBER)
        {
            final VisualView vv = new VisualView(this, /* visual, */false);
            mARHodlerLayout.addView(vv, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            iViews.add(vv);
            mFetcher.loadImage(sticker.imageFileGroup.files.get(0).file.url, vv);
            StickersList list = stickersLists.get(mCurrentSelectPack);
            Property property = new Property(sticker.objectId, list.objectId, list.name, mCurrentSelectPack, String.valueOf(sticker.objectId), false);
            vv.setProperty(property);
            mStickerPlaceds.add(property);
            RokoLogger.addEvents(new Event("_ROKO.Stickers.Placed", property));
            mStickerUseds.add(property);
            addWatermark(mCurrentSelectPack);
        }
    }

    private void handlerOnLocalStickerSelect(int position)
    {
        Integer stickerRes = (Integer) mLocalStickerAdapterList.get(current).getItem(position);
        if (stickerRes != null && iViews.size() < MAX_AR_NUMBER)
        {
            final VisualView vv = new VisualView(this, /* visual, */false);
            vv.setImageResource(stickerRes);
            mARHodlerLayout.addView(vv, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            iViews.add(vv);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
    {
        if (isCustomizeStickers)
            handlerOnLocalStickerSelect(position);
        else
            handlerOnStickerSelect(position);
    }

    static class PointerTrack
    {
        int        pointerId;
        VisualView imageView;
        float      lastX;
        float      lastY;

        public void move(float x, float y)
        {
            imageView.move(x - lastX, y - lastY, 1, 0);
            lastX = x;
            lastY = y;
        }
    }

    private void handleOnStepChanged(int fromStep, int currentStep)
    {
        Log.v(TAG, "handleOnStepChanged :  currentStep = " + currentStep);
        mCurrentStep = currentStep;

        switch (currentStep)
        {
            case STEP_GET_PHOTO:
                if (iViews != null && iViews.size() > 0)
                {
                    for (View vv : iViews)
                    {
                        mARHodlerLayout.removeView(vv);
                    }
                    iViews.clear();
                }
                //acGetPhotoLayout.setVisibility(View.VISIBLE);
                acSharedLayout.setVisibility(View.GONE);
                acEditLayout.setVisibility(View.GONE);
                deleteTempFile();
                deleteAllWaterMark();
                break;
            case STEP_ADD_STICKERS:
                //acGetPhotoLayout.setVisibility(View.GONE);
                acEditLayout.setVisibility(View.VISIBLE);
                if (fromStep == STEP_UPLOAD)
                {
                    TranslateAnimation mHideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                    mHideAnimation.setDuration(300L);
                    mHideAnimation.setAnimationListener(new SimpleAnimationListener()
                    {
                        @Override
                        public void onAnimationEnd(Animation arg0)
                        {

                            acSharedLayout.setVisibility(View.GONE);
                        }
                    });
                    acSharedLayout.startAnimation(mHideAnimation);
                } else
                {
                    //acGetPhotoLayout.setVisibility(View.GONE);
                    acSharedLayout.setVisibility(View.GONE);
                }
                break;
            case STEP_UPLOAD:
                //acGetPhotoLayout.setVisibility(View.GONE);
                acSharedLayout.setVisibility(View.VISIBLE);
                TranslateAnimation mShowAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                mShowAnimation.setDuration(300L);
                mShowAnimation.setAnimationListener(new SimpleAnimationListener()
                {
                    @Override
                    public void onAnimationEnd(Animation arg0)
                    {
                        acEditLayout.setVisibility(View.GONE);
                    }
                });
                acSharedLayout.startAnimation(mShowAnimation);
                break;
        }
    }

    boolean mDeleteMode = false;

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public void onLongPress(MotionEvent e)
        {
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            if (!mDeleteMode)
                return false;
            int index = e.getActionIndex();
            float x = e.getX(index);
            float y = e.getY(index);
            VisualView vv = null;
            for (int i = iViews.size() - 1; i >= 0; i--)
            {
                VisualView iView = iViews.get(i);
                if (iView.isOnImage(x, y))
                {
                    vv = iView;
                    break;
                }
            }
            if (vv == null)
            {
                stopDeleteMode();
            } else
            {
                vv.clearAnimation();
                iViews.remove(vv);
                ((FrameLayout) vv.getParent()).removeView(vv);
                if (iViews.size() == 0)
                    mDeleteMode = false;
            }
            return true;
        }

        public boolean onDoubleTap(MotionEvent event)
        {
            int index = event.getActionIndex();
            float x = event.getX(index);
            float y = event.getY(index);
            for (int i = iViews.size() - 1; i >= 0; i--)
            {
                VisualView iView = iViews.get(i);
                if (iView.isOnImage(x, y))
                {
                    iView.flip();
                    return true;
                }
            }
            return false;
        }
    };

    void stopDeleteMode()
    {
        mDeleteMode = false;
        for (int i = iViews.size() - 1; i >= 0; i--)
        {
            iViews.get(i).setDeleteMode(mDeleteMode);
        }
    }

    void deleteTempFile()
    {
        if (new File(tempCaptureImgPath).exists())
            new File(tempCaptureImgPath).delete();
    }

    void deleteAllWaterMark()
    {
        wViews.clear();
        mStickerPacksUseds.clear();
        mWMHolderLayout.removeAllViews();
    }

    private Rect mRectSrc;

    private void updateDragRemoveZone(MotionEvent event)
    {
        if (mCurrentStep == STEP_ADD_STICKERS)
        {
            boolean inDragZone = isInDeleteZone((int) event.getX(), (int) event.getY());
            if (inDragZone)
                mDragRemoveLayout.setAlpha(1.0f);
            else
                mDragRemoveLayout.setAlpha(0.5f);
        }
    }

    boolean isInDeleteZone(int x, int y)
    {
        return mRectSrc.contains((int) x, (int) y) || y <= mRectSrc.top;
    }

    private void showDragRemoveZone()
    {
        if (mCurrentStep == STEP_ADD_STICKERS)
        {
            mDragRemoveLayout.setVisibility(View.VISIBLE);
            mRectSrc = new Rect();

            Log.d("demo", "Button.Width--->" + mDragRemoveLayout.getWidth());
            Log.d("demo", "Button.Height--->" + mDragRemoveLayout.getHeight());

            mDragRemoveLayout.getLocalVisibleRect(mRectSrc);
            Log.d("demo", "LocalVisibleRect--->" + mRectSrc.left + " , " + mRectSrc.top + " , " + mRectSrc.right + " , " + mRectSrc.bottom);

            mDragRemoveLayout.getGlobalVisibleRect(mRectSrc);
            Log.d("demo", "GlobalVisibleRect--->" + mRectSrc.left + " , " + mRectSrc.top + " , " + mRectSrc.right + " , " + mRectSrc.bottom);
        }
    }

    private void hideDragRemoveZone()
    {
        if (mCurrentStep == STEP_ADD_STICKERS)
        {
            mDragRemoveLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void showFaceGallery()
    {
        mCloseArrow.setImageResource(R.drawable.close_arrow);
        if(mStickersSettings != null && mStickersSettings.tray != null && mStickersSettings.tray.closeButtonImageFileGroup!=null)
            mArrowFetcher.loadImage(mStickersSettings.tray.closeButtonImageFileGroup.getFirstUrl(), mCloseArrow);
        mStickerViewPager.setVisibility(View.VISIBLE);
        ObjectAnimator oa = ObjectAnimator.ofFloat(mStickerLayout, "translationY", 0);
        oa.setDuration(200L);
        oa.start();
    }

    private void hideFaceGallery()
    {
        mCloseArrow.setImageResource(R.drawable.open_arrow);
        if(mStickersSettings != null && mStickersSettings.tray != null && mStickersSettings.tray.openButtonImageFileGroup!=null)
            mArrowFetcher.loadImage(mStickersSettings.tray.openButtonImageFileGroup.getFirstUrl(), mCloseArrow);
        ObjectAnimator oa = ObjectAnimator.ofFloat(mStickerLayout, "translationY", getResources().getDimension(R.dimen.face_layout_height));
        oa.setDuration(200L);
        oa.start();
        oa.addListener(new AnimatorListener()
        {

            @Override
            public void onAnimationStart(Animator animation)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mStickerViewPager.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                // TODO Auto-generated method stub

            }
        });
    }

    private void sharedImage()
    {
        RokoShare ex = new RokoShare(StickersActivity.this,"sticker");
        ex.contentType = "_ROKO.ImageWithStickers";
        ex.preview = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(tempMergeImgPath));
        ex.image = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(tempMergeImgPath));
        ex.show(this);
    }

    private void getStickersData()
    {
        RokoStickers.getStickerpacks(null, "resolve=stickers,watermark&limit=100", new RokoStickers.CallbackRokoStickers() {
            @Override
            public void success(Response response) {
                String content = response.body;
                StickersListItem data = StickersListItem.getStickersListItem(content);
                if (content != null)
                {
                    Logger.e("handleSuccessMessage() responseBody: stickers'size" + data.data.size());
                    SharedPreHelper.getInstance(mContext).setString(SharedPreHelper.STICKERS, content);
                    stickersLists = data.getActiveStickers();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mPortalPackAdapter.mItems.clear();
                            mPortalPackAdapter.mItems.addAll(stickersLists);
                            mPortalPackAdapter.notifyDataSetChanged();
                            handlerOnPackSelect(0);
                        }
                    });
                }
            }

            @Override
            public void failure(Response response) {

            }
        });
//        RequestBuilder req = new RequestBuilder();
//        req.appendPath(Util.getHttpHost());
//        req.appendPath("stickers/stickerpacks?resolve=stickers,watermark&limit=100");
//
//        HttpApiHelper.callAPI(mContext, CallbackType.CacheFirst, req.toString(), null, new StringResponseListener()
//        {
//
//            @Override
//            public void onResponse(String content, boolean isCacheData)
//            {
//                StickersListItem data = StickersListItem.getStickersListItem(content);
//                if (content != null)
//                {
//                    Logger.e("handleSuccessMessage() responseBody: stickers'size" + data.data.size());
//                    SharedPreHelper.getInstance(mContext).setString(SharedPreHelper.STICKERS, content);
//                    stickersLists = data.getActiveStickers();
//                    mPortalPackAdapter.mItems.clear();
//                    mPortalPackAdapter.mItems.addAll(stickersLists);
//                    mPortalPackAdapter.notifyDataSetChanged();
//                    //pd.dismiss();
//                    handlerOnPackSelect(0);
//                }
//            }
//
//            @Override
//            public void onBackgrounResponse(String content, boolean isCacheData)
//            {
//                // TODO Auto-generated method stub
//
//            }
//
//        });
    }

    private void getStickersSettingsData()
    {
        RequestBuilder req = new RequestBuilder();
        req.appendPath(Util.getHttpHost());
        req.appendPath("stickers/settings?resolve=watermark");

        HttpApiHelper.callAPI(this, CallbackType.CacheFirst, req.toString(), null, new StringResponseListener()
        {

            @Override
            public void onResponse(String content, boolean isCacheData)
            {
                StickersSettingsItem data = StickersSettingsItem.getStickersSettings(content);
                updateView(data.data);
            }

            @Override
            public void onBackgrounResponse(String content, boolean isCacheData)
            {
                // TODO Auto-generated method stub

            }


        });
    }

    private void trackStickerAnalytics(int triggerFrom)
    {
        List<Event> events = new ArrayList<Event>();

        Event eventPlaced = null;
//        for (int i = 0; i < mStickerPlaceds.size(); i++)
//        {
//            eventPlaced = new Event("_ROKO.Stickers.Placed", mStickerPlaceds.get(i));
//            events.add(eventPlaced);
//        }

        for (int i = 0; i < mStickerUseds.size(); i++)
        {
            eventPlaced = new Event("_ROKO.Stickers.Used", mStickerUseds.get(i));
            events.add(eventPlaced);
        }

        if (triggerFrom == R.id.share)
        {
            for (int i = 0; i < mStickerUseds.size(); i++)
            {
                eventPlaced = new Event("_ROKO.Stickers.Shared", mStickerUseds.get(i));
                events.add(eventPlaced);
            }
        }

        RokoLogger.addEvents(events);

        mStickerUseds.clear();
        mStickerPlaceds.clear();
    }

    private void updateView(StickersSettings data)
    {
        mStickersSettings = data;
        NavigationBar nav = data.navigationBar;
        mTitleTxt.setText(nav.title);
        mTitleTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, nav.titleFont.size);
        mTitleTxt.setTextColor(ColorUtils.getColor(nav.titleFont.color));
        mTitle2Txt.setText(nav.title);
        mTitle2Txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, nav.titleFont.size);
        mTitle2Txt.setTextColor(ColorUtils.getColor(nav.titleFont.color));
        mDoneTxt.setText("DONE");
        mDoneTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, nav.buttonFont.size);
        mDoneTxt.setTextColor(ColorUtils.getColor(nav.buttonFont.color));
        mCloseArrow.setImageResource(R.drawable.close_arrow);
        if(data.tray.closeButtonImageFileGroup != null)
            mArrowFetcher.loadImage(data.tray.closeButtonImageFileGroup.getFirstUrl(), mCloseArrow);

        ((View) mTitleTxt.getParent()).setBackgroundColor(ColorUtils.getColor(nav.backgroundColor));
        ((View) mTitle2Txt.getParent()).setBackgroundColor(ColorUtils.getColor(nav.backgroundColor));
        findViewById(R.id.gallery_layout).setBackgroundColor(ColorUtils.getColor(data.tray.backgroundColor));

        if (data.navigationBar.useTextForButtons)
        {
            mBackTxt.setText(nav.leftButtonText);
            mBackTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, nav.buttonFont.size);
            mBackTxt.setTextColor(ColorUtils.getColor(nav.buttonFont.color));
            mBack2Txt.setText(nav.leftButtonText);
            mBack2Txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, nav.buttonFont.size);
            mBack2Txt.setTextColor(ColorUtils.getColor(nav.buttonFont.color));
            mNextTxt.setText(nav.rightButtonText);
            mNextTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, nav.buttonFont.size);
            mNextTxt.setTextColor(ColorUtils.getColor(nav.buttonFont.color));
            mRightImg.setVisibility(View.GONE);
            mLeftImg.setVisibility(View.GONE);
            mLeft2Img.setVisibility(View.GONE);
        } else
        {
//            mArrowFetcher.loadImage(data.navigationBar.rightButtonImageFileGroup.getFirstUrl(), mRightImg);
//            mArrowFetcher.loadImage(data.navigationBar.leftButtonImageFileGroup.getFirstUrl(), mLeft2Img);
//            mArrowFetcher.loadImage(data.navigationBar.leftButtonImageFileGroup.getFirstUrl(), mLeftImg);
//            mBackTxt.setVisibility(View.GONE);
//            mBack2Txt.setVisibility(View.GONE);
//            mNextTxt.setVisibility(View.GONE);
        }
    }

    private void addWatermark(int selectPackPosition)
    {
        if (mStickerPacksUseds.get(selectPackPosition) != null)
            return;
        else
            mStickerPacksUseds.put(selectPackPosition, selectPackPosition);
        Property property = mStickerUseds.get(mStickerUseds.size() - 1);
        int selectPack = (Integer) property.get("positionInPack");
        StickersList stickersList = stickersLists.get(selectPack);

        Bitmap bitmap = ((BitmapDrawable) _stillPicture.getDrawable()).getBitmap();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();
        if (stickersList.useWatermark)
        {
            float dpfix = getResources().getDisplayMetrics().density/1f;
            if (stickersList.useApplicationDefaultWatermark)
            {
                IconFile wIconFile = mStickersSettings.applicationDefaultWatermark.imageFileGroup.files.get(0).file;
                WatermarkHelper watermarkHelper = new WatermarkHelper(wIconFile, LayoutUtils.getLayoutPosition(stickersList.watermarkPosition), stickersLists.get(selectPack).watermarkScaleFactor * dpfix, picWidth, picHeight, mContext);
                WatermarkView watermarkView = createWatermarkView(selectPackPosition);
                watermarkView.mWatermark = watermarkHelper;
                mWMHolderLayout.addView(watermarkView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                mFetcher.loadImage(wIconFile.url, watermarkView);
                wViews.add(watermarkView);
            } else
            {
                IconFile wIconFile = stickersList.watermark.imageFileGroup.files.get(0).file;
                WatermarkHelper watermarkHelper = new WatermarkHelper(wIconFile, LayoutUtils.getLayoutPosition(stickersList.watermarkPosition), stickersLists.get(selectPack).watermarkScaleFactor * dpfix, picWidth, picHeight, mContext);
                WatermarkView watermarkView = createWatermarkView(selectPackPosition);
                watermarkView.mWatermark = watermarkHelper;
                mWMHolderLayout.addView(watermarkView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                mFetcher.loadImage(wIconFile.url, watermarkView);
                wViews.add(watermarkView);
            }
        }
    }

    private void removeWaterMark(int selectPackPosition)
    {
        if (!shouldRemoveWaterMark(selectPackPosition))
            return;

        for (int i = 0; i < mWMHolderLayout.getChildCount(); i++)
        {
            WatermarkView view = (WatermarkView) mWMHolderLayout.getChildAt(i);
            int packId = (Integer) view.getTag();
            if (packId == selectPackPosition)
            {
                mWMHolderLayout.removeView(view);
                mStickerPacksUseds.remove(selectPackPosition);
                wViews.remove(view);
                return;
            }
        }
    }

    private boolean shouldRemoveWaterMark(int selectPackPosition)
    {
        for (int i = 0; i < mStickerUseds.size(); i++)
        {
            if ((Integer)mStickerUseds.get(i).get("positionInPack") == selectPackPosition)
                return false;
        }
        return true;
    }

    private GridView createStickerGridView()
    {

        GridView view = new GridView(this);
        view.setOnItemClickListener(this);
        view.setNumColumns(getGridSize());
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setHorizontalSpacing(1);
        view.setVerticalSpacing(1);
        view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        view.setCacheColorHint(0);
        view.setPadding(5, 0, 5, 0);
        view.setSelector(new ColorDrawable(Color.TRANSPARENT));
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        view.setGravity(Gravity.CENTER);
        return view;
    }

    private int getGridSize() {
        int orientation = getResources().getConfiguration().orientation;
        int num = orientation == Configuration.ORIENTATION_LANDSCAPE? 10:5;
        return num;
    }

    private WatermarkView createWatermarkView(int tag)
    {
        WatermarkView watermarkView = new WatermarkView(this);
        watermarkView.setAdjustViewBounds(true);
        watermarkView.setScaleType(ScaleType.MATRIX);
        watermarkView.setTag((Integer) tag);
        return watermarkView;
    }
}
