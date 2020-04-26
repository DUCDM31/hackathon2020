package com.hiepdt.annavoochackathon.converse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.converse.suggest.SuggestActivity;
import com.hiepdt.annavoochackathon.models.HttpRequest;
import com.scwang.wave.MultiWaveHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class SupportActivity extends AppCompatActivity {

    private ImageView btnVoice;
    private TextView tvDetect, tvReply;
    private SpeechRecognizer mSpeechRecongizer;
    private Intent mSpeechRecognizerIntent;
    private RelativeLayout background;

    private String POST_URL = "https://infinite-brook-00680.herokuapp.com/api/df_text_query";

    private Integer fun[];
    private Integer sad[];
    private Integer worry[];

    private int STATUS = 1;
    private Integer bg;

    private TextView tutorial;
    private String[] notReply;

    private String[] symptoms;
    private String[] centers;
    private Integer CUR;

    private MultiWaveHeader wave1, wave2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        getSupportActionBar().hide();

        init();
        action();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void init() {
        wave1 = findViewById(R.id.wave1);
        wave2 = findViewById(R.id.wave2);

        CUR = R.drawable.gradient;
        notReply = new String[]{
                "Hiện tại, tôi không có đủ thẩm quyền để trả lời câu hỏi của bạn",
                "Bạn có thể nói lại giúp tôi dễ hiểu hơn được không?",
                "Hmm, tôi xin lỗi nhưng tôi chưa hiểu ý bạn lắm.!!",
                "Tôi không hiểu ý bạn lắm.",
                "Tôi có thể nghe lại câu bạn vừa nói chứ?"
        };
        symptoms = new String[]{
                "đau đầu",
                "đau họng",
                "cảm", "sốt", "mệt mỏi",

        };
        centers = new String[]{
                "trung tâm",
                "tâm lý",
                "hỗ trợ",
                "tổ chức",
                "giúp đỡ",
                "giải tỏa",
        };
        fun = new Integer[]{R.drawable.gradient_fun_1, R.drawable.gradient_fun_2, R.drawable.gradient_fun_3, R.drawable.gradient_fun_4, R.drawable.gradient_fun_5, R.drawable.gradient_fun_6};
        sad = new Integer[]{R.drawable.gradient_sad_1, R.drawable.gradient_sad_2, R.drawable.gradient_sad_3, R.drawable.gradient_sad_4};
        worry = new Integer[]{R.drawable.gradient_worry};
        btnVoice = findViewById(R.id.btnVoice);
        tvDetect = findViewById(R.id.tvDetect);
        tvReply = findViewById(R.id.tvReply);

        bg = R.drawable.gradient_fun_1;
        background = findViewById(R.id.background);
        tutorial = findViewById(R.id.tutorial);
        //-----Animation----//
        animateIntro();

        //----////
        mSpeechRecongizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi_VN");
        mSpeechRecongizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                final ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    wave1.setVisibility(View.INVISIBLE);
                    wave2.setVisibility(View.INVISIBLE);

                    tvDetect.setText(matches.get(0));
                    JSONObject object = new JSONObject();
                    try {
                        object.put("text", matches.get(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new GetData().execute(object);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isContain(symptoms, matches.get(0))) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SupportActivity.this);
                            View viewInflated = LayoutInflater.from(SupportActivity.this).inflate(R.layout.dialog_signal, background, false);
                            Button btnCancel = viewInflated.findViewById(R.id.btnCancel);

                            builder.setView(viewInflated);
                            final AlertDialog dialog = builder.create();
                            Button btnCall = viewInflated.findViewById(R.id.btnCall);
                            btnCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        } else if (isContain(centers, matches.get(0))) {
                            tvReply.setText(Html.fromHtml("<p>Hãy chờ chúng tôi tìm kiếm<font color=\"#396cfd\"> trung tâm hộ trợ tâm lý</font> gần bạn</span></p>"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SupportActivity.this, SuggestActivity.class);
                                    startActivity(intent);
                                }
                            }, 2000);
                        } else {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SupportActivity.this);
                                    View viewInflated = LayoutInflater.from(SupportActivity.this).inflate(R.layout.dialog_end, background, false);
                                    Button btnCancel = viewInflated.findViewById(R.id.btnCancel);

                                    builder.setView(viewInflated);
                                    final AlertDialog dialog = builder.create();
                                    Button btnOk = viewInflated.findViewById(R.id.btnOk);
                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(SupportActivity.this, OptionActivity.class);
                                            intent.putExtra("content", matches.get(0));
                                            startActivity(intent);
                                        }
                                    });
                                    btnCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();

                                        }
                                    });
                                    if (new Random().nextBoolean()) {
                                        dialog.show();
                                    }
                                }
                            }, 3000);
                        }
                    }
                }, 3000);


            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });
    }


    private void action() {
        wave1.setStartColor(Color.parseColor("#ffffff"));
        wave1.setCloseColor(Color.parseColor("#ffffff"));
        wave1.setColorAlpha(.4f);
        wave1.setVelocity(2);
        wave1.setProgress(0.8f);
        wave1.stop();
        wave1.setGradientAngle(45);

        wave2.setStartColor(Color.parseColor("#ffffff"));
        wave2.setCloseColor(Color.parseColor("#ffffff"));
        wave2.setColorAlpha(.4f);
        wave2.setVelocity(2);
        wave2.setProgress(0.8f);
        wave2.stop();
        wave2.setGradientAngle(45);
        checkPermission();
        btnVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        wave1.setVisibility(View.INVISIBLE);
                        wave2.setVisibility(View.INVISIBLE);
                        wave1.stop();
                        wave2.stop();

                        mSpeechRecongizer.stopListening();
                        tvDetect.setHint("You will see the input here");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        tvDetect.setText("");
                        tvDetect.setHint("Listening ... ");
                        wave1.setVisibility(View.VISIBLE);
                        wave2.setVisibility(View.VISIBLE);
                        wave1.start();
                        wave2.start();
                        mSpeechRecongizer.startListening(mSpeechRecognizerIntent);
                        break;
                }
                return false;
            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    public void setSizeTextView() {
        tvReply.getLayoutParams().width = 370 + new Random().nextInt(50) + 50;
    }

    public Integer randomBackground(int status) {
        if (status != STATUS) {
            STATUS = status;
            if (status == 3) {
                bg = worry[0];
            } else if (status == 2) {
                bg = sad[new Random().nextInt(3)];
            } else {
                bg = fun[new Random().nextInt(5)];
            }
        }
        return bg;
    }

    public void animateBackground(final Integer gradient) {
//        background.setAnimation(AnimationUtils.loadAnimation(SupportActivity.this, R.anim.fade_out));
//        background.setBackgroundResource(gradient);
//        background.setAnimation(AnimationUtils.loadAnimation(SupportActivity.this, R.anim.fade_in));
        Drawable[] drawables = new Drawable[]{getDrawable(CUR), getDrawable(gradient)};
        CUR = gradient;

        TransitionDrawable tran = new TransitionDrawable(drawables);
        background.setBackground(tran);
        tran.setCrossFadeEnabled(true);
        tran.startTransition(2000);
    }

    private void animateIntro() {
        tvReply.setAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear_from_top));
        background.setAnimation(AnimationUtils.loadAnimation(this, R.anim.frombottom));
        tvDetect.setAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear));
        btnVoice.setAnimation(AnimationUtils.loadAnimation(this, R.anim.disappear));
    }

    private boolean isContain(String[] arr, String str) {
        for (int i = 0; i < arr.length; i++) {
            if (str.contains(arr[i])) {
                return true;
            }
        }
        return false;
    }

    class GetData extends AsyncTask<JSONObject, Void, String> {

        public GetData() {
        }

        @Override
        protected String doInBackground(JSONObject... jsonObjects) {
            HttpRequest request = new HttpRequest();
            String response = request.sendPOST(POST_URL, jsonObjects[0]);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject object = new JSONObject(result);
                JSONArray array1 = object.getJSONArray("fulfillmentMessages");
                if (array1.length() == 0) {
                    setSizeTextView();
                    animateBackground(randomBackground('2'));
                    tvReply.setText(notReply[new Random().nextInt(notReply.length)]);
                    return;
                }
                JSONObject object1 = array1.getJSONObject(0);
                JSONObject object2 = object1.getJSONObject("text");
                JSONArray array2 = object2.getJSONArray("text");
                String text1 = array2.getString(0);
                String text2 = array2.getString(0);

                String content = text1.substring(0, text1.length() - 4);
                int status = Integer.parseInt(text2.substring(text2.length() - 2, text2.length() - 1));

                System.out.println(status + "");
                setSizeTextView();
                animateBackground(randomBackground(status));
                if (content.length() >= 60) {
                    tvReply.setTextSize(16f);
                }
                tvReply.setText(content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
