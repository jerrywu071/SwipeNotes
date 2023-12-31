package com.example.swipenotes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



import java.text.DateFormat;

import io.realm.Realm;

public class AddNoteActivity extends AppCompatActivity {

    ImageButton saveNote;
    EditText title;

    FloatingActionButton menuFab, boldFab, italicFab;
    EditText body;
    private Note note;

    private boolean update;

    private GestureDetector gestureDetector;
    private Button swipeButton;
    private EditText editText;

    //animations
    private Animation openAnim;
    private Animation closeAnim;
    private Animation toBottomAnim;
    private Animation fromBottomAnim;

    private boolean menuClicked;

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("Gesture", "onFling: " + e1 + ", " + e2);
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();

            if (Math.abs(distanceX) > Math.abs(distanceY) &&
                    Math.abs(distanceX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    // Swipe right (add italic)
                    setItalic(editText);
                } else {
                    // Swipe left (add bold)
                    setBold(editText);
                }
                return true;
            }

            return false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        //define our buttons
        boldFab = findViewById(R.id.boldFab);
        italicFab = findViewById(R.id.italicFab);
        menuFab = findViewById(R.id.menuFab);
        saveNote = findViewById(R.id.savenote);

        //define our text boxes for title and body
        title = findViewById(R.id.titleinput);
        body = findViewById(R.id.notebody);

        //define our animations
        openAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        closeAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottomAnim = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottomAnim = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        menuClicked = false;

        menuFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //call the methods for visibility, animation, and setting clickable for the bold and italic buttons

                setVisibility(menuClicked);
                setAnimation(menuClicked);
                setClickable(menuClicked);

                menuClicked = !menuClicked;
            }
        });

        boldFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBold(body);
            }
        });

        italicFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setItalic(body);
            }
        });

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();


        //reload saved note from database
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            title.setText(bundle.getString("title"));
            body.setText(bundle.getString("body"));





            ///////////////////////////////////////////////////////////////////////////////////////////////
            StyleSpan[] styleSpans = body.getText().getSpans(0, body.getText().length() - 1, StyleSpan.class);

            for (StyleSpan styleSpan : styleSpans) {
                body.getText().removeSpan(styleSpan);
            }
///////////////////////////////////////////////////////////////////////////////////////////////



            Editable editable = body.getText();


