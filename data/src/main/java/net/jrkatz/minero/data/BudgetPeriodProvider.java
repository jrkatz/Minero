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

package net.jrkatz.minero.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.LocalDate;

/**
 * @Author jrkatz
 * @Date 4/16/2017.
 */

public abstract class BudgetPeriodProvider<ProviderContext extends IDataContext> {

    public abstract void clearBudgetPeriods(@NonNull final ProviderContext context) throws ProviderException;

    @NonNull
    public abstract BudgetPeriod createBudgetPeriod(@NonNull final ProviderContext context,
                                                             final long budgetId,
                                                             final long distribution,
                                                             @NonNull final Period period) throws ProviderException;

    @Nullable
    public abstract BudgetPeriod getLatestBudgetPeriod(@NonNull final ProviderContext context,
                                                       final long budgetId) throws ProviderException;

    @Nullable
    public abstract BudgetPeriod getBudgetPeriod(@NonNull final ProviderContext context,
                                        final long budgetId,
                                        final LocalDate date) throws ProviderException;

    @Nullable
    public abstract BudgetPeriod getBudgetPeriod(@NonNull final ProviderContext context,
                                                 final long budgetPeriodId) throws ProviderException;

    @NonNull
    public BudgetPeriod getCurrentBudgetPeriod(@NonNull final ProviderContext context,
                                               final long budgetId) throws ProviderException {
        BudgetPeriod budgetPeriod = getLatestBudgetPeriod(context, budgetId);
        //if this budgetPeriod is too old it may be necessary to create a new one or _several_
        //new ones.
        final LocalDate now = LocalDate.now();
        if (budgetPeriod == null) {
            final Budget budget = context.getBudgetProvider().getBudget(context, budgetId);
            final PeriodDefinition periodDefinition = budget.getPeriodDefinition();
            final Period period = periodDefinition.periodForDate(now);
            budgetPeriod = createBudgetPeriod(context, budgetId, budget.getDistribution(), period);
        }
        else if (!now.isBefore(budgetPeriod.getPeriod().getEnd())) {
            final BudgetProvider bp = context.getBudgetProvider();
            final Budget budget = bp.getBudget(context, budgetId);
            final PeriodDefinition periodDefinition = budget.getPeriodDefinition();
            do {
                //add savings from previous period
                bp.updateBudgetTotal(context, budgetId, budgetPeriod.getRemaining());
                //make subsequent period
                final Period nextPeriod = periodDefinition.periodForDate(budgetPeriod.getPeriod().getEnd());
                budgetPeriod = createBudgetPeriod(context, budgetId, budget.getDistribution(), nextPeriod);
            } while (!now.isBefore(budgetPeriod.getPeriod().getEnd()));
        }
        return budgetPeriod;
    }

    @NonNull
    abstract BudgetPeriod updateBudgetAmount(@NonNull final ProviderContext context,
                                             final long budgetPeriodId,
                                             final long amount) throws ProviderException;
}
