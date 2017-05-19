package com.sunrise.marketingassistant.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.sunrise.javascript.utils.JsonUtils;
import com.sunrise.marketingassistant.R;
import com.sunrise.marketingassistant.activity.SingleFragmentActivity;
import com.sunrise.marketingassistant.adapter.PickInfoAdapter;
import com.sunrise.marketingassistant.entity.ChlBaseInfo;
import com.sunrise.marketingassistant.task.GenericTask;
import com.sunrise.marketingassistant.task.GetCptListTask;
import com.sunrise.marketingassistant.task.TaskListener;
import com.sunrise.marketingassistant.task.TaskResult;
import com.sunrise.marketingassistant.utils.CommUtil;
import com.sunrise.marketingassistant.view.DefaultSearchDialog;
import com.sunrise.marketingassistant.view.DefaultDialog.OnConfirmListener;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PickMessageListFragment extends BaseFragment implements OnClickListener, TaskListener, PullToRefreshBase.OnRefreshListener {

	private Button add;
	private ImageView search;
	// private ListView messageList;
	private PickInfoAdapter adapter;
	/**
	 * 选择对话框
	 */
	private DefaultSearchDialog mSearchDialog;
	private PullToRefreshListView pullToRefreshListView;
	private ListView mListView;
	private static int pageSize = 10;
	private static int page = 1;
	private List<ChlBaseInfo> data = new ArrayList<ChlBaseInfo>();

	@Override
	protected View _onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_pick_message_list, container, false);
		
		add = (Button) view.findViewById(R.id.add_message);
		add.setOnClickListener(this);
		search = (ImageView) view.findViewById(R.id.search);
		search.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.message_list);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		mListView = ((ListView) pullToRefreshListView.getRefreshableView());
		mListView.setSelector(R.color.transparent);

		// adapter = new PickInfoAdapter(mBaseActivity, getData());
		// pullToRefreshListView.setAdapter(adapter);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				String chlId = String.valueOf(data.get(arg2 - 1).getChlId());
				String imgId = String.valueOf(data.get(arg2 - 1).getImgId());
				getPreferences().saveImageName(null);
				startActivity(SingleFragmentActivity.createIntent(mBaseActivity, PickMessageFragment.class, chlId+"&"+imgId, null, null, null));
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		data.clear();
		pageSize = 10;
		page = 1;
		initDialog(true, false, null);
		showDialog("获取列表信息，请稍候……");
		startTask();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPreExecute(GenericTask task) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPostExecute(GenericTask task, TaskResult result) {
		// TODO Auto-generated method stub
		dismissDialog();
		if (pullToRefreshListView.isRefreshing())
			pullToRefreshListView.onRefreshComplete();
		if (result != TaskResult.OK) {
			CommUtil.showAlert(mBaseActivity, null, task.getException().getMessage(), getString(R.string.Return), null);
		}
	}

	@Override
	public void onProgressUpdate(GenericTask task, Object param) {
		// TODO Auto-generated method stub
		if (pullToRefreshListView.isRefreshing()) {
			pullToRefreshListView.onRefreshComplete();
		}
		if (param != null)
			try {
				JSONObject localJSONObject = new JSONObject((String) param);
				if (localJSONObject.has("RETURN")) {
					localJSONObject = localJSONObject.getJSONObject("RETURN");
					if (localJSONObject.has("RETURN_CODE")) {
						if (CommUtil.parse2Integer(localJSONObject.getString("RETURN_CODE"), -1) != 0) {
							Toast.makeText(mBaseActivity, localJSONObject.getString("RETURN_MESSAGE"), Toast.LENGTH_SHORT).show();
							return;
						}
						List<ChlBaseInfo> localArrayList = new ArrayList<ChlBaseInfo>();
						if (localJSONObject.has("RETURN_INFO")) {
							JSONArray array = localJSONObject.getJSONArray("RETURN_INFO");
							if (array.length() > 0) {
								JSONObject info = array.getJSONObject(0);
								if (info.has("DETAIL_INFO")) {
									array = info.getJSONArray("DETAIL_INFO");
									if (array.length() > 0) {
										for (int i = 0; i < array.length(); i++) {
											ChlBaseInfo dto = (ChlBaseInfo) JsonUtils.parseJsonStrToObject(array.getJSONObject(i).toString(),
													ChlBaseInfo.class);
											//dto.setImageUrl("http://pic11.nipic.com/20101110/3320946_160215810000_2.jpg");
											localArrayList.add(dto);
										}
									}
								}
							}

						}
						if (adapter == null) {
							data.clear();
							data.addAll(localArrayList);
							adapter = new PickInfoAdapter(mBaseActivity, data);
							pullToRefreshListView.setAdapter(adapter);
						} else {
							data.addAll(localArrayList);
							adapter.notifyDataSetChanged();
						}
					}

				}
			} catch (JSONException localJSONException) {
				localJSONException.printStackTrace();
			}
	}

	@Override
	public void onCancelled(GenericTask task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.search:
			search();
			break;
		case R.id.add_message:
			startActivity(SingleFragmentActivity.createIntent(mBaseActivity, PickMessageFragment.class, "-999", null, null, null));
			break;
		default:
			break;
		}

	}

	private void search() {
		if (mSearchDialog != null) {
			mSearchDialog.dismiss();
			mSearchDialog = null;
		}
		mSearchDialog = new DefaultSearchDialog(mBaseActivity);
		mSearchDialog.setDilaogTitle("查询网点信息");
		mSearchDialog.setInputFieldHint("请输入网点名称");
		mSearchDialog.setSearchText("开始查询");
		mSearchDialog.setOnSearchListener(new OnConfirmListener() {
			@Override
			public void onClick(DialogInterface dialog, Object resultObj, int which) {
				String inputText = (String) resultObj;
				if (TextUtils.isEmpty(inputText)) {
					Toast.makeText(mBaseActivity, "请输入网点名称!", Toast.LENGTH_SHORT).show();
				} else {
					dialog.dismiss();
					List<ChlBaseInfo> tempList = new ArrayList<ChlBaseInfo>();
					for (ChlBaseInfo temp : data) {
						if (temp.getChlName().contains(inputText)) {
							tempList.add(temp);
						}
					}
					adapter = new PickInfoAdapter(mBaseActivity, tempList);
					pullToRefreshListView.setAdapter(adapter);
				}

			}
		});
		mSearchDialog.show();
	}

	@Override
	public void onRefresh(PullToRefreshBase refreshView) {
		// TODO Auto-generated method stub
		if (refreshView.isHeaderShown()) {
			adapter = null;
			page = 1;
			startTask();
			return;
		}
		page = getNextPage();
		startTask();
	}

	private int getNextPage() {
		if (data == null) {
			return 1;
		} else {
			return (int) (Math.ceil(((double) data.size() / (double) pageSize))) + 1;
		}
	}

	private GenericTask mTask;

	private void startTask() {
		if (mTask != null)
			mTask.cancle();
		mTask = new GetCptListTask().execute(getPreferences().getAuthentication(), getPreferences().getSubAccount(), null, page + "", pageSize + "", this);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mTask != null)
			mTask.cancle();
	}

}
