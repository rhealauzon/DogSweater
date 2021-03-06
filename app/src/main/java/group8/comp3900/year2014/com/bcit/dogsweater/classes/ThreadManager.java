package group8.comp3900.year2014.com.bcit.dogsweater.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ApplicationHandler is a singleton; only one of this instance should be
 * instantiated for the lifetime of the app.
 * This class contains a Handler (mHandler) which runs on the UI thread.
 * Messages sent to this handler to make updates to the UI thread, or they can
 * be delegated to a background, worker thread.
 *
 * Created by Eric Tsang on 19/10/2014.
 */
public class ThreadManager {


    /////////////////////////////
    // DEFINE STATIC CONSTANTS //
    /////////////////////////////

    // ThreadPoolExecutor constructor parameters
    /**
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static final int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();

    /** Sets amount of time an idle thread waits before terminating (seconds) */
    private static final int KEEP_ALIVE_TIME = 1;

    /** Sets the Time Unit to seconds */
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // CustomHandler message types
    /**
     * Message type describing a message that is a runnable, that must be run
     * on some sort of worker thread. Such message types will be assigned to a
     * worker thread of this class to do its thing.
     */
    public static final int START_RUNNABLE_TASK = 0;

    /**
     * Message type describing a message that needs to run on the UI thread;
     * executed on the same thread as this class. Such message types must
     * implement the UpdateUITask interface. The updateUI method will be run on
     * the UI thread.
     */
    public static final int UPDATE_UI_TASK = 1;

    // Image processing
    /** specifies the ways that images can be cropped */
    public enum CropPattern {

        /** indicates a square image */
        SQUARE,

        /** indicates no cropping */
        DEFAULT
    }


    ////////////////////////////////////////
    // INSTANTIATE STATIC SUPPORT OBJECTS //
    ////////////////////////////////////////
    /** A queue of Runnables to be passed to the threadPool for execution */
    private static final BlockingQueue<Runnable> workQueue =
            new LinkedBlockingQueue<Runnable>();

    /** Handler object that's attached to the UI thread */
    private final Handler mHandler;

    /** Reference to the singleton ApplicationHandler */
    private static final ThreadManager mInstance;

    /** Creates a thread pool manager & instantiate one */
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            NUMBER_OF_CORES,       // Initial pool size
            NUMBER_OF_CORES,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue);

    /** Instantiates a single ApplicationHandler */
    static { mInstance = new ThreadManager(); }


    //////////////////
    // CONSTRUCTORS //
    //////////////////
    /**
     * Constructs the work queues and thread pools used to download
     * and decode images. Because the constructor is marked private,
     * it's unavailable to other classes, even in the same package.
     */
    private ThreadManager() {

        // Defines a Handler object that's attached to the UI thread
        mHandler = new CustomHandler(Looper.getMainLooper());
    }


    ///////////////////////
    // INTERFACE METHODS //
    ///////////////////////
    /**
     * runs the run method of the passed Runnable instance (runnable) on a
     * worker thread when one becomes available.
     *
     * @param runnable a Runnable instance who's run method will be executed on a
     * background thread when one becomes available.
     */
    public static void runOnWorkerThread(Runnable runnable) {
        mInstance.mHandler.obtainMessage(
                START_RUNNABLE_TASK, runnable).sendToTarget();
    }

    /**
     * runs the updateUI method of the passed UpdateUITask instance
     * (updateUITask) on the UI thread.
     *
     * @param updateUITask an UpdateUITask instance who's updateUI method will
     * be run on the main application thread; it can access UI components.
     */
    public static void runOnMainThread(Runnable updateUITask) {
        mInstance.mHandler.obtainMessage(
                UPDATE_UI_TASK, updateUITask).sendToTarget();
    }

    /**
     * gets the image at the passed Uri (imageUri), and decodes it on a worker
     * thread. once the image has been decoded into a bitmap, the passed
     * OnResponseListener's (onResponseListener) onResponse method will be
     * invoked on the main ui thread, and passed the decoded bitmap.
     *
     * @param context the application's context
     * @param imageUri Uri to the image to load on the local file system
     * @param cropPattern pattern used to crop the bitmap (i.e.: Square, 4:3...)
     * @param imageWidth width of the image to scale the image into; if the
     *   decoded Bitmap has a width smaller than the passed width, then it will
     *   be scaled up. if the width of the image is larger than the passed
     *   width, then it will be shrunk down.
     * @param onResponseListener an OnResponseListener who's onResponse method
     *   will be invoked when the bitmap has been decoded successfully
     */
    public static void loadImage(final Context context, final Uri imageUri, final CropPattern cropPattern,
            final int imageWidth, final OnResponseListener onResponseListener) {

        // setting dialog image...use a worker thread to load the image
        runOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                try {
                    // TODO: add some sort of caching process maybe???
                    // load and parse bitmap
                    InputStream stream = context.getContentResolver().openInputStream(imageUri);
                    Bitmap bm = BitmapFactory.decodeStream(stream);
                    switch (cropPattern) {

                        case SQUARE:
                            int minLength = Math.min(bm.getWidth(), bm.getHeight());
                            bm = Bitmap.createBitmap(bm, bm.getWidth() / 2 - (minLength / 2), bm.getHeight() / 2 - (minLength / 2), minLength, minLength);
                            bm = Bitmap.createScaledBitmap(bm, 600, 600, false);
                            break;

                        case DEFAULT:
                            bm = Bitmap.createScaledBitmap(bm, imageWidth, bm.getHeight() * imageWidth / bm.getWidth(), false);
                            break;
                    }
                    final Bitmap croppedBm = bm;

                    // use the UI thread to display the image
                    runOnMainThread( new Runnable() {
                        @Override
                        public void run() {
                            onResponseListener.onResponse(croppedBm);
                        }
                    });
                } catch (Exception e) {
                    Log.e("trouble parsing URI", e.toString());
                }
            }
        });
    }



    ////////////////
    // INTERFACES //
    ////////////////
    /**
     * interface that provides methods that will be used as callbacks then
     * loadImage returns with its decoded bitmap.
     */
    public interface OnResponseListener {

        /**
         * method run when the loadImage method responds.
         *
         * @param bitmap decoded bitmap resource. this is the bitmap of the
         * image at the URI that was passed into the loadImage method.
         */
        public void onResponse(Bitmap bitmap);
    }



    ///////////////////
    // INNER CLASSES //
    ///////////////////

    /**
     * message Handler class of the ThreadManager; when it has a message to
     * handle, it computes what kind of message it is, and runs it on the
     * appropriate thread.
     */
    public class CustomHandler extends Handler {

        private CustomHandler(Looper looper) {
            super(looper);
        }

        /*
         * handleMessage() defines the operations to perform when
         * the Handler receives a new Message to process.
         */
        public void handleMessage(Message messageIn) {

            switch (messageIn.what) {

                /*
                 * Assumes that messageIn.obj implements Runnable. Executes
                 * the Runnable's run method on a background worker thread.
                 */
                case ThreadManager.START_RUNNABLE_TASK:

                    // Adds task to the thread pool for execution on a
                    // worker thread
                    threadPool.execute((Runnable) messageIn.obj);
                    break;

                /*
                 * Assumes that messageIn.obj implements UpdateUITask.
                 * Executes the UpdateUITask's updateUI method on the UI
                 * thread.
                 */
                case ThreadManager.UPDATE_UI_TASK:

                    // Runs task on the UI thread (this thread).
                    ((Runnable) messageIn.obj).run();
                    break;

                /* Pass along other messages from the UI */
                default:
                    super.handleMessage(messageIn);
                    break;
            }
        }
    }
}
