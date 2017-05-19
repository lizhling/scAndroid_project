package com.starcpt.cmuc.ui.skin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.starcpt.cmuc.CmucApplication;
import com.starcpt.cmuc.R;
import com.starcpt.cmuc.exception.business.BusinessException;
import com.starcpt.cmuc.exception.http.HttpException;
import com.starcpt.cmuc.model.bean.SkinBean;
import com.starcpt.cmuc.model.bean.SkinsBean;
import com.starcpt.cmuc.service.DownSkinService;
import com.starcpt.cmuc.task.GenericTask;
import com.starcpt.cmuc.task.TaskAdapter;
import com.starcpt.cmuc.task.TaskListener;
import com.starcpt.cmuc.task.TaskParams;
import com.starcpt.cmuc.task.TaskResult;
import com.starcpt.cmuc.ui.activity.CommonActions;
import com.starcpt.cmuc.utils.AsyncImageLoader;
import com.starcpt.cmuc.utils.FileUtils;
import com.starcpt.cmuc.utils.AsyncImageLoader.ImageCallBack;
import com.starcpt.cmuc.utils.ZipFileUtils;

/**
 * @author LUOYUN
 */
public class SkinSwitchActivity extends Activity {
	
	//private static String TAG = "SkinSwitchActivity"; 
	
	private static final int INSTALL_SKIN_SUCCESSED = 0;
	private static final int INSTALL_SKIN_FAILED = 1;
	private static final int IINT_SKIN_START = 2;
	private static final int IINT_SKIN_END = 3;
	
	private GridView mSkinGridView;
	private SkinListAdapter mSkinListAdaptor;
	private ArrayList<SkinBean> mSkinBeans= new ArrayList<SkinBean>();
	private HashMap<Integer, SkinBean> mSkinMap;
	private ProgressDialog mProgressDialog;
	private String currentSkin = SkinManager.DEFAULT_SKIN;
	private ComparatorSkin mComparatorSkin;
	
	interface OnUseSkinListener{
		/**
		 * @param lastSkinPos	上一个item position
		 * @param currentSkinPos  当前item position
		 */
		void onClicked(int lastSkinPos,int currentSkinPos);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CommonActions.setScreenOrientation(this);
		setContentView(R.layout.skin_switch);
		mSkinBeans = new ArrayList<SkinBean>();
		mSkinGridView = (GridView) findViewById(R.id.skinGridView);
		mSkinListAdaptor = new SkinListAdapter(mSkinBeans, this, 
				mOnItemBtnClickListener,mOnUseSkinListener);
		mSkinGridView.setAdapter(mSkinListAdaptor);
		currentSkin = SkinManager.getCurrentSkin(this);
		//Log.i(TAG, "currenSkin:"+currentSkin);
		loadSkinInfo();
		registerDownloadSkinReceiver();
		CommonActions.addActivity(this);
	}
	
	/**
	 *  排序
	 */
	private void sortSkinBeans(){
		mComparatorSkin = new ComparatorSkin();
		Collections.sort(mSkinBeans, mComparatorSkin);
	}
	
	/**
	 * 更新皮肤列表
	 * @param position	皮肤位置
	 * @param skinBean	替换皮肤
	 */
	private void refreshSkins(SkinBean skinBean){
		mSkinMap.put(skinBean.getSkinId(), skinBean);
		mSkinBeans.clear();
		mSkinBeans.addAll(mSkinMap.values());
		sortSkinBeans();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegisterDownloadSkinReceiver();
	}
	/**
	 * 皮肤使用事件
	 */
	private OnUseSkinListener mOnUseSkinListener = new OnUseSkinListener() {

		@Override
		public void onClicked(int lastSkinPos, int currentSkinPos) {
			// TODO Auto-generated method stub
			SkinBean lastSkinBean = mSkinBeans.get(lastSkinPos);
			SkinBean currentBean = mSkinBeans.get(currentSkinPos);
			lastSkinBean.setCurrentSkin(false);
			currentBean.setCurrentSkin(true);
			if (currentBean.getSkinState()==SkinBean.SKIN_STATE_DEFAULT) {
				currentSkin = SkinManager.DEFAULT_SKIN;
			} else {
				currentSkin = SkinManager.getSkinFolderName(currentBean.getDownUrl());
			}
			useSkin(SkinSwitchActivity.this,currentSkinPos);
			refreshView();
		}
	};
	
