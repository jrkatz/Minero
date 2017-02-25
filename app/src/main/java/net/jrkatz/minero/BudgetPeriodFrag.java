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

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.jrkatz.minero.budget.BudgetPeriod;

import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BudgetPeriodFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetPeriodFrag extends Fragment {
    private static final String ARG_BUDGET_PERIOD = "budgetPeriod";

    private BudgetPeriod mBudgetPeriod;
    private DebitListFrag mDebitListFragment;

    public BudgetPeriodFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     *
     * @param budgetPeriod The budget period to display
     * @return A new instance of fragment BudgetPeriodFrag.
     */
    public static BudgetPeriodFrag newInstance(BudgetPeriod budgetPeriod) {
        BudgetPeriodFrag fragment = new BudgetPeriodFrag();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BUDGET_PERIOD, budgetPeriod);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBudgetPeriod = getArguments().getParcelable(ARG_BUDGET_PERIOD);
        }
    }

    private void renderBudgetPeriod() {
        final View v = getView();
        final DebitListFrag debitList = (DebitListFrag) v.findViewById(R.id.debit_list_fragment);
        debitList.bind(new ArrayList<>(mBudgetPeriod.getDebits()));

        final TextView remainingAmt = (TextView) v.findViewById(R.id.remainingAmt);
        remainingAmt.setText(Long.toString(mBudgetPeriod.getRemaining()));

        final TextView periodView = (TextView)v.findViewById(R.id.period);
        final LocalDate end = mBudgetPeriod.getPeriod().getEnd();
        final String untilDateString = String.format(getResources().getString(R.string.until_date_format), end.toString(getResources().getString(R.string.ymd_format)));
        periodView.setText(untilDateString);
    }

    public void updateBudgetPeriod(BudgetPeriod budgetPeriod) {
        mBudgetPeriod = budgetPeriod;
        renderBudgetPeriod();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget_period, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        renderBudgetPeriod();
    }
}