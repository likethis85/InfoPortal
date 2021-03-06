package com.iBeiKe.InfoPortal.news;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.iBeiKe.InfoPortal.R;
import com.iBeiKe.InfoPortal.common.MessageHandler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 意见建议信息获取显示。
 * 建立两个子线程，用于处理对从服务器的意见建议数据的获取
 * 以及通过一个阻塞队列对获取的信息存储，对界面更新显示。
 *
 */
public class advise extends Activity implements Runnable {
	private NewsHandler handler;
	private NewsHelper helper;
	private NewsListAdapter adapter;
	private BlockingQueue<Map<String, String>> queue;
	private BlockingQueue<Integer> msg;
	private String urlString = "http://city.ibeike.com/rss.php?fid=74&auth=1";
	static String table = "news_list";
	static String[] columns = new String[]{"title", "link",
			"description", "category", "author", "pubDate"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news);
		queue = new LinkedBlockingQueue<Map<String,String>>();
		msg = new LinkedBlockingQueue<Integer>();
		
        handler = new NewsHandler(queue, msg);
        helper = new NewsHelper(this);
        adapter = new NewsListAdapter(this);

    	ListView list = (ListView)findViewById(R.id.news_list);
    	list.setAdapter(adapter);
        ExecutorService exec = Executors.newCachedThreadPool();
        exec.execute(new RSSParser());
        Button btn = (Button)findViewById(R.id.top_back);
        btn.setVisibility(View.GONE);
        TextView tv = (TextView)findViewById(R.id.header_title);
        tv.setText(R.string.advise);
        tv = (TextView)findViewById(R.id.news_title);
        tv.setText(R.string.advise);
        
        Thread thread = new Thread(this);
        thread.start();
	}
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.getData().containsKey("0")) {
				MessageHandler mcr = new MessageHandler();
				Map<String,String> item = mcr.getMap("0", msg);
				helper.saveMap(item, table);
				adapter.notifyDataSetChanged();
			}
    	}
	};

	public void run() {
		while(!Thread.interrupted()) {
			try {
				Map<String,String> item = queue.take();
				MessageHandler mcr = new MessageHandler();
				mcr.bundle("0", item);
				mHandler.sendMessage(mcr.get());
			} catch (InterruptedException e) {
				Log.e("Queue Error", e.toString());
			}
		}
	}
    
    class RSSParser implements Runnable {
    	public void run() {
    		if(!Thread.interrupted()) {
    			try {
    				Log.d("RSSParser", "in RSSParser");
					handler.getData(urlString);
				} catch (Exception e) {
					Log.e("Library: ", "rss parse: " + e.toString());
				}
    		}
    	}
    }
}