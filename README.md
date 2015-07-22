# AndroidStudioTestDemo
> 原文来自于： http://hukai.me/android-training-course-in-chinese/testing/activity-testing/index.html

## 测试UI组建
通常情况下，Activity，包括用户界面组件（如按钮，复选框，可编辑的文本域，和选框）允许用户与Android应用程序交互。
>  Android Instrumentation框架适用于创建应用程序中UI部件的白盒测试。
>  了解更多关于如何在你的Android应用程序进行黑盒测试，请阅读 
>  [UI Testing guide](http://developer.android.com/training/testing/ui-testing/index.html)。

### 1. 使用 Instrumentation 建立UI测试
> * 为了安全地将Intent注入到Activity，或是在UI线程中执行测试方法，我们可以让测试类继承于[ActivityInstrumentationTestCase2](
> http://developer.android.com/reference/android/test/ActivityInstrumentationTestCase2.html)。
> * ActivityInstrumentationTestCase2测试可以与Android系统通信，发送键盘输入及点击事件到UI中。
> * 要学习如何在UI线程运行测试方法，请看在 [UI线程测试](http://developer.android.com/tools/testing/activity_testing.html#RunOnUIThread)。

###  2. UI响应测试
* 验证布局参数
``` java
 // example
  @MediumTest
  public void testClickMeButton_layout() {
      final View decorView = mClickFunActivity.getWindow().getDecorView();
  
      ViewAsserts.assertOnScreen(decorView, mClickMeButton);
  
      final ViewGroup.LayoutParams layoutParams =
              mClickMeButton.getLayoutParams();
      assertNotNull(layoutParams);
      assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
      assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
  }
```
> 1. 调用getDecorView()方法得到一个Activity中修饰试图（Decor View）的引用。要修饰的View在布
> 局层次视图中是最上层的ViewGroup(FrameLayout)
> 2. 在调用assertOnScreen()方法时，传递根视图以及期望呈现在屏幕上的视图作为参数。如果想呈现
> 的视图没有在根视图中,该方法会抛出一个AssertionFailedError异常，否则测试通过。


* 验证按钮的行为
``` java
 // example
  @MediumTest
  public void testClickMeButton_clickButtonAndExpectInfoText() {
      String expectedInfoText = mClickFunActivity.getString(R.string.info_text);
      TouchUtils.clickView(this, mClickMeButton);
      assertTrue(View.VISIBLE == mInfoTextView.getVisibility());
      assertEquals(expectedInfoText, mInfoTextView.getText());
  }
 ```
> 注意:[TouchUtils](http://developer.android.com/reference/android/test/TouchUtils.html)
> 辅助类提供与应用程序交互的方法可以方便进行模拟触摸操作。我们可以使用这些方法来模拟点击，
> 轻敲，或应用程序屏幕拖动View。

> 警告TouchUtils方法的目的是将事件安全地从测试线程发送到UI线程。我们不可
> 以直接在UI线程或任何标注@UIThread的测试方法中使用TouchUtils这样做可能会增加错误线程异常。


## Android单元测试
Activity单元测试可以快速且独立地（和系统其它部分分离）验证一个Activity的状态以及其与其它
组件交互的正确性。一个单元测试通常用来测试代码中最小单位的代码块（可以是一个方法，类，或
者组件），而且也不依赖于系统或网络资源。
> 注意：1. 单元测试一般不适合测试与系统有复杂交互的UI。
>  2. 对这类复杂交互的UI我们应该使用测试UI组件的ActivityInstrumentationTestCase2进行测试。
>  3. 如果要针对系统或者外部依赖进行测试，我们可以使用Mocking Framework的[Mock](http://de
> veloper.android.com/tools/testing/testing_android.html#MockObjectClasses)类，并把它集
> 成到我们的单元测试中。

### 1. 启动一个Activity
``` java
// Example
  // 1. 继承ActiviUnitTestCase的Activity不会被Android自动启动,
  // 2. 我们需要显式的调用startActivity()方法，并传递一个Intent来启动我们的目标Activity。
  public class LaunchActivityTest
          extends ActivityUnitTestCase<LaunchActivity> {
      ...
  
      @Override
      protected void setUp() throws Exception {
          super.setUp();
          mLaunchIntent = new Intent(getInstrumentation()
                  .getTargetContext(), LaunchActivity.class);
          startActivity(mLaunchIntent, null, null);
          final Button launchNextButton =
                  (Button) getActivity()
                  .findViewById(R.id.launch_next_activity_button);
      }
  }
```

### 2. 验证Activity是否启动
``` java
// Example
  // 1. 为了验证一个触发Intent的Button的事件，我们可以使用getStartedActivityIntent()方法;
  // 2. LaunchActivity是独立运行的，所以不可以使用TouchUtils库来操作UI;
  // 3. 要进行Button点击，我们可以调用perfoemClick()方法。
  @MediumTest
  public void testNextActivityWasLaunchedWithIntent() {
      startActivity(mLaunchIntent, null, null);
      final Button launchNextButton =
              (Button) getActivity()
              .findViewById(R.id.launch_next_activity_button);
      launchNextButton.performClick();
  
      final Intent launchIntent = getStartedActivityIntent();
      assertNotNull("Intent was null", launchIntent);
      assertTrue(isFinishCalled());
  
      final String payload =
              launchIntent.getStringExtra(NextActivity.EXTRAS_PAYLOAD_KEY);
      assertEquals("Payload is empty", LaunchActivity.STRING_PAYLOAD, payload);
  }
```

## 功能测试
> * 功能测试包括验证单个应用中的各个组件是否与使用者期望的那样（与其它组件）协同工作。
> 比如，我们可以创建一个功能测试验证在用户执行UI交互时Activity是否正确启动目标Activity。
> * 要为Activity创建功能测试，我们应该对ActivityInstrumentationTestCase2进行扩展。

### 1. 验证函数的行为
> 为了在应用中监视单个Activity我们可以注册一个[ActivityMonitor](http://developer.android.
> com/reference/android/app/Instrumentation.ActivityMonitor.html)。每当一个符合要求的Activity
> 启动时，系统会通知ActivityMoniter，进而更新碰撞数目。
> 通常来说要使用ActivityMoniter，我们可以这样：

> * 使用getInstrumentation()方法为测试用例实现Instrumentation。
> * 使用Instrumentation的一种addMonitor()方法为当前instrumentation添加一个Instrumentation.ActivityMonitor实例。匹配规则可以通过IntentFilter或者类名字符串。
> * 等待开启一个Activity。
> * 验证监视器撞击次数的增加。
> * 移除监视器。

``` java
// Example
  // 
  @MediumTest
  public void testSendMessageToReceiverActivity() {
      final Button sendToReceiverButton = (Button)
              mSenderActivity.findViewById(R.id.send_message_button);
  
      final EditText senderMessageEditText = (EditText)
              mSenderActivity.findViewById(R.id.message_input_edit_text);
      
      // 1. Set up an ActivityMonitor
      ActivityMonitor receiverActivityMonitor =
              getInstrumentation().addMonitor(ReceiverActivity.class.getName(),
              null, false);
              
      // 2. Send string input value
      ...
      
      // 3. Validate that ReceiverActivity is started
      TouchUtils.clickView(this, sendToReceiverButton);
      ReceiverActivity receiverActivity = (ReceiverActivity)
              receiverActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);
      assertNotNull("ReceiverActivity is null", receiverActivity);
      
      // 4. Validate that ReceiverActivity has the correct data
      assertEquals("Monitor for ReceiverActivity has not been called",
              1, receiverActivityMonitor.getHits());
      assertEquals("Activity is of wrong type",
              ReceiverActivity.class, receiverActivity.getClass());
      ...
      
      // 5. Remove the ActivityMonitor
      getInstrumentation().removeMonitor(receiverActivityMonitor);
  }
```

### 2. 使用Instrumentation发送一个键盘输入
> 如果Activity有一个EditText，我们可以测试用户是否可以给EditText对象输入数值。
> 通常在ActivityInstrumentationTestCase2中给EditText对象发送串字符，我们可以这样做：
> * 使用runOnMainSync()方法在一个循环中同步地调用requestFocus()。这样，我们的UI线程就会在获得焦点前一直被阻塞。
> * 调用waitForIdleSync()方法等待主线程空闲（也就是说,没有更多事件需要处理）。
> * 调用sendStringSync()方法给EditText对象发送一个我们输入的字符串。

``` java
// Example
    // Send string input value
    getInstrumentation().runOnMainSync(new Runnable() {
        @Override
        public void run() {
            senderMessageEditText.requestFocus();
        }
    });
    getInstrumentation().waitForIdleSync();
    getInstrumentation().sendStringSync("Hello Android!");
    getInstrumentation().waitForIdleSync();
```

