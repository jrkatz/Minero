/*
 *     Minero is a minimal budget application
 *     Copyright (C) 2017 Jacob Katz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     aint with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jrkatz.minero;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ParseException;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.jrkatz.minero.data.BudgetPeriod;
import net.jrkatz.minero.data.DataContextFactory;
import net.jrkatz.minero.data.Debit;
import net.jrkatz.minero.data.IDataContext;
import net.jrkatz.minero.data.ProviderException;

import org.joda.time.DateTime;

/**
 * @Author jrkatz
 * @Date 3/5/2017.
 */

public class DebitEntryView extends FrameLayout {
    private static final int AMT_ENTRY = 0;
    private static final int TAG_ENTRY = 1;

    private DebitCreationListener mListener = null;
    private final EditText mSpendAmt;
    private final EditText mSpendTag;

    private int mState = AMT_ENTRY;
    private long mBudgetId;

    public DebitEntryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_debit_entry, this);

        mSpendAmt = (EditText) findViewById(R.id.spend_amt);
        mSpendAmt.setRawInputType(Configuration.KEYBOARD_12KEY);

        mSpendTag = (EditText) findViewById(R.id.spend_tag);
        mState = AMT_ENTRY;
        updateView();

        mSpendAmt.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final Integer val = sanitizeAmt();
                    if (val != null) {
                        mState = TAG_ENTRY;
                        updateView();
                    }
                }
                else if (mState != AMT_ENTRY){
                    mState = AMT_ENTRY;
                    updateView();
                }
            }
        });

        mSpendAmt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView spendAmt, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        final Integer val = sanitizeAmt();
                        if (val != null) {
                            mState = TAG_ENTRY;
                            updateView();
                        }
                }
                return true;
            }
        });

        mSpendTag.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView spendAmt, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        createDebit();
                }
                return true;
            }
        });
    }

    public void bind(final long budgetId) {
        mBudgetId = budgetId;
    }

    private Integer sanitizeAmt() {
        final String txt = mSpendAmt.getText().toString();
        Integer amt = null;
        if (txt != null && !txt.isEmpty()) {
            //try parsing as a double to neatly handle periods.
            //This we don't lose precision because every 32 bit int
            //has an exact Double-precision counterpart.
            try {
                double dblVal = Double.parseDouble(txt);
                if (((double) Integer.MAX_VALUE) >= dblVal) {
                    amt = (int) dblVal; //convert & be happy
                }
                else {
                    //TODO show user an error indicating # too large
                }
            } catch (ParseException e) {
                //TODO show user an error indicating malformed input
            }
        }

        if (amt != null && amt > 0) {
            mSpendAmt.setText(amt.toString());
        }
        else {
            mSpendAmt.setText("");
            amt = null;
            //TODO show user an error indicating they can't use negative numbers
        }

        return amt;
    }

    private void setupAmtEntry() {
        mSpendTag.setVisibility(View.GONE);
        mSpendAmt.requestFocus();
    }

    private void setupTagEntry() {
        mSpendTag.setVisibility(View.VISIBLE);
        mSpendTag.requestFocus();
    }

    private void updateView() {
        switch(mState) {
            case TAG_ENTRY:
                setupTagEntry();
                break;
            case AMT_ENTRY:
            default:
                setupAmtEntry();
                break;
        }
    }

    private void createDebit() {
        final Integer amt = sanitizeAmt();
        if (amt == null) {
            return;
        }
        final String tag = mSpendTag.getText().toString();
        final Debit debit;
        try (final IDataContext providerContext = DataContextFactory.getDataContext(getContext())) {
            final BudgetPeriod budgetPeriod = providerContext.getBudgetPeriodProvider().getCurrentBudgetPeriod(providerContext, mBudgetId);
            debit = providerContext.getDebitProvider().createDebit(
                    providerContext,
                    budgetPeriod.getBudgetId(),
                    budgetPeriod.getId(),
                    amt,
                    tag,
                    DateTime.now());
            providerContext.markSuccessful();
        } catch (ProviderException e) {
            //TODO handle better
            throw new RuntimeException(e);
        }
        mListener.onDebitCreated(debit);

        mSpendTag.setText("");
        mSpendAmt.setText("");
        mState = AMT_ENTRY;
        updateView();
    }

    //I don't really like this model of effecting changes. I'd rather register
    //this with a data source and supply changes directly to it, and let it invalidate
    //views of that data. But this is faster for now.
    public void setListener(final DebitCreationListener listener) {
        mListener = listener;
    }

    public interface DebitCreationListener {
        void onDebitCreated(Debit debit);
    }
}