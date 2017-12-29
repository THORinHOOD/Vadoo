package catwithbowtie.chatchat.Fragments;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import catwithbowtie.chatchat.Fragments.Items.Msg;
import catwithbowtie.chatchat.R;


import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.MsgViewHolder>{
    public List<Msg> msgs;
    private Context ctx;
    private View view;

    public DialogAdapter(List<Msg> msgs, Context ctx, View view){
        this.msgs = msgs;
        this.ctx = ctx;
        this.view = view;
    }

    @Override
    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_card_item, parent, false);
        MsgViewHolder pvh = new MsgViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(MsgViewHolder holder, int position) {
        Msg msg = msgs.get(position);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        if (msg.from.equals("me")) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            holder.cv.setCardBackgroundColor(view.getResources().getColor(R.color.green));
            holder.container.removeView(holder.name);
        } else {
            holder.cv.setCardBackgroundColor(view.getResources().getColor(R.color.vk_white));
            holder.name.setText(msg.from);
        }

        holder.cv.setRadius(20);
        holder.cv.setCardElevation(0);
        holder.cv.setContentPadding(25,12,25,12);
        holder.cv.setPadding(5,5,5,5);
        holder.cv.setLayoutParams(params);

        holder.text.setText(msg.text);
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class MsgViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public TextView name;
        public CardView cv;
        public RelativeLayout container;
        MsgViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardview_msg);
            text = (TextView) cv.findViewById(R.id.text_msg);
            name = (TextView) cv.findViewById(R.id.name_msg);
            container = (RelativeLayout) cv.findViewById(R.id.container_msg);
        }
    }
}
