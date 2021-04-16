package ma.ensaj.geolocation.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.User;

public class PositionsAdapter extends RecyclerView.Adapter<PositionsAdapter.ViewHolder> {

    private View view;
    private List<User> users;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    public PositionsAdapter(Context context, List<User> users) {
        this.mInflater = LayoutInflater.from(context);
        this.users = users;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.position_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        String username_text = user.getNom() + " " + user.getPrenom();
        holder.username.setText(username_text);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username;

        ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.friend_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    User getItem(int id) {
        return users.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
