package com.mycode.noteapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mycode.noteapp.Entities.Note;
import com.mycode.noteapp.R;
import com.mycode.noteapp.database.Notedb;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNote extends AppCompatActivity {

    private EditText inputtitle,inputnotesubtitle,inputnotetext;
    private TextView textdatetime;
    private String selectNotecolor;
    private View subtitlecard;
    private static final int REQEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQEST_CODE_SELECT_IMAGE=2;
    private ImageView imgnote;
    private String imgpath;
    private Note availableNote;
    private AlertDialog dialogDeleteNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        //find view by id
        inputtitle =findViewById(R.id.inputnoteTitle);
        inputnotesubtitle = findViewById(R.id.inputnoteSubtitle);
        inputnotetext = findViewById(R.id.Inputtext);
        textdatetime = findViewById(R.id.textDatetime);
        subtitlecard =findViewById(R.id.viewsubtitle);
        imgnote= findViewById(R.id.imgNote);

        //icon back to first view , set action
        ImageView imageBack=findViewById(R.id.imageback);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //icon save the note, set action
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });


        //set date-time
        textdatetime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date())
        );


        //select color for background note
        selectNotecolor="#0A0B0A";
        imgpath="";
        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            availableNote=(Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        //delete img
        findViewById(R.id.imgdelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgnote.setImageBitmap(null);
                imgnote.setVisibility(View.GONE);
                findViewById(R.id.imgdelete).setVisibility(View.GONE);
                imgpath="";
            }
        });

        if(getIntent().getBooleanExtra("isFormQuickAction",false)){
            String type= getIntent().getStringExtra("QuickActionType");
            if(type != null){
                if(type.equals("image")){
                    imgpath = getIntent().getStringExtra("imagePath");
                    imgnote.setImageBitmap(BitmapFactory.decodeFile(imgpath));
                    imgnote.setVisibility(View.VISIBLE);
                    findViewById(R.id.imgdelete).setVisibility(View.VISIBLE);

                }
            }

        }

        initMiscellanous();
        setviewcardcolor();



    }

    private void setViewOrUpdateNote(){
       inputtitle.setText(availableNote.getTitle());
       inputnotesubtitle.setText(availableNote.getSubtitle());
       inputnotetext.setText(availableNote.getTextnote());
       textdatetime.setText(availableNote.getDatetime());
       if(availableNote.getImagepath()!=null && !availableNote.getImagepath().trim().isEmpty()){
           imgnote.setImageBitmap(BitmapFactory.decodeFile(availableNote.getImagepath()));
           imgnote.setVisibility(View.VISIBLE);
           findViewById(R.id.imgdelete).setVisibility(View.VISIBLE);
           imgpath =availableNote.getImagepath();
       }


    }

    //save the note
    private void saveNote(){
        if(inputtitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Title can't be empty!,please text your title!",Toast.LENGTH_SHORT).show();
            return;
        }else if(inputnotesubtitle.getText().toString().trim().isEmpty() && inputnotetext.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Do not let's it empty!",Toast.LENGTH_LONG).show();
            return;
        }

        //crete new entity
        final Note note =new Note();
        note.setTitle(inputtitle.getText().toString());
        note.setSubtitle(inputnotesubtitle.getText().toString());
        note.setDatetime(textdatetime.getText().toString());
        note.setTextnote(inputnotetext.getText().toString());
        note.setColor(selectNotecolor);
        note.setImagepath(imgpath);

        //set id for update note
        if(availableNote != null){
            note.setId(availableNote.getId());
        }

        //using async for room, because of that dosent allow db operation on the main thread.
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void,Void,Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                Notedb.getdb(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }



        new SaveNoteTask().execute();
    }//end of savenote's method

    // method of select color note
    private void initMiscellanous(){
        final LinearLayout layoutMiscellaneous =findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        //find the color, chose color , add action
        final ImageView imgcolor =layoutMiscellaneous.findViewById(R.id.imgcolor);
        final ImageView imgcolor2 =layoutMiscellaneous.findViewById(R.id.imgcolor2);
        final ImageView imgcolor3 =layoutMiscellaneous.findViewById(R.id.imgcolor3);
        final ImageView imgcolor4 =layoutMiscellaneous.findViewById(R.id.imgcolor4);
        final ImageView imgcolor5 =layoutMiscellaneous.findViewById(R.id.imgcolor5);
        layoutMiscellaneous.findViewById(R.id.viewColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNotecolor="#0A0B0A";
                imgcolor.setImageResource(R.drawable.ic_donecolor);
                imgcolor2.setImageResource(0);
                imgcolor3.setImageResource(0);
                imgcolor4.setImageResource(0);
                imgcolor5.setImageResource(0);
                setviewcardcolor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNotecolor="#FD9D24";

                imgcolor.setImageResource(0);
                imgcolor2.setImageResource(R.drawable.ic_donecolor);
                imgcolor3.setImageResource(0);
                imgcolor4.setImageResource(0);
                imgcolor5.setImageResource(0);
                setviewcardcolor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNotecolor="#EA2929";

                imgcolor.setImageResource(0);
                imgcolor2.setImageResource(0);
                imgcolor3.setImageResource(R.drawable.ic_donecolor);
                imgcolor4.setImageResource(0);
                imgcolor5.setImageResource(0);
                setviewcardcolor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNotecolor="#17B1F6";

                imgcolor.setImageResource(0);
                imgcolor2.setImageResource(0);
                imgcolor3.setImageResource(0);
                imgcolor4.setImageResource(R.drawable.ic_donecolor);
                imgcolor5.setImageResource(0);
                setviewcardcolor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNotecolor="#F30E5C";

                imgcolor.setImageResource(0);
                imgcolor2.setImageResource(0);
                imgcolor3.setImageResource(0);
                imgcolor4.setImageResource(0);
                imgcolor5.setImageResource(R.drawable.ic_donecolor);
                setviewcardcolor();
            }
        });

        if(availableNote != null && availableNote.getColor()!= null && !availableNote.getColor().trim().isEmpty()){
            switch (availableNote.getColor()){
                case "#0A0B0A":
                    layoutMiscellaneous.findViewById(R.id.viewColor).performClick();
                    break;
                case "#FD9D24":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#EA2929":
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#17B1F6":
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#F30E5C":
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

        //find layout add img and set action add img
        layoutMiscellaneous.findViewById(R.id.layoutAddimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            CreateNote.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQEST_CODE_STORAGE_PERMISSION
                    );
                }
                else{
                    selectImage();
                }
            }
        });


        //delete note
       if (availableNote != null){
           layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
           layoutMiscellaneous.findViewById(R.id.deletenote).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   // BottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showdeleteDialog();
               }
           });
       }
    }// end of method chose color, medthod add img


    //show dialog delete
    private void showdeleteDialog(){
        if(dialogDeleteNote != null){
            AlertDialog.Builder builder=new AlertDialog.Builder(CreateNote.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_deletenote,
                    findViewById(R.id.layoutDeleteNotedialog)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            }
            view.findViewById(R.id.textdeletenote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    class DeleteNoteTask extends AsyncTask<Void,Void,Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            Notedb.getdb(getApplicationContext()).noteDao().deleteNote(availableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent=new Intent();
                            intent.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }

                    }
                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textcancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();

                }
            });
        }

        dialogDeleteNote.show();
    }

    private void setviewcardcolor(){
        GradientDrawable gradientDrawable= (GradientDrawable) subtitlecard.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectNotecolor));
    }

    // add img to notes
    private void selectImage(){
    Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    if(intent.resolveActivity(getPackageManager())!=null){
        startActivityForResult(intent,REQEST_CODE_SELECT_IMAGE);
    }
    }

    // the permission to chose the forder of img
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
        }else{
            Toast.makeText(this,"PERMISSION DENIED!",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQEST_CODE_SELECT_IMAGE && resultCode==RESULT_OK){
            if(data!=null){
                Uri selectedImageUri =data.getData();
                if(selectedImageUri != null){
                    try(InputStream inputStream=getContentResolver().openInputStream(selectedImageUri)){
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgnote.setImageBitmap(bitmap);
                        imgnote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imgdelete).setVisibility(View.VISIBLE);

                        imgpath = getPathfromUri(selectedImageUri);

                    }catch(Exception exception){
                        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT);
                    }
                }
            }
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
}