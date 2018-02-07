Activity面试详情
一、activity生命周期
    1.activity的四种状态
        running 处于活动状态，点击屏幕，屏幕作出相应，activity处于栈顶状态
        paused  activity失去交点，或者被非全屏的activity占据，或者被透明的activity放置栈顶，只是失去与用户交互状态
        stopped 当前activity被另外一个activity完全覆盖，不可见，一些内存状态信息存在
        killed  当前activity被系统回收掉
    2.activity生命周期分析
        activity launched
        activity启动  onCreate()  onStart()[处于用户可见状态，但无法触摸]   onResume()[可见，与用户交互]
        activity runniing
        退出当前activity    onPause()[可见，不能被触摸]   onStop()[完全不可见]    onDestory()[回收工作，资源释放]
        activity shut down

        onStop()  -->  onRestart() onStart()

        点击Home键回到主页面[activity不可见]   onPause()   onStop()
        当我们再次回到activity时    onRestart() onStart()   onResume()
    3.android的进程优先级
        前台  处于与用户交互的activity，或与前台activity绑定的service
        可见  可见，不可点击
        服务  后台开启service
        后台  不会被kill掉
        空    没有活跃的组件
二、androir生命栈
    后进先出，一个任务栈包含activity集合，android系统有序的管理每一个activity。一个app任务栈不是唯一的
三、activity启动模式
    1.standard      标准      每次启动都会创建一个新的activity
    2.singletop     栈顶复用    创建的activity处于栈顶会复用
    3.singletask    栈内复用    检测整个任务栈中是否存在，存在，复用至于栈顶，销毁其他
    4.singleinstance    独享
四、scheme跳转协议
    android中scheme是一种页面内跳转协议，是一种非常好的实现机制，通过定义自己的scheme协议，可以非常方便跳转app中的各个页面；
    通过scheme协议，服务器可以定制化告诉app跳转的那个页面，可以通过通知栏消息定制化跳转页面，可以通过H5页面跳转页面等。

Fragment面试详解
一、fragment为什么被成为第五大组件
    1.fragment为什么被成为第五大组件
        更节省内存，ui切换更舒适，使用频率高，有生命周期，灵活加载到activity
    2.fragment加载到activity的两种方式
        1）添加Fragment到activity的布局文件当中
        2）动态在activity中添加fragment
            步骤一：添加一个FragmentTransaction
            步骤二：用add()方法加上Fragment的对象
            步骤三：用commit()方法使得FragmentTransaction实例改变生效
    3.FragmentPagerAdapter与FragmentStatePagerAdapter区别
        viewPager实现页面滑动，内存消耗
        FragmentPagerAdapter适用于页面较少，detach
        FragmentStatePagerAdapter适用于页面较多，remove，真正释放内存
二、fragment的生命周期
    onAttach()  onCreate()  onCreateView()  onViewCreated() onActivityCreated() onStart()   onResume()  onPause()   onStop()    onDestroyView() onDestroy() onDetach()
    onAttach()      在fragment与activity关联
    onCreate()      只是创建fragment
    onCreateView()  创建UI
    onViewCreated() 初始化资源
    onActivityCreated() 在activityonCreate()调用后调用此方法
    activity    onStart()   fragment    onStart()
    activity    onResume()  fragment    onResume()
    fragment    onPause()   activity    onPause()
    fragment    onStop()    activity    onStop()
    onDestroyView() fragment即将结束
    fragment    onDestroy()
    onDetach()
    activity    onDestroy()
三、fragment之间的通信
    1.在Fragment中调用Activity的方法 getActivity()
    2.在Activity中调用Fragment中的方法 接口回调
    3.在Fragment中调用Fragment中的方法 findFragmentById
四、fragment管理器
    replace     替换
    add         将fragment实例添加到activity上层
    remove         移除

Service面试详情
一、service的应用场景，以及和Thread区别
    1.service是什么
        一个一种可以在后台执行长时间运行操作而没有用户界面的应用程序。可有其他组件启动。线程是主线程
        处理耗时逻辑，长时间，保活。
    2.和Thread的区别
        A.定义
            thread程序执行的最小单元，运行相对独立，分配cpu的基本单位
            没有任何关联
            service中创建子线程，activity很难对子线程进行控制，service是轻量级的ipc通信
        B.实际开发
            主线程负责UI绘制，不能做耗时操作(ANR)
        C.应用场景
            开启子线程
二、开启service的两种方式
    1.startService
        onCreate()      首次创建服务时，系统将调用此方法执行一次性设置程序(在调用onStartCommand()或onBind()之前),如果服务已在运行，则不会调用此方法。该方法只被调用一次
        onStartCommand()    返回START_STICKY重新创建
        onDestroy()     服务销毁的回调
        1.定义一个类继承Service
        2.在Manifest.xml文件中配置该Service
        3.使用Context的startService(Intent)方法启动该Service
        4.不再使用时，调用stopService(Intent)方法停止该服务
    2.bindService
        onBind()    返回Binder对象，返回给客户端使用，提供数据交互接口

        1.创建BindService服务端，继承Service类并在类中，创建一个实现IBinder接口的实例对象并提供公共方法给客户端调用
        2.从onBind()回调方法返回此Binder实例
        3.在客户端中，从onServiceConnented()回调方法中接受Binder，并使用提供的方法调用绑定服务。
        onServiceConnected()        与服务端交互方法，绑定服务的时候被回调，在这个方法获取绑定service传递过来的IBinder对象，通过这个IBinder对象，实现宿主和Service交互
        onServiceDisconnected()     当取消绑定的时候被回调，但正常情况下是不会被调用，它的调用时机是当Service服务被意外销毁时，例如内存的资源不足时这个方法被自动调用

