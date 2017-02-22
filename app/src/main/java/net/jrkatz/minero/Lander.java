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

import android.app.FragmentManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


import net.jrkatz.minero.budget.Budget;
import net.jrkatz.minero.budget.BudgetDbHelper;
import net.jrkatz.minero.budget.BudgetPeriod;
import net.jrkatz.minero.budget.Debit;
import net.jrkatz.minero.budget.period.MonthlyPeriodDefinition;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class Lander extends AppCompatActivity {

    private BudgetPeriod mBudgetPeriod;

    private void refreshBudget() {
        try(SQLiteDatabase db = new BudgetDbHelper(this).getReadableDatabase()) {
            mBudgetPeriod = BudgetPeriod.loadBudgetPeriod(
                    db,
                    new Budget(new MonthlyPeriodDefinition(1),
                            400,
                            "default"
                    ),
                    new MonthlyPeriodDefinition(1).periodForDate(LocalDate.now()));
        }
        renderBudget();
    }

    private void renderBudget() {
        final FragmentManager fragmentManager = getFragmentManager();
        BudgetPeriodFrag budgetPeriodFrag = (BudgetPeriodFrag) fragmentManager.findFragmentByTag("budget_period");
        if(budgetPeriodFrag != null) {
            budgetPeriodFrag.updateBudgetPeriod(mBudgetPeriod);
        }
        else {
            budgetPeriodFrag = BudgetPeriodFrag.newInstance(mBudgetPeriod);
            getFragmentManager().beginTransaction().add(R.id.budget_period_fragment, budgetPeriodFrag, "budget_period").commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lander);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        refreshBudget();
        final Lander lander = this;
        final EditText spendAmt = (EditText)findViewById(R.id.spendAmt);
        spendAmt.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView spendAmt, int actionId, KeyEvent event) {
                switch(actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        final int value = Integer.parseInt(spendAmt.getText().toString());
                        try(SQLiteDatabase db = new BudgetDbHelper(lander).getWritableDatabase()) {
                            Debit.createDebit(db, value, "test", LocalDateTime.now());
                            spendAmt.setText("");
                            lander.refreshBudget(); //this is obviously inefficient but it's easy
                            return false;
                        }
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final EditText spendAmt = (EditText)findViewById(R.id.spendAmt);
        spendAmt.requestFocus();
    }
}