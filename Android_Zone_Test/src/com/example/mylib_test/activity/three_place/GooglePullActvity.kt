package com.example.mylib_test.activity.three_place

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.ButterKnife
import com.example.mylib_test.R
import com.example.mylib_test.delegates.TextDelegates
import com.zone.adapter3.QuickRcvAdapter
import com.zone.adapter3.base.IAdapter
import com.zone.adapter3.loadmore.OnScrollRcvListener
import com.zone.lib.utils.system_hardware_software_receiver_shell.software.wifi.NetManager
import kotlinx.android.synthetic.main.a_threeplace_google.*
import java.util.*

/**
 *[2018/7/10] by Zone
 */
class GooglePullActvity : Activity(), SwipeRefreshLayout.OnRefreshListener, Handler.Callback {
    companion object {
        val data = LinkedList<String>()

        init {
            for (i in 0..29)
                data.add("一直很桑心")
        }
    }

    var adapter: IAdapter<*>? = null
    var handler = Handler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_threeplace_google)
        ButterKnife.bind(this)
        swipe_container.setOnRefreshListener(this)
//		swipe_container.setColorScheme(android.R.color.holo_blue_bright,  android.R.color.holo_green_light,
//	    android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipe_container.setColorScheme(android.R.color.holo_red_light)
        lv.layoutManager = LinearLayoutManager(this)
        adapter = QuickRcvAdapter(this, data)
                .addViewHolder(TextDelegates())
                .relatedList(lv)
                .addOnScrollListener(object : OnScrollRcvListener() {
                    override fun loadMore(recyclerView: RecyclerView) {
                        super.loadMore(recyclerView)
                        //相当于告诉他加载完成了
                        Handler().postDelayed({
                            data.addLast("上啦加载的数据~")
                            adapter?.notifyDataSetChanged()
                            adapter?.loadMoreComplete()
                        }, 3000)
                    }
                })
    }

    override fun handleMessage(msg: Message?): Boolean {
        data.addFirst("当前没有网络呢")
        adapter?.notifyDataSetChanged()
        swipe_container.isRefreshing = false
        return false
    }

    override fun onRefresh() {
        if (!NetManager.haveNetWork(this)) {
            handler.sendEmptyMessage(1)
            return
        }
        Handler().postDelayed({
            // Do work to refresh the list here.
            GetDataTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }, 3000)
    }

    inner class GetDataTask : AsyncTask<Void, Void, String>() {
        // 后台处理部分
        override fun doInBackground(vararg params: Void?): String {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
            }

            return "Added after refresh...I add"
        }

        //这里是对刷新的响应，可以利用addFirst（）和addLast()函数将新加的内容加到LISTView中
        //根据AsyncTask的原理，onPostExecute里的result的值就是doInBackground()的返回值
        override fun onPostExecute(result: String?) {
            //在头部增加新添内容
            data.addFirst(result)
            //通知程序数据集已经改变，如果不做通知，那么将不会刷新mListItems的集合
            adapter?.notifyDataSetChanged()
            // Call onRefreshComplete when the list has been refreshed.
            swipe_container.isRefreshing = false
            super.onPostExecute(result)//这句是必有的，AsyncTask规定的格式
        }

    }

}