Broadcast Receiver面试详解
一、广播
    1.广播定义
        在Android中，Broadcast是一种广泛的在应用程序之间传输信息的机制，Android中我们要发送的广播内容是一个Intent,这个Intent中可以携带我们要传送的数据
    2.广播使用场景
        A.同一个app具有多个进程的不同组件之间的消息通信
        B.不同app之间的组件之间的消息通信
    3.广播种类
        1)Normal Broadcast: Context.sendBroadcast
        2)System Broadcast: Context.sendOrderdBroadcast
        3)Local Broadcast:  只在自身App内传播
二、实现广播
    1.静态注册：注册完成就一直存在
    2.动态注册：跟随activity的生命周期
三、内部实现机制
    1.自定义广播接受者BroadcastReceiver，并重写onRecvice()方法
    2.通过Binder机制向AMS(Activity Manager Service)进行注册
        Binder  通信核心，客户端服务端CS结构，客户端获取服务端代理，通过代理读取服务端，实现通信
        AMS     四大组件的启动，调动
    3.广播接收者通过Binder机制向AMS发送广播
    4.AMS查找符合相应条件(IntentFilter/Permission等)的BroadcastReceiver，将广播发送到BroadcastReceiver(一般情况下是Activity)相应的消息循环队列中
    5.消息循环执行拿到此广播，回调BroadcastReceiver中的onReceive()方法
四、LocalBroadcastManager详解
    1.使用它发送的广播将只在自身app内传播，因此你不必担心泄露隐私数据
    2.其他app无法对你的app发送该广播，因为你的app根本就不可能接受到非自身应用的该广播，因此你不必担心有安全泄露可以利用
    3.比系统的全局广播更加高效
    高效原因：
        1.LocalBroadcastManager高效的原因主要是因为它内部是通过Handler实现的，它的sendBroadcast()方法并非和我们平时所用的一样，其实是用过handler发送一个Message实现的
        2.既然内部是通过Handler实现广播的发送的，那么相比与系统广播通过Binder实现哪肯定是更高效，同时使用Handler来实现，别的应用无法向我们的应用发送该广播，
        而我们应用内部发送的广播也不会离开我们的应用
        3.LocalBroadcastManager内部协作主要是靠这两个Map集合：mReceivers和mActions，当然还有一个List集合mPendingBroadcasts,这个主要就是存储接受的广播对象

Webview面试详解
    一、Webview常见的一些坑
        1.Android API level 16以及之前的版本存在远程代码执行安全漏洞，该漏洞于程序没有正确限制使用WebView.addJavascripterface方法，远程攻击者可以通过使用
        Java Refiection API利用该漏洞执行任意Java对象的方法
        2.webview在布局文件中的使用：webview写在其他容器中时，
        3.jsbridge
        4.webviewClient.onPageFinished --> WebChromeClient.onProgressChanged
        5.后台耗电
        6.webview硬件加速导致页面渲染问题，容易出现页面加载白块，闪烁。关闭
    二、关于Webview的内存泄露问题
        1.独立进程，简单暴力，不过可能涉及到进程间通信
        2.动态添加webview，对传入的webview中使用的Context使用弱引用，动态添加webview意思在布局创建个ViewGroup用来放置webview，activity创建时add进来，在activity停止时remove掉

Binder面试详解
    一、Linux内核的基础知识
        1.进程隔离/虚拟地址空间
            保护操作系统中进程互不干扰，设计进程隔离技术，避免进程A操作进程B，两进程虚拟地址空间不一样。一个进程与另一个进程通信，需要Binder机制通信。
        2.系统调用
            内核层和用户空间分离开，用户系统调用访问内核某些程序
        3.binder驱动
            负责各个用户进程，通过Binder通信的内核进行交互的模块
    二、Binder通信机制介绍
        1.为什么使用binder
            1)Android使用的Linux内核拥有非常多的跨进程通信机制
                binder机制，管道，socket
            2)性能
            3)安全
                身份校验
        2.binder通信模型
            1)通信录：binder驱动
            2)电话基站：serverManager
                1.ServerManager建立表
                2.Server1 注册  Server2 注册
                3.Client查询ServerManager，找到对应Server，通过Binder驱动通信
                ServerManager查询中：返回对象的代理对象，只包含空方法。包装好交给内核驱动
                binder驱动方法：收到client代理方法，再通知Server端调用方法
        客户端进程持有服务端的代理对象的引用，通过代理对象协助完成的
        3.binder通信机制原理
        1)通常意义下，Binder指的是一种通信机制
        2)对于Server进程来说，Binder指的是Binder本地对象 / 对于Client来说，Binder指的是Binder代理对象
        3)对于传输过程而言，Binder是可以进行跨进程传递的对象
    三、AIDL
        策略模式

