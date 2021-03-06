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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.jrkatz.minero.data.Budget;
import net.jrkatz.minero.data.BudgetPeriod;

import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 * A custom view for displaying Budget Period data
 */
public class BudgetPeriodView extends FrameLayout {
    private BudgetPeriod mBudgetPeriod;
    private Budget mBudget;
    private DebitListView.AttemptDebitEdit mDebitRemoval;

    public BudgetPeriodView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_budget_period, this);
    }

    public void bind(@NonNull final Budget budget,
                     @NonNull final BudgetPeriod budgetPeriod,
                     @Nullable final DebitListView.AttemptDebitEdit debitRemoval) {
        mBudget = budget;
        mBudgetPeriod = budgetPeriod;
        mDebitRemoval = debitRemoval;
        final DebitListView debitList = (DebitListView) findViewById(R.id.debit_list_fragment);
        debitList.bind(new ArrayList<>(mBudgetPeriod.getDebits()), mDebitRemoval);
        updateView();
    }

    public void updateView() {
        final TextView remainingAmt = (TextView) findViewById(R.id.remaining_amt);
        final Resources r = getResources();
        final String remainingStr = r.getString(R.string.currency_fmt,
                r.getString(R.string.currency_symbol),
                Long.toString(mBudgetPeriod.getRemaining()));

        remainingAmt.setText(remainingStr);
        remainingAmt.setTextColor(
                mBudgetPeriod.getRemaining() < 0
                        ? getResources().getColor(R.color.budget_bold_negative, getContext().getTheme())
                        : getResources().getColor(R.color.budget_bold_positive, getContext().getTheme())
        );

        final TextView periodView = (TextView) findViewById(R.id.period);
        final LocalDate end = mBudgetPeriod.getPeriod().getEnd();
        final String untilDateString = String.format(getResources().getString(R.string.until_date_format),
                end.toString(getResources().getString(R.string.ymd_format)));
        periodView.setText(untilDateString);

        final TextView totalView = (TextView) findViewById(R.id.total_savings);
        final long totalSavings = mBudget.getRunningTotal() + mBudgetPeriod.getRemaining();
        final String totalStr = r.getString(R.string.currency_fmt, "$", Long.toString(totalSavings));
        totalView.setText(totalStr);
        totalView.setTextColor(
                totalSavings < 0
                        ? getResources().getColor(R.color.budget_negative, getContext().getTheme())
                        : getResources().getColor(R.color.budget_positive, getContext().getTheme())
        );
    }
}