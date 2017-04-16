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
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jrkatz.minero;

import android.content.Context;
import android.net.ParseException;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.jrkatz.minero.data.Budget;
import net.jrkatz.minero.data.DataContextFactory;
import net.jrkatz.minero.data.IDataContext;
import net.jrkatz.minero.data.ProviderException;

/**
 * @Author jrkatz
 * @Date 4/16/2017.
 */

public class BudgetEditView extends ConstraintLayout {
    private final Button mOkButton;
    private final Button mCancelButton;
    private final EditText mEditAmt;

    public BudgetEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_budget_edit, this);

        mOkButton = (Button) findViewById(R.id.btn_ok);
        mCancelButton = (Button) findViewById(R.id.btn_cancel);
        mEditAmt = (EditText) findViewById(R.id.edit_amt);
    }

    public static interface OnDone {
        void onDone(boolean canceled);
    }

    public void bind(final long budgetId, @NonNull final OnDone onDone) {
        try(final IDataContext dataContext = DataContextFactory.getDataContext(getContext())) {
            Budget budget = dataContext.getBudgetProvider().getBudget(dataContext, budgetId);
            mEditAmt.setText(Long.toString(budget.getDistribution()));
        } catch (ProviderException e) {
            //TODO handle better
            throw new RuntimeException(e);
        }

        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDone.onDone(true);
            }
        });

        mOkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Long amt = sanitizeAmt();
                if (amt != null) {
                    try(IDataContext dataContext = DataContextFactory.getDataContext(getContext())) {
                        dataContext.getBudgetProvider().updateBudgetAmount(dataContext, budgetId, amt);
                        dataContext.markSuccessful();
                    } catch (ProviderException e) {
                        //TODO some kind of error message?
                    }
                    onDone.onDone(false);
                }
            }
        });
    }

    private Long sanitizeAmt() {
        final String txt = mEditAmt.getText().toString();
        Long amt = null;
        if (txt != null && !txt.isEmpty()) {
            try {
                double dblVal = Double.parseDouble(txt);
                if (((double) Long.MAX_VALUE) >= dblVal) {
                    amt = (long) dblVal; //convert & be happy
                }
                else {
                    //TODO show user an error indicating # too large
                }
            } catch (ParseException e) {
                //TODO show user an error indicating malformed input
            }
        }

        if (amt != null && amt > 0) {
            mEditAmt.setText(amt.toString());
        }
        else {
            mEditAmt.setText("");
            amt = null;
            //TODO show user an error indicating they can't use negative numbers
        }

        return amt;
    }
}