Handler面试详解
一、什么是Handler
    handler通过发送和处理Message和Runnable对象来关联相对应线程的MessageQueue
    1.可以让对应的Message和Runable在未来的某个时间点进行相应处理
    2.让自己想要处理的耗时操作放在子线程，让更新ui的操作放在主线程
二、handler的使用方法
    1.post(runnable)    [sendMessage(message)]
    2.sendMessage(message)
三、handler机制的原理
    1.Handler创建消息
        每一个消息都需要被指定的Handler处理，通过Handler创建消息便可以完成此功能。Android消息机制中引入了消息池，Handler创建消息时首先查询消息池中是否有消息存在，
        如果有直接从消息池中取得，如果没有则重新初始化一个消息实例。使用消息池的好处：消息不被使用时，并不作为垃圾回收，而是放入消息池，可供下次Handler创建消息时使用。
        消息池提高了消息对象的复用，减少了系统垃圾回收的次数。
    2.Handler发送消息
        UI主线程初始化第一个Handler时会通过创建唯一的一个Looper，该Looper与UI主线程一一对应。使用ThreadLocal的目的时保证每一个线程中只创建唯一一个Looper。之后
        其他Handler初始化的时候直接获取第一个handler创建的Looper。Looper初始化的时候会创建一个消息队列MessageQueue，至此，主线程、消息循环、消息队列之间的关系1：1:1
        Handler持有对UI主线程消息队列MessageQueue和消息循环Looper的引用，子线程可以通过Handler将消息发送到UI线程的消息队列MessageQueue中。
    3.Handler处理消息
        UI主线程通过Looper循环查询消息队列UI_MQ，当发现有消息存在时会将消息从消息队列中取出。首先分析消息，通过消息的参数判断该消息对应的Handler，然后将消息分发到
        指定的Handler进行处理。
四、handler引起的内存泄露以及解决办法
    原因：静态内部类持有外部类的匿名引用，导致外部activity无法释放
    解决办法：handler内部持有外部activity的弱引用，比把handler改为静态内部类，mHandler.removeCallback()

AsyncTask面试详解
一、什么时AsyncTask
    android提供的轻量级的异步类，本质上就是一个封装了线程池和handler的异步框架。
二、AsyncTask的使用方法
    1.三个参数
    2.5个方法
三、AsyncTask内部原理
    1.AsyncTask的本质是一个静态的线程池，AsyncTask派生出的子类可以实现不同的异步任务，这些任务都是提交到静态的线程池中执行。
    2.线程池中的工作线程执行doInBackground(mParams)方法执行异步任务
    3.当任务状态改变之后，工作线程会向UI线程发送消息，AsyncTask内部的InternalHandler影响这些消息，并调用相关的回调函数
四、AsyncTask的注意事项
    一、内存泄露
        1.设为静态
        2.弱引用
        3.主动销毁
    二、生命周期
    三、结果丢失
    四、并行or串行

handlerThread面试详解
一、handlerThread是什么
    产生背景
        开启Thread子线程进行耗时操作，多此创建和销毁线程是很耗资源的
        handler+thread+looper，是一个thread内部有looper
    handlerThread本质上是一个线程类，它继承了Thread
    handlerThread有自己的内部Looper对象，可以进行looper循环
    通过获取HandlerThread的looper对象传递给Handler对象，可以在handlerMessage方法中执行异步任务
    优点是不会堵塞，减少了对性能的消耗，缺点是不能同时进行多任务的处理，需要等待进行处理。处理效率较低，性能较好
    与线程池注重并发不同，HandlerThread是一个串行队列，HandlerThread背后只有一个线程
二、handlerThread源码解析

IntentService面试详解
一、IntentService是什么
    IntentService是继承并处理异步请求的一个类，在IntentService内有一个工作线程来处理耗时操作，启动IntentService的方式和启动传统的Service一样，同时，当任务执行完后，
    IntentService会自动停止，而不需要我们手动去控制或者stopSelf()。另外，可以启动IntentService多次，而每一个耗时操作会以工作队列的方式在IntentService的onHandleIntent
    回调方法中执行，并且，每次只会执行一个工作线程，执行完第一个再执行第二个
    它本质上是一种特殊的Service，继承自Service并且本身就是一个抽象类
    它内部通过HandlerThread和Handler实现异步操作
二、IntentService使用方法
    创建IntnetServie时，只需实现onHandlerIntent和构造方法，onHandlerIntent为异步方法，可以执行耗时操作
三、IntentService源码解析
    它本质上就是一个封装了HandlerThread和handler的异步框架

View的绘制机制
一、View树的绘制流程
    measure --> layout --> draw
二、measure
    从上到下有序遍历
    1.ViewGroup.LayoutParams
    2.MeasureSpec   32位int值，前两位测量模式，后面大小
    重要方法：
        1.measure
        2.onMeasure
        3.setMeasuredDimension()
