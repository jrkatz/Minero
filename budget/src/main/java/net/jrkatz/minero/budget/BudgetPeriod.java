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

package net.jrkatz.minero.budget;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import net.jrkatz.minero.budget.period.Period;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */

public class BudgetPeriod {
    private final Budget mBudget;
    private final Period mPeriod;
    private final long mRemaining;
    private final ImmutableList<SpendEvent> mSpendEvents;

    public BudgetPeriod(
            Budget budget,
            Period period,
            long remaining,
            ImmutableList<SpendEvent> spendEvents
    ) {
        this.mBudget = budget;
        this.mPeriod = period;
        this.mRemaining = remaining;
        this.mSpendEvents = spendEvents;
    }

    public long getRemaining() {
        return mRemaining;
    }

    public Period getPeriod() {
        return mPeriod;
    }

    public ImmutableCollection<SpendEvent> getSpendEvents() {
        return mSpendEvents;
    }

    public Budget getBudget() {
        return mBudget;
    }

    public BudgetPeriod spend(final SpendEvent spendEvent) {
        return new BudgetPeriod(
                mBudget,
                mPeriod,
                mRemaining - spendEvent.getAmount(),
                ImmutableList.<SpendEvent>builder().addAll(mSpendEvents).add(spendEvent).build()
        );
    }

}