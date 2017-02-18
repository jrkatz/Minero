package net.jrkatz.minero;

import android.net.ParseException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class Lander extends AppCompatActivity {

    private int mDollars = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lander);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final TextView dollarView = (TextView)findViewById(R.id.remainingAmt);
        dollarView.setText(Integer.toString(mDollars));

        final EditText spendAmt = (EditText)findViewById(R.id.spendAmt);
        spendAmt.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView spendAmt, int actionId, KeyEvent event) {
                switch(actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        try {
                            final String spendVal = spendAmt.getText().toString();
                            final int dollars = Integer.parseInt(spendVal);
                            mDollars -= dollars;
                            dollarView.setText(Integer.toString(mDollars));
                            return true;

                        } catch (ParseException e) {
                            return false; //just don't take the input
                        }
                    default:
                }
                return false;
            }
        });
    }
}
