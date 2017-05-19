package com.sunrise.scmbhc.task;

import java.util.Observable;
import java.util.Observer;

import com.sunrise.scmbhc.exception.logic.ServerInterfaceException;

import android.os.AsyncTask;

public abstract class GenericTask extends AsyncTask<TaskParams, Object, TaskResult> implements Observer {
	public static final String URL_PARAM = "url";
	private Exception exception;
	private int id = -1;
	private TaskListener mListener = null;
	private boolean isCancelable = true;
	private static String name;

	abstract protected TaskResult _doInBackground(TaskParams... params);

	public void setListener(TaskListener taskListener) {
		mListener = taskListener;
	}

	public TaskListener getListener() {
		return mListener;
	}

	public void doPublishProgress(Object... values) {
		super.publishProgress(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		if (mListener != null) {
			mListener.onCancelled(this);
		}

	}

	@Override
	protected void onPostExecute(TaskResult result) {
		super.onPostExecute(result);

		if (mListener != null) {
			mListener.onPostExecute(this, result);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (mListener != null) {
			mListener.onPreExecute(this);
		}
	}

	@Override
	protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);

		if (mListener != null) {
			if (values != null && values.length > 0) {
				mListener.onProgressUpdate(this, values[0]);
			}
		}
	}

	@Override
	protected TaskResult doInBackground(TaskParams... params) {
		return _doInBackground(params);
	}

	public void cancle() {
		if (getStatus() == GenericTask.Status.RUNNING) {
			cancel(true);
		}
	}

	public void update(Observable o, Object arg) {
		if (TaskManager.CANCEL_ALL == (Integer) arg && isCancelable) {
			if (getStatus() == GenericTask.Status.RUNNING) {
				cancel(true);
			}
		}
	}

	public void setCancelable(boolean flag) {
		isCancelable = flag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		GenericTask.name = name;
	}

	/**
	 * @return 是否是令牌失效？
	 */
	public boolean isBusinessAuthenticationTimeOut() {

		if (exception == null)
			return false;

		if (!(exception instanceof ServerInterfaceException))
			return false;

		ServerInterfaceException e = (ServerInterfaceException) exception;
		if (e.getStatusCode() == ServerInterfaceException.BUSINESS_AUTHENTICATION_TIME_OUT)
			return true;

		return false;
	}

}
