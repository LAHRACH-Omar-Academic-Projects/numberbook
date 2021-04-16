package ma.ensaj.geolocation.ui.contacts;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import java.util.List;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.FriendingState;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ViewHolder> {
    private String imei;
    private DataService service;
    private View view;
    private List<ContactInfo> contactInfoList;
    private Context context;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public ContactsListAdapter(Context context, List<ContactInfo> contactInfoList, String imei) {
        this.mInflater = LayoutInflater.from(context);
        this.contactInfoList = contactInfoList;
        this.imei = imei;
        this.service = RetrofitInstance.getInstance().create(DataService.class);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.contact_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ContactInfo contactInfo = contactInfoList.get(position);
        holder.displayName.setText(contactInfo.getDisplayName());
        holder.addFriend_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                holder.loader.setVisibility(View.VISIBLE);
                holder.addFriend_btn.setText("");
                FriendingState friendingState = new FriendingState();
                friendingState.setStatus(1);
                friendingState.setDate();
                setRequester(friendingState);
            }

            private void setRequester(FriendingState friendingState) {
                Call<User> call = service.getUserByDeviceImei(imei);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        friendingState.setRequester(response.body());
                        setFriend(friendingState);
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("Error", t.toString());
                    }
                });
            }

            private void setFriend(FriendingState friendingState) {
                Call<User> call = service.getUserByTelephone(contactInfo.getPhoneNumber());
                call.enqueue(new Callback<User>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        friendingState.setResponder(response.body());
                        addFriendingState(friendingState);
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.d("Error", t.toString());
                    }
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            private void addFriendingState(FriendingState friendingState) {
                Call<Boolean> call = service.addFriend(friendingState);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        holder.addFriend_btn.setVisibility(View.GONE);
                        holder.loader.setVisibility(View.GONE);
                        holder.request_sent_icon.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "Demande envoyée", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Log.d("Error", t.toString());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView displayName;
        ProgressBar loader;
        Button addFriend_btn;
        ImageView request_sent_icon;

        ViewHolder(View itemView) {
            super(itemView);

            displayName = itemView.findViewById(R.id.displayName);
            addFriend_btn = itemView.findViewById(R.id.add_btn);
            loader = itemView.findViewById(R.id.loader);
            request_sent_icon = itemView.findViewById(R.id.request_sent);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    ContactInfo getItem(int id) {
        return contactInfoList.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}