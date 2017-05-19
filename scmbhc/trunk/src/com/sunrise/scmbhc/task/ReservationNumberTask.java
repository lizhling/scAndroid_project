package com.sunrise.scmbhc.task;

import com.sunrise.scmbhc.App;
import com.sunrise.scmbhc.UserInfoControler;
import com.sunrise.scmbhc.entity.ReservationNumberReslut;

/**
 * 预约排号
 * 
 * @author 珩
 */
public class ReservationNumberTask extends GenericTask {

	public static final ReservationNumberTask execute(String id, TaskListener listener) {

		ReservationNumberTask task = new ReservationNumberTask();
		task.setListener(listener);

		task.execute(new TaskParams("ID", id));

		return task;
	}

	@Override
	protected TaskResult _doInBackground(TaskParams... params) {

		String id = params[0].getString("ID");
		try {
			ReservationNumberReslut reservationNumberReslut = App.sServerClient.reservationNumber(UserInfoControler.getInstance()
					.getUserName(), id, UserInfoControler.getInstance().getAuthorKey());
			publishProgress(reservationNumberReslut);
		} catch (Exception e) {
			e.printStackTrace();
			return TaskResult.FAILED;
		}

		return TaskResult.OK;
	}

}
