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

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import net.jrkatz.minero.data.budgetPeriod.BudgetPeriod;
import net.jrkatz.minero.data.budgetPeriod.BudgetPeriodProvider;
import net.jrkatz.minero.data.debit.Debit;
import net.jrkatz.minero.data.debit.DebitProvider;

import org.joda.time.LocalDateTime;

public class Lander extends AppCompatActivity {
    private BudgetPeriod mBudgetPeriod;

    private void refreshBudget() {
        mBudgetPeriod = new BudgetPeriodProvider(this).loadBudgetPeriod();
        renderBudget();
    }

    private void renderBudget() {
        BudgetPeriodView budgetPeriodView = (BudgetPeriodView) findViewById(R.id.budget_period);
        budgetPeriodView.bind(mBudgetPeriod);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lander);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshBudget();
        final DebitEntryView debitEntryView = (DebitEntryView) findViewById(R.id.debit_entry);
        debitEntryView.setListener(new DebitEntryView.DebitCreationListener() {
            @Override
            public void onDebitCreated(Debit debit) {
                refreshBudget();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void showSettingsMenu() {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettingsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final EditText spendAmt = (EditText)findViewById(R.id.spend_amt);
        spendAmt.requestFocus();
    }
}