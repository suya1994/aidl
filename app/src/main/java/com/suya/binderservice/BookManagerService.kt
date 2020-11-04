package com.suya.binderservice

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.RemoteCallbackList
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class BookManagerService : Service() {

    //使用CopyOnWriteArrayList是因为如果多个客户端访问服务端，避免并发
    private val mBookList = CopyOnWriteArrayList<Book>()

    /**
     * 为什么用RemoteCallbackList
     * binder会把客户端传过来的对象重新转化并生成一个新的对象
     * 虽然客户端register和unregister使用的是同一个对象，但是转化后就是两个不同的对象
     * 故使用CopyOnWriteArrayList是无法找到对象的
     *
     * 注意：对象是不能直接跨进程传输的，故才需要使用序列化
     *
     * 为什么用RemoteCallbackList中有ArrayMap<IBinder, Callback>
     * Callback中封装了远程listener
     * 当客户端register的时候，map的key/value分别如下
     * key:IBinder = listener.asBinder()
     * value:Callback = Callback(listener,cookie)
     * 同一个IOnNewBookArrivedlInterface的底层key:binder是同一个
     *
     */
    private val mListenerList = RemoteCallbackList<IOnNewBookArrivedlInterface>()

    private val mIsServiceDestroyed = AtomicBoolean(false)

    private val mBinder = object : IBookManager.Stub() {
        override fun getBookList(): MutableList<Book> {
            return mBookList
        }

        override fun addBook(book: Book?) {
            println("addBook:${Thread.currentThread()}")
            mBookList.add(book)
        }

        override fun registerListener(listener: IOnNewBookArrivedlInterface?) {
            mListenerList.register(listener)
        }

        override fun unregisterListener(listener: IOnNewBookArrivedlInterface?) {
            mListenerList.unregister(listener)
        }

    }

    private fun onNewBookArrived(book: Book) {
        println("onNewBookArrived:${Thread.currentThread()}")
        mBookList.add(book)
        val n = mListenerList.beginBroadcast()
        var i = 0
        while (i < n) {
            val listener = mListenerList.getBroadcastItem(i)
            listener?.onNewBookArrieved(book)
            i++
        }
        mListenerList.finishBroadcast()
    }

    override fun onCreate() {
        super.onCreate()
        mBookList.add(Book(1, "android"))
        mBookList.add(Book(2, "ios"))
        thread { worker() }
    }

    override fun onDestroy() {
        super.onDestroy()
        mIsServiceDestroyed.set(true)
    }

    override fun onBind(intent: Intent): IBinder? {
        //检查权限
        val check = checkCallingOrSelfPermission("com.suya.binder.permission.ACCESS")
        if (check == PackageManager.PERMISSION_DENIED) {
            println("no permission")
            return null
        }
        return mBinder
    }

    private fun worker() {
        while (!mIsServiceDestroyed.get()) {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val bookId = mBookList.size + 1
            val book = Book(bookId, "new book#$bookId")
            onNewBookArrived(book)
        }
    }

}