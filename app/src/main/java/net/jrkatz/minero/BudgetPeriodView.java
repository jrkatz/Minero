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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.jrkatz.minero.budget.BudgetPeriod;

import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 * A custom view for displaying Budget Period data
 */
public class BudgetPeriodView extends FrameLayout {
    private BudgetPeriod mBudgetPeriod;

    public BudgetPeriodView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_budget_period, this);
    }

    public void bind(final BudgetPeriod budgetPeriod) {
        mBudgetPeriod = budgetPeriod;
        updateView();
    }

    public void updateView() {
        final DebitListView debitList = (DebitListView) findViewById(R.id.debit_list_fragment);
        debitList.bind(new ArrayList<>(mBudgetPeriod.getDebits()));

        final TextView remainingAmt = (TextView) findViewById(R.id.remaining_amt);
        Resources r = getResources();
        String remainingStr = String.format(r.getString(R.string.currency_fmt),
                r.getString(R.string.currency_symbol),
                Long.toString(mBudgetPeriod.getRemaining()));

        remainingAmt.setText(remainingStr);

        final TextView periodView = (TextView) findViewById(R.id.period);
        final LocalDate end = mBudgetPeriod.getPeriod().getEnd();
        final String untilDateString = String.format(getResources().getString(R.string.until_date_format), end.toString(getResources().getString(R.string.ymd_format)));
        periodView.setText(untilDateString);
    }
}