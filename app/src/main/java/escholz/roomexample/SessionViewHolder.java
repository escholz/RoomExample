package escholz.roomexample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import escholz.roomexample.entity.Session;

/**
 * {@link RecyclerView.ViewHolder} for session summary
 */
public final class SessionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public interface OnSessionSelectedListener {
        void onSessionSelected(SessionViewHolder viewHolder);
    }

    private final TextView sessionIdTextView;
    private final TextView nameTextView;
    private final TextView isDeletedTextView;
    private Session session;
    private WeakReference<OnSessionSelectedListener> onSessionSelectedListenerReference;

    public SessionViewHolder(View itemView, OnSessionSelectedListener onSessionSelectedListener) {
        super(itemView);

        itemView.setOnClickListener(this);

        sessionIdTextView = itemView.findViewById(R.id.session_id);
        nameTextView = itemView.findViewById(R.id.name);
        isDeletedTextView = itemView.findViewById(R.id.is_deleted);
        onSessionSelectedListenerReference = new WeakReference<>(onSessionSelectedListener);
    }

    @Override
    public void onClick(View view) {
        OnSessionSelectedListener listener = onSessionSelectedListenerReference.get();
        if (listener != null)
            listener.onSessionSelected(this);
    }

    public void onBind(Session session) {
        this.session = session;
        sessionIdTextView.setText(Long.toString(session.id));
        nameTextView.setText(session.name);
        isDeletedTextView.setText(Boolean.toString(session.isDeleted));
    }

    public Session getSession() {
        return session;
    }
}