	/**
	 * 初始化皮肤
	 */
	private void skinLoadSuccess(){
		mSkinMap = new HashMap<Integer, SkinBean>();
		int size = mSkinBeans.size();
		boolean hasCurSkin = false;
		for (int i = 0; i < size; i++) {
			SkinBean skinBean = mSkinBeans.get(i);
			String skinFolderName = SkinManager.getSkinFolderName(skinBean.getDownUrl());
			String skinUrl = CmucApplication.APP_SKIN_INSTALLED_DIR+File.separator+skinFolderName;
			boolean isInstalled = FileUtils.directoryIsExist(skinUrl);
			if (isInstalled) {
				skinBean.setSkinState(SkinBean.SKIN_STATE_INSTALLED);
				if (currentSkin.equals(skinFolderName)) {
					skinBean.setCurrentSkin(true);
					hasCurSkin = true;
				}
			} else {
				skinBean.setSkinState(SkinBean.SKIN_STATE_NEWSKIN);
			}
			mSkinMap.put(skinBean.getSkinId(), skinBean);
		}
		SkinBean skinBean = new SkinBean(-1, SkinManager.DEFAULT_SKIN, "", "", "", "", "", SkinBean.SKIN_STATE_DEFAULT);
		if (!hasCurSkin) {
			skinBean.setCurrentSkin(true);
		}
		mSkinBeans.add(0,skinBean);
		mSkinMap.put(skinBean.getSkinId(), skinBean);
		sortSkinBeans();
		refreshView();
	}
	
	private void showProgressDialog(String msg){
		if (mProgressDialog==null) {
			mProgressDialog = ProgressDialog.show(this, null, msg, true, false);
		} else {
			mProgressDialog.setMessage(msg);
			mProgressDialog.show();
		}
	}
	
	private void dismissProgressDialog(){
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}
	
	/**
	 * 使用皮肤
	 */
	public void useSkin(Context context,int position) {
		boolean isSuccess = SkinManager.setCurrentSkin(context,currentSkin);
		if (isSuccess) {
			new Thread(new InitSkinThread()).start();
		}
	}
	
	/**
	 * 发送皮肤设置广播
	 */
	private void sendSkinChangedBroadcast(){
		Intent intent = new Intent(SkinManager.SKIN_CHANGED_RECEIVER);
		sendBroadcast(intent);
	}
	
	/**
	 * 下载事件处理
	 */
	private OnItemBtnClickListener mOnItemBtnClickListener = new OnItemBtnClickListener() {
		
		@Override
		public void onItemBtnClick(View view, int position) {
			SkinBean skinBean = mSkinBeans.get(position);
			skinBean.setSkinState(SkinBean.SKIN_STATE_DOWNLOADING);
			refreshView();
			downloadSkin(skinBean,position);
			 
		}
	};
	