三、layout
    自上而下进行遍历
    onLayout()
四、draw
    1.invalidate()
    2.requestLayout()   布局发生变化，重新测量大小触发onMeasure(),onLayout()

事件分发机制
一、为什么会有事件分发机制
    android上面的View是树形结构的，View可能会重叠在一起，当我们点击的地方有多个View都可以响应的时候，这个点击事件应该给谁呢？为了解决这个问题，就有了事件分发机制
    phoneWindow --> decorView --> rootView --> ...
    PhoneWindow 所有视图的最顶层的管理，window唯一实现类
    DecorView
二、三个重要的事件分发的方法
    1.dispatchTouchEvent        是否拦截
    2.onInterceptTouchEvent     拦截事件
    3.onTouchEvent              处理传递到view的事件
三、事件分发流程
    activity --> PhoneWindow --> DecorView --> rootView --> ... --> View
    最后一个view没有被消费，回传。

ListView面试详解
一、什么是listview
    1.ListView就是一个能数据集合以动态滚动的方式展示到用户界面上的View
二、listview适配器模式
    保证数据和View的分离
三、listview的recycleBin机制
四、listview的优化
    converview重用 / viewHolder
    三级缓存 / 监听滑动事件

Android构建
1.Android构建流程
    1. 通过aapt打包res资源文件，生成R.java、resources.arsc和res文件（二进制 & 非二进制如res/raw和pic保持原样）
    2. 处理.aidl文件，生成对应的Java接口文件
    3. 通过Java Compiler编译R.java、Java接口文件、Java源文件，生成.class文件
    4. 通过dex命令，将.class文件和第三方库中的.class文件处理生成classes.dex
    5. 通过apkbuilder工具，将aapt生成的resources.arsc和res文件、assets文件和classes.dex一起打包生成apk
    6. 通过Jarsigner工具，对上面的apk进行debug或release签名
    7. 通过zipalign工具，将签名后的apk进行对齐处理。
2.jenkins持续集成构建

git
一、.git容易混淆的两个概念
    1.工作区
    2..gitignore
二、一些常用的git命令
    1.git init  创建Git仓库
    2.git status    查看当前仓库的状态
    3.git diff  查看这次和上一次修改了那些内容
    4.git add   添加文件到栈存中
    5.git commit    栈存中到仓库中
    6.git clone 克隆
    7.git branch    查看分支
    8.git checkout
三、.git的两种工作流
    1.fort/clone
    2.clone

Proguard
一、proguard到底是什么
    Proguard工具是具有压缩、优化、混淆我们的代码，主要作用是可以移除代码中的无用类、字段、方法和属性同时可以混淆
二、Proguard
    1.压缩
    2.优化
    3.混淆
    4.预检测
三、proguard工作原理
    EntryPoint
    对即将发布的程序进行组织和处理

anr&oom面试详解
一、anr
    Application Not Responding(无响应)
二、造成anr的主要原因
    应用程序的响应性是由Activity Manager和WindowManager系统服务监视的
    1.主线程被IO操作
    2.主线程中存在耗时的计算
    Android中哪些操作是在主线程
        1.Activity的所有生命周期回调都是执行在主线程的
        2.Service默认执行在主线程
        3.BroadcastReceiver的onReceive回调是执行在主线程的
        4.没有使用子线程的looper的Handler的handleMessage，post(Runnable)是执行在主线程的
        5.AsyncTask的回调除了doInBackground，其他都是执行在主线程的
三、如何解决anr
    使用Asynctask处理耗时IO操作
    使用Thread或者Handler Thread提高优先级
    使用handler来处理工作线程的耗时操作
    Activity的onCreate和onResume回调中尽量避免耗时操作

一、oom
    当前占用的内存加上我们申请的内存资源超过了Dalvik虚拟机的最大内存限制就会抛出的Out of memory异常
二、一些容易混淆的概念
    内存溢出
    内存抖动
    内存泄漏
三、如何解决oom
    有关bitmap
        1.图片显示  加载合适尺寸的图片，listview滑动不显示图片
        2.及时释放内存
        3.图片压缩  inSampleSize
        4.inBitmap属性    主要就是指的复用内存块，不需要在重新给这个bitmap申请一块新的内存,避免了一次内存的分配和回收，从而改善了运行效率。LRUCache来缓存bitmap
        5.捕获异常
    其他方法
        1.ListView：convertview / lru (最近最少使用)
        2.避免在onDraw方法里执行对象的创建
        3.谨慎使用多进程

bitmap
一、recycle(回收)
    bitmap存储内存两部分，一部分在java，一部分在c。Bitmap对象是由Java部分分配的，不用的时候系统就会自动回收了，但是那个对应的C可用的内存区域，虚拟机是不能直接回收的
二、LRU
    Lru缓存机制本质上就是存储在一个LinkedHashMap存储，为了保障插入的数据顺序，方便清理。
三、计算inSampleSize
四、缩略图
五、三级缓存
    网络缓存、本地缓存、内存缓存

