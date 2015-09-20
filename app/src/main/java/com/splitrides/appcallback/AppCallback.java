package com.splitrides.appcallback;

/**
 * This interface has been used for performing any callback
 * operation on completion of some task.
 * Created by abhishek purwar on 9/19/15.
 */
public interface AppCallback {

    /*
    * This callback api has been used for performing require task
    * on successful completion of async task.
    * */
    void successActivityCallback();

    /*
    * This callback api has beenused for performing require task
    * on failure of async task.
    * */
    void failureActivityCallback();
}
