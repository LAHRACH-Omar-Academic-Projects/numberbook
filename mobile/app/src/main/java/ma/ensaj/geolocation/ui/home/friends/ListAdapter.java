package ma.ensaj.geolocation.ui.home.friends;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.FriendingState;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private DataService service;
    private View view;
    private List<User> list;
    private int position;
    private Context context;
    private String listName;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String imei;

    public ListAdapter(Context context, List<User> list, String listName, String imei) {
        this.imei = imei;
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
        this.service = RetrofitInstance.getInstance().create(DataService.class);
        this.context = context;
        this.listName = listName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (listName) {
            case "myFriends":
                view = mInflater.inflate(R.layout.my_friend_row, parent, false);
                break;
            case "friendRequests":
                view = mInflater.inflate(R.layout.friend_request_row, parent, false);
                break;
            case "friendsWaiting":
                view = mInflater.inflate(R.layout.friends_waiting_row, parent, false);
                break;
            default:
                break;
        }
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        this.position = position;
        User user = list.get(position);
        String username = user.getPrenom() + " " + user.getNom();
        holder.displayName.setText(username);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView displayName;
        Button accept_btn;
        Button refuse_btn;
        Button request_sent_btn;

        ViewHolder(View itemView, ListAdapter adapter) {
            super(itemView);

            displayName = itemView.findViewById(R.id.displayName);
            switch (listName) {
                case "friendRequests":
                    accept_btn = itemView.findViewById(R.id.accept_btn);
                    refuse_btn = itemView.findViewById(R.id.refuse_btn);

                    accept_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Call<User> call = service.getUserByDeviceImei(imei);
                            call.enqueue(new Callback<User>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                    User currentUser = response.body();
                                    User friend = list.get(position);

                                    FriendingState friendingState = new FriendingState();
                                    friendingState.setRequester(friend);
                                    friendingState.setResponder(currentUser);
                                    friendingState.setDate();
                                    friendingState.setStatus(2);

                                    Call<Boolean> call1 = service.addFriend(friendingState);
                                    call1.enqueue(new Callback<Boolean>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                                            if(response.body()) {
                                                Log.d("Accepted", "hi");
                                                list.remove(position);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NonNull Call<Boolean> call, Throwable t) {
                                            Log.d("Error", t.toString());
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(@NonNull Call<User> call, Throwable t) {
                                    Log.d("Error", t.toString());
                                }
                            });
                        }
                    });

                    refuse_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Call<User> call = service.getUserByDeviceImei(imei);
                            call.enqueue(new Callback<User>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                    User currentUser = response.body();
                                    User friend = list.get(position);

                                    FriendingState friendingState = new FriendingState();
                                    friendingState.setRequester(friend);
                                    friendingState.setResponder(currentUser);

                                    Call<Boolean> call1 = service.refuseFriend(friendingState);
                                    call1.enqueue(new Callback<Boolean>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                                            list.remove(position);
                                            adapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onFailure(@NonNull Call<Boolean> call, Throwable t) {
                                            Log.d("Error", t.toString());
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(@NonNull Call<User> call, Throwable t) {
                                    Log.d("Error", t.toString());
                                }
                            });
                        }
                    });
                    break;
                case "friendsWaiting":
                    request_sent_btn = itemView.findViewById(R.id.request_sent_btn);
                    break;
                default:
                    break;
            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    User getItem(int id) {
        return list.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}