UI卡顿
一、UI卡顿原理
    60fps --> 16ms
    overdraw    屏幕上某一块过度绘制，UI布局中重叠
二、UI卡顿原因分析
    1.人为的在UI线程中做轻微的耗时操作，导致UI线程卡顿
    2.布局Layout过于复杂，无法在16ms内完成渲染
    3.同一时间动画执行的次数过多，导致CPU或GPU负载过重
    4.View过度绘制，导致某些像素在同一帧时间内被绘制多次，从而使CPU和GPU负载过重
    5.View频繁的出发measure、layout，导致measure、layout累计耗时过多及整个View频繁的重新渲染
    6.内存频繁出发GC过多，导致暂时阻塞渲染操作
    7.冗余资源及逻辑等导致加载和执行缓慢
    8.ANR
三、UI卡顿总结
    1.布局优化
    2.列表及Adapter优化
    3.背景和图片等内存分配优化
    4.避免ANR

内存泄露
一、java内存泄露基础知识
    1.java内存分配策略
        1)静态存储区     静态变量，整个程序运行期间都存在
        2)栈区        栈内存容量有限，在方法体内定义的变量
        3)堆区        不使用时会回收，所有用new创建出来的对象
    2.java是如何管理内存的(对象的分配和释放)
        new
        GC垃圾回收
    3.java中的内存泄露
        内存泄露是指无用对象(不再使用的对象)持续占有内存或无用对象的内存得不到及时的释放，从而造成的内存空间的浪费称为内存泄漏
二、android
    1.单例
    2.匿名内部类
    3.handler
    4.避免使用static变量
    5.资源未关闭造成的内存泄露

内存管理
一、内存管理机制概述
    1.分配机制
    2.回收机制
二、Android内存管理机制
三、内存管理机制的特点
四、内存优化的方法
五、内存溢出和内存泄漏

其他优化
一、android不用静态变量存储数据
    1.静态变量等税局由于进程已经被杀死而被初始化
    2.使用其他数据传输方式：文件 / sp / contentProvider
二、有关sp的安全问题
    1.不能跨进程同步
    2.存储sp的文件过大问题
三、内存对象序列化
    序列化：将对象的状态信息转化为可以存储或传输的形式的过程
    1.Serializeble是java的序列化方式，Parcelable是Android特有额序列化方式
    2.在使用内存的时候，Parcelable比Serializable性能高
    3.Serializable在序列化的时候会产生大量的临时变量，从而引起频繁的GC
    4.Parcelable不能使用在要将数据存储在磁盘上的情况
四、避免在ui线程中做频繁的操作

android插件化
一、插件化来由
    65536 / 64K
    1.通过DexClassLoader加载。
    2.代理模式添加生命周期。
    3.Hook思想跳过清单验证。
二、插件化要解决的问题
    1.动态加载apk
        通过DexClassLoader加载这个插件APK
    2.资源加载
    3.代码加载

android热更新
一、热更新流程
    1.线上检测到严重的crash
    2.拉出bugfix分支并在分支上修复问题
    3.Jenkins构建和补丁生成
    4.app通过推送或主动拉去补丁文件
    5.将bugfix代码合到master上
二、主流热更新框架介绍
    1.Dexposed  阿里巴巴
    2.AndFix    阿里巴巴
    3.Nuwa      基于android dex分包
三、热更新原理
    1.android类加载机制
        1.PathClassLoader
        2.DexClassLoader
    2.热修复机制
        1.dexElements
        2.ClassLoader会遍历这个数组

进程保活
    一、android进程优先级
        1.Foreground process
        2.Visible process
        3.Service process
        4.Background process
        5.Empty process
    二、android进程的回收策略
        1.Low memory killer:通过一些比较复杂的评分机制，对进程进行打分，然后将分数高的进程判定为bad进程，杀死并释放内存
        2.OOM_ODJ：判断进程的优先级
    三、进程保活方案
        1.利用系统广播拉活(开机、网络数据变化，文件卸载)
        2.利用系统Service机制拉活(系统内存不足，5s,10s,30s)
        3.利用Native进程拉活(5.0后失效)
        4.利用JobScheduler机制拉活
        5.利用账号同步机制拉活

link检查
    一、什么是Android link检查
        Android Lint是一个静态代码分析工具，它能狗对你的Android项目中潜在的bug、可优化的代码、安全性、性能、可用性、可访问性、国际化等进行检查

Jaca高级面试 - IO
一、java网络编程
    1.基础知识
        1)ip地址和端口号
        2)tcp / udp协议
        3)URL
        4)InetAddress
    2.socket
        1)创建socket实例
        2)客户端连接
            1.创建Socket对象
            2.连接建立后，通过输出流向服务端发送请求信息
            3.通过输入流获取服务器响应的信息
            4，关闭响应资源
        3)服务端连接
            1.创建ServerSocket对象，绑定监听端口
            2.通过accept()方法监听客户端请求
            3.简历连接后，通过输入流读取客户端发送的请求信息
            4.通过输出流向客户端发送信息
            5.关闭相关资源
        4)总结
            1.创建ServerSocket和Socket
            2.打开连接到Socket的输入/输出流
            3.按照协议对Socket进行读/写操作
            4.关闭输入输出流、关闭Socket
