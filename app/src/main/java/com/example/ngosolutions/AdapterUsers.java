//package com.example.ngosolutions;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {
//
//    Context context;
//    List<ModalUsers> usersList;
//
//    public AdapterUsers(Context context, List<ModalUsers> usersList) {
//        this.context = context;
//        this.usersList = usersList;
//    }
//
//    @NonNull
//    @Override
//    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(context).inflate(R.layout.row_users , viewGroup, false);
//        return new MyHolder(view) ;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyHolder myholder, int i) {
//    String userImage = usersList.get(i).getImage();
//    String userName = usersList.get(i).getName();
//    final String userEmail = usersList.get(i).getEmail();
//    myholder.mName.setText(userName);
//    myholder.mEmail.setText(userEmail);
//    try{
//        Picasso.get().load(userImage).placeholder(R.drawable.icon_face).into(myholder.mAvatar);
//    }
//    catch (Exception e){
//
//    }
//    myholder.itemView.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Toast.makeText(context, ""+userEmail, Toast.LENGTH_SHORT).show();
//        }
//    });
//    }
//
//    @Override
//    public int getItemCount() {
//        return usersList.size();
//    }
//
//    class MyHolder extends RecyclerView.ViewHolder{
//        ImageView mAvatar;
//        TextView mName ,  mEmail;
//        public MyHolder(@NonNull View itemView) {
//            super(itemView);
//            mAvatar = itemView.findViewById(R.id.avatar);
//            mName = itemView.findViewById(R.id.name);
//            mEmail = itemView.findViewById(R.id.email);
//
//        }
//    }
//}
