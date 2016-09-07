package tomerbu.edu.firebaseupdatechildrenandondisconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tomerbu.edu.firebaseupdatechildrenandondisconnect.models.User;
import tomerbu.edu.firebaseupdatechildrenandondisconnect.tools.Intents;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail;
    EditText etPassword;
    private CallbackManager callbackManager;
    private LoginButton btnFace;
    private final static int RC_SIGN_IN_GOOGLE = 20;
    private SignInButton btnGoogle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //On Layout Click -> Hide keyboard
        hideKeyboardWhenNeeded();

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        setupFacebookLoginButton();
        setupGoogleLoginButton();
    }

    /**
     * Hides the keyboard when layout is touched.
     */
    private void hideKeyboardWhenNeeded() {
        findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });
    }

    private void hideKeyboard() {
        View v = getCurrentFocus();
        if (v == null)
            v = new View(LoginActivity.this);
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void login(final View view) {
        hideKeyboard();
        showProgressDialog();
        FirebaseAuth.getInstance().
                signInWithEmailAndPassword(getEmail(), getPassword()).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        gotoMain();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackBar(e, view);
            }
        });
    }

    private ProgressDialog dialog;

    private void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setTitle("Logging you in...");
            dialog.setMessage("Connecting to server");
        }
        dialog.show();
    }

    private void hideProgress() {
        if (dialog != null)
            dialog.dismiss();
    }


    private void showSnackBar(Exception e, View view) {
        hideProgress();
        Snackbar.make(view, e.getLocalizedMessage(),
                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        }).show();
    }

    /**
     * Start an intent without adding the activity to the stack
     */
    private void gotoMain() {
        hideProgress();
        Intents.gotoMain(getApplicationContext());
    }

    public void signUp(final View view) {
        hideKeyboard();
        showProgressDialog();
        FirebaseAuth.
                getInstance().
                createUserWithEmailAndPassword(getEmail(), getPassword()).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        saveUserToDataBase();
                        gotoMain();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showSnackBar(e, view);
                    }
                });
    }

    private void saveUserToDataBase() {

        //get the current user:
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //init a model of user:
        User user = new User(currentUser.getUid(), currentUser.getEmail());

        //get a reference to the users table
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUID());
        // save the new User under the node <userID>

        ref.setValue(user);
    }


    public String getEmail() {
        return etEmail.getText().toString();
    }

    public String getPassword() {
        return etPassword.getText().toString();
    }


    /*Facebook Login*/
    private void setupFacebookLoginButton() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        btnFace = (LoginButton) findViewById(R.id.btnFace);
        btnFace.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e("TomerBu", "Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("TomerBu", error.getLocalizedMessage());
            }
        });
    }

    /*Facebook Login*/
    private void handleFacebookToken(AccessToken token) {
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        loginWithCredentails(credential);
    }

    /*Facebook Login & Google Login*/
    private void loginWithCredentails(AuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful() && task.getException() != null) {
                    showSnackBar(task.getException(), btnFace);
                } else {
                    saveUserToDataBase();
                    gotoMain();
                }
            }
        });
    }

    /*Facebook Login & Google Login*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            handleGoogleToken(data);
        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode())
            //Facebook uses an Event BUS,
            // we will get the result with the callback Manager in the button listener.
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /* Google Login */
    private void setupGoogleLoginButton() {
        btnGoogle = (SignInButton) findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }


    /* Google Login */
    private void signInWithGoogle() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        //The credentials go into the gs options object
        builder.addApi(Auth.GOOGLE_SIGN_IN_API, gso);
        GoogleApiClient googleApiClient = builder.build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    /* Google Login */
    private void handleGoogleToken(Intent data) {
        showProgressDialog();
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            loginWithCredentails(credential);
        } else {
            showSnackBar(new Exception("Google Sign In failed"), btnGoogle);
        }
    }
}