/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package pt.up.beta.mobile.authenticator;

import pt.up.beta.mobile.AccountAuthenticatorActivity;
import pt.up.beta.mobile.Constants;
import pt.up.beta.mobile.R;
import pt.up.beta.mobile.content.SigarraContract;
import pt.up.beta.mobile.datatypes.User;
import pt.up.beta.mobile.sifeup.AuthenticationUtils;
import pt.up.beta.mobile.sifeup.ResponseCommand;
import pt.up.beta.mobile.ui.dialogs.AboutDialogFragment;
import pt.up.beta.mobile.ui.dialogs.ProgressDialogFragment;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays login screen to the user.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity
		implements ResponseCommand, OnDismissListener {

	private static final String DIALOG_AUTHENTICATING = "connecting";
	private static final String DIALOG_ABOUT = "about";
	/** The Intent flag to confirm credentials. */
	public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

	/** The Intent extra to store password. */
	public static final String PARAM_PASSWORD = "password";

	/** The Intent extra to store username. */
	public static final String PARAM_USERNAME = "username";

	/** The Intent extra to store username. */
	public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

	
	/** The tag used to log to adb console. */
	private static final String TAG = "AuthenticatorActivity";
	private AccountManager mAccountManager;

	/** Keep track of the login task so can cancel it if requested */
	private AsyncTask<String, Void, ERROR_TYPE> mAuthTask = null;

	/**
	 * If set we are just checking that the user knows their credentials; this
	 * doesn't cause the user's password or authToken to be changed on the
	 * device.
	 */
	private Boolean mConfirmCredentials = false;

	private TextView mMessage;

	private String mPassword;

	private EditText mPasswordEdit;

	/** Was the original caller asking for an entirely new account? */
	protected boolean mRequestNewAccount = false;

	private String mUsername;

	private EditText mUsernameEdit;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onCreate(Bundle icicle) {

		Log.i(TAG, "onCreate(" + icicle + ")");
		super.onCreate(icicle);
		mAccountManager = AccountManager.get(this);
		Log.i(TAG, "loading data from Intent");
		final Intent intent = getIntent();
		mUsername = intent.getStringExtra(PARAM_USERNAME);
		mRequestNewAccount = mUsername == null;
		mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS,
				false);
		Log.i(TAG, "    request new: " + mRequestNewAccount);
		setContentView(R.layout.login);
		mMessage = (TextView) findViewById(R.id.message);
		mUsernameEdit = (EditText) findViewById(R.id.login_username);
		mPasswordEdit = (EditText) findViewById(R.id.login_pass);

		final CheckBox showPassword = (CheckBox) findViewById(R.id.show_password);
		showPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mPasswordEdit.setTransformationMethod(null);
				} else {
					mPasswordEdit
							.setTransformationMethod(new PasswordTransformationMethod());
				}
			}
		});
		if (!TextUtils.isEmpty(mUsername))
			mUsernameEdit.setText(mUsername);
		mMessage.setText(R.string.login_activity_newaccount_text);
	}

	public void showAbout(View view) {
		AboutDialogFragment.newInstance().show(getSupportFragmentManager(),
				DIALOG_ABOUT);
	}

	/**
	 * Handles onClick event on the Submit button. Sends username/password to
	 * the server for authentication. The button is configured to call
	 * handleLogin() in the layout XML.
	 * 
	 * @param view
	 *            The Submit button for which this method is invoked
	 */
	public void handleLogin(View view) {
		if (mRequestNewAccount) {
			mUsername = mUsernameEdit.getText().toString();
		}
		mPassword = mPasswordEdit.getText().toString();
		if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
			mMessage.setText(getMessage());
		} else {
			// Show a progress dialog, and kick off a background task to perform
			// the user login attempt.
			showProgress();

			mAuthTask = AuthenticationUtils.authenticate(mUsername, mPassword,
					this);
		}
	}

	/**
	 * Called when response is received from the server for confirm credentials
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller.
	 * 
	 * @param result
	 *            the confirmCredentials result.
	 */
	private void finishConfirmCredentials(boolean result) {
		Log.i(TAG, "finishConfirmCredentials()");
		final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);
		mAccountManager.setPassword(account, mPassword);
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * Called when response is received from the server for authentication
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller. We store the
	 * authToken that's returned from the server as the 'password' for this
	 * account - so we're never storing the user's actual password locally.
	 * 
	 * @param result
	 *            the confirmCredentials result.
	 */
	private void finishLogin(User user) {

		Log.i(TAG, "finishLogin()");
		final Account account = new Account(mUsername, Constants.ACCOUNT_TYPE);
		if (mRequestNewAccount) {
			mAccountManager.addAccountExplicitly(account, mPassword,
					null);
			// Set contacts sync for this account.
			ContentResolver.setSyncAutomatically(account,
					SigarraContract.CONTENT_AUTHORITY, true);
		} else {
			mAccountManager.setPassword(account, user.getPassword());
		}
		mAccountManager.setUserData(account, Constants.USER_TYPE,user.getType());
		mAccountManager.setUserData(account, Constants.USER_NAME,user.getUser());
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * Returns the message to be displayed at the top of the login dialog box.
	 */
	private CharSequence getMessage() {
		if (TextUtils.isEmpty(mUsername)) {
			// If no username, then we ask the user to log in using an
			// appropriate service.
			final CharSequence msg = getText(R.string.toast_login_error_empty_username);
			return msg;
		}
		if (TextUtils.isEmpty(mPassword)) {
			// We have an account but no password
			return getText(R.string.toast_login_error_empty_password);
		}
		return null;
	}

	/**
	 * Shows the progress UI for a lengthy operation.
	 */
	private void showProgress() {
		ProgressDialogFragment.newInstance(
				getString(R.string.lb_login_authenticating), true).show(
				getSupportFragmentManager(), DIALOG_AUTHENTICATING);

	}

	/**
	 * Hides the progress UI for a lengthy operation.
	 */
	private void hideProgress() {
		removeDialog(DIALOG_AUTHENTICATING);
	}

	public void onError(ERROR_TYPE error) {
		hideProgress();
		switch (error) {
		case CANCELLED:
			// Our task is complete, so clear it out
			mAuthTask = null;
			break;
		case AUTHENTICATION:
			mMessage.setText(R.string.toast_login_error_wrong_password);
			break;
		case NETWORK:
			mMessage.setText(R.string.toast_server_error);
			break;
		default:
			mMessage.setText(R.string.general_error);
			break;
		}
	}

	public void onResultReceived(Object... results) {
		hideProgress();
		User user = (User) results[0];
		if (!mConfirmCredentials) {
			finishLogin(user);
		} else {
			finishConfirmCredentials(true);
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

		Log.i(TAG, "user cancelling authentication");
		if (mAuthTask != null) {
			mAuthTask.cancel(true);
		}
	}

	protected void removeDialog(String dialog) {
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag(dialog);
		if (prev != null) {
			ft.remove(prev).commitAllowingStateLoss();
		}
	}
}
