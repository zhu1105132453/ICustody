package com.example.zjj20181218.icustody.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.example.zjj20181218.icustody.MyApplication;
import com.example.zjj20181218.icustody.javaBean.Content;
import com.example.zjj20181218.icustody.R;
import com.example.zjj20181218.icustody.util.HttpUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Zjj on 2018/12/20.
 */

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {

    private static final int NAME_S = 10;
    private static final int NAME_F = 11;
    private static final int GENDER_S = 20;
    private static final int GENDER_F = 21;
    private static final int BIRTH_S = 30;
    private static final int BIRTH_F = 31;
    private static final int EMAIL_S = 40;
    private static final int EMAIL_F = 41;

    private Context mContext;
    private List<Content> mContentList;
    private AlertView mAlertViewExt;
    private InputMethodManager imm;
    private ViewGroup extView;
    private EditText etName;
    private ViewGroup parentViewGroup;
    private String[] filed = {"userid", "name", "gender", "birth", "email"};
    private String[] data;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View contentView;
        TextView textView_title;
        TextView textView_content;
        private LinearLayout item;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            item = (LinearLayout) itemView.findViewById(R.id.item);
            textView_title = (TextView) itemView.findViewById(R.id.title_user);
            textView_content = (TextView) itemView.findViewById(R.id.content_user);
        }
    }

    public SettingAdapter(List<Content> contentList, Context context) {
        mContentList = contentList;
        mContext = context;
        extView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.alertext_form, null);
        etName = (EditText) extView.findViewById(R.id.etName);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                //输入框出来则往上移动
                boolean isOpen = imm.isActive();
                mAlertViewExt.setMarginBottom(isOpen && focus ? 120 : 0);
                System.out.println(isOpen);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_presonal, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView_title.setText(mContentList.get(position).getUser_title());
        holder.textView_content.setText(mContentList.get(position).getUser_content());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("");
                switch (position) {
                    case 0:
                        etName.setText(MyApplication.user.getName());
                        mAlertViewExt = new AlertView("修改姓名", "请输入您要修改的姓名！", "取消",
                                null, new String[]{"确定修改"}, mContext, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, final int position) {
                                closeKeyboard();
                                //判断是否是拓展窗口View，而且点击的是非取消按钮
                                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                                    final String name = etName.getText().toString();
                                    if (name.isEmpty()) {
                                        Toast.makeText(mContext, "啥都没填呢,修改失败", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (!MyApplication.user.getName().equals(name)) {
                                            data = new String[]{MyApplication.user.getId(), name, "", "", ""};

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        HttpUtil.post("http://120.79.229.78/zjj/android/database/setting.php", filed, data).enqueue(new Callback() {
                                                            @Override
                                                            public void onFailure(Call call, IOException e) {
                                                                Message message = new Message();
                                                                message.what = NAME_F;
                                                                handler.sendMessage(message);
                                                            }

                                                            @Override
                                                            public void onResponse(Call call, Response response) throws IOException {
                                                                Message message = new Message();
                                                                message.what = NAME_S;
                                                                message.obj = name;
                                                                handler.sendMessage(message);
                                                            }
                                                        });
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                        } else {
                                            Toast.makeText(mContext, "无效修改！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    return;
                                }
                            }
                        });
                        parentViewGroup = (ViewGroup) extView.getParent();
                        if (parentViewGroup != null) {
                            parentViewGroup.removeAllViews();
                        }
                        mAlertViewExt.addExtView(extView);
                        mAlertViewExt.setCancelable(true).show();
                        break;
                    case 1:
                        new AlertView("修改性别", null, "取消", null,
                                new String[]{"男", "女"}, mContext, AlertView.Style.ActionSheet, new OnItemClickListener() {
                            public void onItemClick(Object o, int position) {
                                switch (position) {
                                    case 0:
                                        //修改性别为男
                                        if (MyApplication.user.getGender().equals("男")){
                                            Toast.makeText(mContext, "无效修改！", Toast.LENGTH_SHORT).show();
                                            break;
                                        } else {
                                            changeGender("男");
                                            break;
                                        }
                                    case 1:
                                        //修改性别为女
                                        if (MyApplication.user.getGender().equals("女")){
                                            Toast.makeText(mContext, "无效修改！", Toast.LENGTH_SHORT).show();
                                            break;
                                        } else {
                                            changeGender("女");
                                            break;
                                        }
                                }
                            }
                        }).setCancelable(true).show();
                        break;
                    case 2:
                        etName.setText(MyApplication.user.getBirth());
                        mAlertViewExt = new AlertView("修改出生年月", "请按正确格式输入您的出生年月！", "取消",
                                null, new String[]{"确定修改"}, mContext, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, final int position) {
                                closeKeyboard();
                                //判断是否是拓展窗口View，而且点击的是非取消按钮
                                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                                    final String birth = etName.getText().toString();
                                    if (birth.isEmpty()) {
                                        Toast.makeText(mContext, "啥都没填呢,修改失败", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (!MyApplication.user.getBirth().equals(birth)) {
                                            data = new String[]{MyApplication.user.getId(), "", "", birth, ""};
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        HttpUtil.post("http://120.79.229.78/zjj/android/database/setting.php", filed, data).enqueue(new Callback() {
                                                            @Override
                                                            public void onFailure(Call call, IOException e) {
                                                                Message message = new Message();
                                                                message.what = BIRTH_F;
                                                                handler.sendMessage(message);
                                                            }

                                                            @Override
                                                            public void onResponse(Call call, Response response) throws IOException {
                                                                Message message = new Message();
                                                                message.what = BIRTH_S;
                                                                message.obj = birth;
                                                                handler.sendMessage(message);
                                                            }
                                                        });
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                        } else {
                                            Toast.makeText(mContext, "无效修改！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    return;
                                }
                            }
                        });
                        parentViewGroup = (ViewGroup) extView.getParent();
                        if (parentViewGroup != null) {
                            parentViewGroup.removeAllViews();
                        }
                        mAlertViewExt.addExtView(extView);
                        mAlertViewExt.setCancelable(true).show();
                        break;
                    case 3:
                        etName.setText(MyApplication.user.getEmail());
                        mAlertViewExt = new AlertView("修改邮箱", "请输入您要更换绑定的邮箱！", "取消",
                                null, new String[]{"确定修改"}, mContext, AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, final int position) {
                                closeKeyboard();
                                //判断是否是拓展窗口View，而且点击的是非取消按钮
                                if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
                                    final String email = etName.getText().toString();
                                    if (email.isEmpty()) {
                                        Toast.makeText(mContext, "啥都没填呢,修改失败", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (!MyApplication.user.getEmail().equals(email)) {
                                            data = new String[]{MyApplication.user.getId(), "", "", "", email};
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        HttpUtil.post("http://120.79.229.78/zjj/android/database/setting.php", filed, data).enqueue(new Callback() {
                                                            @Override
                                                            public void onFailure(Call call, IOException e) {
                                                                Message message = new Message();
                                                                message.what = EMAIL_F;
                                                                handler.sendMessage(message);
                                                            }

                                                            @Override
                                                            public void onResponse(Call call, Response response) throws IOException {
                                                                Message message = new Message();
                                                                message.what = EMAIL_S;
                                                                message.obj = email;
                                                                handler.sendMessage(message);
                                                            }
                                                        });
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
                                        } else {
                                            Toast.makeText(mContext, "无效修改！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    return;
                                }
                            }
                        });
                        parentViewGroup = (ViewGroup) extView.getParent();
                        if (parentViewGroup != null) {
                            parentViewGroup.removeAllViews();
                        }
                        mAlertViewExt.addExtView(extView);
                        mAlertViewExt.setCancelable(true).show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void changeGender(final String gender){
        data = new String[]{MyApplication.user.getId(), "", gender, "", ""};
        Log.i("ffffff", gender);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil.post("http://120.79.229.78/zjj/android/database/setting.php", filed, data).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Message message = new Message();
                            message.what = GENDER_F;
                            handler.sendMessage(message);
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.i("ttttt", response.body().string());
                            Message message = new Message();
                            message.what = GENDER_S;
                            message.obj = gender;
                            handler.sendMessage(message);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return mContentList.size();
    }

    private void closeKeyboard() {
        //关闭软键盘
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
        //恢复位置
        mAlertViewExt.setMarginBottom(0);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String result = (String) msg.obj;
            switch (msg.what){
                case NAME_S:
                    MyApplication.user.setName(result);
                    mContentList.get(0).setUser_content(result);
                    notifyItemChanged(0);
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case NAME_F:
                    Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                    break;
                case GENDER_S:
                    MyApplication.user.setGender(result);
                    mContentList.get(1).setUser_content(result);
                    notifyItemChanged(1);
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case GENDER_F:
                    Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                    break;
                case BIRTH_S:
                    MyApplication.user.setBirth(result);
                    mContentList.get(2).setUser_content(result);
                    notifyItemChanged(2);
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case BIRTH_F:
                    Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                    break;
                case EMAIL_S:
                    MyApplication.user.setEmail( result);
                    mContentList.get(3).setUser_content(result);
                    notifyItemChanged(3);
                    Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                    break;
                case EMAIL_F:
                    Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
