 package com.mycode.noteapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mycode.noteapp.Activities.CreateNote;
import com.mycode.noteapp.Adapter.NoteAdapter;
import com.mycode.noteapp.Entities.Note;
import com.mycode.noteapp.Listeners.NotesListener;
import com.mycode.noteapp.R;
import com.mycode.noteapp.database.Notedb;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    public static final int REQUEST_CODE_ADD_NOTE=1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    public static final int REQUEST_CODE_SHOW_NOTE =3;
    public static final int REQUEST_CODE_SELECT_IMAGE =4;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 5;

    private RecyclerView noteRecyclerView;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;


    private int noteClickedPositon=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddNoteMain=findViewById(R.id.imgAddnoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNote.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });

        //find recycleview
        noteRecyclerView = findViewById(R.id.noteRecycleview);
        noteRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );

        noteList=new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList,this);
        noteRecyclerView.setAdapter(noteAdapter);

        //get note method run
        getNote(REQUEST_CODE_SHOW_NOTE,false);

        EditText textSearch = findViewById(R.id.inputSearch);
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(noteList.size()!=0){
                    noteAdapter.SearchNote(s.toString());
                }
            }
        });

        // quick action add note
        findViewById(R.id.addnoteimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNote.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });

        // quick action add img
        findViewById(R.id.addimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }
                else{
                    selectImage();
                }
            }
        });
    }

    private void selectImage(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
        }else{
            Toast.makeText(this,"PERMISSION DENIED!",Toast.LENGTH_SHORT).show();
        }
    }

    private String getPathfromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri,null,null,null,null);

        if(cursor==null){
            filePath=contentUri.getPath();

        }else{
            cursor.moveToFirst();
            int index=cursor.getColumnIndex("_data");
            filePath=cursor.getString(index);
            cursor.close();
        }
        return filePath;

    }

    //view note text
    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPositon=position;
        Intent intent =new Intent(getApplicationContext(),CreateNote.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }


    private void getNote(final int requestCode,final boolean isNotedelete){

        //Async for room, bc of that doesnt operation in main thread

        class GetNoteTask extends AsyncTask<Void,Void, List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return Notedb.getdb(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
             if(requestCode == REQUEST_CODE_SHOW_NOTE){
                 noteList.addAll(notes);
                 noteAdapter.notifyDataSetChanged();
             }else if(requestCode == REQUEST_CODE_ADD_NOTE){
                 noteList.add(0,notes.get(0));
                 noteAdapter.notifyItemInserted(0);
                 noteRecyclerView.smoothScrollToPosition(0);
             }else if(requestCode == REQUEST_CODE_UPDATE_NOTE){
                 noteList.remove(noteClickedPositon);
                 if(isNotedelete){
                     noteAdapter.notifyItemRemoved(noteClickedPositon);
                 }else{
                     noteList.add(noteClickedPositon,notes.get(noteClickedPositon));
                     noteAdapter.notifyItemInserted(noteClickedPositon);
                 }

             }

            }}
        new GetNoteTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ADD_NOTE && resultCode==RESULT_OK){
            getNote(REQUEST_CODE_ADD_NOTE,false);
        }else if(requestCode==REQUEST_CODE_UPDATE_NOTE && resultCode==RESULT_OK ){
            if(data != null){
                getNote(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));

            }
        }
        else if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode==RESULT_OK){
            if(data!=null){
                Uri selectedImageUri= data.getData();
                if(selectedImageUri != null){
                    try{
                        String selectedImgPath=getPathfromUri(selectedImageUri);
                        Intent intent =new Intent(getApplicationContext(),CreateNote.class);
                        intent.putExtra("isFormQuickAction",true);
                        intent.putExtra("QuickActionType","image");
                        intent.putExtra("imagePath",selectedImgPath);
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);

                    }catch (Exception ex){
                        Toast.makeText(this,ex.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }

    }
}