package com.example.compumovilp.adapters;

        import android.content.Context;
        import android.content.Intent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.bumptech.glide.Glide;
        import com.example.compumovilp.OpcionesSeguimiento;
        import com.example.compumovilp.R;
        import com.example.compumovilp.User;
        import com.example.compumovilp.chat;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;

        import java.util.List;

public class cordinadoresAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private int mLayoutResourceId;
    private List<User> mUsers;
    private FirebaseAuth mAuth;
    public cordinadoresAdapter(Context context, int layoutResourceId, List<User> users) {
        super(context, layoutResourceId, users);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.contactsrow, parent, false);
        }
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser usera = mAuth.getCurrentUser();
        User user = mUsers.get(position);

        ImageView profileImageView = convertView.findViewById(R.id.profileImage);
        TextView nameTextView = convertView.findViewById(R.id.contactName);
        TextView cedulaTextView = convertView.findViewById(R.id.cedulaTxt);

        // Asigna la imagen de perfil del usuario a la ImageView
        Glide.with(mContext).load(user.getProfileImageURL()).into(profileImageView);

        // Asigna el nombre del usuario al TextView
        nameTextView.setText(user.getName());
        cedulaTextView.setText(user.getEmail());
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, chat.class);
                intent.putExtra("COORD_ID", user.getUserID());
                intent.putExtra("real", user.getUserID());
                intent.putExtra("USER_ID", usera.getUid());// agrega el userid como un extra
                mContext.startActivity(intent);
            }
        });

        return convertView;}

}