	/**
	 * 皮肤下载完成广播
	 */
	private BroadcastReceiver mDownloadSkinReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			SkinBean skinBean = (SkinBean) intent.getParcelableExtra(SkinManager.SKIN_BEAN);
			if (skinBean.getSkinState()==SkinBean.SKIN_STATE_DOWNLOAD_OK) {
				refreshSkins(skinBean);
				refreshView();
				startInstall(skinBean);
			} else if (skinBean.getSkinState()==SkinBean.SKIN_STATE_DOWNLOAD_FAILED) {
				showMsg("皮肤 "+skinBean.getSkinName()+" 下载失败，请重试");
				skinBean.setSkinState(SkinBean.SKIN_STATE_NEWSKIN);
				refreshSkins(skinBean);
				refreshView();
			}
		}
	};
	
	private void showMsg(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 皮肤下载完成广播
	 */
	private void registerDownloadSkinReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(SkinManager.SKIN_DOWNLOAD_COMPLETED_RECEIVER);
		registerReceiver(mDownloadSkinReceiver, filter);
	}
 
	private void unRegisterDownloadSkinReceiver(){
		unregisterReceiver(mDownloadSkinReceiver);
	}
	
	/**
	 * 开启下载皮肤服务
	 * @param skinBean
	 */
	private void downloadSkin(SkinBean skinBean,int position){
		Intent service = new Intent(this,DownSkinService.class);
		service.putExtra(SkinManager.SKIN_BEAN, skinBean);
		startService(service);
	}
	
	private Handler mHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case INSTALL_SKIN_SUCCESSED:
				SkinBean skinBean = (SkinBean) msg.obj;
				showMsg(skinBean.getSkinName()+ getString(R.string.install_success));
				refreshSkins(skinBean);
				refreshView();
				break;
			case INSTALL_SKIN_FAILED:
				SkinBean skinBean2 = (SkinBean) msg.obj;
				showMsg(skinBean2.getSkinName()+ getString(R.string.install_failed));
				refreshView();
				break;
			case IINT_SKIN_START:
				showProgressDialog(getString(R.string.initing_skin));
				break;
			case IINT_SKIN_END:
				dismissProgressDialog();
				sendSkinChangedBroadcast();
				showMsg(getString(R.string.skin_set_complete));
				//finish();
				break;
			default:
				break;
			}
			return false;
		}
	});
	
	/**
	 * 开始安装皮肤
	 * @param skinBean	当前安装皮肤
	 * @param position	当前安装皮肤的位置
	 */
	private void startInstall(SkinBean skinBean){
		new Thread(new InstallSkinThread(skinBean)).start();
	}
	
	class InstallSkinThread implements Runnable{

		private SkinBean skinBean;

		public InstallSkinThread(SkinBean skinBean) {
			super();
			this.skinBean = skinBean;
		}

		@Override
		public void run() {
			String zipFile = FileUtils.getAbsPath(CmucApplication.APP_SKIN_CACHE_DIR, SkinManager.getSkinName(skinBean.getDownUrl()));

			String targetDir = FileUtils.getAbsPath(CmucApplication.APP_SKIN_INSTALLED_DIR
					+File.separator+SkinManager.getSkinFolderName(skinBean.getDownUrl()),null);
			boolean isSuccess = ZipFileUtils.Unzip(zipFile, targetDir);
//			FileUtils.deleteFileAbsolutePath(zipFile);
			if (isSuccess) {
				skinBean.setSkinState(SkinBean.SKIN_STATE_INSTALLED);
				sendHandlerMsg(skinBean,INSTALL_SKIN_SUCCESSED);
			} else {
				skinBean.setSkinState(SkinBean.SKIN_STATE_NEWSKIN);
				//安装失败，删除安装文件夹
				FileUtils.deleteFileAbsolutePath(targetDir);
				sendHandlerMsg(skinBean,INSTALL_SKIN_FAILED);
			}
		}
	}

	class InitSkinThread implements Runnable{
		
		@Override
		public void run() {
			sendHandlerMsg(null, IINT_SKIN_START);
			SkinManager.initCurSkin(SkinSwitchActivity.this);
			sendHandlerMsg(null, IINT_SKIN_END);
		}
	}
	
	private void sendHandlerMsg(SkinBean skinBean,int flag){
		Message message = mHandler.obtainMessage();
		message.obj = skinBean;
		message.what = flag;
		message.sendToTarget();
	}
	
	/**
	 * 从网络加载皮肤信息
	 */
	private void loadSkinInfo(){
		LoadSkinTask loadSkinTask = new LoadSkinTask();
		loadSkinTask.setListener(mTaskListener);
		loadSkinTask.execute();
	}
	
	private TaskListener mTaskListener = new TaskAdapter() {
		
		@Override
		public void onPreExecute(GenericTask task) {
			// TODO Auto-generated method stub
			super.onPreExecute(task);
			showProgressDialog(getString(R.string.initing_skin));
		}

		@Override
		public void onPostExecute(GenericTask task, TaskResult result) {
			// TODO Auto-generated method stub
			super.onPostExecute(task, result);
			if (result==TaskResult.OK) {
				skinLoadSuccess();
				dismissProgressDialog();
			} else {
				dismissProgressDialog();
				Toast.makeText(SkinSwitchActivity.this, getString(R.string.init_skin_failed), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			return "loadSkinTask";
		}
	};
	

	private void refreshView(){
		mSkinListAdaptor.notifyDataSetChanged();
	}
	
	class LoadSkinTask extends GenericTask{

		@Override
		protected TaskResult _doInBackground(TaskParams... params) {
			// TODO Auto-generated method stub
			try {
				SkinsBean skinsBean = CmucApplication.sServerClient.getSkinList("480~800",CmucApplication.OS);
				List<SkinBean> skinBeans = skinsBean.getSkinBeanList();
				mSkinBeans.addAll(skinBeans);
				return TaskResult.OK;
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return TaskResult.FAILED;
		}
	}
	
	interface OnItemBtnClickListener{
		void onItemBtnClick(View view,int position);
	}
	
	class SkinListAdapter extends BaseAdapter{

		private ArrayList<SkinBean> skinBeans;
		private Context context;
		private OnItemBtnClickListener onItemBtnClickListener;
		private AsyncImageLoader asyncImageLoader;
		private OnUseSkinListener onUseSkinListener;
		private int lastPosition;

		public SkinListAdapter(ArrayList<SkinBean> skinBeans, Context context,
				OnItemBtnClickListener onItemBtnClickListener,
				OnUseSkinListener onUseSkinListener) {
			super();
			this.skinBeans = skinBeans;
			this.context = context;
			this.onItemBtnClickListener = onItemBtnClickListener;
			this.onUseSkinListener = onUseSkinListener;
			asyncImageLoader=new AsyncImageLoader(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return skinBeans.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return skinBeans.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder; 
			final int pos = position;
			if (convertView==null) {
				holder = new ViewHolder();
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.skin_item, null);
				holder.skinIcon = (RelativeLayout) convertView.findViewById(R.id.skin_icon);
				holder.skinCheckIcon = (ImageView) convertView.findViewById(R.id.skin_check);
				holder.skinName = (TextView) convertView.findViewById(R.id.skinName);
				holder.downloadButton = (Button) convertView.findViewById(R.id.download_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final RelativeLayout skinIcon = holder.skinIcon;
			final SkinBean skinBean = skinBeans.get(position);
			holder.skinName.setText(skinBean.getSkinName());
			holder.downloadButton.setVisibility(View.INVISIBLE);
			holder.downloadButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (onItemBtnClickListener!=null) { 
						onItemBtnClickListener.onItemBtnClick(v,pos);
					}
				}
			});
			final int state = skinBean.getSkinState();
			holder.skinIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (state==SkinBean.SKIN_STATE_INSTALLED||state==SkinBean.SKIN_STATE_DEFAULT) {
						onUseSkinListener.onClicked(lastPosition, pos); 
					}
				}
			});
			
			if (state!=SkinBean.SKIN_STATE_DEFAULT) {
				ImageCallBack imageCallBack=new ImageCallBack(){
					@Override
					public void loadImage(Bitmap d) {
						// TODO Auto-generated method stub
						if (d!=null) {
							skinIcon.setBackgroundDrawable(new BitmapDrawable(d));
						}
					}
				};
				asyncImageLoader.loadDrawable(skinBean.getSkinIcon(), imageCallBack);
				if (state==SkinBean.SKIN_STATE_NEWSKIN) {
					holder.downloadButton.setVisibility(View.VISIBLE);
					holder.downloadButton.setEnabled(true);
					holder.downloadButton.setText(getString(R.string.download));
				} else if (state==SkinBean.SKIN_STATE_DOWNLOADING) {
					holder.downloadButton.setVisibility(View.VISIBLE);
					holder.downloadButton.setEnabled(false);
					holder.downloadButton.setText(getString(R.string.downloadling));
				} else if (state==SkinBean.SKIN_STATE_DOWNLOAD_OK) {
					holder.downloadButton.setVisibility(View.VISIBLE);
					holder.downloadButton.setEnabled(false);
					holder.downloadButton.setText(getString(R.string.installing));
				}
			}
			if (skinBean.isCurrentSkin()&&skinBean.getSkinState()!=SkinBean.SKIN_STATE_NEWSKIN) {
				lastPosition = position;
				holder.downloadButton.setVisibility(View.INVISIBLE);
				holder.skinCheckIcon.setVisibility(View.VISIBLE);
			} else {
				holder.skinCheckIcon.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	class ViewHolder{
		TextView skinName;
		Button downloadButton;
		RelativeLayout skinIcon;
		ImageView skinCheckIcon;
	}
	
	/**
	 * 排序类
	 * 排序方式，以安装的皮肤在前，未安装的皮肤在后。然后按时间顺序排列
	 */
	class ComparatorSkin implements Comparator<SkinBean>{

		@Override
		public int compare(SkinBean lhs, SkinBean rhs) {
			// TODO Auto-generated method stub
			int lhsState = lhs.getSkinState();
			int rhsState = rhs.getSkinState();
			if (lhsState==rhsState) {
				return lhs.getCreateTime().compareTo(rhs.getCreateTime());
			} else {
				return lhsState - rhsState;
			} 
		}
	
	}
}
