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

package net.jrkatz.minero.data.budgetPeriod;

import android.content.Context;

import com.google.common.collect.ImmutableList;

import net.jrkatz.minero.data.budget.Budget;
import net.jrkatz.minero.data.budget.BudgetProvider;
import net.jrkatz.minero.data.debit.Debit;
import net.jrkatz.minero.data.debit.DebitProvider;
import net.jrkatz.minero.data.period.Period;

import org.joda.time.LocalDate;

/**
 * @Author jrkatz
 * @Date 3/1/2017.
 */

public class BudgetPeriodProvider {
    private final BudgetProvider mBudgetProvider;
    private final DebitProvider mDebitProvider;

    public BudgetPeriodProvider(final Context context) {
        mBudgetProvider = new BudgetProvider(context);
        mDebitProvider = new DebitProvider(context);
    }

    public BudgetPeriod loadBudgetPeriod() {
        final Budget budget = mBudgetProvider.getBudget();
        long remaining = budget.getDistribution();

        final Period period = budget.getPeriodDefinition().periodForDate(LocalDate.now());

        final ImmutableList.Builder<Debit> debits = ImmutableList.builder();

        for(final Debit debit : mDebitProvider.readDebits(period)) {
            debits.add(debit);
            remaining -= debit.getAmount();
        }

        return new BudgetPeriod(budget, period, remaining, debits.build());
    }
}
