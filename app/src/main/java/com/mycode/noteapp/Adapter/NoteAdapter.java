package com.mycode.noteapp.Adapter;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mycode.noteapp.Entities.Note;
import com.mycode.noteapp.Listeners.NotesListener;
import com.mycode.noteapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> noteSource;

    public NoteAdapter(List<Note> note, NotesListener notesListener)
    {
        this.notes = note;
        this.notesListener=notesListener;
        noteSource =note;

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesListener.onNoteClicked(notes.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //Create view holder
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageNote;
        LinearLayout layoutNote;
        //find the view
       TextView txttitle,txtsubtitle,txtdate;
        NoteViewHolder(@NonNull View itemView) {
           super(itemView);
           txttitle = itemView.findViewById(R.id.textTitle);
           txtsubtitle = itemView.findViewById(R.id.textSubtitle);
           txtdate = itemView.findViewById(R.id.textDate);
           layoutNote =itemView.findViewById(R.id.layoutNote);
           imageNote =itemView.findViewById(R.id.imageNote);
       }

       @SuppressLint("Range")
       void setNote(Note note){
           //set title
            txttitle.setText(note.getTitle());
           // set subtitle
            if(note.getSubtitle().trim().isEmpty()){
               txtsubtitle.setVisibility(View.GONE);
           }else{
               txtsubtitle.setText(note.getSubtitle());
           }
            //set datetime
           txtdate.setText(note.getDatetime());

           GradientDrawable gradientDrawable= (GradientDrawable) layoutNote.getBackground();
           if(note.getColor() != null){
               gradientDrawable.setColor((Color.parseColor(note.getColor())));
           }
           else {
               gradientDrawable.setColor(Color.parseColor("0A0B0A"));
           }if(note.getImagepath() != null){
               imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagepath()));
               imageNote.setVisibility(View.VISIBLE);
           }else{
               imageNote.setVisibility(View.GONE);
           }
        }
   }

   public void SearchNote(final String searchKey){
        timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKey.trim().isEmpty()){
                    notes=noteSource;
                }
                else{
                    ArrayList<Note> temp=new ArrayList<>();
                    for(Note note:noteSource){
                        if(note.getTitle().toLowerCase().contains(searchKey.toLowerCase())
                        ||note.getSubtitle().toLowerCase().contains(searchKey.toLowerCase())
                        ||note.getTextnote().toLowerCase().contains(searchKey.toLowerCase())){
                            temp.add(note);
                        }
                    }
                    notes=temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
   }

   public void cancelTimer(){
        if(timer!=null){
            timer.cancel();
        }
   }

}