二、阻塞IO
    1.java的I\O接口
        1)基于字节操作的I/O接口
        2)基于字符操作的I/O接口
        3)基于磁盘操作的I/O接口
        4)基于网络操作的I/O接口
    2.阻塞IO的通信模型
    3.总结
        1.BIO数据在写入OutputStream或者从InputStream读取时都有可能会阻塞
        2.当前一些需要大量HTTP长连接的情况
        3.需要另外一种新I/O操作方式
三、NIO
    1.工作原理
        服务端和客户端各自维护一个管理通道的对象
    2.通信模型
    3.总结
        1)获得一个Socket通道，并对该通道做一些初始化的工作
            1.获得一个Socket通道
            2.设置通道为非阻塞
            3.客户端连接服务器，其实方法执行并没有实现连接，需要在listener()方法中调用channel.finishConnect()才能完成
            4.将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件
        2)采用轮询的方式监听selector上是否需要处理的事件，如果有，则进行处理
            1.轮询访问selector
            2.获取selector中选中的项的迭代器
            3.删除已选的key，以防重复处理、
            4.连接事件发生
            。。。

多线程
一、多线程的创建
    1.thread / runnable
        1.继承Thread
        2.实现runnable接口
    2.两种启动线程方法的区别
        1.共同点
            thread start开启
        2.不同点
            接口更灵活，适合多(大多数runnable)
    3.start方法和run方法的区别
        start开启线程，处于就绪状态，不一定运行
        run方法结束，线程结束
二、线程间通信
    1.synchronized关键字
        1)synchronized对象锁
        2)synchronized / volatile
        3)synchronized来实现线程间通信
        4)synchronized / lock
    2.sleep / wait
        阻塞线程执行
    3.wait / notify机制
        等待锁 / 释放锁
三、线程池
    1.好处
        1)降低资源消耗
        2)提高响应速度
        3)提高线程的可管理性
    2.ThreadPollExecutor
        corePoolSize    线程池的大小
        maxinumPollSize 线程池最大容量限制
        keepAlineTime   活动保持的时间
        (TimeUnit)unit  时间单位
        workQueue       线程队列
        threadFactory   线程工厂
        handler         饱和策略，常用：满的时候抛出异常
        创建线程池 --> 提交任务
    3.线程池的工作流程
        1)首先线程池判断基本线程池是否已满
        2)其次线程池判断工作队列是否已满
        3)最后线程池判断整个线程池是否已满

异常
一、异常体系
    1.error / Exception
    2.运行时异常和非运行时异常
二、异常使用
    1.运行java异常处理机制
        1.try...catch语句
        2.finally语句：任何情况下都必须执行的代码
        3.throws子句：声明可能会抛出的异常
        4.throw语句：抛出异常
    2.异常处理的原理
        1)java虚拟机用方法调用栈来跟踪每个线程中一系列的方法调用过程
        2)如果在执行方法的过中抛出异常，则java虚拟机必须找到能捕获该异常的catch代码块
        3)当java虚拟机追溯到调用栈的底部的方法时，如果仍然没有找到处理该异常的代码块
    3.异常流程的运行过程
        1)finally语句不被执行的唯一情况就是先执行了用于终止程序的System.exit()方法
        2)return语句用于退出本方法
        3)finally代码块虽然在return语句之前被执行，但finally代码块不能通过重新给变量赋值的方式改变return语句的返回值
        4)建议不要在finally代码块中使用return
三、一些异常的面试题
    1.java中检查型异常和非检查异常有什么区别
    2.throw和throws两个关键字在java中有什么不同
    3.如果执行finally代码块之前方法返回了结果，或者JVM退出了，finally块中的代码还会执行吗?
    4.java中final、finalize、finally关键字的区别

注解
一、注解概念
    1.什么是注解
        Annotation(注解)就是Java提供了一种元程序中的元素关联任何信息和着任何元数据(metadata)的途径和方法
        基本的规则：Annotation不能影响程序代码的执行，无论增加、删除Annotation，代码都始终如一的执行
    2.什么是metadata
        1.元数据以标签的形式存在于java代码中
        2.元数据描述的信息是类型安全的
        3.元数据需要编译器之外的工具额外的处理用来生成其它的程序部件
        4.元数据可以只存在于java原代码级别，也可以存在于编译之后的Class文件内部
二、注解分类
    1.系统内置的标准注解
        1)Override
        2)Deprecated(代码过时)
        3)SuppressWarnnings()
    2.元注解
        1)@Target       注解所修饰的类型范围
        2)@Retention    定义注解保留时间的长短
        3)@Documented
        4)@Inherited
三、Android support annotations
    1.Nullness注解    Nullable
    2.Resource Type 资源类型检查
    3.Threading     线程注解
    4.Overriding Methods
四、总结

