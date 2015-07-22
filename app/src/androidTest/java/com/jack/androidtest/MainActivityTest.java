package com.jack.androidtest;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

/**
 * Created by Pactera on 2015/7/21.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

  private MainActivity mMainActivity;
  private Button mButton;

  public MainActivityTest() {
    super(MainActivity.class);
  }


  /**
   * Test if your test fixture has been set up correctly. You should always implement a test that
   * checks the correct setup of your test fixture. If this tests fails all other tests are
   * likely to fail as well.
   */
  public void testPreconditions(){

    //Try to add a message to add context to your assertions. These messages will be shown if
    //a tests fails and make it easy to understand why a test failed
    assertNotNull("mMainActivity is null",mMainActivity);
    assertNotNull("mButton is null",mButton);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    // Starts the activity under test using the default Intent with:
    // action = {@link Intent#ACTION_MAIN}
    // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
    // All other fields are null or empty.
    mMainActivity = getActivity();
    mButton = (Button)mMainActivity.findViewById(R.id.btn_device_test);

  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
