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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import net.jrkatz.minero.data.Budget;
import net.jrkatz.minero.data.BudgetPeriod;
import net.jrkatz.minero.data.DataContextFactory;
import net.jrkatz.minero.data.Debit;
import net.jrkatz.minero.data.IDataChangeListener;
import net.jrkatz.minero.data.IDataContext;
import net.jrkatz.minero.data.ProviderException;

public class Lander extends AppCompatActivity implements IDataChangeListener {

    private void refreshBudget() {
        BudgetPeriod budgetPeriod;
        try (final IDataContext providerContext = DataContextFactory.getDataContext(this)) {
            final Budget budget = providerContext.getBudgetProvider().getDefaultBudget(providerContext);
            budgetPeriod = providerContext.getBudgetPeriodProvider().getCurrentBudgetPeriod(providerContext, budget.getId());
            providerContext.markSuccessful();
        } catch (ProviderException e) {
            //TODO handle
            throw new RuntimeException(e);
        }
        renderBudget(budgetPeriod);
    }

    private void renderBudget(@NonNull final BudgetPeriod budgetPeriod) {
        final BudgetPeriodView budgetPeriodView = (BudgetPeriodView) findViewById(R.id.budget_period);
        final DebitEntryView debitEntryView = (DebitEntryView) findViewById(R.id.debit_entry);
        debitEntryView.bind(budgetPeriod.getBudgetId());
        budgetPeriodView.bind(budgetPeriod, new DebitListView.ConfirmDebitRemoval() {
            @Override
            public void confirmDebitRemoval(@NonNull final Debit debit) {
                RemoveDebitFragment.newInstance(debit).show(getFragmentManager(), "remove_debit");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lander);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
    protected void onResume() {
        super.onResume();
        rerender();
    }

    @Override
    public void rerender() {
        refreshBudget();
        final DebitEntryView debitEntryView = (DebitEntryView) findViewById(R.id.debit_entry);
        debitEntryView.setListener(new DebitEntryView.DebitCreationListener() {
            @Override
            public void onDebitCreated(Debit debit) {
                refreshBudget();
            }
        });
    }
}