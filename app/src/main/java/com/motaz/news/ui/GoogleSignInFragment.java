package com.motaz.news.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.motaz.news.R;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoogleSignInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoogleSignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoogleSignInFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    //Fragment XML
    @BindView(R.id.sign_in_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.sign_in_but)
    SignInButton signInButton;
    @BindView(R.id.sign_out_but)
    Button signOutButton;
    @BindView(R.id.login_status)
    TextView mStatusTextView;
    @BindView(R.id.profile_photo_image)
    CircleImageView profileImageView;
    //Fragment Fields
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private final int SINGED_IN = 0;

    public GoogleSignInFragment() {
        // Required empty public constructor
    }

    public static GoogleSignInFragment newInstance() {
        return new GoogleSignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() , this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_sign_in, container, false);
        ButterKnife.bind(this,view);
//        mLinearLayout = view.findViewById(R.id.sign_in_layout);
//        signOutButton = view.findViewById(R.id.sign_out_but);
//        signInButton = view.findViewById(R.id.sign_in_but);
//        mStatusTextView = view.findViewById(R.id.login_status);
//        profileImageView = view.findViewById(R.id.profile_photo_image);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 0);
            }

        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                refreshUI(false);
                            }
                        });
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> optionalPendingResult = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (optionalPendingResult.isDone()) {
            GoogleSignInResult googleSignInResult = optionalPendingResult.get();
            handleResult(googleSignInResult);
        } else {
            startLoading();
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    endLoading();
                    handleResult(googleSignInResult);
                }
            });
        }
    }
    private void handleResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, googleSignInAccount.getDisplayName()));
            if(googleSignInAccount.getPhotoUrl() != null)
                new LoadProfilePictureTask(profileImageView).execute(googleSignInAccount.
                        getPhotoUrl().toString());
            refreshUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            refreshUI(false);
        }
    }
    private void refreshUI(boolean isSignedIn) {
        if (isSignedIn) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            Bitmap defaultImage = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.anomyous_user);
            profileImageView.setImageBitmap(defaultImage);
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(mLinearLayout,R.string.failed_to_conect,Snackbar.LENGTH_LONG);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SINGED_IN) {
            GoogleSignInResult signInResultFromIntent = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(signInResultFromIntent);
        }
        endLoading();
    }

    //Helper methods
    private void startLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void endLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    class LoadProfilePictureTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView gmailImage;
        public LoadProfilePictureTask(CircleImageView gmailImage) {
            this.gmailImage = gmailImage;
        }
        protected Bitmap doInBackground(String... uri) {
            String url = uri[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                //Bitmap resized = Bitmap.createScaledBitmap(result,200,200, true);
                gmailImage.setImageBitmap(result);
                //mageBitmap(ImageHelper.getRoundedCornerBitmap(getContext(),resized,250,200,200, false, false, false, false));

            }
        }
    }
}
