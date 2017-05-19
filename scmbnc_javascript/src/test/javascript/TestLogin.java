package test.javascript;

import com.sunrise.javascript.JavaScriptConfig;
import com.sunrise.javascript.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class TestLogin extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.test_login);
	findViewById(R.id.test_login).setOnClickListener(new OnClickListener() {		
		@Override
		public void onClick(View v) {
			JavaScriptConfig.setLogin(true);
			Toast.makeText(TestLogin.this, "登录成功", Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK);
			finish();
		}
	});
}
@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