GC相关
一、classloader
    1.什么是类加载器
        ClassLoader就是用来动态加载class文件到内存当中用的
    2.类加载器类型
        BootStrap ClassLoader
        Exiension ClassLoader   扩展类加载器
        App ClassLoader         应用程序类加载器
        java虚拟机
            启动类加载器 c++
            所有其它类加载器 java
    3.双亲委托模型
        自动类加载器 <-- 扩展类加载器 <-- 应用程序类加载器 <-- 自定义类加载器
    4.类加载的过程
        加载 --> 验证 --> 准备 --> 解析 --> 初始化 --> 使用 --> 卸载
        1.加载
            1)通过一个类的全限定名来获取其定义的二进制字节流
            2)将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构
            3)在JAVA堆中生成一个代表这个类的java.lang.Class对象，作为对方法区中这些数据的访问入口
        2.验证
            1)为了确保Class文件中的字节流包含的信息符合当前虚拟机的要求
            2)文件格式的验证、元数据的验证、字节码验证和符号引用验证
        3.准备
            1)对基本数据类型
            2)对于同时被static和final修饰的常量
            3)对于引用数据类型reference
            4)在数组舒适化时
        4.解析
            1)类或接口的解析
            2)字段解析
            3)类方法解析
            4)接口方法解析
        5.初始化
            初始化阶段时执行类构造器方法的过程
二、java堆 / 栈
    1.java程序运行时的内存分配策略
        1)静态存储区：主要存放静态数据、全局static数据和常量
        2)栈区：方法体内的局部变量都在栈上创建
        3)堆区：通常就是指程序运行时直接new出来的内存
    2.栈内存 / 堆内存的区别
        在方法体内定义的(局部变量)一些基本类型的变量和对象的引用变量都是在方法的栈内存中分配的
        堆内存用来存放所有由new从创建的对象(包括该对象其中的所有成员变量)和数组。在堆中分配的内存，将由java垃圾回收器来自动管理
    3.java内存回收机制
    4.java内存泄漏引起的原因
        长生命周期的对象持有短生命周期的对象的引用就很可能发生内存泄漏
总结：
    堆：运行时，垃圾回收，动态分配内存，存取速度慢
    栈：存取速度快，生命周期绑定

反射
一、编译时 vs 运行时
    编译时：将java代码编译成.class文件的过程
    运行时：就是java虚拟机执行.class文件的过程
    编译时类型：编译时类型由声明该变量时使用的类型决定
    运行时类型：运行时类型由实际赋给该变量的对象决定
    动态绑定：
        1.在编译时，时调用声明类型的成员方法(多态的实现原理)，也就是所谓的编译时类的方法
        2.到了运行时，调用的时实际的类型的成员方法，也就是所谓的运行时类的方法
        3.对于调用引用实例的成员变量，无论是编译时还是运行时，均是调用编译时类型的成员变量
二、什么是反射
    在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意一个方法和属性
三、class类
    编译.class文件生成Class对象
四、反射的运用
五、Android中反射的运用
    1.通过原始的java反射机制调用方式调用资源
    2.Activity的启动过程中Activity的对象的创建

