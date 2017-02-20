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

import android.net.ParseException;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;

import net.jrkatz.minero.budget.Budget;
import net.jrkatz.minero.budget.BudgetPeriod;
import net.jrkatz.minero.budget.SpendEvent;
import net.jrkatz.minero.budget.period.MonthlyPeriodDefinition;

import org.joda.time.LocalDate;

public class Lander extends AppCompatActivity implements BudgetPeriodFrag.OnFragmentInteractionListener {

    private final BudgetPeriod mBudgetPeriod = new BudgetPeriod(
            new Budget(new MonthlyPeriodDefinition(1),
                400,
                "default"
                ),
            new MonthlyPeriodDefinition(1).periodForDate(LocalDate.now()),
            400,
            ImmutableList.<SpendEvent>of()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lander);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Fragment budgetPeriodFrag = BudgetPeriodFrag.newInstance(mBudgetPeriod);
        getSupportFragmentManager().beginTransaction().add(R.id.budget_period_fragment, budgetPeriodFrag).commit();

        final EditText spendAmt = (EditText)findViewById(R.id.spendAmt);
        spendAmt.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView spendAmt, int actionId, KeyEvent event) {
                switch(actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        try {
                            final String spendVal = spendAmt.getText().toString();
                            final int dollars = Integer.parseInt(spendVal);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}