///////////////////////////////////////////////////////////////////////////////////////////////
            String[] spanArray = bundle.getString("spans").split("_");

            Log.i("Span Array Length", String.valueOf(spanArray.length));
            Log.i("Span Array First Entry", spanArray[0]);


            if (spanArray.length > 1 && !spanArray[0].equals("")) {
                for (int i = 0; i < spanArray.length; i++) {
                    String[] spanComponents = spanArray[i].split(",");
                    SpanContainer spanContainer = new SpanContainer(spanComponents[0], Integer.parseInt(spanComponents[1]), Integer.parseInt(spanComponents[2]));

                    StyleSpan span;

                    if (spanContainer.getSpan().equals("2")) {
                        span = new StyleSpan(Typeface.ITALIC);
                        editable.setSpan(span, spanContainer.getBegin(), spanContainer.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    } else if (spanContainer.getSpan().equals("1")) {
                        span = new StyleSpan(Typeface.BOLD);
                        editable.setSpan(span, spanContainer.getBegin(), spanContainer.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }
                    else {


//                        StyleSpan[] styleSpans = editable.getSpans(i, i + 1, StyleSpan.class);
//
//                        for (StyleSpan styleSpan : styleSpans) {
//
//                            if (styleSpan.getStyle() == 2) {
//                                editable.removeSpan(styleSpan);
//
//                            } else if (styleSpan.getStyle() == 1) {
//                                editable.removeSpan(styleSpan);
//
//                            }
//
//                        }

//////////////////////////////////////////////////////////////////////////////////////////////
                    }


                }
            }
        }




        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        gestureDetector = new GestureDetector(this, new MyGestureListener());
        swipeButton = findViewById(R.id.swipeButton);
        editText = findViewById(R.id.notebody);

        swipeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    // This method is called when the user clicks the back button.
    // it saves the note if it is new or updates the note if notes was already saved
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //convert to string in order to save
        String titleText = title.getText().toString();
        String notebody = body.getText().toString();


        //if new note is empty
        if (notebody.length() == 0) {
            Toast.makeText(getApplicationContext(), "Note not saved. Note content is empty", Toast.LENGTH_SHORT).show();
            finish();
        }

        //save note via bundle
        else {
            Realm.init(getApplicationContext());
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            Note note;

            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                note = new Note();
            } else {
                note = realm.where(Note.class).equalTo("timeCreated", bundle.getString("timeCreated")).findFirst();
            }

            note.setDescription(notebody);
            note.setTitle(titleText);




            Editable editable = body.getText();
            for (int i = 0; i < editable.length(); i++) {

                StyleSpan[] styleSpans = editable.getSpans(i, i + 1, StyleSpan.class);

                if (styleSpans.length > 0) {
                    for (int j = 0; j < styleSpans.length; j++) {
                        SpanContainer spanContainer = new SpanContainer(String.valueOf(styleSpans[j].getStyle()), i, i + 1);
                        note.addSpanContainers(spanContainer);
                    }
                }
                else {
                    SpanContainer spanContainer = new SpanContainer("0", i, i + 1);
                    note.addSpanContainers(spanContainer);

                }


            }





            realm.copyToRealmOrUpdate(note);

            realm.commitTransaction();

            if(titleText.length() == 0) {
                Toast.makeText(getApplicationContext(), "Note with no title saved.", Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(getApplicationContext(), "Note with title " + '"' + titleText + '"' + " Saved", Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    //method for setting bold
    public void setBold(EditText edit) {
        StyleSpan bold = new StyleSpan(Typeface.BOLD);

        Editable editable = edit.getText();
        int selectionStart = edit.getSelectionStart();
        int selectionEnd = edit.getSelectionEnd();

        StyleSpan[] styleSpans = editable.getSpans(selectionStart, selectionEnd, StyleSpan.class);

        if (styleSpans.length > 0) {
            int notBoldCount = 0;
            int boldCount = 0;
            // Remove all bold spans in the selected range
            for (StyleSpan span : styleSpans) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);

                if ((spanStart < selectionEnd && spanEnd > selectionStart)) {

                    if (span.getStyle() == Typeface.BOLD) {
                        editable.removeSpan(span);
                        boldCount++;
                    }
                    else {
                        notBoldCount++;
                    }

                    // Add a new span for the unbolded part before the selection
                    if (spanStart < selectionStart) {
                        editable.setSpan(bold, spanStart, selectionStart, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    // Add a new span for the unbolded part after the selection
                    if (spanEnd > selectionEnd) {
                        editable.setSpan(bold, selectionEnd, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            if (boldCount == 0 && notBoldCount > 0) {
                editable.setSpan(bold, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                edit.setText(editable);
            }

            edit.setText(editable);
        } else {
            // If no bold spans found, add a new bold span to the entire selected range
            editable.setSpan(bold, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            edit.setText(editable);
        }

        edit.setSelection(selectionEnd);
    }

    public void setItalic(EditText edit) {
        StyleSpan italic = new StyleSpan(Typeface.ITALIC);

        Editable editable = edit.getText();
        int selectionStart = edit.getSelectionStart();
        int selectionEnd = edit.getSelectionEnd();

        StyleSpan[] styleSpans = editable.getSpans(selectionStart, selectionEnd, StyleSpan.class);

        if (styleSpans.length > 0) {
            // Remove all italic spans in the selected range

            int notItalicCount = 0;
            int italicCount = 0;

            for (StyleSpan span : styleSpans) {
                int spanStart = editable.getSpanStart(span);
                int spanEnd = editable.getSpanEnd(span);

                if ((spanStart < selectionEnd && spanEnd > selectionStart)) {

                    if (span.getStyle() == Typeface.ITALIC) {
                        editable.removeSpan(span);
                        italicCount++;
                    }
                    else {
                        notItalicCount++;
                    }

                    // Add a new span for the unitaliced part before the selection
                    if (spanStart < selectionStart) {
                        editable.setSpan(italic, spanStart, selectionStart, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    // Add a new span for the unitaliced part after the selection
                    if (spanEnd > selectionEnd) {
                        editable.setSpan(italic, selectionEnd, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            if (italicCount == 0 && notItalicCount > 0) {
                editable.setSpan(italic, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                edit.setText(editable);
            }

            edit.setText(editable);
        } else {
            // If no italic spans found, add a new italic span to the entire selected range
            editable.setSpan(italic, selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            edit.setText(editable);
        }

        edit.setSelection(selectionEnd);
    }

    //set visibility of buttons
    private void setVisibility(boolean clicked) {
        if(!clicked) {
            boldFab.setVisibility(View.VISIBLE);
            italicFab.setVisibility(View.VISIBLE);
        }

        else {
            boldFab.setVisibility(View.INVISIBLE);
            italicFab.setVisibility(View.INVISIBLE);
        }
    }

    //start animations
    private void setAnimation(boolean clicked) {
        if(!clicked) {
            boldFab.startAnimation(fromBottomAnim);
            italicFab.startAnimation(fromBottomAnim);
            menuFab.startAnimation(openAnim);
        }

        else {
            boldFab.startAnimation(toBottomAnim);
            italicFab.startAnimation(toBottomAnim);
            menuFab.startAnimation(closeAnim);
        }
    }

    //method to make sure the buttons can't be clicked when menu hasn't been opened
    private void setClickable(boolean clicked) {
        if(!clicked) {
            boldFab.setClickable(true);
            italicFab.setClickable(true);
        }

        else {
            boldFab.setClickable(false);
            italicFab.setClickable(false);
        }
    }
}