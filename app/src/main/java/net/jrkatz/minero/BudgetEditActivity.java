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
import android.support.v7.app.AppCompatActivity;

import net.jrkatz.minero.data.DataContextFactory;
import net.jrkatz.minero.data.IDataContext;
import net.jrkatz.minero.data.ProviderException;

public class BudgetEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_edit);
    }

    protected void onResume() {
        super.onResume();
        final long budgetId;
        try(final IDataContext dataContext = DataContextFactory.getDataContext(this)) {
            budgetId = dataContext.getBudgetProvider().getDefaultBudget(dataContext).getId();
        } catch (ProviderException e) {
            //TODO better than this
            throw new RuntimeException(e);
        }

        final BudgetEditView bev = (BudgetEditView) findViewById(R.id.edit_view);
        bev.bind(budgetId, new BudgetEditView.OnDone() {
            @Override
            public void onDone(boolean canceled) {
                //activity over.
                finish();
            }
        });
    }
}
