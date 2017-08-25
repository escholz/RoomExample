package escholz.roomexample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import escholz.roomexample.entity.Session;

/**
 * {@link RecyclerView.Adapter<SessionViewHolder>} for session summary
 */
public final class SessionsAdapter extends RecyclerView.Adapter<SessionViewHolder> {

    private List<Session> sessions = new ArrayList<>();
    SessionViewHolder.OnSessionSelectedListener onSessionSelectedListener;

    public SessionsAdapter(SessionViewHolder.OnSessionSelectedListener onSessionSelectedListener)
    {
        this.onSessionSelectedListener = onSessionSelectedListener;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions.clear();
        if (sessions != null)
            this.sessions.addAll(sessions);
        notifyDataSetChanged();
    }

    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View holderLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_summary, parent, false);
        return new SessionViewHolder(holderLayout, onSessionSelectedListener);
    }

    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        holder.onBind(session);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }
}
