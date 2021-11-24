package com.cookandroid.aifooddiaryapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Frag_FoodCalendar extends Fragment {
    private View view;

    // 위젯 변수 생성
    TextView tv_date;
    CardView cv_morning, cv_lunch, cv_dinner, cv_snack;
    ImageView img_morning, img_lunch, img_dinner, img_snack;

    // 식단을 추가했는데 사진을 추가 안 했을 경우 다음 이미지를 띄우게 됨
    ImageView img_no_image_morning, img_no_image_lunch, img_no_image_dinner, img_no_image_snack;

    Bundle bundle = new Bundle();
    // 날짜 변수 생성
    Date today;
    String currentDate;
    Intent intent;

    Bitmap bitmap;
    String mCurrentPhotoPath_m, mCurrentPhotoPath_l, mCurrentPhotoPath_d, mCurrentPhotoPath_s;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_foodcalendar, container, false);
        intent = new Intent(getActivity(),Camera.class);
        // 위젯 변수 id 연결
        tv_date = (TextView) view.findViewById(R.id.tv_date);

        cv_morning = (CardView) view.findViewById(R.id.cv_morning);
        cv_lunch = (CardView) view.findViewById(R.id.cv_lunch);
        cv_dinner = (CardView) view.findViewById(R.id.cv_dinner);
        cv_snack = (CardView) view.findViewById(R.id.cv_snack);

        img_morning = (ImageView) view.findViewById(R.id.img_morning);
        img_lunch = (ImageView) view.findViewById(R.id.img_lunch);
        img_dinner = (ImageView) view.findViewById(R.id.img_dinner);
        img_snack = (ImageView) view.findViewById(R.id.img_snack);

        // 식단 추가시 수기로 (이미지 없이) 추가 했을 때 다음 이미지를 띄게 됨
        img_no_image_morning = (ImageView) view.findViewById(R.id.img_no_image_morning);
        img_no_image_lunch = (ImageView) view.findViewById(R.id.img_no_image_lunch);
        img_no_image_dinner = (ImageView) view.findViewById(R.id.img_no_image_dinner);
        img_no_image_snack = (ImageView) view.findViewById(R.id.img_no_image_snack); 

        // 현재 날짜 불러와서 tv_date에 찍어줌
        today = new Date();
        tv_date.setText(new SimpleDateFormat("MM월 dd일").format(today));
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(today);

        // 현재 날짜에 식단 존재시 다이어리 가져오는 과정 필요
        // 푸드 다이어리 부분
        // 현재 보고 있는 데이트에 식단이 존재 한다면 기존에 있는 다이어리 내용 가져오는 과정 필요
        Response.Listener<String> getresponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject getjsonObject = new JSONObject(response);

                    // DB에 저장된 사용자의 사진 저장 파일 경로 변수에 저장함
                    if(getjsonObject.isNull("mealPhoto_m")) {

                    } else {
                        mCurrentPhotoPath_m = getjsonObject.getString("mealPhoto_m");
                    }
                    if(getjsonObject.isNull("mealPhoto_l")) {

                    } else {
                        mCurrentPhotoPath_l = getjsonObject.getString("mealPhoto_l");
                    }
                    if(getjsonObject.isNull("mealPhoto_d")) {

                    } else {
                         mCurrentPhotoPath_d = getjsonObject.getString("mealPhoto_d");
                    }
                    if(getjsonObject.isNull("mealPhoto_s")) {

                    } else {
                        mCurrentPhotoPath_s = getjsonObject.getString("mealPhoto_s");
                    }


                    if(getjsonObject.getBoolean("available") == true) {
                        // 해당 날짜에 데이터가 있다는 것.
                        if(getjsonObject.getInt("mealMorning") > 0) {
                            // 모닝에 데이터가 존재한다면 해당 음식 데이터 가져옴
                            String mealMorning = getjsonObject.getString("Morning");

                            // 가져온 음식 데이터는 , 를 기준으로 구분이 되어있으므로 스플릿해줌
                            String morning[] = mealMorning.split(",");

                            // 가져올 음식이 있는 것이므로 +모양(추가 모양)은 안 보이게 함.
                            cv_morning.setVisibility(View.GONE);

                            // 띄워줄 사진이 있는지 확인함
                            if(mCurrentPhotoPath_m.equals("")) {
                                // 띄워줄 사진이 없으면 img_no_image_morning을 보이게 함
                                img_morning.setVisibility(View.GONE);
                                img_no_image_morning.setVisibility(View.VISIBLE);

                            } else {
                                //이미지뷰에 음식사진 불러오기
                                img_morning.setVisibility(View.VISIBLE);
                                File file = new File(mCurrentPhotoPath_m);
                                if (Build.VERSION.SDK_INT >= 29) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                                    try {
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                        if (bitmap != null) {
                                            img_morning.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                                        if (bitmap != null) {
                                            img_morning.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }

                        if(getjsonObject.getInt("mealLunch") > 0) {
                            // 점심에 데이터가 존재한다면 해당 음식 데이터 가져옴
                            String mealLunch = getjsonObject.getString("Lunch");

                            // 가져온 음식 데이터는 , 를 기준으로 구분이 되어있으므로 스플릿해줌
                            String lunch[] = mealLunch.split(",");

                            // 가져올 음식이 있는 것이므로 +모양(추가 모양)은 안 보이게 함.
                            cv_lunch.setVisibility(View.GONE);

                            // 여기서부터 코딩 해주면 됩니다.
                            // 띄워줄 사진이 있는지 확인함
                            if(mCurrentPhotoPath_l.equals("")) {
                                // 띄워줄 사진이 없으면 img_no_image_lunch을 보이게 함
                                img_lunch.setVisibility(View.GONE);
                                img_no_image_lunch.setVisibility(View.VISIBLE);
                            } else {
                                //이미지뷰에 음식사진 불러오기
                                img_lunch.setVisibility(View.VISIBLE);
                                File file = new File(mCurrentPhotoPath_l);
                                if (Build.VERSION.SDK_INT >= 29) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                                    try {
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                        if (bitmap != null) {
                                            img_lunch.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                                        if (bitmap != null) {
                                            img_lunch.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        if(getjsonObject.getInt("mealDinner") > 0) {
                            // 점심에 데이터가 존재한다면 해당 음식 데이터 가져옴
                            String mealDinner = getjsonObject.getString("Dinner");

                            // 가져온 음식 데이터는 , 를 기준으로 구분이 되어있으므로 스플릿해줌
                            String dinner[] = mealDinner.split(",");

                            // 가져올 음식이 있는 것이므로 +모양(추가 모양)은 안 보이게 함.
                            cv_dinner.setVisibility(View.GONE);

                            // 띄워줄 사진이 있는지 확인함
                            if(mCurrentPhotoPath_d.equals("")) {
                                // 띄워줄 사진이 없으면 img_no_image_dinner을 보이게 함
                                img_dinner.setVisibility(View.GONE);
                                img_no_image_dinner.setVisibility(View.VISIBLE);
                            } else {
                                //이미지뷰에 음식사진 불러오기
                                img_dinner.setVisibility(View.VISIBLE);
                                File file = new File(mCurrentPhotoPath_d);
                                if (Build.VERSION.SDK_INT >= 29) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                                    try {
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                        if (bitmap != null) {
                                            img_dinner.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                                        if (bitmap != null) {
                                            img_dinner.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        if(getjsonObject.getInt("mealSnack") > 0) {
                            // 점심에 데이터가 존재한다면 해당 음식 데이터 가져옴
                            String mealSnack = getjsonObject.getString("Snack");

                            // 가져온 음식 데이터는 , 를 기준으로 구분이 되어있으므로 스플릿해줌
                            String sncak[] = mealSnack.split(",");

                            // 가져올 음식이 있는 것이므로 +모양(추가 모양)은 안 보이게 함.
                            cv_snack.setVisibility(View.GONE);

                            // 여기서부터 코딩 해주면 됩니다.
                            // 띄워줄 사진이 있는지 확인함
                            if(mCurrentPhotoPath_s.equals("")) {
                                // 띄워줄 사진이 없으면 img_no_image_sncak을 보이게 함
                                img_snack.setVisibility(View.GONE);
                                img_no_image_snack.setVisibility(View.VISIBLE);
                            } else {
                                //이미지뷰에 음식사진 불러오기
                                img_snack.setVisibility(View.VISIBLE);
                                File file = new File(mCurrentPhotoPath_s);
                                if (Build.VERSION.SDK_INT >= 29) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                                    try {
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                        if (bitmap != null) {
                                            img_snack.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                                        if (bitmap != null) {
                                            img_snack.setImageBitmap(bitmap);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                    } else {
                        // 가져온 데이터가 없다는 것이므로 + 모양(추가 모양)이 다시 보이게함
                        cv_morning.setVisibility(View.VISIBLE);
                        cv_lunch.setVisibility(View.VISIBLE);
                        cv_dinner.setVisibility(View.VISIBLE);
                        cv_snack.setVisibility(View.VISIBLE);

                        // 또한 no_image 시리즈 안 보이게 함
                        img_no_image_morning.setVisibility(View.GONE);
                        img_no_image_lunch.setVisibility(View.GONE);
                        img_no_image_dinner.setVisibility(View.GONE);
                        img_no_image_snack.setVisibility(View.GONE);

                        img_morning.setVisibility(View.GONE);
                        img_lunch.setVisibility(View.GONE);
                        img_dinner.setVisibility(View.GONE);
                        img_snack.setVisibility(View.GONE);

                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함.
        FoodCalendar_GetRequest foodCalendar_getRequest= new FoodCalendar_GetRequest(HomeActivity.userID, currentDate, getresponseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(foodCalendar_getRequest);



        // 데이트 피커 생성
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String today = month + 1 + "월 " + dayOfMonth + "일";
                tv_date.setText(today);

                // 현재 선택한 날짜를 담을 변수 선언
                currentDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                // 푸드 다이어리 부분
                // 현재 보고 있는 데이트에 식단이 존재 한다면 기존에 있는 다이어리 내용 가져오는 과정 필요
                Response.Listener<String> getresponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject getjsonObject = new JSONObject(response);

                            // DB에 저장된 사용자의 사진 저장 파일 경로 변수에 저장함
                            if(getjsonObject.isNull("mealPhoto_m")) {

                            } else {
                                mCurrentPhotoPath_m = getjsonObject.getString("mealPhoto_m");
                            }
                            if(getjsonObject.isNull("mealPhoto_l")) {

                            } else {
                                mCurrentPhotoPath_l = getjsonObject.getString("mealPhoto_l");
                            }
                            if(getjsonObject.isNull("mealPhoto_d")) {

                            } else {
                                mCurrentPhotoPath_d = getjsonObject.getString("mealPhoto_d");
                            }
                            if(getjsonObject.isNull("mealPhoto_s")) {

                            } else {
                                mCurrentPhotoPath_s = getjsonObject.getString("mealPhoto_s");
                            }


                            if(getjsonObject.getBoolean("available") == true) {
                                // 해당 날짜에 데이터가 있다는 것.
                                if(getjsonObject.getInt("mealMorning") > 0) {
                                    // 모닝에 데이터가 존재한다면 해당 음식 데이터 가져옴
                                    String mealMorning = getjsonObject.getString("Morning");

                                    // 가져온 음식 데이터는 , 를 기준으로 구분이 되어있으므로 스플릿해줌
                                    String morning[] = mealMorning.split(",");

                                    // 가져올 음식이 있는 것이므로 +모양(추가 모양)은 안 보이게 함.
                                    cv_morning.setVisibility(View.GONE);

                                    // 띄워줄 사진이 있는지 확인함
                                    if(mCurrentPhotoPath_m.equals("")) {
                                        // 띄워줄 사진이 없으면 img_no_image_morning을 보이게 함
                                        img_morning.setVisibility(View.GONE);
                                        img_no_image_morning.setVisibility(View.VISIBLE);

                                    } else {
                                        //이미지뷰에 음식사진 불러오기
                                        File file = new File(mCurrentPhotoPath_m);
                                        if (Build.VERSION.SDK_INT >= 29) {
                                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                                            try {
                                                bitmap = ImageDecoder.decodeBitmap(source);
                                                if (bitmap != null) {
                                                    img_morning.setImageBitmap(bitmap);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                                                if (bitmap != null) {
                                                    img_morning.setImageBitmap(bitmap);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }

                                if(getjsonObject.getInt("mealLunch") > 0) {
                                    // 점심에 데이터가 존재한다면 해당 음식 데이터 가져옴
                                    String mealLunch = getjsonObject.getString("Lunch");

                                    // 가져온 음식 데이터는 , 를 기준으로 구분이 되어있으므로 스플릿해줌
                                    String lunch[] = mealLunch.split(",");

                                    // 가져올 음식이 있는 것이므로 +모양(추가 모양)은 안 보이게 함.
                                    cv_lunch.setVisibility(View.GONE);

                                    // 여기서부터 코딩 해주면 됩니다.
                                    // 띄워줄 사진이 있는지 확인함
                                    if(mCurrentPhotoPath_l.equals("")) {
                                        // 띄워줄 사진이 없으면 img_no_image_lunch을 보이게 함
                                        img_lunch.setVisibility(View.GONE);
                                        img_no_image_lunch.setVisibility(View.VISIBLE);
                                    } else {
                                        //이미지뷰에 음식사진 불러오기
                                        File file = new File(mCurrentPhotoPath_l);
                                        if (Build.VERSION.SDK_INT >= 29) {
                                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                                            try {
                                                bitmap = ImageDecoder.decodeBitmap(source);
                                                if (bitmap != null) {
                                                    img_lunch.setImageBitmap(bitmap);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                                                if (bitmap != null) {
                                                    img_lunch.setImageBitmap(bitmap);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                                if(getjsonObject.getInt("mealDinner") > 0) {
                                    // 점심에 데이터가 존재한다면 해당 음식 데이터 가져옴
                                    String mealDinner = getjsonObject.getString("Dinner");

                                    // 가져온 음식 데이터는 , 를 기준으로 구분이 되어있으므로 스플릿해줌
                                    String dinner[] = mealDinner.split(",");

                                    // 가져올 음식이 있는 것이므로 +모양(추가 모양)은 안 보이게 함.
                                    cv_dinner.setVisibility(View.GONE);

                                    // 여기서부터 코딩 해주면 됩니다.
                                    // 띄워줄 사진이 있는지 확인함
                                    if(mCurrentPhotoPath_d.equals("")) {
                                        // 띄워줄 사진이 없으면 img_no_image_dinner을 보이게 함
                                        img_dinner.setVisibility(View.GONE);
                                        img_no_image_dinner.setVisibility(View.VISIBLE);
                                    } else {
                                        //이미지뷰에 음식사진 불러오기
                                        File file = new File(mCurrentPhotoPath_d);
                                        if (Build.VERSION.SDK_INT >= 29) {
                                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                                            try {
                                                bitmap = ImageDecoder.decodeBitmap(source);
                                                if (bitmap != null) {
                                                    img_dinner.setImageBitmap(bitmap);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                                                if (bitmap != null) {
                                                    img_dinner.setImageBitmap(bitmap);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                                if(getjsonObject.getInt("mealSnack") > 0) {
                                    // 점심에 데이터가 존재한다면 해당 음식 데이터 가져옴
                                    String mealSnack = getjsonObject.getString("Snack");

                                    // 가져온 음식 데이터는 , 를 기준으로 구분이 되어있으므로 스플릿해줌
                                    String sncak[] = mealSnack.split(",");

                                    // 가져올 음식이 있는 것이므로 +모양(추가 모양)은 안 보이게 함.
                                    cv_snack.setVisibility(View.GONE);

                                    // 여기서부터 코딩 해주면 됩니다.
                                    // 띄워줄 사진이 있는지 확인함
                                    if(mCurrentPhotoPath_s.equals("")) {
                                        // 띄워줄 사진이 없으면 img_no_image_sncak을 보이게 함
                                        img_snack.setVisibility(View.GONE);
                                        img_no_image_snack.setVisibility(View.VISIBLE);
                                    } else {

                                        //이미지뷰에 음식사진 불러오기
                                        File file = new File(mCurrentPhotoPath_s);
                                        if (Build.VERSION.SDK_INT >= 29) {
                                            ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), Uri.fromFile(file));
                                            try {
                                                bitmap = ImageDecoder.decodeBitmap(source);
                                                if (bitmap != null) {
                                                    img_snack.setImageBitmap(bitmap);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                                                if (bitmap != null) {
                                                    img_snack.setImageBitmap(bitmap);
                                                }
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                            } else {
                                // 가져온 데이터가 없다는 것이므로 + 모양(추가 모양)이 다시 보이게함
                                cv_morning.setVisibility(View.VISIBLE);
                                cv_lunch.setVisibility(View.VISIBLE);
                                cv_dinner.setVisibility(View.VISIBLE);
                                cv_snack.setVisibility(View.VISIBLE);

                                // 또한 no_image 시리즈 안 보이게 함
                                img_no_image_morning.setVisibility(View.GONE);
                                img_no_image_lunch.setVisibility(View.GONE);
                                img_no_image_dinner.setVisibility(View.GONE);
                                img_no_image_snack.setVisibility(View.GONE);
                            }

                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                // 서버로 Volley를 이용해서 요청을 함.
                FoodCalendar_GetRequest foodCalendar_getRequest= new FoodCalendar_GetRequest(HomeActivity.userID, currentDate, getresponseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(foodCalendar_getRequest);


            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.setTitle("날짜 선택");
        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);

        // 날짜를 클릭하면 데이트 피커가 나오고 날짜가 변경되게 함
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // cv_morning 클릭 했을 때의 이벤트 처리
        cv_morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다음 프래그먼트에도 날짜 정보 끼니 정보 전달
                intent.putExtra("meal","아침");
                intent.putExtra("date",currentDate);
                startActivity(intent);
            }
        });

        // cv_lunch 클릭 했을 때의 이벤트 처리
        cv_lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("meal","점심");
                intent.putExtra("date",currentDate);
                startActivity(intent);
            }
        });

        // cv_dinner 클릭 했을 때의 이벤트 처리
        cv_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("meal","저녁");
                intent.putExtra("date",currentDate);
                startActivity(intent);
            }
        });

        // cv_snack 클릭 했을 때의 이벤트 처리
        cv_snack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("meal","간식");
                intent.putExtra("date",currentDate);
                startActivity(intent);
            }
        });

        // 푸드 이미지 뷰 클릭 했을 때의 이벤트 처리
        img_morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFoodName();
                // 음식이 여러 개일시 여러번 푸드 인포 접근하도록 함.
                getFoodInfo();
            }
        });



        return view;
    }

    public void getFoodName() {
        Response.Listener<String> getresponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject getjsonObject = new JSONObject(response);

                    String userMeal = getjsonObject.getString("Morning");

                    String meals[] = userMeal.split(",");

                    for(int i = 0; i < meals.length; i++ ) {

                    }



                } catch(Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청을 함.
        FoodCalendarInfo_Request foodCalendarInfoRequest = new FoodCalendarInfo_Request(HomeActivity.userID, currentDate, getresponseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(foodCalendarInfoRequest);
    }

    public void getFoodInfo() {
        //add_camera에 있는 getFoodInfo 메소드와 비슷하게 구성하면됨.

    }
}