设计模式
    单例
        一、单例介绍
            1.单例概念
                单例模式是一种对象创建模式，它用于产生一个对象的具体实例，它可以确保系统中一个类只产生一个实例
            2.好处
                对于频繁使用的对象，可以省略创建对象所花费的时间，这对于那些重量级对象而言，是非常可观的一笔系统开销
                由于new操作的次数减少，因而对系统内存的使用频率也会降低，这将减轻GC压力，缩短GC停顿时间
        二、单例的写法和各自特点
            饿汉 / 懒汉 / 懒汉线程安全 / DCL / 静态内部类 / 枚举
            饿汉
                不足之处：无法对instance实例做延迟加载
            懒汉
                不足之处：在多线程并发下这样的实现是无法保证实例唯一的
            懒汉线程安全
                不足之处：使用synchronized，性能缺陷
            DCL
                不足之处：JVM的即时编译器中存在指令重排序的优化
            静态内部类
                优点：JVM本身机制保证了线程安全 / 没有性能缺陷， 原因 static / final
            枚举
        三、android中的单例
            1.application

            2.单例模式引起的内存泄漏
            3.eventbus的坑
    builder
        一、java的builder模式
            1.概念
                建造者模式是较为复杂的创建型模式，它将客户端和包含多个组成部分(或部件)的复杂的创建过程分离
            2.使用场景
                当构造一个对象需要很多参数的时候，并且参数的个数或者类型不固定的时候
            3.UML结构图分析

            4.实际代码分析
                1.Builder：它为创建一个产品Product对象的各个部件指定抽象接口
                2.ConcreteBulider：它实现了Builder接口，实现各个部件的具体构造和装配方法
                3.Product：它是被构建的复杂对象，包含多个组成部分
                4.Director：指挥者又称为导演类，它负责安排复杂对象的建造次序，指挥者与抽象建造者之间存在关联关系
            5.builder模式优点
                松散耦合：生成器模式可以用同一个构建算法构建出表现上完全不同的产品，实现产品构建和产品表现上的分离
                可以很容易的改变产品的内部表示
                更好的复用性：生成器模式很好的实现构建算法和具体产品实现的分离
            6.builder模式缺点
                会产生多余的Builder对象以及Director对象，消耗内存
                对象的构建过程暴露
        二、builder模式在android中的实际运用
            1.alertDialog
            2.Glide / okhttp
    adapter
        一、adapter模式详解
            1.适配器模式的定义
                将一个接口转化成客户端希望的另一个接口，适配器模式使接口不兼容的那些类可以一起工作，其别名为包装器(Wrapper)
            2.类适配器
                把适配的类的API转换称为目标类的API
                类适配器使用的对象继承的方式，是静态的定义方式
                对于类适配器，适配器可以重定义Adapter的部分行为
                对于类适配器，仅仅引入了一个对象，并不需要额外的引用来间接得到adapter
            3.对象适配器
                与类适配器模式一样，对象的适配器模式把适配的类API转换称为目标的API，与类的适配器模式不同的是，对象的适配器模式不是使用继承关系连接到Adaptee类，
                而是委派关系连接到Adaptee类
                对象适配器使用对象组合的方式，是动态组合的方式
                对于对象适配器，一个适配器可以把多种不同的源适配到同一个目标
                对于对象适配器，要重定义Adaptee的行为比较困难
                对于对象适配器，需要额外的引用来间接得到adaptee
        二、adapter模式在android中的实际运用
            listview
                1.ListView的布局是由一条一条的Item组成的，这每一个Item又是一个View。通过Adapter适配器这个桥梁将View添加到ListView中
                2.一个Adapter是AdapterView视图与数据之间的桥梁，Adapter提供对数据的访问，也负责每一项数据产生一个对应的View
                3.每一项数据产生对应的View之后，然后将View添加到ListView之中
                4.MVC
    装饰模式
        一、装饰模式详解
            1.概念
                动态地给一个对象增加一些额外的职责，就增加对象功能来说，装饰模式比生成子类实现更为灵活。装饰模式是一种对象结构型模式
            2.使用场景
                1)在不影响其他对象的情况下，以动态、透明的方式给单个对象添加 职责
                2)当不能采用继承的方式对系统进行扩展或者采用继承不利于系统扩展和维护时可以使用装饰模式
            3.UML结构图
            4.实际代码
            5.优点
                1)对于扩展一个对象的功能，装饰模式比继承模式更加灵活性，不会导致类的个数急剧增加
                2)可以通过一种动态的方式扩展一个对象的功能
                3)可以对一个对象进行多次装饰，通过使用不同的具体装饰类以及这些装饰类的排列组合
        二、装饰模式在android中的实际运用
            context类簇在装饰模式的运用
    外观
        一、外观模式详解
            1.概念
                外观模式的主要目的在于让外部减少与子系统内部多个模块的交互，从而让外部能够更简单得使用子系统。它负责把客户端得请求转发给子系统内部得各个模块进行处理
            2.使用场景
                1)当你要为一个复杂子系统提供一个简单接口时
                2)客户程序与抽象类的实现部分之间存在着很大的依赖性
                3)当你需要构建一个层次结构的子系统时
            3.外观模式优点
                1)由于Facade类封装了各个模块交互的过程，如果今后内部模块调用关系发生了关系，只需要修改Facade实现就可以了
                2)Facede实现是可以被多个客户端调用的
        二、android中运用
            contextImpl --> android中的外观类
    组合
        一、详解
            1.将对象以树行结构组织起来，以达成"部分-整体"的层次结构，使得客户端对单个对象和组合对象的使用具有一致性
            2.使用场景
                1)需要表示一个对象整体或部分层次
                2)让客户端能够忽略不同对象层次的变化
            3)优点
                1)高层模块调用简单
                2)节点自由增加
    策略
        一、详解
            1.概念
                定义一系列的算法，把它们一个个封装起来，并且使他们可互相替换。本模式使得算法可独立于使用它的客户而变化
            2.使用场景
                一个类定义了多张行为，并且这些行为在这个类的方法中以多个条件语句的形式出现，那么可以使用策略模式避免在类中使用大量的条件语句
            3.优点
                1)上下文和具体策略是松耦合
                2)策略模式满足开-闭原则
        二、应用
    模板方法
        一、模板方法模式
            1.概念
                通过定义一个算法骨架，而将算法中的步骤延迟到子类，这样子类就可以复写这些步骤的实现来实现特定的算法
            2.使用场景
                1)多个子类有共有的子类，并且逻辑基本相同
                2)重要、复杂的算法，可以把核心算法设计为模板方法
    观察者
        一、详解
            1.概念
                定义对象之间的一种一对多依赖关系，使得每当一个对象状态发生改变时，其依赖对象皆得到通知并被自动更新
            2.使用场景
                1)一个抽象模型有两个方面，其中一个方面依赖另一个方面
                2)一个对象的改变将导致一个或多个其他对象也发生改变
                3)需要在系统中创建一个触发链
        二、观察者模式在andrid中的实际运用：回调模式
            1.回调模式：
                实现了抽象类 / 接口的实例同时实现了父类的提供的抽象方法后，将该方法交换给父类来处理
            2.listview中notifyDataSetChanged()
    责任链

















